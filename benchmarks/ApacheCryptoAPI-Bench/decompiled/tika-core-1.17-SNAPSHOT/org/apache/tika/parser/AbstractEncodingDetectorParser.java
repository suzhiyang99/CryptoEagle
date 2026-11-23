package org.apache.tika.parser;

import org.apache.tika.detect.DefaultEncodingDetector;
import org.apache.tika.detect.EncodingDetector;

public abstract class AbstractEncodingDetectorParser extends AbstractParser {
   private EncodingDetector encodingDetector;

   public AbstractEncodingDetectorParser() {
      this.encodingDetector = new DefaultEncodingDetector();
   }

   public AbstractEncodingDetectorParser(EncodingDetector encodingDetector) {
      this.encodingDetector = encodingDetector;
   }

   protected EncodingDetector getEncodingDetector(ParseContext parseContext) {
      EncodingDetector fromParseContext = (EncodingDetector)parseContext.get(EncodingDetector.class);
      return fromParseContext != null ? fromParseContext : this.getEncodingDetector();
   }

   public EncodingDetector getEncodingDetector() {
      return this.encodingDetector;
   }

   public void setEncodingDetector(EncodingDetector encodingDetector) {
      this.encodingDetector = encodingDetector;
   }
}
