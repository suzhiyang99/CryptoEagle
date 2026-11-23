package org.apache.tika.metadata;

public interface XMPMM {
   String NAMESPACE_URI = "http://ns.adobe.com/xap/1.0/mm/";
   String PREFIX = "xmpMM";
   String PREFIX_ = "xmpMM:";
   Property DOCUMENTID = Property.externalText("xmpMM:DocumentID");
   Property INSTANCEID = Property.externalText("xmpMM:InstanceID");
   Property ORIGINAL_DOCUMENTID = Property.externalText("xmpMM:OriginalDocumentID");
   Property RENDITION_CLASS = Property.externalOpenChoise("xmpMM:RenditionClass", "default", "draft", "low-res", "proof", "screen", "thumbnail");
   Property RENDITION_PARAMS = Property.externalText("xmpMM:RenditionParams");
   Property HISTORY_EVENT_INSTANCEID = Property.externalTextBag("xmpMM:History:InstanceID");
   Property HISTORY_ACTION = Property.externalTextBag("xmpMM:History:Action");
   Property HISTORY_WHEN = Property.externalTextBag("xmpMM:History:When");
   Property HISTORY_SOFTWARE_AGENT = Property.externalTextBag("xmpMM:History:SoftwareAgent");
   Property DERIVED_FROM_DOCUMENTID = Property.externalText("xmpMM:DerivedFrom:DocumentID");
   Property DERIVED_FROM_INSTANCEID = Property.externalText("xmpMM:DerivedFrom:InstanceID");
}
