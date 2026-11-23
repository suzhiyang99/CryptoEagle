package org.apache.wicket.util.license;

import java.io.File;
import java.util.List;

interface ILicenseHeaderHandler {
   List<String> getSuffixes();

   List<String> getIgnoreFiles();

   boolean addLicenseHeader(File var1);

   boolean checkLicenseHeader(File var1);

   String getLicenseType(File var1);
}
