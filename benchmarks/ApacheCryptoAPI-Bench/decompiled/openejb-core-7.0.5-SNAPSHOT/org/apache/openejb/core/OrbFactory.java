package org.apache.openejb.core;

import org.apache.openejb.loader.SystemInstance;
import org.omg.CORBA.ORB;

public class OrbFactory {
   public ORB create() {
      ORB orb = (ORB)SystemInstance.get().getComponent(ORB.class);
      if (orb == null) {
         orb = ORB.init();
         SystemInstance.get().setComponent(ORB.class, orb);
      }

      return orb;
   }
}
