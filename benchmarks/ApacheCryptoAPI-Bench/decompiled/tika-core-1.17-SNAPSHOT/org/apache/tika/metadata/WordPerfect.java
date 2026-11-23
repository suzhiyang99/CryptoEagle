package org.apache.tika.metadata;

public interface WordPerfect {
   String WORDPERFECT_METADATA_NAME_PREFIX = "wordperfect";
   Property FILE_SIZE = Property.internalText("wordperfect:FileSize");
   Property FILE_ID = Property.internalText("wordperfect:FileId");
   Property PRODUCT_TYPE = Property.internalInteger("wordperfect:ProductType");
   Property FILE_TYPE = Property.internalInteger("wordperfect:FileType");
   Property MAJOR_VERSION = Property.internalInteger("wordperfect:MajorVersion");
   Property MINOR_VERSION = Property.internalInteger("wordperfect:MinorVersion");
   Property ENCRYPTED = Property.internalBoolean("wordperfect:Encrypted");
}
