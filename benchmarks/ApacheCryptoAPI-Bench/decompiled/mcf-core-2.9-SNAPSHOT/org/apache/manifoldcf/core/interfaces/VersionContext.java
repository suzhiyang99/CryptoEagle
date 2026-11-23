package org.apache.manifoldcf.core.interfaces;

public class VersionContext {
   public static final String _rcsid = "@(#)$Id$";
   protected final String versionString;
   protected final ConfigParams params;
   protected final Specification specification;

   public VersionContext(String versionString, ConfigParams params, Specification specification) {
      this.versionString = versionString;
      this.params = params;
      this.specification = specification;
   }

   public String getVersionString() {
      return this.versionString;
   }

   public ConfigParams getParams() {
      return this.params;
   }

   public Specification getSpecification() {
      return this.specification;
   }
}
