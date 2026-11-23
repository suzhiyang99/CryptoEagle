package org.apache.wicket.util.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.wicket.util.lang.Args;

public class DeferredFileOutputStream extends ThresholdingOutputStream {
   private OutputStream currentOutputStream;
   private ByteArrayOutputStream memoryOutputStream;
   private File outputFile;
   private final FileFactory fileFactory;

   public DeferredFileOutputStream(int threshold, File outputFile) {
      super(threshold);
      this.outputFile = (File)Args.notNull(outputFile, "outputFile");
      this.fileFactory = null;
      this.memoryOutputStream = new ByteArrayOutputStream();
      this.currentOutputStream = this.memoryOutputStream;
   }

   public DeferredFileOutputStream(int threshold, FileFactory fileFactory) {
      super(threshold);
      this.fileFactory = (FileFactory)Args.notNull(fileFactory, "fileFactory");
      this.memoryOutputStream = new ByteArrayOutputStream();
      this.currentOutputStream = this.memoryOutputStream;
   }

   public byte[] getData() {
      return this.memoryOutputStream != null ? this.memoryOutputStream.toByteArray() : null;
   }

   public File getFile() {
      return this.outputFile;
   }

   public boolean isInMemory() {
      return !this.isThresholdExceeded();
   }

   protected OutputStream getStream() throws IOException {
      return this.currentOutputStream;
   }

   protected void thresholdReached() throws IOException {
      byte[] data = this.memoryOutputStream.toByteArray();
      if (this.outputFile == null) {
         this.outputFile = this.fileFactory.createFile();
      }

      FileOutputStream fos = new FileOutputStream(this.outputFile);
      fos.write(data);
      this.currentOutputStream = fos;
      this.memoryOutputStream = null;
   }

   public interface FileFactory {
      File createFile();
   }
}
