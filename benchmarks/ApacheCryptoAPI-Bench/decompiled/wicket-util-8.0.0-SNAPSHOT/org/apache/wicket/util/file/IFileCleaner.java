package org.apache.wicket.util.file;

import org.apache.commons.io.FileDeleteStrategy;

public interface IFileCleaner {
   void track(java.io.File var1, Object var2);

   void track(java.io.File var1, Object var2, FileDeleteStrategy var3);

   void destroy();
}
