package org.apache.tika.metadata;

public interface Geographic {
   Property LATITUDE = Property.internalReal("geo:lat");
   Property LONGITUDE = Property.internalReal("geo:long");
   Property ALTITUDE = Property.internalReal("geo:alt");
}
