package org.apache.directory.shared.kerberos.codec;

import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.components.PrincipalName;
import org.apache.directory.shared.kerberos.messages.KerberosMessage;
import org.apache.directory.shared.kerberos.messages.Ticket;

public class KerberosMessageContainer extends AbstractContainer {
   private KerberosMessage message;
   private PrincipalName principalName;
   private boolean isTCP;
   private int tcpLength = -1;

   public KerberosMessageContainer() {
      this.setGrammar(KerberosMessageGrammar.getInstance());
      this.setTransition(KerberosMessageStatesEnum.START_STATE);
   }

   public KerberosMessage getMessage() {
      return this.message;
   }

   public void setMessage(KerberosMessage message) {
      this.message = message;
   }

   public Ticket getTicket() {
      return (Ticket)this.message;
   }

   public PrincipalName getPrincipalName() {
      return this.principalName;
   }

   public void setPrincipalName(PrincipalName principalName) {
      this.principalName = principalName;
   }

   public boolean isTCP() {
      return this.isTCP;
   }

   public void setTCP(boolean isTCP) {
      this.isTCP = isTCP;
   }

   public int getTcpLength() {
      return this.tcpLength;
   }

   public void setTcpLength(int tcpLength) {
      this.tcpLength = tcpLength;
   }
}
