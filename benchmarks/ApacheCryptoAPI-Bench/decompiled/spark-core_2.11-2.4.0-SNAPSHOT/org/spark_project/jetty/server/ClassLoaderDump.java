package org.spark_project.jetty.server;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Collections;
import org.spark_project.jetty.util.TypeUtil;
import org.spark_project.jetty.util.component.ContainerLifeCycle;
import org.spark_project.jetty.util.component.Dumpable;

public class ClassLoaderDump implements Dumpable {
   final ClassLoader _loader;

   public ClassLoaderDump(ClassLoader loader) {
      this._loader = loader;
   }

   public String dump() {
      return ContainerLifeCycle.dump((Dumpable)this);
   }

   public void dump(Appendable out, String indent) throws IOException {
      if (this._loader == null) {
         out.append("No ClassLoader\n");
      } else if (this._loader instanceof Dumpable) {
         ContainerLifeCycle.dump(out, indent, Collections.singleton(this._loader));
      } else if (this._loader instanceof URLClassLoader) {
         out.append(String.valueOf(this._loader)).append("\n");
         ClassLoader parent = this._loader.getParent();
         if (parent == null) {
            ContainerLifeCycle.dump(out, indent, TypeUtil.asList(((URLClassLoader)this._loader).getURLs()));
         } else if (parent == Server.class.getClassLoader()) {
            ContainerLifeCycle.dump(out, indent, TypeUtil.asList(((URLClassLoader)this._loader).getURLs()), Collections.singleton(parent.toString()));
         } else if (parent instanceof Dumpable) {
            ContainerLifeCycle.dump(out, indent, TypeUtil.asList(((URLClassLoader)this._loader).getURLs()), Collections.singleton(parent));
         } else {
            ContainerLifeCycle.dump(out, indent, TypeUtil.asList(((URLClassLoader)this._loader).getURLs()), Collections.singleton(new ClassLoaderDump(parent)));
         }
      } else {
         out.append(String.valueOf(this._loader)).append("\n");
         ClassLoader parent = this._loader.getParent();
         if (parent == Server.class.getClassLoader()) {
            ContainerLifeCycle.dump(out, indent, Collections.singleton(parent.toString()));
         } else if (parent instanceof Dumpable) {
            ContainerLifeCycle.dump(out, indent, Collections.singleton(parent));
         } else if (parent != null) {
            ContainerLifeCycle.dump(out, indent, Collections.singleton(new ClassLoaderDump(parent)));
         }
      }

   }
}
