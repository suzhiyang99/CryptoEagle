package org.apache.tika.metadata;

public interface DublinCore {
   String NAMESPACE_URI_DC = "http://purl.org/dc/elements/1.1/";
   String NAMESPACE_URI_DC_TERMS = "http://purl.org/dc/terms/";
   String PREFIX_DC = "dc";
   String PREFIX_DC_TERMS = "dcterms";
   Property FORMAT = Property.internalText("dc:format");
   Property IDENTIFIER = Property.internalText("dc:identifier");
   Property MODIFIED = Property.internalDate("dcterms:modified");
   Property CONTRIBUTOR = Property.internalTextBag("dc:contributor");
   Property COVERAGE = Property.internalText("dc:coverage");
   Property CREATOR = Property.internalTextBag("dc:creator");
   Property CREATED = Property.internalDate("dcterms:created");
   Property DATE = Property.internalDate("dc:date");
   Property DESCRIPTION = Property.internalText("dc:description");
   Property LANGUAGE = Property.internalText("dc:language");
   Property PUBLISHER = Property.internalText("dc:publisher");
   Property RELATION = Property.internalText("dc:relation");
   Property RIGHTS = Property.internalText("dc:rights");
   Property SOURCE = Property.internalText("dc:source");
   Property SUBJECT = Property.internalTextBag("dc:subject");
   Property TITLE = Property.internalText("dc:title");
   Property TYPE = Property.internalText("dc:type");
}
