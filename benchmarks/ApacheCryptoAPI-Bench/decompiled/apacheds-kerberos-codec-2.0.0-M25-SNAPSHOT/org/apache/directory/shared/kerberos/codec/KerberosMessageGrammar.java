package org.apache.directory.shared.kerberos.codec;

import java.nio.ByteBuffer;
import java.nio.InvalidMarkException;
import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.shared.kerberos.codec.apRep.ApRepContainer;
import org.apache.directory.shared.kerberos.codec.apReq.ApReqContainer;
import org.apache.directory.shared.kerberos.codec.asRep.AsRepContainer;
import org.apache.directory.shared.kerberos.codec.asReq.AsReqContainer;
import org.apache.directory.shared.kerberos.codec.krbCred.KrbCredContainer;
import org.apache.directory.shared.kerberos.codec.krbError.KrbErrorContainer;
import org.apache.directory.shared.kerberos.codec.krbPriv.KrbPrivContainer;
import org.apache.directory.shared.kerberos.codec.krbSafe.KrbSafeContainer;
import org.apache.directory.shared.kerberos.codec.tgsRep.TgsRepContainer;
import org.apache.directory.shared.kerberos.codec.tgsReq.TgsReqContainer;
import org.apache.directory.shared.kerberos.messages.KerberosMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KerberosMessageGrammar extends AbstractGrammar<KerberosMessageContainer> {
   static final Logger LOG = LoggerFactory.getLogger(KerberosMessageGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<KerberosMessageContainer> instance;
   Asn1Decoder kerberosDecoder = new Asn1Decoder();

   private KerberosMessageGrammar() {
      this.setName(KerberosMessageGrammar.class.getName());
      super.transitions = new GrammarTransition[KerberosMessageStatesEnum.LAST_KERBEROS_MESSAGE_STATE.ordinal()][256];
      super.transitions[KerberosMessageStatesEnum.START_STATE.ordinal()][106] = new GrammarTransition(KerberosMessageStatesEnum.START_STATE, KerberosMessageStatesEnum.AS_REQ_STATE, 106, new DecodeKerberosMessage());
      super.transitions[KerberosMessageStatesEnum.START_STATE.ordinal()][107] = new GrammarTransition(KerberosMessageStatesEnum.START_STATE, KerberosMessageStatesEnum.AS_REP_TAG_STATE, 107, new DecodeKerberosMessage());
      super.transitions[KerberosMessageStatesEnum.START_STATE.ordinal()][108] = new GrammarTransition(KerberosMessageStatesEnum.START_STATE, KerberosMessageStatesEnum.TGS_REQ_TAG_STATE, 108, new DecodeKerberosMessage());
      super.transitions[KerberosMessageStatesEnum.START_STATE.ordinal()][109] = new GrammarTransition(KerberosMessageStatesEnum.START_STATE, KerberosMessageStatesEnum.TGS_REP_TAG_STATE, 109, new DecodeKerberosMessage());
      super.transitions[KerberosMessageStatesEnum.START_STATE.ordinal()][110] = new GrammarTransition(KerberosMessageStatesEnum.START_STATE, KerberosMessageStatesEnum.AP_REQ_TAG_STATE, 110, new DecodeKerberosMessage());
      super.transitions[KerberosMessageStatesEnum.START_STATE.ordinal()][111] = new GrammarTransition(KerberosMessageStatesEnum.START_STATE, KerberosMessageStatesEnum.AP_REP_TAG_STATE, 111, new DecodeKerberosMessage());
      super.transitions[KerberosMessageStatesEnum.START_STATE.ordinal()][116] = new GrammarTransition(KerberosMessageStatesEnum.START_STATE, KerberosMessageStatesEnum.KRB_SAFE_STATE, 116, new DecodeKerberosMessage());
      super.transitions[KerberosMessageStatesEnum.START_STATE.ordinal()][117] = new GrammarTransition(KerberosMessageStatesEnum.START_STATE, KerberosMessageStatesEnum.KRB_PRIV_STATE, 117, new DecodeKerberosMessage());
      super.transitions[KerberosMessageStatesEnum.START_STATE.ordinal()][118] = new GrammarTransition(KerberosMessageStatesEnum.START_STATE, KerberosMessageStatesEnum.KRB_CRED_STATE, 118, new DecodeKerberosMessage());
      super.transitions[KerberosMessageStatesEnum.START_STATE.ordinal()][126] = new GrammarTransition(KerberosMessageStatesEnum.START_STATE, KerberosMessageStatesEnum.KRB_ERROR_STATE, 126, new DecodeKerberosMessage());
   }

   public static Grammar<KerberosMessageContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new KerberosMessageGrammar();
   }

   private class DecodeKerberosMessage extends GrammarAction<KerberosMessageContainer> {
      private DecodeKerberosMessage() {
      }

      public void action(KerberosMessageContainer kerberosMessageContainer) throws DecoderException {
         ByteBuffer stream = kerberosMessageContainer.getStream();

         try {
            stream.reset();
         } catch (InvalidMarkException var34) {
            stream.rewind();
         }

         TLV tlv = kerberosMessageContainer.getCurrentTLV();
         kerberosMessageContainer.setGrammarEndAllowed(true);
         switch (tlv.getTag()) {
            case 106:
               AsReqContainer asReqContainer = new AsReqContainer(stream);

               try {
                  KerberosMessageGrammar.this.kerberosDecoder.decode(stream, asReqContainer);
               } catch (DecoderException de) {
                  throw de;
               }

               KerberosMessage asReq = asReqContainer.getAsReq();
               kerberosMessageContainer.setMessage(asReq);
               break;
            case 107:
               AsRepContainer asRepContainer = new AsRepContainer(stream);

               try {
                  KerberosMessageGrammar.this.kerberosDecoder.decode(stream, asRepContainer);
               } catch (DecoderException de) {
                  throw de;
               }

               KerberosMessage asRep = asRepContainer.getAsRep();
               kerberosMessageContainer.setMessage(asRep);
               break;
            case 108:
               TgsReqContainer tgsReqContainer = new TgsReqContainer(stream);

               try {
                  KerberosMessageGrammar.this.kerberosDecoder.decode(stream, tgsReqContainer);
               } catch (DecoderException de) {
                  throw de;
               }

               KerberosMessage tgsReq = tgsReqContainer.getTgsReq();
               kerberosMessageContainer.setMessage(tgsReq);
               break;
            case 109:
               TgsRepContainer tgsRepContainer = new TgsRepContainer(stream);

               try {
                  KerberosMessageGrammar.this.kerberosDecoder.decode(stream, tgsRepContainer);
               } catch (DecoderException de) {
                  throw de;
               }

               KerberosMessage tgsRep = tgsRepContainer.getTgsRep();
               kerberosMessageContainer.setMessage(tgsRep);
               break;
            case 110:
               ApReqContainer apReqContainer = new ApReqContainer(stream);

               try {
                  KerberosMessageGrammar.this.kerberosDecoder.decode(stream, apReqContainer);
               } catch (DecoderException de) {
                  throw de;
               }

               KerberosMessage apReq = apReqContainer.getApReq();
               kerberosMessageContainer.setMessage(apReq);
               break;
            case 111:
               ApRepContainer apRepContainer = new ApRepContainer(stream);

               try {
                  KerberosMessageGrammar.this.kerberosDecoder.decode(stream, apRepContainer);
               } catch (DecoderException de) {
                  throw de;
               }

               KerberosMessage apRep = apRepContainer.getApRep();
               kerberosMessageContainer.setMessage(apRep);
            case 112:
            case 113:
            case 114:
            case 115:
            case 119:
            case 120:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            default:
               break;
            case 116:
               KrbSafeContainer krbSafeContainer = new KrbSafeContainer(stream);

               try {
                  KerberosMessageGrammar.this.kerberosDecoder.decode(stream, krbSafeContainer);
               } catch (DecoderException de) {
                  throw de;
               }

               KerberosMessage krbSafe = krbSafeContainer.getKrbSafe();
               kerberosMessageContainer.setMessage(krbSafe);
               break;
            case 117:
               KrbPrivContainer krbPrivContainer = new KrbPrivContainer(stream);

               try {
                  KerberosMessageGrammar.this.kerberosDecoder.decode(stream, krbPrivContainer);
               } catch (DecoderException de) {
                  throw de;
               }

               KerberosMessage krbPriv = krbPrivContainer.getKrbPriv();
               kerberosMessageContainer.setMessage(krbPriv);
               break;
            case 118:
               KrbCredContainer krbCredContainer = new KrbCredContainer(stream);

               try {
                  KerberosMessageGrammar.this.kerberosDecoder.decode(stream, krbCredContainer);
               } catch (DecoderException de) {
                  throw de;
               }

               KerberosMessage krbCred = krbCredContainer.getKrbCred();
               kerberosMessageContainer.setMessage(krbCred);
               break;
            case 126:
               KrbErrorContainer krbErrorContainer = new KrbErrorContainer(stream);

               try {
                  KerberosMessageGrammar.this.kerberosDecoder.decode(stream, krbErrorContainer);
               } catch (DecoderException de) {
                  throw de;
               }

               KerberosMessage krbError = krbErrorContainer.getKrbError();
               kerberosMessageContainer.setMessage(krbError);
         }

         if (KerberosMessageGrammar.IS_DEBUG) {
            KerberosMessageGrammar.LOG.debug("Decoded KerberosMessage {}", kerberosMessageContainer.getMessage());
         }

      }
   }
}
