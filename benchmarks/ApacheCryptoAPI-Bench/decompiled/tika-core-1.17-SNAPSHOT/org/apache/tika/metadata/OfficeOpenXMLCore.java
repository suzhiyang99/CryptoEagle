package org.apache.tika.metadata;

public interface OfficeOpenXMLCore {
   String NAMESPACE_URI = "http://schemas.openxmlformats.org/package/2006/metadata/core-properties/";
   String PREFIX = "cp";
   Property CATEGORY = Property.externalText("cp:category");
   Property CONTENT_STATUS = Property.externalText("cp:contentStatus");
   Property LAST_MODIFIED_BY = Property.externalText("cp:lastModifiedBy");
   Property LAST_PRINTED = Property.externalDate("cp:lastPrinted");
   Property REVISION = Property.externalText("cp:revision");
   Property VERSION = Property.externalText("cp:version");
   Property SUBJECT = Property.externalText("cp:subject");
}
