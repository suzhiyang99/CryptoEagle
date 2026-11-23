package org.apache.directory.shared.kerberos.codec.etypeInfo;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.etypeInfo.actions.AddETypeInfoEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ETypeInfoGrammar extends AbstractGrammar<ETypeInfoContainer> {
   static final Logger LOG = LoggerFactory.getLogger(ETypeInfoGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<ETypeInfoContainer> instance;

   private ETypeInfoGrammar() {
      this.setName(ETypeInfoGrammar.class.getName());
      super.transitions = new GrammarTransition[ETypeInfoStatesEnum.LAST_ETYPE_INFO_STATE.ordinal()][256];
      super.transitions[ETypeInfoStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(ETypeInfoStatesEnum.START_STATE, ETypeInfoStatesEnum.ETYPE_INFO_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[ETypeInfoStatesEnum.ETYPE_INFO_SEQ_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(ETypeInfoStatesEnum.ETYPE_INFO_SEQ_STATE, ETypeInfoStatesEnum.ETYPE_INFO_SEQ_STATE, UniversalTag.SEQUENCE, new AddETypeInfoEntry());
   }

   public static Grammar<ETypeInfoContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new ETypeInfoGrammar();
   }
}
