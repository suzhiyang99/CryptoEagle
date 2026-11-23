package org.apache.tika.metadata;

public interface TikaCoreProperties {
   String TIKA_META_PREFIX = "X-TIKA:";
   String TIKA_META_EXCEPTION_PREFIX = "X-TIKA:EXCEPTION:";
   Property TIKA_META_EXCEPTION_WARNING = Property.internalTextBag("X-TIKA:EXCEPTION:warn");
   Property TIKA_META_EXCEPTION_EMBEDDED_STREAM = Property.internalTextBag("X-TIKA:EXCEPTION:embedded_stream_exception");
   Property ORIGINAL_RESOURCE_NAME = Property.internalTextBag("X-TIKA:origResourceName");
   Property CONTENT_TYPE_HINT = Property.internalText("Content-Type-Hint");
   Property CONTENT_TYPE_OVERRIDE = Property.internalText("Content-Type-Override");
   Property FORMAT = Property.composite(DublinCore.FORMAT, new Property[]{Property.internalText("format")});
   Property IDENTIFIER = Property.composite(DublinCore.IDENTIFIER, new Property[]{Property.internalText("identifier")});
   Property CONTRIBUTOR = Property.composite(DublinCore.CONTRIBUTOR, new Property[]{Property.internalText("contributor")});
   Property COVERAGE = Property.composite(DublinCore.COVERAGE, new Property[]{Property.internalText("coverage")});
   Property CREATOR = Property.composite(DublinCore.CREATOR, new Property[]{Office.AUTHOR, Property.internalTextBag("creator"), Property.internalTextBag("Author")});
   Property MODIFIER = Property.composite(Office.LAST_AUTHOR, new Property[]{Property.internalText("Last-Author")});
   Property CREATOR_TOOL = XMP.CREATOR_TOOL;
   Property LANGUAGE = Property.composite(DublinCore.LANGUAGE, new Property[]{Property.internalText("language")});
   Property PUBLISHER = Property.composite(DublinCore.PUBLISHER, new Property[]{Property.internalText("publisher")});
   Property RELATION = Property.composite(DublinCore.RELATION, new Property[]{Property.internalText("relation")});
   Property RIGHTS = Property.composite(DublinCore.RIGHTS, new Property[]{Property.internalText("rights")});
   Property SOURCE = Property.composite(DublinCore.SOURCE, new Property[]{Property.internalText("source")});
   Property TYPE = Property.composite(DublinCore.TYPE, new Property[]{Property.internalText("type")});
   Property TITLE = Property.composite(DublinCore.TITLE, new Property[]{Property.internalText("title")});
   Property DESCRIPTION = Property.composite(DublinCore.DESCRIPTION, new Property[]{Property.internalText("description")});
   Property KEYWORDS = Property.composite(DublinCore.SUBJECT, new Property[]{Office.KEYWORDS, Property.internalTextBag("Keywords"), Property.internalTextBag("subject")});
   Property CREATED = Property.composite(DublinCore.CREATED, new Property[]{Office.CREATION_DATE, MSOffice.CREATION_DATE});
   Property MODIFIED = Property.composite(DublinCore.MODIFIED, new Property[]{Metadata.DATE, Office.SAVE_DATE, MSOffice.LAST_SAVED, Property.internalText("modified"), Property.internalText("Last-Modified")});
   Property PRINT_DATE = Property.composite(Office.PRINT_DATE, new Property[]{MSOffice.LAST_PRINTED});
   Property METADATA_DATE = XMP.METADATA_DATE;
   Property LATITUDE = Geographic.LATITUDE;
   Property LONGITUDE = Geographic.LONGITUDE;
   Property ALTITUDE = Geographic.ALTITUDE;
   Property RATING = XMP.RATING;
   Property COMMENTS = Property.composite(OfficeOpenXMLExtended.COMMENTS, new Property[]{Property.internalTextBag("comment"), Property.internalTextBag("Comments")});
   /** @deprecated */
   @Deprecated
   Property TRANSITION_KEYWORDS_TO_DC_SUBJECT = Property.composite(DublinCore.SUBJECT, new Property[]{Property.internalTextBag("Keywords")});
   /** @deprecated */
   @Deprecated
   Property TRANSITION_SUBJECT_TO_DC_DESCRIPTION = Property.composite(DublinCore.DESCRIPTION, new Property[]{Property.internalText("subject")});
   /** @deprecated */
   @Deprecated
   Property TRANSITION_SUBJECT_TO_DC_TITLE = Property.composite(DublinCore.TITLE, new Property[]{Property.internalText("subject")});
   /** @deprecated */
   @Deprecated
   Property TRANSITION_SUBJECT_TO_OO_SUBJECT = Property.composite(OfficeOpenXMLCore.SUBJECT, new Property[]{Property.internalText("subject")});
   Property EMBEDDED_RESOURCE_TYPE = Property.internalClosedChoise("embeddedResourceType", TikaCoreProperties.EmbeddedResourceType.ATTACHMENT.toString(), TikaCoreProperties.EmbeddedResourceType.INLINE.toString());

   public static enum EmbeddedResourceType {
      INLINE,
      ATTACHMENT,
      MACRO;
   }
}
