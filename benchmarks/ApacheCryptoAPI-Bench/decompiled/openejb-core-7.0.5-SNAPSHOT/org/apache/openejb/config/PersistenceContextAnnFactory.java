package org.apache.openejb.config;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceProperty;
import javax.persistence.SynchronizationType;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.loader.IO;
import org.apache.xbean.asm5.AnnotationVisitor;
import org.apache.xbean.asm5.ClassReader;
import org.apache.xbean.asm5.FieldVisitor;
import org.apache.xbean.asm5.MethodVisitor;
import org.apache.xbean.asm5.shade.commons.EmptyVisitor;

public class PersistenceContextAnnFactory {
   private static boolean useAsm;
   public Map<String, AsmPersistenceContext> contexts = new HashMap();
   private final Set<String> processed = new HashSet();

   public void addAnnotations(Class c) throws OpenEJBException {
      if (useAsm) {
         if (!this.processed.contains(c.getName())) {
            try {
               URL u = c.getResource("/" + c.getName().replace('.', '/') + ".class");
               ClassReader r = new ClassReader(IO.read(u));
               r.accept(new PersistenceContextReader(this.contexts), 2);
            } catch (IOException var4) {
               throw new OpenEJBException("Unable to read class " + c.getName());
            }

            this.processed.add(c.getName());
         }
      }
   }

   public PersistenceContextAnn create(PersistenceContext persistenceContext, AnnotationDeployer.Member member) throws OpenEJBException {
      if (useAsm) {
         if (member != null) {
            this.addAnnotations(member.getDeclaringClass());
         }

         String name = persistenceContext.name();
         if (name == null || name.equals("")) {
            name = member == null ? null : member.getDeclaringClass().getName() + "/" + member.getName();
         }

         AsmPersistenceContext asmPersistenceContext = (AsmPersistenceContext)this.contexts.get(name);
         if (asmPersistenceContext == null) {
            throw new NullPointerException("PersistenceContext " + name + " not found");
         } else {
            return asmPersistenceContext;
         }
      } else {
         return new DirectPersistenceContext(persistenceContext);
      }
   }

   static {
      boolean isPersistenceContextAnnotationValid = false;

      try {
         Class<?> persistenceContextClass = Class.forName("javax.persistence.PersistenceContext");
         persistenceContextClass.getMethod("properties", (Class[])null);
         isPersistenceContextAnnotationValid = true;
      } catch (Exception var2) {
      }

      useAsm = !isPersistenceContextAnnotationValid;
   }

   private static class DirectPersistenceContext implements PersistenceContextAnn {
      private final PersistenceContext persistenceContext;

      public DirectPersistenceContext(PersistenceContext persistenceContext) {
         if (persistenceContext == null) {
            throw new NullPointerException("persistenceContext is null");
         } else {
            this.persistenceContext = persistenceContext;
         }
      }

      public String name() {
         return this.persistenceContext.name();
      }

      public String synchronization() {
         return this.persistenceContext.synchronization().name();
      }

      public String unitName() {
         return this.persistenceContext.unitName();
      }

      public String type() {
         return this.persistenceContext.type() == null ? null : this.persistenceContext.type().toString();
      }

      public Map<String, String> properties() {
         Map<String, String> properties = new LinkedHashMap();

         for(PersistenceProperty property : this.persistenceContext.properties()) {
            properties.put(property.name(), property.value());
         }

         return properties;
      }

      public String toString() {
         return this.persistenceContext.toString();
      }
   }

   private static class AsmPersistenceContext implements PersistenceContextAnn {
      public String name;
      public String unitName;
      public String type;
      public String synchronization;
      public final Map<String, String> properties;

      private AsmPersistenceContext() {
         this.synchronization = SynchronizationType.SYNCHRONIZED.name();
         this.properties = new LinkedHashMap();
      }

      public String name() {
         return this.name;
      }

      public String synchronization() {
         return this.synchronization;
      }

      public String unitName() {
         return this.unitName;
      }

      public String type() {
         return this.type;
      }

      public Map<String, String> properties() {
         return this.properties;
      }

      public String toString() {
         return "@PersistenceContext(name = \"" + this.name + "\", unitName = \"" + this.unitName + "\", type = PersistenceContextType." + this.type + ", " + this.properties + ")";
      }
   }

   private static final class PersistenceContextReader extends EmptyVisitor {
      private String className;
      private String currentName;
      private final Map<String, AsmPersistenceContext> contexts;

      private PersistenceContextReader(Map<String, AsmPersistenceContext> contexts) {
         this.contexts = contexts;
      }

      public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
         this.className = name.replace("/", ".");
         super.visit(version, access, name, signature, superName, interfaces);
      }

      public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
         this.currentName = name;
         return super.visitField(access, name, desc, signature, value);
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         this.currentName = name;
         if (this.currentName.startsWith("set")) {
            this.currentName = this.currentName.substring(3);
         }

         if (this.currentName.length() > 0) {
            this.currentName = Character.toLowerCase(this.currentName.charAt(0)) + this.currentName.substring(1);
         }

         return super.visitMethod(access, name, desc, signature, exceptions);
      }

      public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
         return this.visitAnnotation((String)null, desc);
      }

      public AnnotationVisitor visitAnnotation(String name, String desc) {
         if ("Ljavax/persistence/PersistenceContext;".equals(desc)) {
            return new PersistenceContextVisitor(this.className, this.currentName, this.contexts);
         } else {
            return "Ljavax/persistence/PersistenceContexts;".equals(desc) ? super.visitAnnotation(name, desc) : (new EmptyVisitor()).annotationVisitor();
         }
      }

      public AnnotationVisitor visitArray(String string) {
         return this.annotationVisitor();
      }

      public AnnotationVisitor visitMethodParameterAnnotation(int i, String string, boolean b) {
         return (new EmptyVisitor()).annotationVisitor();
      }

      public AnnotationVisitor visitParameterAnnotation(int i, String string, boolean b) {
         return (new EmptyVisitor()).annotationVisitor();
      }

      public AnnotationVisitor visitAnnotationDefault() {
         return (new EmptyVisitor()).annotationVisitor();
      }
   }

   private static class PersistenceContextVisitor extends AnnotationVisitor {
      private final Map<String, AsmPersistenceContext> contexts;
      private final AsmPersistenceContext persistenceContext = new AsmPersistenceContext();

      public PersistenceContextVisitor(String className, String memberName, Map<String, AsmPersistenceContext> contexts) {
         super(327680);
         this.contexts = contexts;
         this.persistenceContext.name = className + "/" + memberName;
      }

      public void visit(String name, Object value) {
         this.setValue(name, value == null ? null : value.toString());
      }

      public void visitEnum(String name, String type, String value) {
         this.setValue(name, value == null ? null : value.toString());
      }

      public AnnotationVisitor visitAnnotation(String name, String desc) {
         this.setValue(name, desc);
         return this;
      }

      private void setValue(String name, String value) {
         if ("name".equals(name)) {
            this.persistenceContext.name = value;
         } else if ("unitName".equals(name)) {
            this.persistenceContext.unitName = value;
         } else if ("type".equals(name)) {
            this.persistenceContext.type = value;
         } else if ("synchronization".equals(name)) {
            this.persistenceContext.synchronization = value;
         }

      }

      public AnnotationVisitor visitArray(String string) {
         return (new EmptyVisitor() {
            private String name;
            private String value;

            public void visit(String n, Object v) {
               if ("name".equals(n)) {
                  this.name = v == null ? null : v.toString();
               } else if ("value".equals(n)) {
                  this.value = v == null ? null : v.toString();
               }

            }

            public void visitEnd() {
               PersistenceContextVisitor.this.persistenceContext.properties.put(this.name, this.value);
            }
         }).annotationVisitor();
      }

      public void visitEnd() {
         this.contexts.put(this.persistenceContext.name, this.persistenceContext);
      }
   }
}
