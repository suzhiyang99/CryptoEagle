package org.apache.wicket.util.value;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.wicket.util.parse.metapattern.MetaPattern;
import org.apache.wicket.util.parse.metapattern.parsers.VariableAssignmentParser;
import org.apache.wicket.util.string.IStringIterator;
import org.apache.wicket.util.string.StringList;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.StringValueConversionException;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.time.Time;

public class ValueMap extends LinkedHashMap<String, Object> implements IValueMap {
   public static final ValueMap EMPTY_MAP = new ValueMap();
   private static final long serialVersionUID = 1L;
   private boolean immutable;

   public ValueMap() {
      this.immutable = false;
   }

   public ValueMap(Map<? extends String, ?> map) {
      this.immutable = false;
      super.putAll(map);
   }

   public ValueMap(String keyValuePairs) {
      this(keyValuePairs, ",");
   }

   public ValueMap(String keyValuePairs, String delimiter) {
      this.immutable = false;
      int start = 0;
      int equalsIndex = keyValuePairs.indexOf(61);
      int delimiterIndex = keyValuePairs.indexOf(delimiter, equalsIndex);
      if (delimiterIndex == -1) {
         delimiterIndex = keyValuePairs.length();
      }

      while(equalsIndex != -1) {
         if (delimiterIndex < keyValuePairs.length()) {
            int equalsIndex2 = keyValuePairs.indexOf(61, delimiterIndex + 1);
            if (equalsIndex2 != -1) {
               delimiterIndex = keyValuePairs.lastIndexOf(delimiter, equalsIndex2);
            } else {
               delimiterIndex = keyValuePairs.length();
            }
         }

         String key = keyValuePairs.substring(start, equalsIndex);
         String value = keyValuePairs.substring(equalsIndex + 1, delimiterIndex);
         this.add(key, value);
         if (delimiterIndex < keyValuePairs.length()) {
            start = delimiterIndex + 1;
            equalsIndex = keyValuePairs.indexOf(61, start);
            if (equalsIndex != -1) {
               delimiterIndex = keyValuePairs.indexOf(delimiter, equalsIndex);
               if (delimiterIndex == -1) {
                  delimiterIndex = keyValuePairs.length();
               }
            }
         } else {
            equalsIndex = -1;
         }
      }

   }

   public ValueMap(String keyValuePairs, String delimiter, MetaPattern valuePattern) {
      this.immutable = false;
      StringList pairs = StringList.tokenize(keyValuePairs, delimiter);
      IStringIterator iterator = pairs.iterator();

      while(iterator.hasNext()) {
         String pair = iterator.next();
         VariableAssignmentParser parser = new VariableAssignmentParser(pair, valuePattern);
         if (!parser.matches()) {
            throw new IllegalArgumentException("Invalid key value list: '" + keyValuePairs + '\'');
         }

         this.put((String)parser.getKey(), parser.getValue());
      }

   }

   public final void clear() {
      this.checkMutability();
      super.clear();
   }

   public final boolean getBoolean(String key) throws StringValueConversionException {
      return this.getStringValue(key).toBoolean();
   }

   public final double getDouble(String key) throws StringValueConversionException {
      return this.getStringValue(key).toDouble();
   }

   public final double getDouble(String key, double defaultValue) {
      return this.getStringValue(key).toDouble(defaultValue);
   }

   public final Duration getDuration(String key) throws StringValueConversionException {
      return this.getStringValue(key).toDuration();
   }

   public final int getInt(String key) throws StringValueConversionException {
      return this.getStringValue(key).toInt();
   }

   public final int getInt(String key, int defaultValue) {
      return this.getStringValue(key).toInt(defaultValue);
   }

   public final long getLong(String key) throws StringValueConversionException {
      return this.getStringValue(key).toLong();
   }

   public final long getLong(String key, long defaultValue) {
      return this.getStringValue(key).toLong(defaultValue);
   }

   public final String getString(String key, String defaultValue) {
      String value = this.getString(key);
      return value != null ? value : defaultValue;
   }

   public final String getString(String key) {
      Object o = this.get(key);
      if (o == null) {
         return null;
      } else if (o.getClass().isArray() && Array.getLength(o) > 0) {
         Object arrayValue = Array.get(o, 0);
         return arrayValue == null ? null : arrayValue.toString();
      } else {
         return o.toString();
      }
   }

   public final CharSequence getCharSequence(String key) {
      Object o = this.get(key);
      if (o == null) {
         return null;
      } else if (o.getClass().isArray() && Array.getLength(o) > 0) {
         Object arrayValue = Array.get(o, 0);
         if (arrayValue == null) {
            return null;
         } else {
            return (CharSequence)(arrayValue instanceof CharSequence ? (CharSequence)arrayValue : arrayValue.toString());
         }
      } else {
         return (CharSequence)(o instanceof CharSequence ? (CharSequence)o : o.toString());
      }
   }

   public String[] getStringArray(String key) {
      Object o = this.get(key);
      if (o == null) {
         return null;
      } else if (o instanceof String[]) {
         return (String[])o;
      } else if (o.getClass().isArray()) {
         int length = Array.getLength(o);
         String[] array = new String[length];

         for(int i = 0; i < length; ++i) {
            Object arrayValue = Array.get(o, i);
            if (arrayValue != null) {
               array[i] = arrayValue.toString();
            }
         }

         return array;
      } else {
         return new String[]{o.toString()};
      }
   }

   public StringValue getStringValue(String key) {
      return StringValue.valueOf(this.getString(key));
   }

   public final Time getTime(String key) throws StringValueConversionException {
      return this.getStringValue(key).toTime();
   }

   public final boolean isImmutable() {
      return this.immutable;
   }

   public final IValueMap makeImmutable() {
      this.immutable = true;
      return this;
   }

   public Object put(String key, Object value) {
      this.checkMutability();
      return super.put(key, value);
   }

   public final Object add(String key, String value) {
      this.checkMutability();
      Object o = this.get(key);
      if (o == null) {
         return this.put((String)key, value);
      } else if (o.getClass().isArray()) {
         int length = Array.getLength(o);
         String[] destArray = new String[length + 1];

         for(int i = 0; i < length; ++i) {
            Object arrayValue = Array.get(o, i);
            if (arrayValue != null) {
               destArray[i] = arrayValue.toString();
            }
         }

         destArray[length] = value;
         return this.put((String)key, destArray);
      } else {
         return this.put((String)key, new String[]{o.toString(), value});
      }
   }

   public void putAll(Map<? extends String, ?> map) {
      this.checkMutability();
      super.putAll(map);
   }

   public Object remove(Object key) {
      this.checkMutability();
      return super.remove(key);
   }

   public String getKey(String key) {
      for(String other : this.keySet()) {
         if (other.equalsIgnoreCase(key)) {
            return other;
         }
      }

      return null;
   }

   public String toString() {
      StringBuilder buffer = new StringBuilder();
      boolean first = true;

      for(Map.Entry<String, Object> entry : this.entrySet()) {
         if (!first) {
            buffer.append(' ');
         }

         first = false;
         buffer.append((String)entry.getKey());
         buffer.append(" = \"");
         Object value = entry.getValue();
         if (value == null) {
            buffer.append("null");
         } else if (value.getClass().isArray()) {
            buffer.append(Arrays.asList(value));
         } else {
            buffer.append(value);
         }

         buffer.append('"');
      }

      return buffer.toString();
   }

   private void checkMutability() {
      if (this.immutable) {
         throw new UnsupportedOperationException("Map is immutable");
      }
   }

   public Boolean getAsBoolean(String key) {
      if (!this.containsKey(key)) {
         return null;
      } else {
         try {
            return this.getBoolean(key);
         } catch (StringValueConversionException var3) {
            return null;
         }
      }
   }

   public boolean getAsBoolean(String key, boolean defaultValue) {
      if (!this.containsKey(key)) {
         return defaultValue;
      } else {
         try {
            return this.getBoolean(key);
         } catch (StringValueConversionException var4) {
            return defaultValue;
         }
      }
   }

   public Integer getAsInteger(String key) {
      if (!this.containsKey(key)) {
         return null;
      } else {
         try {
            return this.getInt(key);
         } catch (StringValueConversionException var3) {
            return null;
         }
      }
   }

   public int getAsInteger(String key, int defaultValue) {
      return this.getInt(key, defaultValue);
   }

   public Long getAsLong(String key) {
      if (!this.containsKey(key)) {
         return null;
      } else {
         try {
            return this.getLong(key);
         } catch (StringValueConversionException var3) {
            return null;
         }
      }
   }

   public long getAsLong(String key, long defaultValue) {
      return this.getLong(key, defaultValue);
   }

   public Double getAsDouble(String key) {
      if (!this.containsKey(key)) {
         return null;
      } else {
         try {
            return this.getDouble(key);
         } catch (StringValueConversionException var3) {
            return null;
         }
      }
   }

   public double getAsDouble(String key, double defaultValue) {
      return this.getDouble(key, defaultValue);
   }

   public Duration getAsDuration(String key) {
      return this.getAsDuration(key, (Duration)null);
   }

   public Duration getAsDuration(String key, Duration defaultValue) {
      if (!this.containsKey(key)) {
         return defaultValue;
      } else {
         try {
            return this.getDuration(key);
         } catch (StringValueConversionException var4) {
            return defaultValue;
         }
      }
   }

   public Time getAsTime(String key) {
      return this.getAsTime(key, (Time)null);
   }

   public Time getAsTime(String key, Time defaultValue) {
      if (!this.containsKey(key)) {
         return defaultValue;
      } else {
         try {
            return this.getTime(key);
         } catch (StringValueConversionException var4) {
            return defaultValue;
         }
      }
   }

   public <T extends Enum<T>> T getAsEnum(String key, Class<T> eClass) {
      return (T)this.getEnumImpl(key, eClass, (Enum)null);
   }

   public <T extends Enum<T>> T getAsEnum(String key, T defaultValue) {
      if (defaultValue == null) {
         throw new IllegalArgumentException("Default value cannot be null");
      } else {
         return (T)this.getEnumImpl(key, defaultValue.getClass(), defaultValue);
      }
   }

   public <T extends Enum<T>> T getAsEnum(String key, Class<T> eClass, T defaultValue) {
      return (T)this.getEnumImpl(key, eClass, defaultValue);
   }

   private <T extends Enum<T>> T getEnumImpl(String key, Class<?> eClass, T defaultValue) {
      if (eClass == null) {
         throw new IllegalArgumentException("eClass value cannot be null");
      } else {
         String value = this.getString(key);
         if (value == null) {
            return defaultValue;
         } else {
            Method valueOf = null;

            try {
               valueOf = eClass.getMethod("valueOf", String.class);
            } catch (NoSuchMethodException e) {
               throw new RuntimeException("Could not find method valueOf(String s) for " + eClass.getName(), e);
            }

            try {
               return (T)(valueOf.invoke(eClass, value));
            } catch (IllegalAccessException e) {
               throw new RuntimeException("Could not invoke method valueOf(String s) on " + eClass.getName(), e);
            } catch (InvocationTargetException e) {
               if (e.getCause() instanceof IllegalArgumentException) {
                  return defaultValue;
               } else {
                  throw new RuntimeException(e);
               }
            }
         }
      }
   }

   static {
      EMPTY_MAP.makeImmutable();
   }
}
