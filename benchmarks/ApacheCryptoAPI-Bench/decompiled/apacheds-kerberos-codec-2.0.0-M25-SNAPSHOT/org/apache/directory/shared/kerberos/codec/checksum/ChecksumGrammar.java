package org.apache.directory.shared.kerberos.codec.checksum;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.checksum.actions.ChecksumInit;
import org.apache.directory.shared.kerberos.codec.checksum.actions.StoreChecksum;
import org.apache.directory.shared.kerberos.codec.checksum.actions.StoreCksumType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChecksumGrammar extends AbstractGrammar<ChecksumContainer> {
   static final Logger LOG = LoggerFactory.getLogger(ChecksumGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<ChecksumContainer> instance;

   private ChecksumGrammar() {
      this.setName(ChecksumGrammar.class.getName());
      super.transitions = new GrammarTransition[ChecksumStatesEnum.LAST_CHECKSUM_STATE.ordinal()][256];
      super.transitions[ChecksumStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(ChecksumStatesEnum.START_STATE, ChecksumStatesEnum.CHECKSUM_SEQ_STATE, UniversalTag.SEQUENCE, new ChecksumInit());
      super.transitions[ChecksumStatesEnum.CHECKSUM_SEQ_STATE.ordinal()][160] = new GrammarTransition(ChecksumStatesEnum.CHECKSUM_SEQ_STATE, ChecksumStatesEnum.CHECKSUM_TYPE_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[ChecksumStatesEnum.CHECKSUM_TYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(ChecksumStatesEnum.CHECKSUM_TYPE_TAG_STATE, ChecksumStatesEnum.CHECKSUM_TYPE_STATE, UniversalTag.INTEGER, new StoreCksumType());
      super.transitions[ChecksumStatesEnum.CHECKSUM_TYPE_STATE.ordinal()][161] = new GrammarTransition(ChecksumStatesEnum.CHECKSUM_TYPE_STATE, ChecksumStatesEnum.CHECKSUM_CHECKSUM_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[ChecksumStatesEnum.CHECKSUM_CHECKSUM_TAG_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(ChecksumStatesEnum.CHECKSUM_CHECKSUM_TAG_STATE, ChecksumStatesEnum.CHECKSUM_CHECKSUM_STATE, UniversalTag.OCTET_STRING, new StoreChecksum());
   }

   public static Grammar<ChecksumContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new ChecksumGrammar();
   }
}
