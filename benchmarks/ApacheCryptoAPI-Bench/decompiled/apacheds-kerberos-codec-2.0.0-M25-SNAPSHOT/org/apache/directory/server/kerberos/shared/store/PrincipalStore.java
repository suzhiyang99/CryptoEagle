package org.apache.directory.server.kerberos.shared.store;

import javax.security.auth.kerberos.KerberosPrincipal;
import org.apache.directory.server.kerberos.changepwd.exceptions.ChangePasswordException;

public interface PrincipalStore {
   void changePassword(KerberosPrincipal var1, KerberosPrincipal var2, String var3, boolean var4) throws ChangePasswordException;

   PrincipalStoreEntry getPrincipal(KerberosPrincipal var1) throws Exception;
}
