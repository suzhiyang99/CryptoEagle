package org.apache.tika.metadata;

public interface QuattroPro {
   String QUATTROPRO_METADATA_NAME_PREFIX = "wordperfect";
   Property ID = Property.internalText("wordperfect:Id");
   Property VERSION = Property.internalInteger("wordperfect:Version");
   Property BUILD = Property.internalInteger("wordperfect:Build");
   Property LOWEST_VERSION = Property.internalInteger("wordperfect:LowestVersion");
}
