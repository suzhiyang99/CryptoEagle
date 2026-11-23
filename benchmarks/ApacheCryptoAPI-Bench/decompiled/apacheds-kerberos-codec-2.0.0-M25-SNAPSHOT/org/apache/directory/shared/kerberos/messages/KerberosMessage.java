package org.apache.directory.shared.kerberos.messages;

import org.apache.directory.api.asn1.Asn1Object;
import org.apache.directory.shared.kerberos.KerberosMessageType;

public abstract class KerberosMessage implements Asn1Object {
   private int protocolVersionNumber;
   private KerberosMessageType messageType;

   public KerberosMessage(KerberosMessageType type) {
      this(5, type);
   }

   public KerberosMessage(int versionNumber, KerberosMessageType type) {
      this.protocolVersionNumber = 5;
      this.protocolVersionNumber = versionNumber;
      this.messageType = type;
   }

   public KerberosMessageType getMessageType() {
      return this.messageType;
   }

   public void setMessageType(KerberosMessageType type) {
      this.messageType = type;
   }

   public int getProtocolVersionNumber() {
      return this.protocolVersionNumber;
   }

   public void setProtocolVersionNumber(int versionNumber) {
      this.protocolVersionNumber = versionNumber;
   }
}
