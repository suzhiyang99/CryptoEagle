package org.apache.spark.api.r;

public final class SerializationFormats$ {
   public static final SerializationFormats$ MODULE$;
   private final String BYTE;
   private final String STRING;
   private final String ROW;

   static {
      new SerializationFormats$();
   }

   public String BYTE() {
      return this.BYTE;
   }

   public String STRING() {
      return this.STRING;
   }

   public String ROW() {
      return this.ROW;
   }

   private SerializationFormats$() {
      MODULE$ = this;
      this.BYTE = "byte";
      this.STRING = "string";
      this.ROW = "row";
   }
}
