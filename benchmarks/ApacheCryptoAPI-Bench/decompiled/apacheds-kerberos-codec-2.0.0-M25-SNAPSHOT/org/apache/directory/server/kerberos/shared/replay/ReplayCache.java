package org.apache.directory.server.kerberos.shared.replay;

import javax.security.auth.kerberos.KerberosPrincipal;
import org.apache.directory.shared.kerberos.KerberosTime;

public interface ReplayCache {
   boolean isReplay(KerberosPrincipal var1, KerberosPrincipal var2, KerberosTime var3, int var4);

   void save(KerberosPrincipal var1, KerberosPrincipal var2, KerberosTime var3, int var4);

   void clear();
}
