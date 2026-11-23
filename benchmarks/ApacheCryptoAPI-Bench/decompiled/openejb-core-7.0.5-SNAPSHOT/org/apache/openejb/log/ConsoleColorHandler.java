package org.apache.openejb.log;

import java.util.logging.ConsoleHandler;
import org.apache.openejb.loader.SystemInstance;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.WindowsAnsiOutputStream;

public class ConsoleColorHandler extends ConsoleHandler {
   private static final boolean wrapped;

   public ConsoleColorHandler() {
      this.setFormatter(new ColorFormatter());
      this.setLevel(JULUtil.level());
      if (wrapped) {
         this.setOutputStream(AnsiConsole.out);
      } else {
         this.setOutputStream(System.out);
      }

   }

   static {
      wrapped = AnsiConsole.wrapOutputStream(System.out) instanceof WindowsAnsiOutputStream;
      if (wrapped && "true".equals(SystemInstance.get().getProperty("openejb.log.color.install", "true"))) {
         AnsiConsole.systemInstall();
      }

   }
}
