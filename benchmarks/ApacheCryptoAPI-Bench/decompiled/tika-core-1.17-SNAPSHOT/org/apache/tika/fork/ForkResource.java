package org.apache.tika.fork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface ForkResource {
   Throwable process(DataInputStream var1, DataOutputStream var2) throws IOException;
}
