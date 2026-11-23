package org.apache.openejb.core.security.jaas;

import java.util.List;
import javax.security.auth.login.FailedLoginException;

public interface LoginProvider {
   List<String> authenticate(String var1, String var2) throws FailedLoginException;
}
