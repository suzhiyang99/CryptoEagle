package org.apache.openejb.cdi;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;

public class WebAppElResolver extends ELResolver {
   private final ELResolver parent;
   private final ELResolver resolver;

   public WebAppElResolver(ELResolver elResolver, ELResolver elResolver1) {
      this.resolver = elResolver;
      this.parent = elResolver1;
   }

   public Object getValue(ELContext context, Object base, Object property) throws NullPointerException, PropertyNotFoundException, ELException {
      Object value = this.resolver.getValue(context, base, property);
      if (value == null) {
         value = this.parent.getValue(context, base, property);
      }

      return value;
   }

   public Class<?> getType(ELContext context, Object base, Object property) throws NullPointerException, PropertyNotFoundException, ELException {
      Class<?> value = this.resolver.getType(context, base, property);
      if (value == null) {
         value = this.parent.getType(context, base, property);
      }

      return value;
   }

   public void setValue(ELContext context, Object base, Object property, Object value) throws NullPointerException, PropertyNotFoundException, PropertyNotWritableException, ELException {
   }

   public boolean isReadOnly(ELContext context, Object base, Object property) throws NullPointerException, PropertyNotFoundException, ELException {
      return false;
   }

   public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
      return null;
   }

   public Class<?> getCommonPropertyType(ELContext context, Object base) {
      return null;
   }
}
