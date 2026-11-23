package org.apache.openejb.assembler.classic;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.naming.NamingException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.apache.openejb.bval.ValidatorUtil;

public class LazyValidator implements InvocationHandler, Serializable {
   private transient ValidatorFactory factory;
   private transient volatile Validator validator;

   public LazyValidator(ValidatorFactory factory) {
      this.factory = factory;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      this.ensureDelegate();

      try {
         return method.invoke(this.validator, args);
      } catch (InvocationTargetException ite) {
         throw ite.getCause();
      }
   }

   private void ensureDelegate() {
      if (this.validator == null) {
         synchronized(this) {
            if (this.validator == null && this.validator == null) {
               this.validator = (this.factory == null ? this.findFactory() : this.factory).usingContext().getValidator();
            }
         }
      }

   }

   private ValidatorFactory findFactory() {
      try {
         return ValidatorUtil.lookupFactory();
      } catch (NamingException var2) {
         return Validation.buildDefaultValidatorFactory();
      }
   }

   public Validator getValidator() {
      this.ensureDelegate();
      return this.validator;
   }
}
