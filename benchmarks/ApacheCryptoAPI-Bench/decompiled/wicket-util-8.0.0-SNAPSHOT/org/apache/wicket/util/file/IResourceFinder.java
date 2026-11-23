package org.apache.wicket.util.file;

import org.apache.wicket.util.resource.IResourceStream;

@FunctionalInterface
public interface IResourceFinder {
   IResourceStream find(Class<?> var1, String var2);
}
