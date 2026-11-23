package org.apache.manifoldcf.core.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;

public class Base64 {
   public static final String _rcsid = "@(#)$Id: Base64.java 988245 2010-08-23 18:39:35Z kwright $";
   private static final char[] base64CharacterArray = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
   private static final byte[] mapArray = new byte[128];
   private static final char base64PadCharacter = '=';
   protected char[] characterBuffer = new char[4];
   protected byte[] byteBuffer = new byte[3];

   public boolean decodeNextWord(Reader inputBuffer, OutputStream outputBuffer) throws ManifoldCFException {
      try {
         int bufferIndex = 0;

         while(bufferIndex < this.characterBuffer.length) {
            int character = inputBuffer.read();
            if (character == -1) {
               if (bufferIndex != 0) {
                  throw new ManifoldCFException("Unexpected end of base64 input");
               }

               return false;
            }

            char ch = (char)character;
            if (ch == '=' || ch < mapArray.length && mapArray[ch] != 127) {
               this.characterBuffer[bufferIndex++] = ch;
            }
         }

         int outlen = 3;
         if (this.characterBuffer[3] == '=') {
            outlen = 2;
         }

         if (this.characterBuffer[2] == '=') {
            outlen = 1;
         }

         int b0 = mapArray[this.characterBuffer[0]];
         int b1 = mapArray[this.characterBuffer[1]];
         int b2 = mapArray[this.characterBuffer[2]];
         int b3 = mapArray[this.characterBuffer[3]];
         switch (outlen) {
            case 1:
               outputBuffer.write((byte)(b0 << 2 & 252 | b1 >> 4 & 3));
               break;
            case 2:
               outputBuffer.write((byte)(b0 << 2 & 252 | b1 >> 4 & 3));
               outputBuffer.write((byte)(b1 << 4 & 240 | b2 >> 2 & 15));
               break;
            case 3:
               outputBuffer.write((byte)(b0 << 2 & 252 | b1 >> 4 & 3));
               outputBuffer.write((byte)(b1 << 4 & 240 | b2 >> 2 & 15));
               outputBuffer.write((byte)(b2 << 6 & 192 | b3 & 63));
               break;
            default:
               throw new RuntimeException("Should never occur");
         }

         return true;
      } catch (IOException e) {
         throw new ManifoldCFException("IO error converting from base 64", e);
      }
   }

   public void decodeStream(Reader inputBuffer, OutputStream outputBuffer) throws ManifoldCFException {
      while(this.decodeNextWord(inputBuffer, outputBuffer)) {
      }

   }

   public byte[] decodeString(String inputString) throws ManifoldCFException {
      try {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream((inputString.length() >> 2) * 3 + 3);

         byte[] var4;
         try {
            Reader reader = new StringReader(inputString);

            try {
               this.decodeStream(reader, outputStream);
               outputStream.flush();
               var4 = outputStream.toByteArray();
            } finally {
               reader.close();
            }
         } finally {
            outputStream.close();
         }

         return var4;
      } catch (IOException e) {
         throw new ManifoldCFException("Error streaming through base64 decoder", e);
      }
   }

   public boolean encodeNextWord(InputStream inputStream, Writer outputWriter) throws ManifoldCFException {
      try {
         int actualLength = inputStream.read(this.byteBuffer);
         if (actualLength == -1) {
            return false;
         } else {
            switch (actualLength) {
               case 0:
                  throw new ManifoldCFException("Read 0 bytes!");
               case 1:
                  int i = this.byteBuffer[0] & 255;
                  outputWriter.write(base64CharacterArray[i >> 2]);
                  outputWriter.write(base64CharacterArray[i << 4 & 63]);
                  outputWriter.write(61);
                  outputWriter.write(61);
                  break;
               case 2:
                  int i = ((this.byteBuffer[0] & 255) << 8) + (this.byteBuffer[1] & 255);
                  outputWriter.write(base64CharacterArray[i >> 10]);
                  outputWriter.write(base64CharacterArray[i >> 4 & 63]);
                  outputWriter.write(base64CharacterArray[i << 2 & 63]);
                  outputWriter.write(61);
                  break;
               case 3:
                  int i = ((this.byteBuffer[0] & 255) << 16) + ((this.byteBuffer[1] & 255) << 8) + (this.byteBuffer[2] & 255);
                  outputWriter.write(base64CharacterArray[i >> 18]);
                  outputWriter.write(base64CharacterArray[i >> 12 & 63]);
                  outputWriter.write(base64CharacterArray[i >> 6 & 63]);
                  outputWriter.write(base64CharacterArray[i & 63]);
                  break;
               default:
                  throw new RuntimeException("Should never get here");
            }

            return true;
         }
      } catch (IOException e) {
         throw new ManifoldCFException("IO error encoding in base64", e);
      }
   }

   public void encodeStream(InputStream inputStream, Writer outputWriter) throws ManifoldCFException {
      while(this.encodeNextWord(inputStream, outputWriter)) {
      }

   }

   public String encodeByteArray(byte[] inputByteArray) throws ManifoldCFException {
      try {
         Writer writer = new StringWriter(inputByteArray.length * 4 / 3 + 4);

         String var4;
         try {
            InputStream is = new ByteArrayInputStream(inputByteArray);

            try {
               this.encodeStream(is, writer);
               writer.flush();
               var4 = writer.toString();
            } finally {
               is.close();
            }
         } finally {
            writer.close();
         }

         return var4;
      } catch (IOException e) {
         throw new ManifoldCFException("Error streaming through base64 encoder", e);
      }
   }

   static {
      for(int i = 0; i < mapArray.length; mapArray[i++] = 127) {
      }

      for(int var1 = 0; var1 < base64CharacterArray.length; ++var1) {
         mapArray[base64CharacterArray[var1]] = (byte)var1;
      }

   }
}
