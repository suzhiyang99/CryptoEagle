package org.apache.tika.metadata;

public interface XMPIdq {
   String NAMESPACE_URI = "http://ns.adobe.com/xmp/identifier/qual/1.0/";
   String PREFIX = "xmpidq";
   String PREFIX_ = "xmpidq:";
   Property SCHEME = Property.externalText("xmpidq:Scheme");
}
