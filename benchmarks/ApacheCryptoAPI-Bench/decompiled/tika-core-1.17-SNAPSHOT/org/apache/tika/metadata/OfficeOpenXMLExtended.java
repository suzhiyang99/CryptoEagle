package org.apache.tika.metadata;

public interface OfficeOpenXMLExtended {
   String NAMESPACE_URI = "http://schemas.openxmlformats.org/officeDocument/2006/extended-properties/";
   String WORD_PROCESSING_NAMESPACE_URI = "http://schemas.openxmlformats.org/wordprocessingml/2006/main";
   String PREFIX = "extended-properties";
   String WORD_PROCESSING_PREFIX = "w";
   Property TEMPLATE = Property.externalText("extended-properties:Template");
   Property MANAGER = Property.externalTextBag("extended-properties:Manager");
   Property COMPANY = Property.externalText("extended-properties:Company");
   Property PRESENTATION_FORMAT = Property.externalText("extended-properties:PresentationFormat");
   Property NOTES = Property.externalInteger("extended-properties:Notes");
   Property TOTAL_TIME = Property.externalInteger("extended-properties:TotalTime");
   Property HIDDEN_SLIDES = Property.externalInteger("extended-properties:HiddedSlides");
   Property APPLICATION = Property.externalText("extended-properties:Application");
   Property APP_VERSION = Property.externalText("extended-properties:AppVersion");
   Property DOC_SECURITY = Property.externalInteger("extended-properties:DocSecurity");
   Property COMMENTS = Property.externalTextBag("w:comments");
}
