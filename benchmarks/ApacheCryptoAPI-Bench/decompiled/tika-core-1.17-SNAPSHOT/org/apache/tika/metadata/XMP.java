package org.apache.tika.metadata;

public interface XMP {
   String NAMESPACE_URI = "http://ns.adobe.com/xap/1.0/";
   String PREFIX = "xmp";
   String PREFIX_ = "xmp:";
   Property CREATE_DATE = Property.externalDate("xmp:CreateDate");
   Property CREATOR_TOOL = Property.externalText("xmp:CreatorTool");
   Property IDENTIFIER = Property.externalTextBag("xmp:Identifier");
   Property LABEL = Property.externalDate("xmp:Label");
   Property METADATA_DATE = Property.externalDate("xmp:MetadataDate");
   Property MODIFY_DATE = Property.externalDate("xmp:ModifyDate");
   Property RATING = Property.externalReal("xmp:Rating");
}
