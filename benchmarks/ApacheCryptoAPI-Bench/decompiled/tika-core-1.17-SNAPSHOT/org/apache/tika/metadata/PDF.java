package org.apache.tika.metadata;

public interface PDF {
   String PDF_PREFIX = "pdf:";
   String PDFA_PREFIX = "pdfa:";
   String PDFAID_PREFIX = "pdfaid:";
   String PDF_DOC_INFO_PREFIX = "pdf:docinfo:";
   String PDF_DOC_INFO_CUSTOM_PREFIX = "pdf:docinfo:custom:";
   Property DOC_INFO_CREATED = Property.internalDate("pdf:docinfo:created");
   Property DOC_INFO_CREATOR = Property.internalText("pdf:docinfo:creator");
   Property DOC_INFO_CREATOR_TOOL = Property.internalText("pdf:docinfo:creator_tool");
   Property DOC_INFO_MODIFICATION_DATE = Property.internalDate("pdf:docinfo:modified");
   Property DOC_INFO_KEY_WORDS = Property.internalText("pdf:docinfo:keywords");
   Property DOC_INFO_PRODUCER = Property.internalText("pdf:docinfo:producer");
   Property DOC_INFO_SUBJECT = Property.internalText("pdf:docinfo:subject");
   Property DOC_INFO_TITLE = Property.internalText("pdf:docinfo:title");
   Property DOC_INFO_TRAPPED = Property.internalText("pdf:docinfo:trapped");
   Property PDF_VERSION = Property.internalRational("pdf:PDFVersion");
   Property PDFA_VERSION = Property.internalRational("pdfa:PDFVersion");
   Property PDF_EXTENSION_VERSION = Property.internalRational("pdf:PDFExtensionVersion");
   Property PDFAID_CONFORMANCE = Property.internalText("pdfaid:conformance");
   Property PDFAID_PART = Property.internalText("pdfaid:part");
   Property IS_ENCRYPTED = Property.internalBoolean("pdf:encrypted");
   Property ACTION_TRIGGER = Property.internalText("pdf:actionTrigger");
}
