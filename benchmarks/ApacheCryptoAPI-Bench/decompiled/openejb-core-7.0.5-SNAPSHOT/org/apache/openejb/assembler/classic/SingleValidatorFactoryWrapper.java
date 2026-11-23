package org.apache.openejb.assembler.classic;

import java.io.Serializable;
import javax.naming.NamingException;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;
import org.apache.openejb.bval.ValidatorUtil;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class SingleValidatorFactoryWrapper implements ValidatorFactory, Serializable {
   public static final Logger logger;
   private transient volatile ValidatorFactory factory;

   public SingleValidatorFactoryWrapper(ValidatorFactory factory) {
      this.factory = factory;
   }

   public Validator getValidator() {
      return this.factory().getValidator();
   }

   private ValidatorFactory factory() {
      if (this.factory == null) {
         synchronized(this) {
            if (this.factory == null) {
               try {
                  this.factory = ValidatorUtil.lookupFactory();
               } catch (NamingException e) {
                  throw new IllegalStateException(e);
               }
            }
         }
      }

      return this.factory;
   }

   public ValidatorContext usingContext() {
      return this.factory().usingContext();
   }

   public MessageInterpolator getMessageInterpolator() {
      return this.factory().getMessageInterpolator();
   }

   public TraversableResolver getTraversableResolver() {
      return this.factory().getTraversableResolver();
   }

   public ConstraintValidatorFactory getConstraintValidatorFactory() {
      return this.factory().getConstraintValidatorFactory();
   }

   public <T> T unwrap(Class<T> tClass) {
      return (T)this.factory().unwrap(tClass);
   }

   public ParameterNameProvider getParameterNameProvider() {
      return this.factory().getParameterNameProvider();
   }

   public void close() {
      ValidatorFactory factory = this.factory;
      if (factory != null) {
         factory.close();
      }

   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, SingleValidatorFactoryWrapper.class);
   }
}
