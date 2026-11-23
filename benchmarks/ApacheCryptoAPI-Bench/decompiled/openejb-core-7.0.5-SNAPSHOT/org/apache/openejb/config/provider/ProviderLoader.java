package org.apache.openejb.config.provider;

import java.util.List;
import org.apache.openejb.config.sys.ServiceProvider;

public interface ProviderLoader {
   ServiceProvider load(ID var1);

   List<ServiceProvider> load(String var1);
}
