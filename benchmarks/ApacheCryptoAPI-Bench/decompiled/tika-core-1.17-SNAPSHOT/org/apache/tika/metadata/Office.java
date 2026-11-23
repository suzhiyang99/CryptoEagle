package org.apache.tika.metadata;

public interface Office {
   String NAMESPACE_URI_DOC_META = "urn:oasis:names:tc:opendocument:xmlns:meta:1.0";
   String PREFIX_DOC_META = "meta";
   String USER_DEFINED_METADATA_NAME_PREFIX = "custom:";
   Property KEYWORDS = Property.internalTextBag("meta:keyword");
   Property INITIAL_AUTHOR = Property.internalText("meta:initial-author");
   Property LAST_AUTHOR = Property.internalText("meta:last-author");
   Property AUTHOR = Property.internalTextBag("meta:author");
   Property CREATION_DATE = Property.internalDate("meta:creation-date");
   Property SAVE_DATE = Property.internalDate("meta:save-date");
   Property PRINT_DATE = Property.internalDate("meta:print-date");
   Property SLIDE_COUNT = Property.internalInteger("meta:slide-count");
   Property PAGE_COUNT = Property.internalInteger("meta:page-count");
   Property PARAGRAPH_COUNT = Property.internalInteger("meta:paragraph-count");
   Property LINE_COUNT = Property.internalInteger("meta:line-count");
   Property WORD_COUNT = Property.internalInteger("meta:word-count");
   Property CHARACTER_COUNT = Property.internalInteger("meta:character-count");
   Property CHARACTER_COUNT_WITH_SPACES = Property.internalInteger("meta:character-count-with-spaces");
   Property TABLE_COUNT = Property.internalInteger("meta:table-count");
   Property IMAGE_COUNT = Property.internalInteger("meta:image-count");
   Property OBJECT_COUNT = Property.internalInteger("meta:object-count");
   Property MAPI_MESSAGE_CLASS = Property.internalClosedChoise("meta:mapi-message-class", "APPOINTMENT", "CONTACT", "NOTE", "STICKY_NOTE", "POST", "TASK", "UNKNOWN", "UNSPECIFIED");
   Property MAPI_SENT_BY_SERVER_TYPE = Property.internalText("meta:mapi-sent-by-server-type");
   Property MAPI_FROM_REPRESENTING_NAME = Property.internalText("meta:mapi-from-representing-name");
   Property MAPI_FROM_REPRESENTING_EMAIL = Property.internalText("meta:mapi-from-representing-email");
}
