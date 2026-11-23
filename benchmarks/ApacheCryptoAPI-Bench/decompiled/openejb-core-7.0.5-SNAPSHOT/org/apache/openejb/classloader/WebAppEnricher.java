package org.apache.openejb.classloader;

import java.net.URL;

public interface WebAppEnricher {
   URL[] enrichment(ClassLoader var1);
}
