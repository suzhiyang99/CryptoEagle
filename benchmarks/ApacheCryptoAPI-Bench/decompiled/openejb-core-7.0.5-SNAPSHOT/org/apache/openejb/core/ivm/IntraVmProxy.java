package org.apache.openejb.core.ivm;

import java.io.ObjectStreamException;
import java.io.Serializable;

public interface IntraVmProxy extends Serializable {
   Object writeReplace() throws ObjectStreamException;
}
