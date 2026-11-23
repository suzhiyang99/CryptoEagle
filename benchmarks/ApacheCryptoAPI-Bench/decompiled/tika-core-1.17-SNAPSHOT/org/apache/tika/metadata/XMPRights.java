package org.apache.tika.metadata;

public interface XMPRights {
   String NAMESPACE_URI_XMP_RIGHTS = "http://ns.adobe.com/xap/1.0/rights/";
   String PREFIX_XMP_RIGHTS = "xmpRights";
   String PREFIX_ = "xmpRights:";
   Property CERTIFICATE = Property.internalText("xmpRights:Certificate");
   Property MARKED = Property.internalBoolean("xmpRights:Marked");
   Property OWNER = Property.internalTextBag("xmpRights:Owner");
   Property USAGE_TERMS = Property.internalText("xmpRights:UsageTerms");
   Property WEB_STATEMENT = Property.internalText("xmpRights:WebStatement");
}
