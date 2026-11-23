package org.apache.spark.executor;

import org.apache.spark.annotation.DeveloperApi;
import scala.Enumeration;

@DeveloperApi
public final class DataReadMethod$ extends Enumeration {
   public static final DataReadMethod$ MODULE$;
   private final Enumeration.Value Memory;
   private final Enumeration.Value Disk;
   private final Enumeration.Value Hadoop;
   private final Enumeration.Value Network;

   static {
      new DataReadMethod$();
   }

   public Enumeration.Value Memory() {
      return this.Memory;
   }

   public Enumeration.Value Disk() {
      return this.Disk;
   }

   public Enumeration.Value Hadoop() {
      return this.Hadoop;
   }

   public Enumeration.Value Network() {
      return this.Network;
   }

   private DataReadMethod$() {
      MODULE$ = this;
      this.Memory = this.Value();
      this.Disk = this.Value();
      this.Hadoop = this.Value();
      this.Network = this.Value();
   }
}
