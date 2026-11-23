package org.apache.tika.metadata;

public interface RTFMetadata {
   String PREFIX_RTF_META = "rtf_meta";
   String RTF_PICT_META_PREFIX = "rtf_pict:";
   Property THUMBNAIL = Property.internalBoolean("rtf_meta:thumbnail");
   Property EMB_APP_VERSION = Property.internalText("rtf_meta:emb_app_version");
   Property EMB_CLASS = Property.internalText("rtf_meta:emb_class");
   Property EMB_TOPIC = Property.internalText("rtf_meta:emb_topic");
   Property EMB_ITEM = Property.internalText("rtf_meta:emb_item");
}
