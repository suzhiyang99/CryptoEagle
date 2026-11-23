package org.apache.wicket.util.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.Strings;

public final class XmlReader extends Reader {
   private static final Pattern xmlDecl = Pattern.compile("[\\s\\n\\r]*<\\?xml(\\s+.*)?\\?>");
   private static final Pattern encodingPattern = Pattern.compile("\\s+encoding\\s*=\\s*([\"'](.*?)[\"']|(\\S*)).*\\?>");
   private String encoding;
   private final InputStream inputStream;
   private Reader reader;

   public XmlReader(InputStream inputStream, String defaultEncoding) throws IOException {
      Args.notNull(inputStream, "inputStream");
      if (!inputStream.markSupported()) {
         this.inputStream = new BufferedInputStream(new BOMInputStream(inputStream));
      } else {
         this.inputStream = new BOMInputStream(inputStream);
      }

      this.encoding = defaultEncoding;
      this.init();
   }

   public final String getEncoding() {
      return this.encoding;
   }

   public void init() throws IOException {
      int readAheadSize = 80;
      this.inputStream.mark(80);
      String xmlDeclaration = this.getXmlDeclaration(this.inputStream, 80);
      if (!Strings.isEmpty(xmlDeclaration)) {
         this.encoding = this.determineEncoding(xmlDeclaration);
      } else {
         this.inputStream.reset();
      }

      if (this.encoding == null) {
         this.reader = new InputStreamReader(this.inputStream);
      } else {
         this.reader = new InputStreamReader(this.inputStream, this.encoding);
      }

   }

   private String determineEncoding(CharSequence string) {
      Matcher matcher = encodingPattern.matcher(string);
      if (!matcher.find()) {
         return null;
      } else {
         String encoding = matcher.group(2);
         if (encoding == null || encoding.length() == 0) {
            encoding = matcher.group(3);
         }

         if (encoding != null) {
            encoding = encoding.trim();
         }

         return encoding;
      }
   }

   private String getXmlDeclaration(InputStream in, int readAheadSize) throws IOException {
      StringBuilder pushBack = new StringBuilder(readAheadSize);

      int value;
      while((value = in.read()) != -1) {
         pushBack.append((char)value);
         if (value == 62 || value == 10 || value == 13 || pushBack.length() >= readAheadSize - 1) {
            break;
         }
      }

      Matcher matcher = xmlDecl.matcher(pushBack);
      return !matcher.matches() ? null : pushBack.toString().trim();
   }

   public void close() throws IOException {
      try {
         this.reader.close();
      } finally {
         this.inputStream.close();
      }

   }

   public int read(char[] buf, int from, int to) throws IOException {
      return this.reader.read(buf, from, to);
   }

   public String toString() {
      return this.inputStream.toString() + " (" + this.encoding + ")";
   }
}
