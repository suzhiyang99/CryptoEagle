package org.apache.wicket.util.value;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.StringValueConversionException;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.time.Time;

public class CopyOnWriteValueMap implements IValueMap, Serializable {
   private static final long serialVersionUID = 1L;
   private IValueMap wrapped;

   public CopyOnWriteValueMap(IValueMap wrapped) {
      this.wrapped = wrapped;
   }

   public void clear() {
      this.checkAndCopy();
      this.wrapped.clear();
   }

   private void checkAndCopy() {
      if (this.wrapped.isImmutable()) {
         this.wrapped = new ValueMap(this.wrapped);
      }

   }

   public boolean containsKey(Object key) {
      return this.wrapped.containsKey(key);
   }

   public boolean containsValue(Object value) {
      return this.wrapped.containsValue(value);
   }

   public Set<Map.Entry<String, Object>> entrySet() {
      this.checkAndCopy();
      return this.wrapped.entrySet();
   }

   public boolean equals(Object o) {
      return this.wrapped.equals(o);
   }

   public int hashCode() {
      return this.wrapped.hashCode();
   }

   public Object get(Object key) {
      return this.wrapped.get(key);
   }

   public boolean getBoolean(String key) throws StringValueConversionException {
      return this.wrapped.getBoolean(key);
   }

   public CharSequence getCharSequence(String key) {
      return this.wrapped.getCharSequence(key);
   }

   public double getDouble(String key) throws StringValueConversionException {
      return this.wrapped.getDouble(key);
   }

   public double getDouble(String key, double defaultValue) {
      return this.wrapped.getDouble(key, defaultValue);
   }

   public Duration getDuration(String key) throws StringValueConversionException {
      return this.wrapped.getDuration(key);
   }

   public int getInt(String key, int defaultValue) {
      return this.wrapped.getInt(key, defaultValue);
   }

   public int getInt(String key) throws StringValueConversionException {
      return this.wrapped.getInt(key);
   }

   public String getKey(String key) {
      return this.wrapped.getKey(key);
   }

   public long getLong(String key, long defaultValue) {
      return this.wrapped.getLong(key, defaultValue);
   }

   public long getLong(String key) throws StringValueConversionException {
      return this.wrapped.getLong(key);
   }

   public String getString(String key, String defaultValue) {
      return this.wrapped.getString(key, defaultValue);
   }

   public String getString(String key) {
      return this.wrapped.getString(key);
   }

   public String[] getStringArray(String key) {
      return this.wrapped.getStringArray(key);
   }

   public StringValue getStringValue(String key) {
      return this.wrapped.getStringValue(key);
   }

   public Time getTime(String key) throws StringValueConversionException {
      return this.wrapped.getTime(key);
   }

   public boolean isEmpty() {
      return this.wrapped.isEmpty();
   }

   public boolean isImmutable() {
      return false;
   }

   public Set<String> keySet() {
      this.checkAndCopy();
      return this.wrapped.keySet();
   }

   public IValueMap makeImmutable() {
      return this.wrapped.makeImmutable();
   }

   public Object put(String key, Object value) {
      this.checkAndCopy();
      return this.wrapped.put(key, value);
   }

   public void putAll(Map<? extends String, ?> map) {
      this.checkAndCopy();
      this.wrapped.putAll(map);
   }

   public Object remove(Object key) {
      this.checkAndCopy();
      return this.wrapped.remove(key);
   }

   public int size() {
      return this.wrapped.size();
   }

   public Collection<Object> values() {
      return this.wrapped.values();
   }

   public Boolean getAsBoolean(String key) {
      return this.wrapped.getAsBoolean(key);
   }

   public boolean getAsBoolean(String key, boolean defaultValue) {
      return this.wrapped.getAsBoolean(key, defaultValue);
   }

   public Integer getAsInteger(String key) {
      return this.wrapped.getAsInteger(key);
   }

   public int getAsInteger(String key, int defaultValue) {
      return this.wrapped.getAsInteger(key, defaultValue);
   }

   public Long getAsLong(String key) {
      return this.wrapped.getAsLong(key);
   }

   public long getAsLong(String key, long defaultValue) {
      return this.wrapped.getAsLong(key, defaultValue);
   }

   public Double getAsDouble(String key) {
      return this.wrapped.getAsDouble(key);
   }

   public double getAsDouble(String key, double defaultValue) {
      return this.wrapped.getAsDouble(key, defaultValue);
   }

   public Duration getAsDuration(String key) {
      return this.wrapped.getAsDuration(key);
   }

   public Duration getAsDuration(String key, Duration defaultValue) {
      return this.wrapped.getAsDuration(key, defaultValue);
   }

   public Time getAsTime(String key) {
      return this.wrapped.getAsTime(key);
   }

   public Time getAsTime(String key, Time defaultValue) {
      return this.wrapped.getAsTime(key, defaultValue);
   }

   public <T extends Enum<T>> T getAsEnum(String key, Class<T> eClass) {
      return (T)this.wrapped.getAsEnum(key, eClass);
   }

   public <T extends Enum<T>> T getAsEnum(String key, T defaultValue) {
      return (T)this.wrapped.getAsEnum(key, defaultValue);
   }

   public <T extends Enum<T>> T getAsEnum(String key, Class<T> eClass, T defaultValue) {
      return (T)this.wrapped.getAsEnum(key, eClass, defaultValue);
   }
}
