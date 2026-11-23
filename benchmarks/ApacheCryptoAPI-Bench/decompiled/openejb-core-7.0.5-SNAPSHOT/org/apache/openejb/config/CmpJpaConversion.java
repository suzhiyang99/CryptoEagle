package org.apache.openejb.config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.ejb.EJBLocalObject;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.core.cmp.CmpUtil;
import org.apache.openejb.jee.CmpField;
import org.apache.openejb.jee.CmpVersion;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.EjbRelation;
import org.apache.openejb.jee.EjbRelationshipRole;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.EntityBean;
import org.apache.openejb.jee.Multiplicity;
import org.apache.openejb.jee.PersistenceContextRef;
import org.apache.openejb.jee.PersistenceType;
import org.apache.openejb.jee.Query;
import org.apache.openejb.jee.QueryMethod;
import org.apache.openejb.jee.RelationshipRoleSource;
import org.apache.openejb.jee.Relationships;
import org.apache.openejb.jee.jpa.AttributeOverride;
import org.apache.openejb.jee.jpa.Attributes;
import org.apache.openejb.jee.jpa.Basic;
import org.apache.openejb.jee.jpa.CascadeType;
import org.apache.openejb.jee.jpa.Entity;
import org.apache.openejb.jee.jpa.EntityMappings;
import org.apache.openejb.jee.jpa.GeneratedValue;
import org.apache.openejb.jee.jpa.GenerationType;
import org.apache.openejb.jee.jpa.Id;
import org.apache.openejb.jee.jpa.IdClass;
import org.apache.openejb.jee.jpa.ManyToMany;
import org.apache.openejb.jee.jpa.ManyToOne;
import org.apache.openejb.jee.jpa.MappedSuperclass;
import org.apache.openejb.jee.jpa.Mapping;
import org.apache.openejb.jee.jpa.NamedQuery;
import org.apache.openejb.jee.jpa.OneToMany;
import org.apache.openejb.jee.jpa.OneToOne;
import org.apache.openejb.jee.jpa.RelationField;
import org.apache.openejb.jee.jpa.Transient;
import org.apache.openejb.jee.jpa.unit.Persistence;
import org.apache.openejb.jee.jpa.unit.PersistenceUnit;
import org.apache.openejb.jee.jpa.unit.TransactionType;
import org.apache.openejb.jee.oejb3.EjbDeployment;
import org.apache.openejb.jee.oejb3.OpenejbJar;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.Strings;

public class CmpJpaConversion implements DynamicDeployer {
   private static final Logger logger;
   private static final String CMP_PERSISTENCE_UNIT_NAME = "cmp";
   private static final Set<String> ENHANCED_FIELDS;

   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      if (!this.hasCmpEntities(appModule)) {
         return appModule;
      } else {
         EntityMappings cmpMappings = appModule.getCmpMappings();
         if (cmpMappings == null) {
            cmpMappings = new EntityMappings();
            cmpMappings.setVersion("1.0");
            appModule.setCmpMappings(cmpMappings);
         }

         for(EjbModule ejbModule : appModule.getEjbModules()) {
            EjbJar ejbJar = ejbModule.getEjbJar();

            for(EnterpriseBean enterpriseBean : ejbJar.getEnterpriseBeans()) {
               if (isCmpEntity(enterpriseBean)) {
                  this.processEntityBean(ejbModule, cmpMappings, (EntityBean)enterpriseBean);
               }
            }

            Relationships relationships = ejbJar.getRelationships();
            if (relationships != null) {
               Map<String, Entity> entitiesByEjbName = new TreeMap();

               for(Entity entity : cmpMappings.getEntity()) {
                  entitiesByEjbName.put(entity.getEjbName(), entity);
               }

               for(EjbRelation relation : relationships.getEjbRelation()) {
                  this.processRelationship(entitiesByEjbName, relation);
               }
            }

            EntityMappings userMappings = this.getUserEntityMappings(ejbModule);

            for(Entity mapping : userMappings.getEntity()) {
               logger.warning("openejb-cmp-orm.xml mapping ignored: module=" + ejbModule.getModuleId() + ":  <entity class=\"" + mapping.getClazz() + "\">");
            }

            for(MappedSuperclass mapping : userMappings.getMappedSuperclass()) {
               logger.warning("openejb-cmp-orm.xml mapping ignored: module=" + ejbModule.getModuleId() + ":  <mapped-superclass class=\"" + mapping.getClazz() + "\">");
            }
         }

         if (!cmpMappings.getEntity().isEmpty()) {
            PersistenceUnit persistenceUnit = this.getCmpPersistenceUnit(appModule);
            persistenceUnit.getMappingFile().add("META-INF/openejb-cmp-generated-orm.xml");

            for(Entity entity : cmpMappings.getEntity()) {
               persistenceUnit.getClazz().add(entity.getClazz());
            }
         }

         for(Entity entity : appModule.getCmpMappings().getEntity()) {
            if (entity.getAttributes() != null && entity.getAttributes().isEmpty()) {
               entity.setAttributes((Attributes)null);
            }
         }

         return appModule;
      }
   }

   private PersistenceUnit getCmpPersistenceUnit(AppModule appModule) {
      PersistenceUnit persistenceUnit = null;

      for(PersistenceModule persistenceModule : appModule.getPersistenceModules()) {
         Persistence persistence = persistenceModule.getPersistence();

         for(PersistenceUnit unit : persistence.getPersistenceUnit()) {
            if ("cmp".equals(unit.getName())) {
               persistenceUnit = unit;
               break;
            }
         }
      }

      if (persistenceUnit == null) {
         persistenceUnit = new PersistenceUnit();
         persistenceUnit.setName("cmp");
         persistenceUnit.setTransactionType(TransactionType.JTA);
         Properties properties = new Properties();
         String property = SystemInstance.get().getProperty("openejb.cmp.openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=true, Indexes=false, IgnoreErrors=true)");
         if (property != null && !property.isEmpty()) {
            properties.setProperty("openjpa.jdbc.SynchronizeMappings", property);
         }

         properties.setProperty("openjpa.Log", "DefaultLevel=INFO");
         persistenceUnit.setProperties(properties);
         Persistence persistence = new Persistence();
         persistence.setVersion("1.0");
         persistence.getPersistenceUnit().add(persistenceUnit);
         PersistenceModule persistenceModule = new PersistenceModule(appModule, this.getPersistenceModuleId(appModule), persistence);
         appModule.addPersistenceModule(persistenceModule);
      }

      return persistenceUnit;
   }

   private String getPersistenceModuleId(AppModule appModule) {
      if (appModule.getModuleId() != null) {
         return appModule.getModuleId();
      } else {
         Iterator var2 = appModule.getEjbModules().iterator();
         if (var2.hasNext()) {
            EjbModule ejbModule = (EjbModule)var2.next();
            return ejbModule.getModuleId();
         } else {
            throw new IllegalStateException("Comp must be in an ejb module, this one has none: " + appModule);
         }
      }
   }

   private boolean hasCmpEntities(AppModule appModule) {
      for(EjbModule ejbModule : appModule.getEjbModules()) {
         for(EnterpriseBean bean : ejbModule.getEjbJar().getEnterpriseBeans()) {
            if (isCmpEntity(bean)) {
               return true;
            }
         }
      }

      return false;
   }

   private static boolean isCmpEntity(EnterpriseBean bean) {
      return bean instanceof EntityBean && ((EntityBean)bean).getPersistenceType() == PersistenceType.CONTAINER;
   }

   private void processRelationship(Map<String, Entity> entitiesByEjbName, EjbRelation relation) throws OpenEJBException {
      List<EjbRelationshipRole> roles = relation.getEjbRelationshipRole();
      if (roles.size() == 2) {
         EjbRelationshipRole leftRole = (EjbRelationshipRole)roles.get(0);
         RelationshipRoleSource leftRoleSource = leftRole.getRelationshipRoleSource();
         String leftEjbName = leftRoleSource == null ? null : leftRoleSource.getEjbName();
         Entity leftEntity = (Entity)entitiesByEjbName.get(leftEjbName);
         EjbRelationshipRole rightRole = (EjbRelationshipRole)roles.get(1);
         RelationshipRoleSource rightRoleSource = rightRole.getRelationshipRoleSource();
         String rightEjbName = rightRoleSource == null ? null : rightRoleSource.getEjbName();
         Entity rightEntity = (Entity)entitiesByEjbName.get(rightEjbName);
         if (leftEntity != null || rightEntity != null) {
            if (leftEntity == null) {
               throw new OpenEJBException("Role source " + leftEjbName + " defined in relationship role " + relation.getEjbRelationName() + "::" + leftRole.getEjbRelationshipRoleName() + " not found");
            } else if (rightEntity == null) {
               throw new OpenEJBException("Role source " + rightEjbName + " defined in relationship role " + relation.getEjbRelationName() + "::" + rightRole.getEjbRelationshipRoleName() + " not found");
            } else {
               Attributes rightAttributes = rightEntity.getAttributes();
               Map<String, RelationField> rightRelationships = rightAttributes.getRelationshipFieldMap();
               Attributes leftAttributes = leftEntity.getAttributes();
               Map<String, RelationField> leftRelationships = leftAttributes.getRelationshipFieldMap();
               String leftFieldName = null;
               boolean leftSynthetic = false;
               if (leftRole.getCmrField() != null) {
                  leftFieldName = leftRole.getCmrField().getCmrFieldName();
               } else {
                  leftFieldName = rightEntity.getName() + "_" + rightRole.getCmrField().getCmrFieldName();
                  leftSynthetic = true;
               }

               boolean leftIsOne = leftRole.getMultiplicity() == Multiplicity.ONE;
               String rightFieldName = null;
               boolean rightSynthetic = false;
               if (rightRole.getCmrField() != null) {
                  rightFieldName = rightRole.getCmrField().getCmrFieldName();
               } else {
                  rightFieldName = leftEntity.getName() + "_" + leftRole.getCmrField().getCmrFieldName();
                  rightSynthetic = true;
               }

               boolean rightIsOne = rightRole.getMultiplicity() == Multiplicity.ONE;
               if (leftIsOne && rightIsOne) {
                  OneToOne leftOneToOne = null;
                  leftOneToOne = new OneToOne();
                  leftOneToOne.setName(leftFieldName);
                  leftOneToOne.setSyntheticField(leftSynthetic);
                  this.setCascade(rightRole, leftOneToOne);
                  this.addRelationship(leftOneToOne, leftRelationships, leftAttributes.getOneToOne());
                  OneToOne rightOneToOne = null;
                  rightOneToOne = new OneToOne();
                  rightOneToOne.setName(rightFieldName);
                  rightOneToOne.setSyntheticField(rightSynthetic);
                  rightOneToOne.setMappedBy(leftFieldName);
                  this.setCascade(leftRole, rightOneToOne);
                  this.addRelationship(rightOneToOne, rightRelationships, rightAttributes.getOneToOne());
                  leftOneToOne.setRelatedField(rightOneToOne);
                  rightOneToOne.setRelatedField(leftOneToOne);
               } else if (leftIsOne && !rightIsOne) {
                  OneToMany leftOneToMany = null;
                  leftOneToMany = new OneToMany();
                  leftOneToMany.setName(leftFieldName);
                  leftOneToMany.setSyntheticField(leftSynthetic);
                  leftOneToMany.setMappedBy(rightFieldName);
                  this.setCascade(rightRole, leftOneToMany);
                  this.addRelationship(leftOneToMany, leftRelationships, leftAttributes.getOneToMany());
                  ManyToOne rightManyToOne = null;
                  rightManyToOne = new ManyToOne();
                  rightManyToOne.setName(rightFieldName);
                  rightManyToOne.setSyntheticField(rightSynthetic);
                  this.setCascade(leftRole, rightManyToOne);
                  this.addRelationship(rightManyToOne, rightRelationships, rightAttributes.getManyToOne());
                  leftOneToMany.setRelatedField(rightManyToOne);
                  rightManyToOne.setRelatedField(leftOneToMany);
               } else if (!leftIsOne && rightIsOne) {
                  ManyToOne leftManyToOne = null;
                  leftManyToOne = new ManyToOne();
                  leftManyToOne.setName(leftFieldName);
                  leftManyToOne.setSyntheticField(leftSynthetic);
                  this.setCascade(rightRole, leftManyToOne);
                  this.addRelationship(leftManyToOne, leftRelationships, leftAttributes.getManyToOne());
                  OneToMany rightOneToMany = null;
                  rightOneToMany = new OneToMany();
                  rightOneToMany.setName(rightFieldName);
                  rightOneToMany.setSyntheticField(rightSynthetic);
                  rightOneToMany.setMappedBy(leftFieldName);
                  this.setCascade(leftRole, rightOneToMany);
                  this.addRelationship(rightOneToMany, rightRelationships, rightAttributes.getOneToMany());
                  leftManyToOne.setRelatedField(rightOneToMany);
                  rightOneToMany.setRelatedField(leftManyToOne);
               } else if (!leftIsOne && !rightIsOne) {
                  ManyToMany leftManyToMany = null;
                  leftManyToMany = new ManyToMany();
                  leftManyToMany.setName(leftFieldName);
                  leftManyToMany.setSyntheticField(leftSynthetic);
                  this.setCascade(rightRole, leftManyToMany);
                  this.addRelationship(leftManyToMany, leftRelationships, leftAttributes.getManyToMany());
                  ManyToMany rightManyToMany = null;
                  rightManyToMany = new ManyToMany();
                  rightManyToMany.setName(rightFieldName);
                  rightManyToMany.setSyntheticField(rightSynthetic);
                  rightManyToMany.setMappedBy(leftFieldName);
                  this.setCascade(leftRole, rightManyToMany);
                  this.addRelationship(rightManyToMany, rightRelationships, rightAttributes.getManyToMany());
                  leftManyToMany.setRelatedField(rightManyToMany);
                  rightManyToMany.setRelatedField(leftManyToMany);
               }

            }
         }
      }
   }

   private <R extends RelationField> R addRelationship(R relationship, Map<String, RelationField> existing, List<R> relationships) {
      R r = null;

      try {
         r = (R)(existing.get(relationship.getKey()));
      } catch (ClassCastException var6) {
         return relationship;
      }

      if (r == null) {
         r = relationship;
         relationships.add(relationship);
      }

      return r;
   }

   private void processEntityBean(EjbModule ejbModule, EntityMappings entityMappings, EntityBean bean) {
      if (this.addPersistenceContextRef(bean)) {
         Class ejbClass = loadClass(ejbModule.getClassLoader(), bean.getEjbClass());
         String jpaEntityClassName = CmpUtil.getCmpImplClassName(bean.getAbstractSchemaName(), ejbClass.getName());
         EntityMappings userMappings = this.getUserEntityMappings(ejbModule);

         for(Class clazz = ejbClass; clazz != null; clazz = clazz.getSuperclass()) {
            MappedSuperclass mappedSuperclass = this.removeMappedSuperclass(userMappings, clazz.getName());
            if (mappedSuperclass != null) {
               entityMappings.getMappedSuperclass().add(mappedSuperclass);
            }
         }

         Entity entity = this.removeEntity(userMappings, jpaEntityClassName);
         if (entity != null) {
            entity.setXmlMetadataComplete(true);
            entityMappings.getEntity().add(entity);
         } else {
            if (entity == null) {
               entity = new Entity(jpaEntityClassName);
            }

            if (entity.getAttributes() == null) {
               entity.setAttributes(new Attributes());
            }

            entityMappings.getEntity().add(entity);
            entity.setDescription(ejbModule.getModuleId() + "#" + bean.getEjbName());
            String entityName = bean.getAbstractSchemaName();
            entity.setName(entityName);
            entity.setEjbName(bean.getEjbName());
            ClassLoader classLoader = ejbModule.getClassLoader();
            Collection<MappedSuperclass> mappedSuperclasses;
            if (bean.getCmpVersion() == CmpVersion.CMP2) {
               mappedSuperclasses = this.mapClass2x(entity, bean, classLoader);
            } else {
               mappedSuperclasses = this.mapClass1x(bean.getEjbClass(), entity, bean, classLoader);
            }

            if (mappedSuperclasses != null) {
               for(MappedSuperclass mappedSuperclass : mappedSuperclasses) {
                  entityMappings.getMappedSuperclass().add(mappedSuperclass);
               }
            }

            for(Query query : bean.getQuery()) {
               NamedQuery namedQuery = new NamedQuery();
               QueryMethod queryMethod = query.getQueryMethod();
               StringBuilder name = new StringBuilder();
               name.append(entityName).append(".").append(queryMethod.getMethodName());
               if (queryMethod.getMethodParams() != null && !queryMethod.getMethodParams().getMethodParam().isEmpty()) {
                  name.append('(');
                  boolean first = true;

                  for(String methodParam : queryMethod.getMethodParams().getMethodParam()) {
                     if (!first) {
                        name.append(",");
                     }

                     name.append(methodParam);
                     first = false;
                  }

                  name.append(')');
               }

               namedQuery.setName(name.toString());
               namedQuery.setQuery(query.getEjbQl());
               entity.getNamedQuery().add(namedQuery);
            }

            OpenejbJar openejbJar = ejbModule.getOpenejbJar();
            EjbDeployment ejbDeployment = (EjbDeployment)openejbJar.getDeploymentsByEjbName().get(bean.getEjbName());
            if (ejbDeployment != null) {
               for(org.apache.openejb.jee.oejb3.Query query : ejbDeployment.getQuery()) {
                  NamedQuery namedQuery = new NamedQuery();
                  org.apache.openejb.jee.oejb3.QueryMethod queryMethod = query.getQueryMethod();
                  StringBuilder name = new StringBuilder();
                  name.append(entityName).append(".").append(queryMethod.getMethodName());
                  if (queryMethod.getMethodParams() != null && !queryMethod.getMethodParams().getMethodParam().isEmpty()) {
                     name.append('(');
                     boolean first = true;

                     for(String methodParam : queryMethod.getMethodParams().getMethodParam()) {
                        if (!first) {
                           name.append(",");
                        }

                        name.append(methodParam);
                        first = false;
                     }

                     name.append(')');
                  }

                  namedQuery.setName(name.toString());
                  namedQuery.setQuery(query.getObjectQl());
                  entity.getNamedQuery().add(namedQuery);
               }
            }

         }
      }
   }

   private Entity removeEntity(EntityMappings userMappings, String className) {
      Entity entity = (Entity)userMappings.getEntityMap().get(className);
      if (entity != null) {
         userMappings.getEntityMap().remove(entity.getKey());
      }

      return entity;
   }

   private MappedSuperclass removeMappedSuperclass(EntityMappings userMappings, String className) {
      MappedSuperclass mappedSuperclass = (MappedSuperclass)userMappings.getMappedSuperclassMap().get(className);
      if (mappedSuperclass != null) {
         userMappings.getMappedSuperclassMap().remove(mappedSuperclass.getKey());
      }

      return mappedSuperclass;
   }

   private EntityMappings getUserEntityMappings(EjbModule ejbModule) {
      Object o = ejbModule.getAltDDs().get("openejb-cmp-orm.xml");
      return o instanceof EntityMappings ? (EntityMappings)o : new EntityMappings();
   }

   private Collection<MappedSuperclass> mapClass2x(Mapping mapping, EntityBean bean, ClassLoader classLoader) {
      Set<String> allFields = new TreeSet();

      for(CmpField cmpField : bean.getCmpField()) {
         allFields.add(cmpField.getFieldName());
      }

      Class<?> beanClass = null;

      try {
         beanClass = classLoader.loadClass(bean.getEjbClass());
      } catch (ClassNotFoundException var17) {
         return null;
      }

      Map<String, MappedSuperclass> superclassByField = this.mapFields(beanClass, allFields);

      for(Method method : beanClass.getMethods()) {
         if (Modifier.isAbstract(method.getModifiers()) && method.getParameterTypes().length == 0 && !method.getReturnType().equals(Void.TYPE) && !EJBLocalObject.class.isAssignableFrom(method.getReturnType()) && !Collection.class.isAssignableFrom(method.getReturnType()) && !Map.class.isAssignableFrom(method.getReturnType())) {
            String name = method.getName();
            if (name.startsWith("get")) {
               name = name.substring("get".length(), name.length());
            } else {
               if (!name.startsWith("is") || method.getReturnType() != Boolean.TYPE) {
                  continue;
               }

               name = name.substring("is".length(), name.length());
            }

            name = Strings.lcfirst(name);
            if (!allFields.contains(name)) {
               allFields.add(name);
               bean.addCmpField(name);
            }
         }
      }

      Set<String> primaryKeyFields = new HashSet();
      if (bean.getPrimkeyField() != null) {
         String fieldName = bean.getPrimkeyField();
         MappedSuperclass superclass = (MappedSuperclass)superclassByField.get(fieldName);
         if (superclass != null) {
            superclass.addField(new Id(fieldName));
            mapping.addField(new AttributeOverride(fieldName));
         } else {
            mapping.addField(new Id(fieldName));
         }

         primaryKeyFields.add(fieldName);
      } else if ("java.lang.Object".equals(bean.getPrimKeyClass())) {
         String fieldName = "OpenEJB_pk";
         Id field = new Id("OpenEJB_pk");
         field.setGeneratedValue(new GeneratedValue(GenerationType.AUTO));
         mapping.addField(field);
         primaryKeyFields.add("OpenEJB_pk");
      } else if (bean.getPrimKeyClass() != null) {
         Class<?> pkClass = null;

         try {
            pkClass = classLoader.loadClass(bean.getPrimKeyClass());
            MappedSuperclass idclass = null;

            for(Field pkField : pkClass.getFields()) {
               String pkFieldName = pkField.getName();
               int modifiers = pkField.getModifiers();
               if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers) && allFields.contains(pkFieldName)) {
                  MappedSuperclass superclass = (MappedSuperclass)superclassByField.get(pkFieldName);
                  if (superclass != null) {
                     superclass.addField(new Id(pkFieldName));
                     mapping.addField(new AttributeOverride(pkFieldName));
                     idclass = this.resolveIdClass(idclass, superclass, beanClass);
                  } else {
                     mapping.addField(new Id(pkFieldName));
                  }

                  primaryKeyFields.add(pkFieldName);
               }
            }

            if (idclass != null) {
               idclass.setIdClass(new IdClass(bean.getPrimKeyClass()));
            } else {
               mapping.setIdClass(new IdClass(bean.getPrimKeyClass()));
            }
         } catch (ClassNotFoundException e) {
            throw (IllegalStateException)(new IllegalStateException("Could not find entity primary key class " + bean.getPrimKeyClass())).initCause(e);
         }
      }

      for(CmpField cmpField : bean.getCmpField()) {
         if (!primaryKeyFields.contains(cmpField.getFieldName())) {
            String fieldName = cmpField.getFieldName();
            MappedSuperclass superclass = (MappedSuperclass)superclassByField.get(fieldName);
            if (superclass != null) {
               superclass.addField(new Basic(fieldName));
               mapping.addField(new AttributeOverride(fieldName));
            } else {
               mapping.addField(new Basic(fieldName));
            }
         }
      }

      return new HashSet(superclassByField.values());
   }

   private Collection<MappedSuperclass> mapClass1x(String ejbClassName, Mapping mapping, EntityBean bean, ClassLoader classLoader) {
      Class ejbClass = loadClass(classLoader, ejbClassName);
      Set<String> allFields = new TreeSet();

      for(CmpField cmpField : bean.getCmpField()) {
         allFields.add(cmpField.getFieldName());
      }

      Map<String, MappedSuperclass> superclassByField = this.mapFields(ejbClass, allFields);
      Set<String> primaryKeyFields = new HashSet();
      if (bean.getPrimkeyField() != null) {
         String fieldName = bean.getPrimkeyField();
         MappedSuperclass superclass = (MappedSuperclass)superclassByField.get(fieldName);
         if (superclass == null) {
            throw new IllegalStateException("Primary key field " + fieldName + " is not defined in class " + ejbClassName + " or any super classes");
         }

         superclass.addField(new Id(fieldName));
         mapping.addField(new AttributeOverride(fieldName));
         primaryKeyFields.add(fieldName);
      } else if ("java.lang.Object".equals(bean.getPrimKeyClass())) {
         String fieldName = "OpenEJB_pk";
         Id field = new Id("OpenEJB_pk");
         field.setGeneratedValue(new GeneratedValue(GenerationType.AUTO));
         mapping.addField(field);
      } else if (bean.getPrimKeyClass() != null) {
         Class<?> pkClass = null;

         try {
            pkClass = classLoader.loadClass(bean.getPrimKeyClass());
            MappedSuperclass superclass = null;
            MappedSuperclass idclass = null;

            for(Field pkField : pkClass.getFields()) {
               String fieldName = pkField.getName();
               int modifiers = pkField.getModifiers();
               if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers) && allFields.contains(fieldName)) {
                  superclass = (MappedSuperclass)superclassByField.get(fieldName);
                  if (superclass == null) {
                     throw new IllegalStateException("Primary key field " + fieldName + " is not defined in class " + ejbClassName + " or any super classes");
                  }

                  superclass.addField(new Id(fieldName));
                  mapping.addField(new AttributeOverride(fieldName));
                  primaryKeyFields.add(fieldName);
                  idclass = this.resolveIdClass(idclass, superclass, ejbClass);
               }
            }

            if (idclass != null) {
               idclass.setIdClass(new IdClass(bean.getPrimKeyClass()));
            }
         } catch (ClassNotFoundException e) {
            throw (IllegalStateException)(new IllegalStateException("Could not find entity primary key class " + bean.getPrimKeyClass())).initCause(e);
         }
      }

      for(CmpField cmpField : bean.getCmpField()) {
         String fieldName = cmpField.getFieldName();
         if (!primaryKeyFields.contains(fieldName)) {
            MappedSuperclass superclass = (MappedSuperclass)superclassByField.get(fieldName);
            if (superclass == null) {
               throw new IllegalStateException("CMP field " + fieldName + " is not defined in class " + ejbClassName + " or any super classes");
            }

            superclass.addField(new Basic(fieldName));
            mapping.addField(new AttributeOverride(fieldName));
         }
      }

      return new HashSet(superclassByField.values());
   }

   private MappedSuperclass resolveIdClass(MappedSuperclass idclass, MappedSuperclass current, Class ejbClass) {
      if (idclass == null) {
         return current;
      } else {
         String idClassName = idclass.getClazz();
         String currentClassName = current.getClazz();
         if (idClassName.equals(currentClassName)) {
            return idclass;
         } else {
            for(Class clazz = ejbClass; clazz != null; clazz = clazz.getSuperclass()) {
               String name = clazz.getName();
               if (name.equals(currentClassName)) {
                  return current;
               }

               if (name.equals(idClassName)) {
                  return idclass;
               }
            }

            return idclass;
         }
      }
   }

   private static Class loadClass(ClassLoader classLoader, String className) {
      Class ejbClass = null;

      try {
         ejbClass = classLoader.loadClass(className);
         return ejbClass;
      } catch (ClassNotFoundException e) {
         throw new IllegalArgumentException(e);
      }
   }

   private Map<String, MappedSuperclass> mapFields(Class clazz, Set<String> persistantFields) {
      persistantFields = new TreeSet(persistantFields);

      Map<String, MappedSuperclass> fields;
      for(fields = new TreeMap(); !persistantFields.isEmpty() && !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
         MappedSuperclass superclass = new MappedSuperclass(clazz.getName());

         for(Field field : clazz.getDeclaredFields()) {
            if (!field.isSynthetic()) {
               String fieldName = field.getName();
               if (persistantFields.contains(fieldName)) {
                  fields.put(fieldName, superclass);
                  persistantFields.remove(fieldName);
               } else if (!ENHANCED_FIELDS.contains(fieldName)) {
                  Transient transientField = new Transient(fieldName);
                  superclass.addField(transientField);
               }
            }
         }
      }

      return fields;
   }

   private boolean addPersistenceContextRef(EntityBean bean) {
      if (bean.getPersistenceContextRefMap().containsKey("java:comp/env/openejb/cmp")) {
         return false;
      } else {
         PersistenceContextRef persistenceContextRef = new PersistenceContextRef();
         persistenceContextRef.setName("java:comp/env/openejb/cmp");
         persistenceContextRef.setPersistenceUnitName("cmp");
         bean.getPersistenceContextRef().add(persistenceContextRef);
         return true;
      }
   }

   private void setCascade(EjbRelationshipRole role, RelationField field) {
      if (role.getCascadeDelete()) {
         CascadeType cascadeType = new CascadeType();
         cascadeType.setCascadeAll(true);
         field.setCascade(cascadeType);
      }

   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP_CONFIG, CmpJpaConversion.class);
      ENHANCED_FIELDS = Collections.unmodifiableSet(new TreeSet(Arrays.asList("pcInheritedFieldCount", "pcFieldNames", "pcFieldTypes", "pcFieldFlags", "pcPCSuperclass", "pcStateManager", "class$Ljava$lang$String", "class$Ljava$lang$Integer", "class$Lcom$sun$ts$tests$common$ejb$wrappers$CMP11Wrapper", "pcDetachedState", "serialVersionUID")));
   }
}
