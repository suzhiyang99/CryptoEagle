package org.apache.activemq.artemis.utils;

import java.util.Random;
import org.apache.activemq.artemis.api.core.ActiveMQBuffer;
import org.apache.activemq.artemis.api.core.ActiveMQBuffers;
import org.apache.activemq.artemis.api.core.SimpleString;

public class RandomUtil {
   protected static final Random random = new Random();

   public static Random getRandom() {
      return random;
   }

   public static String randomString() {
      return java.util.UUID.randomUUID().toString();
   }

   public static SimpleString randomSimpleString() {
      return new SimpleString(randomString());
   }

   public static char randomChar() {
      return randomString().charAt(0);
   }

   public static long randomLong() {
      return random.nextLong();
   }

   public static long randomPositiveLong() {
      return Math.abs(randomLong());
   }

   public static int randomInt() {
      return random.nextInt();
   }

   public static int randomPositiveInt() {
      return Math.abs(randomInt());
   }

   public static ActiveMQBuffer randomBuffer(int size, long... data) {
      ActiveMQBuffer buffer = ActiveMQBuffers.fixedBuffer(size + 8 * data.length);

      for(long d : data) {
         buffer.writeLong(d);
      }

      for(int i = 0; i < size; ++i) {
         buffer.writeByte(randomByte());
      }

      return buffer;
   }

   public static int randomInterval(int min, int max) {
      return min + random.nextInt(max - min);
   }

   public static int randomMax(int max) {
      int value = randomPositiveInt() % max;
      if (value == 0) {
         value = max;
      }

      return value;
   }

   public static int randomPort() {
      return random.nextInt(65536);
   }

   public static short randomShort() {
      return (short)random.nextInt(32767);
   }

   public static byte randomByte() {
      return Integer.valueOf(random.nextInt()).byteValue();
   }

   public static boolean randomBoolean() {
      return random.nextBoolean();
   }

   public static byte[] randomBytes() {
      return randomString().getBytes();
   }

   public static byte[] randomBytes(int length) {
      byte[] bytes = new byte[length];

      for(int i = 0; i < bytes.length; ++i) {
         bytes[i] = randomByte();
      }

      return bytes;
   }

   public static double randomDouble() {
      return random.nextDouble();
   }

   public static float randomFloat() {
      return random.nextFloat();
   }
}
