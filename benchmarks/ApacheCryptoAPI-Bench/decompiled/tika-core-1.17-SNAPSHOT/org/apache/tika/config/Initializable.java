package org.apache.tika.config;

import java.util.Map;
import org.apache.tika.exception.TikaConfigException;

public interface Initializable {
   void initialize(Map<String, Param> var1) throws TikaConfigException;

   void checkInitialization(InitializableProblemHandler var1) throws TikaConfigException;
}
