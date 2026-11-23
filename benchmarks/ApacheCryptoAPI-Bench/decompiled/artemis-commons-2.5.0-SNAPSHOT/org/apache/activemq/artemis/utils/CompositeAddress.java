package org.apache.activemq.artemis.utils;

import org.apache.activemq.artemis.api.core.SimpleString;

public class CompositeAddress {
   public static String SEPARATOR = "::";
   private final String address;
   private final String queueName;
   private final boolean fqqn;

   public static SimpleString toFullQN(SimpleString address, SimpleString qName) {
      return address.concat(SEPARATOR).concat(qName);
   }

   public static String toFullQN(String address, String qName) {
      return address + SEPARATOR + qName;
   }

   public String getAddress() {
      return this.address;
   }

   public String getQueueName() {
      return this.queueName;
   }

   public CompositeAddress(String address, String queueName) {
      this.address = address;
      this.queueName = queueName;
      this.fqqn = address != null && !address.isEmpty();
   }

   public CompositeAddress(String singleName) {
      int index = singleName.indexOf(SEPARATOR);
      if (index == -1) {
         this.fqqn = false;
         this.address = null;
         this.queueName = singleName;
      } else {
         this.fqqn = true;
         this.address = singleName.substring(0, index);
         this.queueName = singleName.substring(index + 2);
      }

   }

   public boolean isFqqn() {
      return this.fqqn;
   }

   public static boolean isFullyQualified(String address) {
      return address.contains(SEPARATOR);
   }

   public static CompositeAddress getQueueName(String address) {
      int index = address.indexOf(SEPARATOR);
      if (index == -1) {
         throw new IllegalStateException("Not A Fully Qualified Name");
      } else {
         return new CompositeAddress(address.substring(0, index), address.substring(index + 2));
      }
   }

   public static String extractQueueName(String name) {
      int index = name.indexOf(SEPARATOR);
      return index != -1 ? name.substring(index + 2) : name;
   }

   public static SimpleString extractQueueName(SimpleString name) {
      return new SimpleString(extractQueueName(name.toString()));
   }

   public static String extractAddressName(String address) {
      String[] split = address.split(SEPARATOR);
      return split[0];
   }
}
