package org.apache.wicket.util.thread;

import org.slf4j.Logger;

@FunctionalInterface
public interface ICode {
   void run(Logger var1);
}
