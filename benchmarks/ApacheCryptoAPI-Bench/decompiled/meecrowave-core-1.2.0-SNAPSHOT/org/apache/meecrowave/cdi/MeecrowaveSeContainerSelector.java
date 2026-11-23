package org.apache.meecrowave.cdi;

import javax.enterprise.inject.se.SeContainerInitializer;
import org.apache.openwebbeans.se.SeContainerSelector;

public class MeecrowaveSeContainerSelector implements SeContainerSelector {
   public SeContainerInitializer find() {
      return new MeecrowaveSeContainerInitializer();
   }
}
