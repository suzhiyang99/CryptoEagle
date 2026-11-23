package org.apache.tika.metadata;

public interface MSOffice {
   /** @deprecated */
   @Deprecated
   String KEYWORDS = "Keywords";
   /** @deprecated */
   @Deprecated
   String COMMENTS = "Comments";
   /** @deprecated */
   @Deprecated
   String LAST_AUTHOR = "Last-Author";
   /** @deprecated */
   @Deprecated
   String AUTHOR = "Author";
   /** @deprecated */
   @Deprecated
   String APPLICATION_NAME = "Application-Name";
   /** @deprecated */
   @Deprecated
   String REVISION_NUMBER = "Revision-Number";
   /** @deprecated */
   @Deprecated
   String TEMPLATE = "Template";
   /** @deprecated */
   @Deprecated
   String TOTAL_TIME = "Total-Time";
   /** @deprecated */
   @Deprecated
   String PRESENTATION_FORMAT = "Presentation-Format";
   /** @deprecated */
   @Deprecated
   String NOTES = "Notes";
   /** @deprecated */
   @Deprecated
   String MANAGER = "Manager";
   /** @deprecated */
   @Deprecated
   String APPLICATION_VERSION = "Application-Version";
   /** @deprecated */
   @Deprecated
   String VERSION = "Version";
   /** @deprecated */
   @Deprecated
   String CONTENT_STATUS = "Content-Status";
   /** @deprecated */
   @Deprecated
   String CATEGORY = "Category";
   /** @deprecated */
   @Deprecated
   String COMPANY = "Company";
   /** @deprecated */
   @Deprecated
   String SECURITY = "Security";
   /** @deprecated */
   @Deprecated
   Property SLIDE_COUNT = Property.internalInteger("Slide-Count");
   /** @deprecated */
   @Deprecated
   Property PAGE_COUNT = Property.internalInteger("Page-Count");
   /** @deprecated */
   @Deprecated
   Property PARAGRAPH_COUNT = Property.internalInteger("Paragraph-Count");
   /** @deprecated */
   @Deprecated
   Property LINE_COUNT = Property.internalInteger("Line-Count");
   /** @deprecated */
   @Deprecated
   Property WORD_COUNT = Property.internalInteger("Word-Count");
   /** @deprecated */
   @Deprecated
   Property CHARACTER_COUNT = Property.internalInteger("Character Count");
   /** @deprecated */
   @Deprecated
   Property CHARACTER_COUNT_WITH_SPACES = Property.internalInteger("Character-Count-With-Spaces");
   /** @deprecated */
   @Deprecated
   Property TABLE_COUNT = Property.internalInteger("Table-Count");
   /** @deprecated */
   @Deprecated
   Property IMAGE_COUNT = Property.internalInteger("Image-Count");
   /** @deprecated */
   @Deprecated
   Property OBJECT_COUNT = Property.internalInteger("Object-Count");
   String EDIT_TIME = "Edit-Time";
   /** @deprecated */
   @Deprecated
   Property CREATION_DATE = Property.internalDate("Creation-Date");
   /** @deprecated */
   @Deprecated
   Property LAST_SAVED = Property.internalDate("Last-Save-Date");
   /** @deprecated */
   @Deprecated
   Property LAST_PRINTED = Property.internalDate("Last-Printed");
   String USER_DEFINED_METADATA_NAME_PREFIX = "custom:";
}
