package org.apache.openejb.core.cmp;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import org.apache.openejb.OpenEJBException;

public class ComplexKeyGenerator extends AbstractKeyGenerator {
   protected final List<PkField> fields;
   private final Class pkClass;

   public ComplexKeyGenerator(Class entityBeanClass, Class pkClass) throws OpenEJBException {
      this.pkClass = pkClass;
      List<PkField> fields = new ArrayList();

      for(Field pkObjectField : pkClass.getFields()) {
         if (isValidPkField(pkObjectField)) {
            Field entityBeanField = getField(entityBeanClass, pkObjectField.getName());
            if (!isValidPkField(entityBeanField)) {
               throw new OpenEJBException("Invalid primray key field: " + entityBeanField);
            }

            PkField pkField = new PkField(entityBeanField, pkObjectField);
            fields.add(pkField);
         }
      }

      this.fields = Collections.unmodifiableList(fields);
   }

   public Object getPrimaryKey(EntityBean bean) {
      Object pkObject = null;

      try {
         pkObject = this.pkClass.newInstance();
      } catch (Exception e) {
         throw new EJBException("Unable to create complex primary key instance: " + this.pkClass.getName(), e);
      }

      for(PkField pkField : this.fields) {
         pkField.copyToPkObject(bean, pkObject);
      }

      return pkObject;
   }

   protected static class PkField {
      private final Field entityBeanField;
      private final Field pkObjectField;

      public PkField(Field entityBeanField, Field pkObjectField) {
         entityBeanField.setAccessible(true);
         pkObjectField.setAccessible(true);
         this.entityBeanField = entityBeanField;
         this.pkObjectField = pkObjectField;
      }

      public void copyToPkObject(EntityBean bean, Object pkObject) {
         Object value = AbstractKeyGenerator.getFieldValue(this.entityBeanField, bean);
         AbstractKeyGenerator.setFieldValue(this.pkObjectField, pkObject, value);
      }

      public Object getPkFieldValue(Object pkObject) {
         Object value = AbstractKeyGenerator.getFieldValue(this.pkObjectField, pkObject);
         return value;
      }
   }
}
