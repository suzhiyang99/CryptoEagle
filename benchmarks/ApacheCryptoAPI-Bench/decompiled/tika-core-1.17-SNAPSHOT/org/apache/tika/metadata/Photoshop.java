package org.apache.tika.metadata;

public interface Photoshop {
   String NAMESPACE_URI_PHOTOSHOP = "http://ns.adobe.com/photoshop/1.0/";
   String PREFIX_PHOTOSHOP = "photoshop";
   Property AUTHORS_POSITION = Property.internalText("photoshop:AuthorsPosition");
   String[] _COLOR_MODE_CHOICES_INDEXED = new String[]{"Bitmap", "Greyscale", "Indexed Colour", "RGB Color", "CMYK Colour", "Multi-Channel", "Duotone", "LAB Colour", "reserved", "reserved", "YCbCr Colour", "YCgCo Colour", "YCbCrK Colour"};
   Property COLOR_MODE = Property.internalClosedChoise("photoshop:ColorMode", _COLOR_MODE_CHOICES_INDEXED);
   Property CAPTION_WRITER = Property.internalText("photoshop:CaptionWriter");
   Property CATEGORY = Property.internalText("photoshop:Category");
   Property CITY = Property.internalText("photoshop:City");
   Property COUNTRY = Property.internalText("photoshop:Country");
   Property CREDIT = Property.internalText("photoshop:Credit");
   Property DATE_CREATED = Property.internalDate("photoshop:DateCreated");
   Property HEADLINE = Property.internalText("photoshop:Headline");
   Property INSTRUCTIONS = Property.internalText("photoshop:Instructions");
   Property SOURCE = Property.internalText("photoshop:Source");
   Property STATE = Property.internalText("photoshop:State");
   Property SUPPLEMENTAL_CATEGORIES = Property.internalTextBag("photoshop:SupplementalCategories");
   Property TRANSMISSION_REFERENCE = Property.internalText("photoshop:TransmissionReference");
   Property URGENCY = Property.internalText("photoshop:Urgency");
}
