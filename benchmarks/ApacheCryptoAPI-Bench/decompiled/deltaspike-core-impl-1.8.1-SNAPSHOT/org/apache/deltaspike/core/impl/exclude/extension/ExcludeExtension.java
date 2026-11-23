package org.apache.deltaspike.core.impl.exclude.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.util.Nonbinding;
import org.apache.deltaspike.core.api.config.ConfigResolver;
import org.apache.deltaspike.core.api.config.base.CoreBaseConfig.InterceptorCustomization;
import org.apache.deltaspike.core.api.exclude.Exclude;
import org.apache.deltaspike.core.api.interpreter.ExpressionInterpreter;
import org.apache.deltaspike.core.api.projectstage.ProjectStage;
import org.apache.deltaspike.core.impl.exclude.CustomProjectStageBeanFilter;
import org.apache.deltaspike.core.impl.exclude.GlobalAlternative;
import org.apache.deltaspike.core.impl.interpreter.PropertyExpressionInterpreter;
import org.apache.deltaspike.core.impl.util.AnnotationInstanceUtils;
import org.apache.deltaspike.core.spi.activation.Deactivatable;
import org.apache.deltaspike.core.util.ClassDeactivationUtils;
import org.apache.deltaspike.core.util.ClassUtils;
import org.apache.deltaspike.core.util.ProjectStageProducer;
import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

public class ExcludeExtension implements Extension, Deactivatable {
   private static final String GLOBAL_ALTERNATIVES = "globalAlternatives.";
   private static final Logger LOG = Logger.getLogger(ExcludeExtension.class.getName());
   private boolean isActivated = true;
   private boolean isGlobalAlternativeActivated = true;
   private boolean isCustomProjectStageBeanFilterActivated = true;
   private Map<String, String> globalAlternatives = new HashMap();
   private Annotation priorityAnnotationInstance;

   protected void init(@Observes BeforeBeanDiscovery beforeBeanDiscovery, BeanManager beanManager) {
      this.isActivated = ClassDeactivationUtils.isActivated(this.getClass());
      this.isCustomProjectStageBeanFilterActivated = ClassDeactivationUtils.isActivated(CustomProjectStageBeanFilter.class);
      this.isGlobalAlternativeActivated = ClassDeactivationUtils.isActivated(GlobalAlternative.class);
      if (this.isGlobalAlternativeActivated) {
         Map<String, String> allProperties = ConfigResolver.getAllProperties();

         for(Map.Entry<String, String> property : allProperties.entrySet()) {
            if (((String)property.getKey()).startsWith("globalAlternatives.")) {
               String interfaceName = ((String)property.getKey()).substring("globalAlternatives.".length());
               String implementation = (String)property.getValue();
               if (LOG.isLoggable(Level.FINE)) {
                  LOG.fine("Enabling global alternative for interface " + interfaceName + ": " + implementation);
               }

               this.globalAlternatives.put(interfaceName, implementation);
            }
         }

         if (this.globalAlternatives.isEmpty()) {
            this.isGlobalAlternativeActivated = false;
         }

         if (this.isGlobalAlternativeActivated) {
            int priorityValue = InterceptorCustomization.PRIORITY;
            this.priorityAnnotationInstance = AnnotationInstanceUtils.getPriorityAnnotationInstance(priorityValue);
         }
      }

   }

   protected void initProjectStage(@Observes AfterDeploymentValidation afterDeploymentValidation) {
      ProjectStageProducer.getInstance();
   }

   protected void vetoBeans(@Observes ProcessAnnotatedType processAnnotatedType, BeanManager beanManager) {
      if (this.isGlobalAlternativeActivated) {
         this.activateGlobalAlternatives(processAnnotatedType, beanManager);
      }

      if (this.isCustomProjectStageBeanFilterActivated) {
         this.vetoCustomProjectStageBeans(processAnnotatedType);
      }

      if (this.isActivated) {
         ProjectStage projectStage = ProjectStageProducer.getInstance().getProjectStage();
         Exclude exclude = this.extractExcludeAnnotation(processAnnotatedType.getAnnotatedType().getJavaClass());
         if (exclude != null) {
            if (this.evalExcludeWithoutCondition(processAnnotatedType, exclude)) {
               if (this.evalExcludeInProjectStage(processAnnotatedType, exclude, projectStage)) {
                  if (this.evalExcludeNotInProjectStage(processAnnotatedType, exclude, projectStage)) {
                     this.evalExcludeWithExpression(processAnnotatedType, exclude);
                  }
               }
            }
         }
      }
   }

   protected Exclude extractExcludeAnnotation(Class<?> currentClass) {
      Exclude result = (Exclude)currentClass.getAnnotation(Exclude.class);
      if (result != null) {
         return result;
      } else {
         for(Class<?> var3 = currentClass.getSuperclass(); !Object.class.equals(var3) && var3 != null; var3 = var3.getSuperclass()) {
            if (Modifier.isAbstract(var3.getModifiers())) {
               result = (Exclude)var3.getAnnotation(Exclude.class);
            }

            if (result != null) {
               return result;
            }
         }

         return null;
      }
   }

   protected void vetoCustomProjectStageBeans(ProcessAnnotatedType processAnnotatedType) {
      if (ProjectStage.class.isAssignableFrom(processAnnotatedType.getAnnotatedType().getJavaClass())) {
         processAnnotatedType.veto();
      }

   }

   private void activateGlobalAlternatives(ProcessAnnotatedType processAnnotatedType, BeanManager beanManager) {
      Class<Object> currentBean = processAnnotatedType.getAnnotatedType().getJavaClass();
      if (!currentBean.isInterface()) {
         Set<Class> beanBaseTypes = this.resolveBeanTypes(currentBean);
         boolean isAlternativeBeanImplementation = currentBean.isAnnotationPresent(Alternative.class);
         List<Annotation> qualifiersOfCurrentBean = this.resolveQualifiers(processAnnotatedType.getAnnotatedType().getAnnotations(), beanManager);

         for(Class currentType : beanBaseTypes) {
            Set<Annotation> alternativeBeanAnnotations = new HashSet();
            String configuredBeanName = (String)this.globalAlternatives.get(currentType.getName());
            if (configuredBeanName != null && configuredBeanName.length() > 0) {
               Class<Object> alternativeBeanClass = ClassUtils.tryToLoadClassForName(configuredBeanName);
               if (alternativeBeanClass == null) {
                  throw new IllegalStateException("Can't find class " + configuredBeanName + " which is configured for " + currentType.getName());
               }

               if (alternativeBeanClass.isAnnotationPresent(Alternative.class)) {
                  alternativeBeanAnnotations.addAll(Arrays.asList(alternativeBeanClass.getAnnotations()));
                  List<Annotation> qualifiersOfConfiguredBean = this.resolveQualifiers(alternativeBeanAnnotations, beanManager);
                  if (this.doQualifiersMatch(qualifiersOfCurrentBean, qualifiersOfConfiguredBean)) {
                     if (isAlternativeBeanImplementation && alternativeBeanClass.equals(currentBean)) {
                        LOG.info(processAnnotatedType.getAnnotatedType().getJavaClass().getName() + " is configured as global-alternative");
                        if (this.priorityAnnotationInstance == null) {
                           AnnotatedTypeBuilder<Object> annotatedTypeBuilder = (new AnnotatedTypeBuilder()).readFromType(processAnnotatedType.getAnnotatedType());
                           annotatedTypeBuilder.removeFromClass(Alternative.class);
                           processAnnotatedType.setAnnotatedType(annotatedTypeBuilder.create());
                           return;
                        }

                        AnnotatedTypeBuilder<Object> annotatedTypeBuilder = (new AnnotatedTypeBuilder()).readFromType(processAnnotatedType.getAnnotatedType());
                        annotatedTypeBuilder.addToClass(this.priorityAnnotationInstance);
                        processAnnotatedType.setAnnotatedType(annotatedTypeBuilder.create());
                        return;
                     }

                     if (this.priorityAnnotationInstance == null) {
                        processAnnotatedType.veto();
                        return;
                     }
                  }
               }
            }
         }

      }
   }

   private boolean doQualifiersMatch(List<Annotation> qualifiersOfCurrentBean, List<Annotation> qualifiersOfConfiguredBean) {
      if (qualifiersOfCurrentBean.size() != qualifiersOfConfiguredBean.size()) {
         return false;
      } else {
         int matchingQualifiers = 0;

         for(Annotation currentQualifier : qualifiersOfCurrentBean) {
            for(Annotation qualifierConfiguredBean : qualifiersOfConfiguredBean) {
               if (this.doesQualifierMatch(currentQualifier, qualifierConfiguredBean)) {
                  ++matchingQualifiers;
                  break;
               }
            }
         }

         return qualifiersOfConfiguredBean.size() == matchingQualifiers;
      }
   }

   private boolean doesQualifierMatch(Annotation currentQualifier, Annotation qualifierConfiguredBean) {
      if (!currentQualifier.annotationType().equals(qualifierConfiguredBean.annotationType())) {
         return false;
      } else {
         for(Method currentMethod : currentQualifier.annotationType().getDeclaredMethods()) {
            if (!currentMethod.isAnnotationPresent(Nonbinding.class)) {
               try {
                  currentMethod.setAccessible(true);
                  Object currentValue = currentMethod.invoke(currentQualifier);
                  Object valueOfQualifierConfiguredBean = currentMethod.invoke(qualifierConfiguredBean);
                  if (!currentValue.equals(valueOfQualifierConfiguredBean)) {
                     return false;
                  }
               } catch (Exception e) {
                  throw new IllegalStateException("Can't compare " + currentQualifier.annotationType().getName() + " with " + qualifierConfiguredBean.annotationType().getName(), e);
               }
            }
         }

         return true;
      }
   }

   private List<Annotation> resolveQualifiers(Set<Annotation> annotations, BeanManager beanManager) {
      List<Annotation> result = new ArrayList();

      for(Annotation annotation : annotations) {
         if (beanManager.isQualifier(annotation.annotationType())) {
            result.add(annotation);
         }
      }

      return result;
   }

   private Set<Class> resolveBeanTypes(Class beanClass) {
      Set<Class> result = new HashSet();

      for(Class<?> currentClass = beanClass; currentClass != null && !Object.class.getName().equals(currentClass.getName()); currentClass = currentClass.getSuperclass()) {
         result.add(currentClass);

         for(Class interfaceClass : currentClass.getInterfaces()) {
            if (!interfaceClass.getName().startsWith("java.") && !interfaceClass.getName().startsWith("javax.")) {
               result.addAll(this.resolveBeanTypes(interfaceClass));
            }
         }
      }

      return result;
   }

   private boolean evalExcludeWithoutCondition(ProcessAnnotatedType processAnnotatedType, Exclude exclude) {
      if (exclude.ifProjectStage().length == 0 && exclude.exceptIfProjectStage().length == 0 && "".equals(exclude.onExpression())) {
         this.veto(processAnnotatedType, "Stateless");
         return false;
      } else {
         return true;
      }
   }

   private boolean evalExcludeInProjectStage(ProcessAnnotatedType processAnnotatedType, Exclude exclude, ProjectStage currentlyConfiguredProjectStage) {
      Class<? extends ProjectStage>[] activatedIn = exclude.ifProjectStage();
      if (activatedIn.length == 0) {
         return true;
      } else if (this.isInProjectStage(activatedIn, currentlyConfiguredProjectStage)) {
         this.veto(processAnnotatedType, "IfProjectState");
         return false;
      } else {
         return true;
      }
   }

   private boolean evalExcludeNotInProjectStage(ProcessAnnotatedType processAnnotatedType, Exclude exclude, ProjectStage currentlyConfiguredProjectStage) {
      Class<? extends ProjectStage>[] notIn = exclude.exceptIfProjectStage();
      if (notIn.length == 0) {
         return true;
      } else if (!this.isInProjectStage(notIn, currentlyConfiguredProjectStage)) {
         this.veto(processAnnotatedType, "ExceptIfProjectState");
         return false;
      } else {
         return true;
      }
   }

   private void evalExcludeWithExpression(ProcessAnnotatedType processAnnotatedType, Exclude exclude) {
      if (!"".equals(exclude.onExpression())) {
         if (this.isDeactivated(exclude, PropertyExpressionInterpreter.class)) {
            this.veto(processAnnotatedType, "Expression");
         }

      }
   }

   private boolean isInProjectStage(Class<? extends ProjectStage>[] activatedIn, ProjectStage currentlyConfiguredProjectStage) {
      if (activatedIn != null && activatedIn.length > 0) {
         for(Class<? extends ProjectStage> activated : activatedIn) {
            if (currentlyConfiguredProjectStage.getClass().equals(activated)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean isDeactivated(Exclude exclude, Class defaultExpressionInterpreterClass) {
      String expressions = exclude.onExpression();
      Class<? extends ExpressionInterpreter> interpreterClass = exclude.interpretedBy();
      if (interpreterClass.equals(ExpressionInterpreter.class)) {
         interpreterClass = defaultExpressionInterpreterClass;
      }

      ExpressionInterpreter<String, Boolean> expressionInterpreter = (ExpressionInterpreter)ClassUtils.tryToInstantiateClass(interpreterClass);
      if (expressionInterpreter == null) {
         if (LOG.isLoggable(Level.WARNING)) {
            LOG.warning("can't instantiate " + interpreterClass.getClass().getName());
         }

         return true;
      } else {
         return (Boolean)expressionInterpreter.evaluate(expressions);
      }
   }

   private void veto(ProcessAnnotatedType processAnnotatedType, String vetoType) {
      processAnnotatedType.veto();
      LOG.finer(vetoType + " based veto for bean with type: " + processAnnotatedType.getAnnotatedType().getJavaClass());
   }

   private static String getJarVersion(Class targetClass) {
      String manifestFileLocation = getManifestFileLocationOfClass(targetClass);

      try {
         return (new Manifest((new URL(manifestFileLocation)).openStream())).getMainAttributes().getValue(Name.SPECIFICATION_VERSION);
      } catch (Exception var3) {
         return null;
      }
   }

   private static String getManifestFileLocationOfClass(Class targetClass) {
      String manifestFileLocation;
      try {
         manifestFileLocation = getManifestLocation(targetClass);
      } catch (Exception var3) {
         manifestFileLocation = getManifestLocation(targetClass.getSuperclass());
      }

      return manifestFileLocation;
   }

   private static String getManifestLocation(Class targetClass) {
      String classFilePath = targetClass.getCanonicalName().replace('.', '/') + ".class";
      String manifestFilePath = "/META-INF/MANIFEST.MF";
      String classLocation = targetClass.getResource(targetClass.getSimpleName() + ".class").toString();
      return classLocation.substring(0, classLocation.indexOf(classFilePath) - 1) + manifestFilePath;
   }
}
