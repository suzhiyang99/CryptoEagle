package org.spark_project.jetty.client.util;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.spark_project.jetty.client.api.Authentication;
import org.spark_project.jetty.client.api.ContentResponse;
import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.util.Attributes;
import org.spark_project.jetty.util.StringUtil;
import org.spark_project.jetty.util.TypeUtil;

public class DigestAuthentication extends AbstractAuthentication {
   private static final Pattern PARAM_PATTERN = Pattern.compile("([^=]+)=(.*)");
   private final String user;
   private final String password;

   public DigestAuthentication(URI uri, String realm, String user, String password) {
      super(uri, realm);
      this.user = user;
      this.password = password;
   }

   public String getType() {
      return "Digest";
   }

   public Authentication.Result authenticate(Request request, ContentResponse response, Authentication.HeaderInfo headerInfo, Attributes context) {
      Map<String, String> params = this.parseParameters(headerInfo.getParameters());
      String nonce = (String)params.get("nonce");
      if (nonce != null && nonce.length() != 0) {
         String opaque = (String)params.get("opaque");
         String algorithm = (String)params.get("algorithm");
         if (algorithm == null) {
            algorithm = "MD5";
         }

         MessageDigest digester = this.getMessageDigest(algorithm);
         if (digester == null) {
            return null;
         } else {
            String serverQOP = (String)params.get("qop");
            String clientQOP = null;
            if (serverQOP != null) {
               List<String> serverQOPValues = StringUtil.csvSplit((List)null, serverQOP, 0, serverQOP.length());
               if (serverQOPValues.contains("auth")) {
                  clientQOP = "auth";
               } else if (serverQOPValues.contains("auth-int")) {
                  clientQOP = "auth-int";
               }
            }

            String realm = this.getRealm();
            if ("<<ANY_REALM>>".equals(realm)) {
               realm = headerInfo.getRealm();
            }

            return new DigestResult(headerInfo.getHeader(), response.getContent(), realm, this.user, this.password, algorithm, nonce, clientQOP, opaque);
         }
      } else {
         return null;
      }
   }

   private Map<String, String> parseParameters(String wwwAuthenticate) {
      Map<String, String> result = new HashMap();

      for(String part : this.splitParams(wwwAuthenticate)) {
         Matcher matcher = PARAM_PATTERN.matcher(part);
         if (matcher.matches()) {
            String name = matcher.group(1).trim().toLowerCase(Locale.ENGLISH);
            String value = matcher.group(2).trim();
            if (value.startsWith("\"") && value.endsWith("\"")) {
               value = value.substring(1, value.length() - 1);
            }

            result.put(name, value);
         }
      }

      return result;
   }

   private List<String> splitParams(String paramString) {
      List<String> result = new ArrayList();
      int start = 0;

      for(int i = 0; i < paramString.length(); ++i) {
         int quotes = 0;
         char ch = paramString.charAt(i);
         switch (ch) {
            case '"':
               ++quotes;
               break;
            case ',':
               if (quotes % 2 == 0) {
                  String element = paramString.substring(start, i).trim();
                  if (element.length() > 0) {
                     result.add(element);
                  }

                  start = i + 1;
               }
               break;
            case '\\':
               ++i;
         }
      }

      result.add(paramString.substring(start, paramString.length()).trim());
      return result;
   }

   private MessageDigest getMessageDigest(String algorithm) {
      try {
         return MessageDigest.getInstance(algorithm);
      } catch (NoSuchAlgorithmException var3) {
         return null;
      }
   }

   private class DigestResult implements Authentication.Result {
      private final AtomicInteger nonceCount = new AtomicInteger();
      private final HttpHeader header;
      private final byte[] content;
      private final String realm;
      private final String user;
      private final String password;
      private final String algorithm;
      private final String nonce;
      private final String qop;
      private final String opaque;

      public DigestResult(HttpHeader header, byte[] content, String realm, String user, String password, String algorithm, String nonce, String qop, String opaque) {
         this.header = header;
         this.content = content;
         this.realm = realm;
         this.user = user;
         this.password = password;
         this.algorithm = algorithm;
         this.nonce = nonce;
         this.qop = qop;
         this.opaque = opaque;
      }

      public URI getURI() {
         return DigestAuthentication.this.getURI();
      }

      public void apply(Request request) {
         MessageDigest digester = DigestAuthentication.this.getMessageDigest(this.algorithm);
         if (digester != null) {
            String A1 = this.user + ":" + this.realm + ":" + this.password;
            String hashA1 = this.toHexString(digester.digest(A1.getBytes(StandardCharsets.ISO_8859_1)));
            URI uri = request.getURI();
            String A2 = request.getMethod() + ":" + uri;
            if ("auth-int".equals(this.qop)) {
               A2 = A2 + ":" + this.toHexString(digester.digest(this.content));
            }

            String hashA2 = this.toHexString(digester.digest(A2.getBytes(StandardCharsets.ISO_8859_1)));
            String nonceCount;
            String clientNonce;
            String A3;
            if (this.qop != null) {
               nonceCount = this.nextNonceCount();
               clientNonce = this.newClientNonce();
               A3 = hashA1 + ":" + this.nonce + ":" + nonceCount + ":" + clientNonce + ":" + this.qop + ":" + hashA2;
            } else {
               nonceCount = null;
               clientNonce = null;
               A3 = hashA1 + ":" + this.nonce + ":" + hashA2;
            }

            String hashA3 = this.toHexString(digester.digest(A3.getBytes(StandardCharsets.ISO_8859_1)));
            StringBuilder value = new StringBuilder("Digest");
            value.append(" username=\"").append(this.user).append("\"");
            value.append(", realm=\"").append(this.realm).append("\"");
            value.append(", nonce=\"").append(this.nonce).append("\"");
            if (this.opaque != null) {
               value.append(", opaque=\"").append(this.opaque).append("\"");
            }

            value.append(", algorithm=\"").append(this.algorithm).append("\"");
            value.append(", uri=\"").append(uri).append("\"");
            if (this.qop != null) {
               value.append(", qop=\"").append(this.qop).append("\"");
               value.append(", nc=\"").append(nonceCount).append("\"");
               value.append(", cnonce=\"").append(clientNonce).append("\"");
            }

            value.append(", response=\"").append(hashA3).append("\"");
            request.header(this.header, value.toString());
         }
      }

      private String nextNonceCount() {
         String padding = "00000000";
         String next = Integer.toHexString(this.nonceCount.incrementAndGet()).toLowerCase(Locale.ENGLISH);
         return padding.substring(0, padding.length() - next.length()) + next;
      }

      private String newClientNonce() {
         Random random = new Random();
         byte[] bytes = new byte[8];
         random.nextBytes(bytes);
         return this.toHexString(bytes);
      }

      private String toHexString(byte[] bytes) {
         return TypeUtil.toHexString(bytes).toLowerCase(Locale.ENGLISH);
      }
   }
}
