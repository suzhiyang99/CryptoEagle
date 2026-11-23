package org.apache.tika.metadata;

import [Ljava.lang.String;;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public final class Property implements Comparable<Property> {
   private static final Map<String, Property> properties = new HashMap();
   private final String name;
   private final boolean internal;
   private final PropertyType propertyType;
   private final ValueType valueType;
   private final Property primaryProperty;
   private final Property[] secondaryExtractProperties;
   private final Set<String> choices;

   private Property(String name, boolean internal, PropertyType propertyType, ValueType valueType, String[] choices, Property primaryProperty, Property[] secondaryExtractProperties) {
      this.name = name;
      this.internal = internal;
      this.propertyType = propertyType;
      this.valueType = valueType;
      if (choices != null) {
         this.choices = Collections.unmodifiableSet(new HashSet(Arrays.asList(((String;)choices).clone())));
      } else {
         this.choices = null;
      }

      if (primaryProperty != null) {
         this.primaryProperty = primaryProperty;
         this.secondaryExtractProperties = secondaryExtractProperties;
      } else {
         this.primaryProperty = this;
         this.secondaryExtractProperties = null;
         synchronized(properties) {
            properties.put(name, this);
         }
      }

   }

   private Property(String name, boolean internal, PropertyType propertyType, ValueType valueType, String[] choices) {
      this(name, internal, propertyType, valueType, choices, (Property)null, (Property[])null);
   }

   private Property(String name, boolean internal, ValueType valueType, String[] choices) {
      this(name, internal, Property.PropertyType.SIMPLE, valueType, choices);
   }

   private Property(String name, boolean internal, ValueType valueType) {
      this(name, internal, Property.PropertyType.SIMPLE, valueType, (String[])null);
   }

   private Property(String name, boolean internal, PropertyType propertyType, ValueType valueType) {
      this(name, internal, propertyType, valueType, (String[])null);
   }

   public String getName() {
      return this.name;
   }

   public boolean isInternal() {
      return this.internal;
   }

   public boolean isExternal() {
      return !this.internal;
   }

   public boolean isMultiValuePermitted() {
      if (this.propertyType != Property.PropertyType.BAG && this.propertyType != Property.PropertyType.SEQ && this.propertyType != Property.PropertyType.ALT) {
         return this.propertyType == Property.PropertyType.COMPOSITE ? this.primaryProperty.isMultiValuePermitted() : false;
      } else {
         return true;
      }
   }

   public static PropertyType getPropertyType(String key) {
      PropertyType type = null;
      Property prop = (Property)properties.get(key);
      if (prop != null) {
         type = prop.getPropertyType();
      }

      return type;
   }

   public static Property get(String key) {
      return (Property)properties.get(key);
   }

   public PropertyType getPropertyType() {
      return this.propertyType;
   }

   public ValueType getValueType() {
      return this.valueType;
   }

   public Set<String> getChoices() {
      return this.choices;
   }

   public Property getPrimaryProperty() {
      return this.primaryProperty;
   }

   public Property[] getSecondaryExtractProperties() {
      return this.secondaryExtractProperties;
   }

   public static SortedSet<Property> getProperties(String prefix) {
      SortedSet<Property> set = new TreeSet();
      String p = prefix + ":";
      synchronized(properties) {
         for(String name : properties.keySet()) {
            if (name.startsWith(p)) {
               set.add(properties.get(name));
            }
         }

         return set;
      }
   }

   public static Property internalBoolean(String name) {
      return new Property(name, true, Property.ValueType.BOOLEAN);
   }

   public static Property internalClosedChoise(String name, String... choices) {
      return new Property(name, true, Property.ValueType.CLOSED_CHOICE, choices);
   }

   public static Property internalDate(String name) {
      return new Property(name, true, Property.ValueType.DATE);
   }

   public static Property internalInteger(String name) {
      return new Property(name, true, Property.ValueType.INTEGER);
   }

   public static Property internalIntegerSequence(String name) {
      return new Property(name, true, Property.PropertyType.SEQ, Property.ValueType.INTEGER);
   }

   public static Property internalRational(String name) {
      return new Property(name, true, Property.ValueType.RATIONAL);
   }

   public static Property internalOpenChoise(String name, String... choices) {
      return new Property(name, true, Property.ValueType.OPEN_CHOICE, choices);
   }

   public static Property internalReal(String name) {
      return new Property(name, true, Property.ValueType.REAL);
   }

   public static Property internalText(String name) {
      return new Property(name, true, Property.ValueType.TEXT);
   }

   public static Property internalTextBag(String name) {
      return new Property(name, true, Property.PropertyType.BAG, Property.ValueType.TEXT);
   }

   public static Property internalURI(String name) {
      return new Property(name, true, Property.ValueType.URI);
   }

   public static Property externalClosedChoise(String name, String... choices) {
      return new Property(name, false, Property.ValueType.CLOSED_CHOICE, choices);
   }

   public static Property externalOpenChoise(String name, String... choices) {
      return new Property(name, false, Property.ValueType.OPEN_CHOICE, choices);
   }

   public static Property externalDate(String name) {
      return new Property(name, false, Property.ValueType.DATE);
   }

   public static Property externalReal(String name) {
      return new Property(name, false, Property.ValueType.REAL);
   }

   public static Property externalInteger(String name) {
      return new Property(name, false, Property.ValueType.INTEGER);
   }

   public static Property externalBoolean(String name) {
      return new Property(name, false, Property.ValueType.BOOLEAN);
   }

   public static Property externalText(String name) {
      return new Property(name, false, Property.ValueType.TEXT);
   }

   public static Property externalTextBag(String name) {
      return new Property(name, false, Property.PropertyType.BAG, Property.ValueType.TEXT);
   }

   public static Property composite(Property primaryProperty, Property[] secondaryExtractProperties) {
      if (primaryProperty == null) {
         throw new NullPointerException("primaryProperty must not be null");
      } else if (primaryProperty.getPropertyType() == Property.PropertyType.COMPOSITE) {
         throw new PropertyTypeException(primaryProperty.getPropertyType());
      } else {
         if (secondaryExtractProperties != null) {
            for(Property secondaryExtractProperty : secondaryExtractProperties) {
               if (secondaryExtractProperty.getPropertyType() == Property.PropertyType.COMPOSITE) {
                  throw new PropertyTypeException(secondaryExtractProperty.getPropertyType());
               }
            }
         }

         String[] choices = null;
         if (primaryProperty.getChoices() != null) {
            choices = (String[])primaryProperty.getChoices().toArray(new String[primaryProperty.getChoices().size()]);
         }

         return new Property(primaryProperty.getName(), primaryProperty.isInternal(), Property.PropertyType.COMPOSITE, Property.ValueType.PROPERTY, choices, primaryProperty, secondaryExtractProperties);
      }
   }

   public int compareTo(Property o) {
      return this.name.compareTo(o.name);
   }

   public boolean equals(Object o) {
      return o instanceof Property && this.name.equals(((Property)o).name);
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public static enum PropertyType {
      SIMPLE,
      STRUCTURE,
      BAG,
      SEQ,
      ALT,
      COMPOSITE;
   }

   public static enum ValueType {
      BOOLEAN,
      OPEN_CHOICE,
      CLOSED_CHOICE,
      DATE,
      INTEGER,
      LOCALE,
      MIME_TYPE,
      PROPER_NAME,
      RATIONAL,
      REAL,
      TEXT,
      URI,
      URL,
      XPATH,
      PROPERTY;
   }
}
