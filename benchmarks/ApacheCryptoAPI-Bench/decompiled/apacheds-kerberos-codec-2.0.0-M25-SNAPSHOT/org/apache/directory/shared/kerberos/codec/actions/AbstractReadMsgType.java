package org.apache.directory.shared.kerberos.codec.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Container;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.IntegerDecoder;
import org.apache.directory.api.asn1.ber.tlv.IntegerDecoderException;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.api.util.Strings;
import org.apache.directory.shared.kerberos.KerberosMessageType;
import org.apache.directory.shared.kerberos.codec.kdcRep.KdcRepContainer;
import org.apache.directory.shared.kerberos.codec.kdcReq.KdcReqContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractReadMsgType<E extends Asn1Container> extends GrammarAction<E> {
   private static final Logger LOG = LoggerFactory.getLogger(AbstractReadMsgType.class);
   private KerberosMessageType msgType = null;

   public AbstractReadMsgType(String name) {
      super(name);
   }

   public AbstractReadMsgType(String name, KerberosMessageType msgType) {
      super(name);
      this.msgType = msgType;
   }

   public final void action(E container) throws DecoderException {
      TLV tlv = container.getCurrentTLV();
      if (tlv.getLength() != 1) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         BerValue value = tlv.getValue();

         try {
            int msgTypeValue = IntegerDecoder.parse(value);
            if (this.msgType != null) {
               if (this.msgType.getValue() == msgTypeValue) {
                  LOG.debug("msg-type : {}", this.msgType);
               } else {
                  String message = I18n.err(I18n.ERR_04070, new Object[]{Strings.dumpBytes(value.getData())});
                  LOG.error(message);
                  throw new DecoderException(message);
               }
            } else {
               KerberosMessageType messageType = KerberosMessageType.getTypeByValue(msgTypeValue);
               if (container instanceof KdcReqContainer) {
                  if (((KdcReqContainer)container).getKdcReq().getMessageType() == messageType) {
                     return;
                  }
               } else if (container instanceof KdcRepContainer && ((KdcRepContainer)container).getKdcRep().getMessageType() == messageType) {
                  return;
               }

               String message = I18n.err(I18n.ERR_04070, new Object[]{Strings.dumpBytes(value.getData())});
               LOG.error(message);
               throw new DecoderException(message);
            }
         } catch (IntegerDecoderException ide) {
            LOG.error(I18n.err(I18n.ERR_04070, new Object[]{Strings.dumpBytes(value.getData()), ide.getLocalizedMessage()}));
            throw new DecoderException(ide.getMessage());
         }
      }
   }
}
