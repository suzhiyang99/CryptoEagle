package org.apache.tika.parser;

import org.apache.tika.metadata.Metadata;

public interface PasswordProvider {
   String getPassword(Metadata var1);
}
