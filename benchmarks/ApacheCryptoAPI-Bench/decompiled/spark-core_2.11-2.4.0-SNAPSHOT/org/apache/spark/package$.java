package org.apache.spark;

public final class package$ {
   public static final package$ MODULE$;
   private final String SPARK_VERSION;
   private final String SPARK_BRANCH;
   private final String SPARK_REVISION;
   private final String SPARK_BUILD_USER;
   private final String SPARK_REPO_URL;
   private final String SPARK_BUILD_DATE;

   static {
      new package$();
   }

   public String SPARK_VERSION() {
      return this.SPARK_VERSION;
   }

   public String SPARK_BRANCH() {
      return this.SPARK_BRANCH;
   }

   public String SPARK_REVISION() {
      return this.SPARK_REVISION;
   }

   public String SPARK_BUILD_USER() {
      return this.SPARK_BUILD_USER;
   }

   public String SPARK_REPO_URL() {
      return this.SPARK_REPO_URL;
   }

   public String SPARK_BUILD_DATE() {
      return this.SPARK_BUILD_DATE;
   }

   private package$() {
      MODULE$ = this;
      this.SPARK_VERSION = package.SparkBuildInfo$.MODULE$.spark_version();
      this.SPARK_BRANCH = package.SparkBuildInfo$.MODULE$.spark_branch();
      this.SPARK_REVISION = package.SparkBuildInfo$.MODULE$.spark_revision();
      this.SPARK_BUILD_USER = package.SparkBuildInfo$.MODULE$.spark_build_user();
      this.SPARK_REPO_URL = package.SparkBuildInfo$.MODULE$.spark_repo_url();
      this.SPARK_BUILD_DATE = package.SparkBuildInfo$.MODULE$.spark_build_date();
   }
}
