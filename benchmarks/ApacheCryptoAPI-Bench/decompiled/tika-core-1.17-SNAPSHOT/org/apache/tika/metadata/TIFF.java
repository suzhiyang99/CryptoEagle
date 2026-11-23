package org.apache.tika.metadata;

public interface TIFF {
   Property BITS_PER_SAMPLE = Property.internalIntegerSequence("tiff:BitsPerSample");
   Property IMAGE_LENGTH = Property.internalInteger("tiff:ImageLength");
   Property IMAGE_WIDTH = Property.internalInteger("tiff:ImageWidth");
   Property SAMPLES_PER_PIXEL = Property.internalInteger("tiff:SamplesPerPixel");
   Property FLASH_FIRED = Property.internalBoolean("exif:Flash");
   Property EXPOSURE_TIME = Property.internalRational("exif:ExposureTime");
   Property F_NUMBER = Property.internalRational("exif:FNumber");
   Property FOCAL_LENGTH = Property.internalRational("exif:FocalLength");
   Property ISO_SPEED_RATINGS = Property.internalIntegerSequence("exif:IsoSpeedRatings");
   Property EQUIPMENT_MAKE = Property.internalText("tiff:Make");
   Property EQUIPMENT_MODEL = Property.internalText("tiff:Model");
   Property SOFTWARE = Property.internalText("tiff:Software");
   Property ORIENTATION = Property.internalClosedChoise("tiff:Orientation", "1", "2", "3", "4", "5", "6", "7", "8");
   Property RESOLUTION_HORIZONTAL = Property.internalRational("tiff:XResolution");
   Property RESOLUTION_VERTICAL = Property.internalRational("tiff:YResolution");
   Property RESOLUTION_UNIT = Property.internalClosedChoise("tiff:ResolutionUnit", "Inch", "cm");
   Property ORIGINAL_DATE = Property.internalDate("exif:DateTimeOriginal");
   Property EXIF_PAGE_COUNT = Property.externalInteger("exif:PageCount");
}
