package org.apache.tika.metadata;

public interface Database {
   String PREFIX = "database:";
   Property TABLE_NAME = Property.externalTextBag("database:table_name");
   Property COLUMN_COUNT = Property.externalText("database:column_count");
   Property COLUMN_NAME = Property.externalTextBag("database:column_name");
}
