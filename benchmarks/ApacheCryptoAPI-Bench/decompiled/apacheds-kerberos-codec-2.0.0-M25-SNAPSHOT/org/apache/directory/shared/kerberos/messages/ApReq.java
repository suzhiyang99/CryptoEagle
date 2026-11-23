package org.apache.directory.shared.kerberos.messages;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.api.util.Strings;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.shared.kerberos.KerberosMessageType;
import org.apache.directory.shared.kerberos.codec.options.ApOptions;
import org.apache.directory.shared.kerberos.components.EncryptedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApReq extends KerberosMessage {
   private static final Logger LOG = LoggerFactory.getLogger(ApReq.class);
   private static final boolean IS_DEBUG;
   private ApOptions apOptions;
   private Ticket ticket;
   private EncryptedData authenticator;
   private int pvnoLength;
   private int msgTypeLength;
   private int apOptionsLength;
   private int ticketLength;
   private int authenticatorLength;
   private int apReqLength;
   private int apReqSeqLength;

   public ApReq() {
      super(KerberosMessageType.AP_REQ);
   }

   public ApOptions getApOptions() {
      return this.apOptions;
   }

   public Ticket getTicket() {
      return this.ticket;
   }

   public boolean getOption(int option) {
      return this.apOptions.get(option);
   }

   public void setOption(ApOptions apOptions) {
      this.apOptions = apOptions;
   }

   public void clearOption(int option) {
      this.apOptions.clear(option);
   }

   public EncryptedData getAuthenticator() {
      return this.authenticator;
   }

   public void setAuthenticator(EncryptedData authenticator) {
      this.authenticator = authenticator;
   }

   public void setApOptions(ApOptions options) {
      this.apOptions = options;
   }

   public void setTicket(Ticket ticket) {
      this.ticket = ticket;
   }

   public int computeLength() {
      this.reset();
      this.pvnoLength = 2 + BerValue.getNbBytes(this.getProtocolVersionNumber());
      this.msgTypeLength = 2 + BerValue.getNbBytes(this.getMessageType().getValue());
      this.apOptionsLength = 2 + this.apOptions.getBytes().length;
      this.ticketLength = this.ticket.computeLength();
      this.authenticatorLength = this.authenticator.computeLength();
      this.apReqLength = 1 + TLV.getNbBytes(this.pvnoLength) + this.pvnoLength + 1 + TLV.getNbBytes(this.msgTypeLength) + this.msgTypeLength + 1 + TLV.getNbBytes(this.apOptionsLength) + this.apOptionsLength + 1 + TLV.getNbBytes(this.ticketLength) + this.ticketLength + 1 + TLV.getNbBytes(this.authenticatorLength) + this.authenticatorLength;
      this.apReqSeqLength = 1 + TLV.getNbBytes(this.apReqLength) + this.apReqLength;
      return 1 + TLV.getNbBytes(this.apReqSeqLength) + this.apReqSeqLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         buffer = ByteBuffer.allocate(this.computeLength());
      }

      try {
         buffer.put((byte)110);
         buffer.put(TLV.getBytes(this.apReqSeqLength));
         buffer.put(UniversalTag.SEQUENCE.getValue());
         buffer.put(TLV.getBytes(this.apReqLength));
         buffer.put((byte)-96);
         buffer.put(TLV.getBytes(this.pvnoLength));
         BerValue.encode(buffer, this.getProtocolVersionNumber());
         buffer.put((byte)-95);
         buffer.put(TLV.getBytes(this.msgTypeLength));
         BerValue.encode(buffer, this.getMessageType().getValue());
         buffer.put((byte)-94);
         buffer.put(TLV.getBytes(this.apOptionsLength));
         BerValue.encode(buffer, this.apOptions);
         buffer.put((byte)-93);
         buffer.put(TLV.getBytes(this.ticketLength));
         this.ticket.encode(buffer);
         buffer.put((byte)-92);
         buffer.put(TLV.getBytes(this.authenticatorLength));
         this.authenticator.encode(buffer);
      } catch (BufferOverflowException boe) {
         LOG.error(I18n.err(I18n.ERR_137, new Object[]{1 + TLV.getNbBytes(this.apReqLength) + this.apReqLength, buffer.capacity()}));
         throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
      }

      if (IS_DEBUG) {
         LOG.debug("AP-REQ encoding : {}", Strings.dumpBytes(buffer.array()));
         LOG.debug("AP-REQ initial value : {}", this.toString());
      }

      return buffer;
   }

   private void reset() {
      this.pvnoLength = 0;
      this.msgTypeLength = 0;
      this.apOptionsLength = 0;
      this.ticketLength = 0;
      this.authenticatorLength = 0;
      this.apReqLength = 0;
      this.apReqSeqLength = 0;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("AP-REQ :\n");
      sb.append("  pvno : ").append(this.getProtocolVersionNumber()).append("\n");
      sb.append("  msg-type : ").append(this.getMessageType()).append("\n");
      sb.append("  ap-options : ").append(this.apOptions).append("\n");
      sb.append("  ticket : ").append(this.ticket).append("\n");
      sb.append("  authenticator : ").append(this.authenticator).append("\n");
      return sb.toString();
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
