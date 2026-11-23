package org.apache.tika.sax;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.xml.sax.ContentHandler;

public interface ContentHandlerFactory {
   ContentHandler getNewContentHandler();

   ContentHandler getNewContentHandler(OutputStream var1, String var2) throws UnsupportedEncodingException;
}
