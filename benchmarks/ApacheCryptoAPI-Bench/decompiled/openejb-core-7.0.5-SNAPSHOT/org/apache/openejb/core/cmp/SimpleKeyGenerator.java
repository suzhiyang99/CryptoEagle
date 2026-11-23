package org.apache.openejb.core.cmp;

import java.lang.reflect.Field;
import javax.ejb.EntityBean;
import org.apache.openejb.OpenEJBException;

public class SimpleKeyGenerator extends AbstractKeyGenerator {
   private final Field pkField;

   public SimpleKeyGenerator(Class beanClass, String pkField) throws OpenEJBException {
      this.pkField = getField(beanClass, pkField);
      if (!isValidPkField(this.pkField)) {
         throw new OpenEJBException("Invalid primray key field: " + pkField);
      }
   }

   public Object getPrimaryKey(EntityBean bean) {
      Object value = getFieldValue(this.pkField, bean);
      return value;
   }
}
