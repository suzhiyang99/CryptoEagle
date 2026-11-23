package org.apache.tika.sax;

import java.util.Arrays;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StandardsExtractingContentHandler extends ContentHandlerDecorator {
   public static final String STANDARD_REFERENCES = "standard_references";
   private Metadata metadata;
   private StringBuilder stringBuilder;
   private double threshold;

   public StandardsExtractingContentHandler(ContentHandler handler, Metadata metadata) {
      super(handler);
      this.threshold = (double)0.0F;
      this.metadata = metadata;
      this.stringBuilder = new StringBuilder();
   }

   protected StandardsExtractingContentHandler() {
      this(new DefaultHandler(), new Metadata());
   }

   public double getThreshold() {
      return this.threshold;
   }

   public void setThreshold(double score) {
      this.threshold = score;
   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      try {
         String text = new String(Arrays.copyOfRange(ch, start, start + length));
         this.stringBuilder.append(text);
         super.characters(ch, start, length);
      } catch (SAXException e) {
         this.handleException(e);
      }

   }

   public void endDocument() throws SAXException {
      super.endDocument();

      for(StandardReference standardReference : StandardsText.extractStandardReferences(this.stringBuilder.toString(), this.threshold)) {
         this.metadata.add("standard_references", standardReference.toString());
      }

   }
}
