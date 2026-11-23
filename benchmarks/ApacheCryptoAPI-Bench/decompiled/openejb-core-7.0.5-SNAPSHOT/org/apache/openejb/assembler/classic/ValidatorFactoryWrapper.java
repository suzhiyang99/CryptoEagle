package org.apache.openejb.assembler.classic;

import java.io.Serializable;
import java.util.Map;
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

public class ValidatorFactoryWrapper implements ValidatorFactory, Serializable {
   public static final Logger logger;
   private transient Map<ComparableValidationConfig, ValidatorFactory> fallbackValidators;

   private ValidatorFactory factory() {
      try {
         return ValidatorUtil.lookupFactory();
      } catch (NamingException var2) {
         return this.fallbackValidators != null && !this.fallbackValidators.isEmpty() ? (ValidatorFactory)this.fallbackValidators.values().iterator().next() : ValidatorUtil.tryJndiLaterFactory();
      }
   }

   public ValidatorFactoryWrapper(Map<ComparableValidationConfig, ValidatorFactory> validators) {
      this.fallbackValidators = validators;
   }

   public Validator getValidator() {
      return this.factory().getValidator();
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
      this.factory().close();
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, ValidatorFactoryWrapper.class);
   }
}
