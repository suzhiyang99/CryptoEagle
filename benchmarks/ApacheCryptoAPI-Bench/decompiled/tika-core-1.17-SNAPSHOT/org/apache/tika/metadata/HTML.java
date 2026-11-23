package org.apache.tika.metadata;

public interface HTML {
   String PREFIX_HTML_META = "html_meta";
   Property SCRIPT_SOURCE = Property.internalText("html_meta:scriptSrc");
}
