package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import scala.Enumeration;

@DeveloperApi
public final class TaskLocality$ extends Enumeration {
   public static final TaskLocality$ MODULE$;
   private final Enumeration.Value PROCESS_LOCAL;
   private final Enumeration.Value NODE_LOCAL;
   private final Enumeration.Value NO_PREF;
   private final Enumeration.Value RACK_LOCAL;
   private final Enumeration.Value ANY;

   static {
      new TaskLocality$();
   }

   public Enumeration.Value PROCESS_LOCAL() {
      return this.PROCESS_LOCAL;
   }

   public Enumeration.Value NODE_LOCAL() {
      return this.NODE_LOCAL;
   }

   public Enumeration.Value NO_PREF() {
      return this.NO_PREF;
   }

   public Enumeration.Value RACK_LOCAL() {
      return this.RACK_LOCAL;
   }

   public Enumeration.Value ANY() {
      return this.ANY;
   }

   public boolean isAllowed(Enumeration.Value constraint, Enumeration.Value condition) {
      return condition.$less$eq(constraint);
   }

   private TaskLocality$() {
      MODULE$ = this;
      this.PROCESS_LOCAL = this.Value();
      this.NODE_LOCAL = this.Value();
      this.NO_PREF = this.Value();
      this.RACK_LOCAL = this.Value();
      this.ANY = this.Value();
   }
}
