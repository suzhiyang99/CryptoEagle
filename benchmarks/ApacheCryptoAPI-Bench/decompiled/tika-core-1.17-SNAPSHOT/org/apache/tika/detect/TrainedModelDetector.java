package org.apache.tika.detect;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

public abstract class TrainedModelDetector implements Detector {
   private final Map<MediaType, TrainedModel> MODEL_MAP = new HashMap();
   private static final long serialVersionUID = 1L;

   public TrainedModelDetector() {
      this.loadDefaultModels(this.getClass().getClassLoader());
   }

   public int getMinLength() {
      return Integer.MAX_VALUE;
   }

   public MediaType detect(InputStream input, Metadata metadata) throws IOException {
      if (input != null) {
         input.mark(this.getMinLength());
         float[] histogram = this.readByteFrequencies(input);
         Iterator<MediaType> iter = this.MODEL_MAP.keySet().iterator();
         float threshold = 0.5F;
         float maxprob = threshold;
         MediaType maxType = MediaType.OCTET_STREAM;

         while(iter.hasNext()) {
            MediaType key = (MediaType)iter.next();
            TrainedModel model = (TrainedModel)this.MODEL_MAP.get(key);
            float prob = model.predict(histogram);
            if (maxprob < prob) {
               maxprob = prob;
               maxType = key;
            }
         }

         input.reset();
         return maxType;
      } else {
         return null;
      }
   }

   protected float[] readByteFrequencies(InputStream input) throws IOException {
      ReadableByteChannel inputChannel = Channels.newChannel(input);
      float[] histogram = new float[257];
      histogram[0] = 1.0F;
      ByteBuffer buf = ByteBuffer.allocate(5120);
      int bytesRead = inputChannel.read(buf);

      float max;
      for(max = -1.0F; bytesRead != -1; bytesRead = inputChannel.read(buf)) {
         buf.flip();

         int idx;
         for(; buf.hasRemaining(); max = max < histogram[idx] ? histogram[idx] : max) {
            byte byt = buf.get();
            idx = byt + 1;
            if (byt < 0) {
               idx += 256;
               int var10002 = histogram[idx]++;
            } else {
               int var10 = histogram[idx]++;
            }
         }

         buf.clear();
      }

      for(int i = 1; i < histogram.length; ++i) {
         histogram[i] /= max;
         histogram[i] = (float)Math.sqrt((double)histogram[i]);
      }

      return histogram;
   }

   private void writeHisto(float[] histogram) throws IOException {
      Path histPath = (new TemporaryResources()).createTempFile();
      Writer writer = Files.newBufferedWriter(histPath, StandardCharsets.UTF_8);
      Throwable var4 = null;

      try {
         for(float bin : histogram) {
            writer.write(bin + "\t");
         }

         writer.write("\r\n");
      } catch (Throwable var16) {
         var4 = var16;
         throw var16;
      } finally {
         if (writer != null) {
            if (var4 != null) {
               try {
                  writer.close();
               } catch (Throwable var15) {
                  var4.addSuppressed(var15);
               }
            } else {
               writer.close();
            }
         }

      }

   }

   public void loadDefaultModels(Path modelFile) {
      try {
         InputStream in = Files.newInputStream(modelFile);
         Throwable var3 = null;

         try {
            this.loadDefaultModels(in);
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (in != null) {
               if (var3 != null) {
                  try {
                     in.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  in.close();
               }
            }

         }

      } catch (IOException e) {
         throw new RuntimeException("Unable to read the default media type registry", e);
      }
   }

   public void loadDefaultModels(File modelFile) {
      this.loadDefaultModels(modelFile.toPath());
   }

   public abstract void loadDefaultModels(InputStream var1);

   public abstract void loadDefaultModels(ClassLoader var1);

   protected void registerModels(MediaType type, TrainedModel model) {
      this.MODEL_MAP.put(type, model);
   }
}
