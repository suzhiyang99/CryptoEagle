package org.apache.openejb.openjpa;

import org.apache.openjpa.jdbc.meta.ClassMapping;
import org.apache.openjpa.jdbc.meta.FieldMapping;
import org.apache.openjpa.jdbc.meta.MappingRepository;
import org.apache.openjpa.jdbc.schema.Schema;
import org.apache.openjpa.lib.conf.Configurations;
import org.apache.openjpa.persistence.jdbc.PersistenceMappingDefaults;

public class PrefixMappingRepository extends MappingRepository {
   private String prefix;

   public PrefixMappingRepository() {
      this.setMappingDefaults(new PrefixMappingDefaults());
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public void endConfiguration() {
      super.endConfiguration();
      Configurations.configureInstance(this.getMappingDefaults(), this.getConfiguration(), "jdbc.MappingDefaults");
      ((PrefixMappingDefaults)this.getMappingDefaults()).endConfiguration();
   }

   private class PrefixMappingDefaults extends PersistenceMappingDefaults {
      private PrefixMappingDefaults() {
      }

      public String getTableName(ClassMapping cls, Schema schema) {
         return PrefixMappingRepository.this.prefix + super.getTableName(cls, schema);
      }

      public String getTableName(FieldMapping fm, Schema schema) {
         return PrefixMappingRepository.this.prefix + super.getTableName(fm, schema);
      }
   }
}
