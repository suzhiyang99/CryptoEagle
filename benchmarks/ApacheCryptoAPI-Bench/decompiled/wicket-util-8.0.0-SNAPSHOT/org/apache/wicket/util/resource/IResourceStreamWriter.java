package org.apache.wicket.util.resource;

import java.io.IOException;
import java.io.OutputStream;

public interface IResourceStreamWriter extends IResourceStream {
   void write(OutputStream var1) throws IOException;
}
