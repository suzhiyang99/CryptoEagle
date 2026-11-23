package org.apache.directory.server.kerberos.changepwd.io;

import java.nio.ByteBuffer;
import java.nio.InvalidMarkException;
import org.apache.directory.server.kerberos.changepwd.exceptions.ChangePasswordException;
import org.apache.directory.server.kerberos.changepwd.messages.AbstractPasswordMessage;
import org.apache.directory.server.kerberos.changepwd.messages.ChangePasswordError;
import org.apache.directory.server.kerberos.changepwd.messages.ChangePasswordReply;
import org.apache.directory.server.kerberos.changepwd.messages.ChangePasswordRequest;

public class ChangePasswordDecoder {
   public static AbstractPasswordMessage decode(ByteBuffer buf, boolean isTcp) throws ChangePasswordException {
      if (isTcp) {
         buf.getInt();
         buf.mark();
      }

      try {
         return ChangePasswordRequest.decode(buf);
      } catch (Exception var4) {
         resetOrRewind(buf);

         try {
            return ChangePasswordReply.decode(buf);
         } catch (Exception var3) {
            resetOrRewind(buf);
            return ChangePasswordError.decode(buf);
         }
      }
   }

   private static void resetOrRewind(ByteBuffer buf) {
      try {
         buf.reset();
      } catch (InvalidMarkException var2) {
         buf.rewind();
      }

   }
}
