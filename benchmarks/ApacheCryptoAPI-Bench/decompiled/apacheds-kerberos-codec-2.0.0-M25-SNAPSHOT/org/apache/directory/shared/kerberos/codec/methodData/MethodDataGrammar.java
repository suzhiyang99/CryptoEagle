package org.apache.directory.shared.kerberos.codec.methodData;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.methodData.actions.AddPaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MethodDataGrammar extends AbstractGrammar<MethodDataContainer> {
   static final Logger LOG = LoggerFactory.getLogger(MethodDataGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<MethodDataContainer> instance;

   private MethodDataGrammar() {
      this.setName(MethodDataGrammar.class.getName());
      super.transitions = new GrammarTransition[MethodDataStatesEnum.LAST_METHOD_DATA_STATE.ordinal()][256];
      super.transitions[MethodDataStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(MethodDataStatesEnum.START_STATE, MethodDataStatesEnum.METHOD_DATA_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[MethodDataStatesEnum.METHOD_DATA_SEQ_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(MethodDataStatesEnum.METHOD_DATA_SEQ_STATE, MethodDataStatesEnum.METHOD_DATA_SEQ_STATE, UniversalTag.SEQUENCE, new AddPaData());
   }

   public static Grammar<MethodDataContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new MethodDataGrammar();
   }
}
