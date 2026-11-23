package org.apache.directory.shared.kerberos.codec.etypeInfo2;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.etypeInfo2.actions.AddETypeInfo2Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ETypeInfo2Grammar extends AbstractGrammar<ETypeInfo2Container> {
   static final Logger LOG = LoggerFactory.getLogger(ETypeInfo2Grammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<ETypeInfo2Container> instance;

   private ETypeInfo2Grammar() {
      this.setName(ETypeInfo2Grammar.class.getName());
      super.transitions = new GrammarTransition[ETypeInfo2StatesEnum.LAST_ETYPE_INFO2_STATE.ordinal()][256];
      super.transitions[ETypeInfo2StatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(ETypeInfo2StatesEnum.START_STATE, ETypeInfo2StatesEnum.ETYPE_INFO2_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[ETypeInfo2StatesEnum.ETYPE_INFO2_SEQ_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(ETypeInfo2StatesEnum.ETYPE_INFO2_SEQ_STATE, ETypeInfo2StatesEnum.ETYPE_INFO2_SEQ_STATE, UniversalTag.SEQUENCE, new AddETypeInfo2Entry());
   }

   public static Grammar<ETypeInfo2Container> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new ETypeInfo2Grammar();
   }
}
