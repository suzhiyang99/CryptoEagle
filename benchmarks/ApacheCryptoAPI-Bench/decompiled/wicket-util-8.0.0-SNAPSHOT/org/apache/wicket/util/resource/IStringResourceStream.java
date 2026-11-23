package org.apache.wicket.util.resource;

import java.nio.charset.Charset;

public interface IStringResourceStream extends IResourceStream {
   void setCharset(Charset var1);

   String asString();
}
