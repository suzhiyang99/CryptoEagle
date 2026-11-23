package org.apache.activemq.artemis.utils.collections;

import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.activemq.artemis.api.core.ActiveMQPropertyConversionException;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.logs.ActiveMQUtilBundle;
import org.apache.activemq.artemis.utils.ByteUtil;

public class TypedProperties {
   private static final SimpleString AMQ_PROPNAME = new SimpleString("_AMQ_");
   private Map<SimpleString, PropertyValue> properties;
   private volatile int size;
   private boolean internalProperties;

   public TypedProperties() {
   }

   public int size() {
      return this.properties.size();
   }

   public int getMemoryOffset() {
      return this.properties == null ? 0 : this.size + 8 * this.properties.size();
   }

   public TypedProperties(TypedProperties other) {
      this.properties = other.properties == null ? null : new HashMap(other.properties);
      this.size = other.size;
   }

   public boolean hasInternalProperties() {
      return this.internalProperties;
   }

   public void putBooleanProperty(SimpleString key, boolean value) {
      this.checkCreateProperties();
      this.doPutValue(key, TypedProperties.BooleanValue.of(value));
   }

   public void putByteProperty(SimpleString key, byte value) {
      this.checkCreateProperties();
      this.doPutValue(key, TypedProperties.ByteValue.valueOf(value));
   }

   public void putBytesProperty(SimpleString key, byte[] value) {
      this.checkCreateProperties();
      this.doPutValue(key, (PropertyValue)(value == null ? TypedProperties.NullValue.INSTANCE : new BytesValue(value)));
   }

   public void putShortProperty(SimpleString key, short value) {
      this.checkCreateProperties();
      this.doPutValue(key, new ShortValue(value));
   }

   public void putIntProperty(SimpleString key, int value) {
      this.checkCreateProperties();
      this.doPutValue(key, new IntValue(value));
   }

   public void putLongProperty(SimpleString key, long value) {
      this.checkCreateProperties();
      this.doPutValue(key, new LongValue(value));
   }

   public void putFloatProperty(SimpleString key, float value) {
      this.checkCreateProperties();
      this.doPutValue(key, new FloatValue(value));
   }

   public void putDoubleProperty(SimpleString key, double value) {
      this.checkCreateProperties();
      this.doPutValue(key, new DoubleValue(value));
   }

   public void putSimpleStringProperty(SimpleString key, SimpleString value) {
      this.checkCreateProperties();
      this.doPutValue(key, (PropertyValue)(value == null ? TypedProperties.NullValue.INSTANCE : new StringValue(value)));
   }

   public void putNullValue(SimpleString key) {
      this.checkCreateProperties();
      this.doPutValue(key, TypedProperties.NullValue.INSTANCE);
   }

   public void putCharProperty(SimpleString key, char value) {
      this.checkCreateProperties();
      this.doPutValue(key, new CharValue(value));
   }

   public void putTypedProperties(TypedProperties otherProps) {
      if (otherProps != null && otherProps.properties != null) {
         this.checkCreateProperties();

         for(Map.Entry<SimpleString, PropertyValue> otherEntry : otherProps.properties.entrySet()) {
            this.doPutValue((SimpleString)otherEntry.getKey(), (PropertyValue)otherEntry.getValue());
         }

      }
   }

   public Object getProperty(SimpleString key) {
      return this.doGetProperty(key);
   }

   public Boolean getBooleanProperty(SimpleString key) throws ActiveMQPropertyConversionException {
      Object value = this.doGetProperty(key);
      if (value == null) {
         return Boolean.valueOf((String)null);
      } else if (value instanceof Boolean) {
         return (Boolean)value;
      } else if (value instanceof SimpleString) {
         return Boolean.valueOf(((SimpleString)value).toString());
      } else {
         throw new ActiveMQPropertyConversionException("Invalid conversion: " + key);
      }
   }

   public Byte getByteProperty(SimpleString key) throws ActiveMQPropertyConversionException {
      Object value = this.doGetProperty(key);
      if (value == null) {
         return Byte.valueOf((String)null);
      } else if (value instanceof Byte) {
         return (Byte)value;
      } else if (value instanceof SimpleString) {
         return Byte.parseByte(((SimpleString)value).toString());
      } else {
         throw new ActiveMQPropertyConversionException("Invalid conversion: " + key);
      }
   }

   public Character getCharProperty(SimpleString key) throws ActiveMQPropertyConversionException {
      Object value = this.doGetProperty(key);
      if (value == null) {
         throw new NullPointerException("Invalid conversion: " + key);
      } else if (value instanceof Character) {
         return (Character)value;
      } else {
         throw new ActiveMQPropertyConversionException("Invalid conversion: " + key);
      }
   }

   public byte[] getBytesProperty(SimpleString key) throws ActiveMQPropertyConversionException {
      Object value = this.doGetProperty(key);
      if (value == null) {
         return null;
      } else if (value instanceof byte[]) {
         return (byte[])value;
      } else {
         throw new ActiveMQPropertyConversionException("Invalid conversion: " + key);
      }
   }

   public Double getDoubleProperty(SimpleString key) throws ActiveMQPropertyConversionException {
      Object value = this.doGetProperty(key);
      if (value == null) {
         return Double.valueOf((String)null);
      } else if (value instanceof Float) {
         return ((Float)value).doubleValue();
      } else if (value instanceof Double) {
         return (Double)value;
      } else if (value instanceof SimpleString) {
         return Double.parseDouble(((SimpleString)value).toString());
      } else {
         throw new ActiveMQPropertyConversionException("Invalid conversion: " + key);
      }
   }

   public Integer getIntProperty(SimpleString key) throws ActiveMQPropertyConversionException {
      Object value = this.doGetProperty(key);
      if (value == null) {
         return Integer.valueOf((String)null);
      } else if (value instanceof Integer) {
         return (Integer)value;
      } else if (value instanceof Byte) {
         return ((Byte)value).intValue();
      } else if (value instanceof Short) {
         return ((Short)value).intValue();
      } else if (value instanceof SimpleString) {
         return Integer.parseInt(((SimpleString)value).toString());
      } else {
         throw new ActiveMQPropertyConversionException("Invalid conversion: " + key);
      }
   }

   public Long getLongProperty(SimpleString key) throws ActiveMQPropertyConversionException {
      Object value = this.doGetProperty(key);
      if (value == null) {
         return Long.valueOf((String)null);
      } else if (value instanceof Long) {
         return (Long)value;
      } else if (value instanceof Byte) {
         return ((Byte)value).longValue();
      } else if (value instanceof Short) {
         return ((Short)value).longValue();
      } else if (value instanceof Integer) {
         return ((Integer)value).longValue();
      } else if (value instanceof SimpleString) {
         return Long.parseLong(((SimpleString)value).toString());
      } else {
         throw new ActiveMQPropertyConversionException("Invalid conversion: " + key);
      }
   }

   public Short getShortProperty(SimpleString key) throws ActiveMQPropertyConversionException {
      Object value = this.doGetProperty(key);
      if (value == null) {
         return Short.valueOf((String)null);
      } else if (value instanceof Byte) {
         return ((Byte)value).shortValue();
      } else if (value instanceof Short) {
         return (Short)value;
      } else if (value instanceof SimpleString) {
         return Short.parseShort(((SimpleString)value).toString());
      } else {
         throw new ActiveMQPropertyConversionException("Invalid conversion: " + key);
      }
   }

   public Float getFloatProperty(SimpleString key) throws ActiveMQPropertyConversionException {
      Object value = this.doGetProperty(key);
      if (value == null) {
         return Float.valueOf((String)null);
      } else if (value instanceof Float) {
         return (Float)value;
      } else if (value instanceof SimpleString) {
         return Float.parseFloat(((SimpleString)value).toString());
      } else {
         throw new ActiveMQPropertyConversionException("Invalid conversion: " + key);
      }
   }

   public SimpleString getSimpleStringProperty(SimpleString key) throws ActiveMQPropertyConversionException {
      Object value = this.doGetProperty(key);
      if (value == null) {
         return null;
      } else if (value instanceof SimpleString) {
         return (SimpleString)value;
      } else if (value instanceof Boolean) {
         return new SimpleString(value.toString());
      } else if (value instanceof Character) {
         return new SimpleString(value.toString());
      } else if (value instanceof Byte) {
         return new SimpleString(value.toString());
      } else if (value instanceof Short) {
         return new SimpleString(value.toString());
      } else if (value instanceof Integer) {
         return new SimpleString(value.toString());
      } else if (value instanceof Long) {
         return new SimpleString(value.toString());
      } else if (value instanceof Float) {
         return new SimpleString(value.toString());
      } else if (value instanceof Double) {
         return new SimpleString(value.toString());
      } else {
         throw new ActiveMQPropertyConversionException("Invalid conversion: " + key);
      }
   }

   public Object removeProperty(SimpleString key) {
      return this.doRemoveProperty(key);
   }

   public boolean containsProperty(SimpleString key) {
      return this.size == 0 ? false : this.properties.containsKey(key);
   }

   public Set<SimpleString> getPropertyNames() {
      return this.size == 0 ? Collections.emptySet() : this.properties.keySet();
   }

   public synchronized void decode(ByteBuf buffer) {
      byte b = buffer.readByte();
      if (b == 0) {
         this.properties = null;
      } else {
         int numHeaders = buffer.readInt();
         this.properties = new HashMap(numHeaders, 1.0F);
         this.size = 0;

         for(int i = 0; i < numHeaders; ++i) {
            int len = buffer.readInt();
            byte[] data = new byte[len];
            buffer.readBytes(data);
            SimpleString key = new SimpleString(data);
            byte type = buffer.readByte();
            switch (type) {
               case 0:
                  PropertyValue val = TypedProperties.NullValue.INSTANCE;
                  this.doPutValue(key, val);
                  break;
               case 1:
               default:
                  throw ActiveMQUtilBundle.BUNDLE.invalidType(type);
               case 2:
                  PropertyValue val = TypedProperties.BooleanValue.of(buffer.readBoolean());
                  this.doPutValue(key, val);
                  break;
               case 3:
                  PropertyValue val = TypedProperties.ByteValue.valueOf(buffer.readByte());
                  this.doPutValue(key, val);
                  break;
               case 4:
                  PropertyValue val = new BytesValue(buffer);
                  this.doPutValue(key, val);
                  break;
               case 5:
                  PropertyValue val = new ShortValue(buffer);
                  this.doPutValue(key, val);
                  break;
               case 6:
                  PropertyValue val = new IntValue(buffer);
                  this.doPutValue(key, val);
                  break;
               case 7:
                  PropertyValue val = new LongValue(buffer);
                  this.doPutValue(key, val);
                  break;
               case 8:
                  PropertyValue val = new FloatValue(buffer);
                  this.doPutValue(key, val);
                  break;
               case 9:
                  PropertyValue val = new DoubleValue(buffer);
                  this.doPutValue(key, val);
                  break;
               case 10:
                  PropertyValue val = new StringValue(buffer);
                  this.doPutValue(key, val);
                  break;
               case 11:
                  PropertyValue val = new CharValue(buffer);
                  this.doPutValue(key, val);
            }
         }
      }

   }

   public synchronized void encode(ByteBuf buffer) {
      if (this.properties == null) {
         buffer.writeByte(0);
      } else {
         buffer.writeByte(1);
         buffer.writeInt(this.properties.size());
         this.properties.forEach((key, value) -> {
            byte[] data = key.getData();
            buffer.writeInt(data.length);
            buffer.writeBytes(data);
            value.write(buffer);
         });
      }

   }

   public int getEncodeSize() {
      return this.properties == null ? 1 : 5 + this.size;
   }

   public void clear() {
      if (this.properties != null) {
         this.properties.clear();
      }

   }

   public String toString() {
      StringBuilder sb = new StringBuilder("TypedProperties[");
      if (this.properties != null) {
         Iterator<Map.Entry<SimpleString, PropertyValue>> iter = this.properties.entrySet().iterator();

         while(iter.hasNext()) {
            Map.Entry<SimpleString, PropertyValue> iterItem = (Map.Entry)iter.next();
            sb.append(iterItem.getKey() + "=");
            Object theValue = ((PropertyValue)iterItem.getValue()).getValue();
            if (theValue == null) {
               sb.append("NULL-value");
            } else if (theValue instanceof byte[]) {
               sb.append("[" + ByteUtil.maxString(ByteUtil.bytesToHex((byte[])theValue, 2), 150) + ")");
               if (((SimpleString)iterItem.getKey()).toString().startsWith("_AMQ_ROUTE_TO")) {
                  sb.append(",bytesAsLongs(");

                  try {
                     ByteBuffer buff = ByteBuffer.wrap((byte[])theValue);

                     while(buff.hasRemaining()) {
                        long bindingID = buff.getLong();
                        sb.append(bindingID);
                        if (buff.hasRemaining()) {
                           sb.append(",");
                        }
                     }
                  } catch (Throwable e) {
                     sb.append("error-converting-longs=" + e.getMessage());
                  }

                  sb.append("]");
               }
            } else {
               sb.append(theValue.toString());
            }

            if (iter.hasNext()) {
               sb.append(",");
            }
         }
      }

      return sb.append("]").toString();
   }

   private void checkCreateProperties() {
      if (this.properties == null) {
         this.properties = new HashMap();
      }

   }

   private synchronized void doPutValue(SimpleString key, PropertyValue value) {
      if (key.startsWith(AMQ_PROPNAME)) {
         this.internalProperties = true;
      }

      PropertyValue oldValue = (PropertyValue)this.properties.put(key, value);
      if (oldValue != null) {
         this.size += value.encodeSize() - oldValue.encodeSize();
      } else {
         this.size += SimpleString.sizeofString(key) + value.encodeSize();
      }

   }

   private synchronized Object doRemoveProperty(SimpleString key) {
      if (this.properties == null) {
         return null;
      } else {
         PropertyValue val = (PropertyValue)this.properties.remove(key);
         if (val == null) {
            return null;
         } else {
            this.size -= SimpleString.sizeofString(key) + val.encodeSize();
            return val.getValue();
         }
      }
   }

   private synchronized Object doGetProperty(Object key) {
      if (this.size == 0) {
         return null;
      } else {
         PropertyValue val = (PropertyValue)this.properties.get(key);
         return val == null ? null : val.getValue();
      }
   }

   public boolean isEmpty() {
      return this.properties.isEmpty();
   }

   public Map<String, Object> getMap() {
      Map<String, Object> m = new HashMap();

      for(Map.Entry<SimpleString, PropertyValue> entry : this.properties.entrySet()) {
         Object val = ((PropertyValue)entry.getValue()).getValue();
         if (val instanceof SimpleString) {
            m.put(((SimpleString)entry.getKey()).toString(), ((SimpleString)val).toString());
         } else {
            m.put(((SimpleString)entry.getKey()).toString(), val);
         }
      }

      return m;
   }

   public static void setObjectProperty(SimpleString key, Object value, TypedProperties properties) {
      if (value == null) {
         properties.putNullValue(key);
      } else if (value instanceof Boolean) {
         properties.putBooleanProperty(key, (Boolean)value);
      } else if (value instanceof Byte) {
         properties.putByteProperty(key, (Byte)value);
      } else if (value instanceof Character) {
         properties.putCharProperty(key, (Character)value);
      } else if (value instanceof Short) {
         properties.putShortProperty(key, (Short)value);
      } else if (value instanceof Integer) {
         properties.putIntProperty(key, (Integer)value);
      } else if (value instanceof Long) {
         properties.putLongProperty(key, (Long)value);
      } else if (value instanceof Float) {
         properties.putFloatProperty(key, (Float)value);
      } else if (value instanceof Double) {
         properties.putDoubleProperty(key, (Double)value);
      } else if (value instanceof String) {
         properties.putSimpleStringProperty(key, new SimpleString((String)value));
      } else if (value instanceof SimpleString) {
         properties.putSimpleStringProperty(key, (SimpleString)value);
      } else {
         if (!(value instanceof byte[])) {
            throw new ActiveMQPropertyConversionException(value.getClass() + " is not a valid property type");
         }

         properties.putBytesProperty(key, (byte[])value);
      }

   }

   private abstract static class PropertyValue {
      private PropertyValue() {
      }

      abstract Object getValue();

      abstract void write(ByteBuf var1);

      abstract int encodeSize();

      public String toString() {
         return "" + this.getValue();
      }
   }

   private static final class NullValue extends PropertyValue {
      private static final NullValue INSTANCE = new NullValue();

      public Object getValue() {
         return null;
      }

      public void write(ByteBuf buffer) {
         buffer.writeByte(0);
      }

      public int encodeSize() {
         return 1;
      }
   }

   private static final class BooleanValue extends PropertyValue {
      private static final int ENCODE_SIZE = 2;
      private static final BooleanValue TRUE = new BooleanValue(true);
      private static final BooleanValue FALSE = new BooleanValue(false);
      private final boolean val;
      private final Boolean objVal;

      private BooleanValue(boolean val) {
         this.val = val;
         this.objVal = val;
      }

      private static BooleanValue of(boolean val) {
         return val ? TRUE : FALSE;
      }

      public Object getValue() {
         return this.objVal;
      }

      public void write(ByteBuf buffer) {
         buffer.writeByte(2);
         buffer.writeBoolean(this.val);
      }

      public int encodeSize() {
         return 2;
      }
   }

   private static final class ByteValue extends PropertyValue {
      private static final int ENCODE_SIZE = 2;
      private static final ByteValue[] VALUES = new ByteValue[256];
      private final byte val;
      private final Byte objectVal;

      private static ByteValue valueOf(byte b) {
         int offset = 128;
         int index = b + 128;
         ByteValue value = VALUES[index];
         return value == null ? (VALUES[index] = new ByteValue(b)) : value;
      }

      private ByteValue(byte val) {
         this.val = val;
         this.objectVal = val;
      }

      public Object getValue() {
         return this.objectVal;
      }

      public void write(ByteBuf buffer) {
         buffer.writeByte(3);
         buffer.writeByte(this.val);
      }

      public int encodeSize() {
         return 2;
      }
   }

   private static final class BytesValue extends PropertyValue {
      final byte[] val;

      private BytesValue(byte[] val) {
         this.val = val;
      }

      private BytesValue(ByteBuf buffer) {
         int len = buffer.readInt();
         this.val = new byte[len];
         buffer.readBytes(this.val);
      }

      public Object getValue() {
         return this.val;
      }

      public void write(ByteBuf buffer) {
         buffer.writeByte(4);
         buffer.writeInt(this.val.length);
         buffer.writeBytes(this.val);
      }

      public int encodeSize() {
         return 5 + this.val.length;
      }
   }

   private static final class ShortValue extends PropertyValue {
      final short val;

      private ShortValue(short val) {
         this.val = val;
      }

      private ShortValue(ByteBuf buffer) {
         this.val = buffer.readShort();
      }

      public Object getValue() {
         return this.val;
      }

      public void write(ByteBuf buffer) {
         buffer.writeByte(5);
         buffer.writeShort(this.val);
      }

      public int encodeSize() {
         return 3;
      }
   }

   private static final class IntValue extends PropertyValue {
      final int val;

      private IntValue(int val) {
         this.val = val;
      }

      private IntValue(ByteBuf buffer) {
         this.val = buffer.readInt();
      }

      public Object getValue() {
         return this.val;
      }

      public void write(ByteBuf buffer) {
         buffer.writeByte(6);
         buffer.writeInt(this.val);
      }

      public int encodeSize() {
         return 5;
      }
   }

   private static final class LongValue extends PropertyValue {
      final long val;

      private LongValue(long val) {
         this.val = val;
      }

      private LongValue(ByteBuf buffer) {
         this.val = buffer.readLong();
      }

      public Object getValue() {
         return this.val;
      }

      public void write(ByteBuf buffer) {
         buffer.writeByte(7);
         buffer.writeLong(this.val);
      }

      public int encodeSize() {
         return 9;
      }
   }

   private static final class FloatValue extends PropertyValue {
      final float val;

      private FloatValue(float val) {
         this.val = val;
      }

      private FloatValue(ByteBuf buffer) {
         this.val = Float.intBitsToFloat(buffer.readInt());
      }

      public Object getValue() {
         return this.val;
      }

      public void write(ByteBuf buffer) {
         buffer.writeByte(8);
         buffer.writeInt(Float.floatToIntBits(this.val));
      }

      public int encodeSize() {
         return 5;
      }
   }

   private static final class DoubleValue extends PropertyValue {
      final double val;

      private DoubleValue(double val) {
         this.val = val;
      }

      private DoubleValue(ByteBuf buffer) {
         this.val = Double.longBitsToDouble(buffer.readLong());
      }

      public Object getValue() {
         return this.val;
      }

      public void write(ByteBuf buffer) {
         buffer.writeByte(9);
         buffer.writeLong(Double.doubleToLongBits(this.val));
      }

      public int encodeSize() {
         return 9;
      }
   }

   private static final class CharValue extends PropertyValue {
      final char val;

      private CharValue(char val) {
         this.val = val;
      }

      private CharValue(ByteBuf buffer) {
         this.val = (char)buffer.readShort();
      }

      public Object getValue() {
         return this.val;
      }

      public void write(ByteBuf buffer) {
         buffer.writeByte(11);
         buffer.writeShort((short)this.val);
      }

      public int encodeSize() {
         return 3;
      }
   }

   private static final class StringValue extends PropertyValue {
      final SimpleString val;

      private StringValue(SimpleString val) {
         this.val = val;
      }

      private StringValue(ByteBuf buffer) {
         this.val = SimpleString.readSimpleString(buffer);
      }

      public Object getValue() {
         return this.val;
      }

      public void write(ByteBuf buffer) {
         buffer.writeByte(10);
         SimpleString.writeSimpleString(buffer, this.val);
      }

      public int encodeSize() {
         return 1 + SimpleString.sizeofString(this.val);
      }
   }
}
