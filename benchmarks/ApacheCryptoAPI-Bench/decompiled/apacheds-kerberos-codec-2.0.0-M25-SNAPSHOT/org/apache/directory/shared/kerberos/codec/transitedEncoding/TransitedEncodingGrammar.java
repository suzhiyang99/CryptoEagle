package org.apache.directory.shared.kerberos.codec.transitedEncoding;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.transitedEncoding.actions.StoreContents;
import org.apache.directory.shared.kerberos.codec.transitedEncoding.actions.StoreTrType;
import org.apache.directory.shared.kerberos.codec.transitedEncoding.actions.TransitedEncodingInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TransitedEncodingGrammar extends AbstractGrammar<TransitedEncodingContainer> {
   static final Logger LOG = LoggerFactory.getLogger(TransitedEncodingGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<TransitedEncodingContainer> instance;

   private TransitedEncodingGrammar() {
      this.setName(TransitedEncodingGrammar.class.getName());
      super.transitions = new GrammarTransition[TransitedEncodingStatesEnum.LAST_TRANSITED_ENCODING_STATE.ordinal()][256];
      super.transitions[TransitedEncodingStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(TransitedEncodingStatesEnum.START_STATE, TransitedEncodingStatesEnum.TRANSITED_ENCODING_SEQ_STATE, UniversalTag.SEQUENCE, new TransitedEncodingInit());
      super.transitions[TransitedEncodingStatesEnum.TRANSITED_ENCODING_SEQ_STATE.ordinal()][160] = new GrammarTransition(TransitedEncodingStatesEnum.TRANSITED_ENCODING_SEQ_STATE, TransitedEncodingStatesEnum.TRANSITED_ENCODING_TR_TYPE_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[TransitedEncodingStatesEnum.TRANSITED_ENCODING_TR_TYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(TransitedEncodingStatesEnum.TRANSITED_ENCODING_TR_TYPE_TAG_STATE, TransitedEncodingStatesEnum.TRANSITED_ENCODING_TR_TYPE_STATE, UniversalTag.INTEGER, new StoreTrType());
      super.transitions[TransitedEncodingStatesEnum.TRANSITED_ENCODING_TR_TYPE_STATE.ordinal()][161] = new GrammarTransition(TransitedEncodingStatesEnum.TRANSITED_ENCODING_TR_TYPE_STATE, TransitedEncodingStatesEnum.TRANSITED_ENCODING_CONTENTS_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[TransitedEncodingStatesEnum.TRANSITED_ENCODING_CONTENTS_TAG_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(TransitedEncodingStatesEnum.TRANSITED_ENCODING_CONTENTS_TAG_STATE, TransitedEncodingStatesEnum.TRANSITED_ENCODING_CONTENTS_STATE, UniversalTag.OCTET_STRING, new StoreContents());
   }

   public static Grammar<TransitedEncodingContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new TransitedEncodingGrammar();
   }
}
