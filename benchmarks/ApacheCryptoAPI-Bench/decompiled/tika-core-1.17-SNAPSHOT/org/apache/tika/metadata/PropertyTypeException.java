package org.apache.tika.metadata;

public final class PropertyTypeException extends IllegalArgumentException {
   public PropertyTypeException(String msg) {
      super(msg);
   }

   public PropertyTypeException(Property.PropertyType expected, Property.PropertyType found) {
      super("Expected a property of type " + expected + ", but received " + found);
   }

   public PropertyTypeException(Property.ValueType expected, Property.ValueType found) {
      super("Expected a property with a " + expected + " value, but received a " + found);
   }

   public PropertyTypeException(Property.PropertyType unsupportedPropertyType) {
      super(unsupportedPropertyType != Property.PropertyType.COMPOSITE ? unsupportedPropertyType + " is not supported" : "Composite Properties must not include other Composite Properties as either Primary or Secondary");
   }
}
