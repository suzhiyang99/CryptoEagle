package org.spark_project.jetty.security;

import java.security.Principal;
import org.spark_project.jetty.util.B64Code;

public class SpnegoUserPrincipal implements Principal {
   private final String _name;
   private byte[] _token;
   private String _encodedToken;

   public SpnegoUserPrincipal(String name, String encodedToken) {
      this._name = name;
      this._encodedToken = encodedToken;
   }

   public SpnegoUserPrincipal(String name, byte[] token) {
      this._name = name;
      this._token = token;
   }

   public String getName() {
      return this._name;
   }

   public byte[] getToken() {
      if (this._token == null) {
         this._token = B64Code.decode(this._encodedToken);
      }

      return this._token;
   }

   public String getEncodedToken() {
      if (this._encodedToken == null) {
         this._encodedToken = new String(B64Code.encode(this._token, true));
      }

      return this._encodedToken;
   }
}
