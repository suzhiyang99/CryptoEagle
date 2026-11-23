package org.apache.directory.shared.kerberos.codec.asRep;

import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.kerberos.codec.asRep.actions.StoreKdcRep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AsRepGrammar extends AbstractGrammar<AsRepContainer> {
   static final Logger LOG = LoggerFactory.getLogger(AsRepGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<AsRepContainer> instance;

   private AsRepGrammar() {
      this.setName(AsRepGrammar.class.getName());
      super.transitions = new GrammarTransition[AsRepStatesEnum.LAST_AS_REP_STATE.ordinal()][256];
      super.transitions[AsRepStatesEnum.START_STATE.ordinal()][107] = new GrammarTransition(AsRepStatesEnum.START_STATE, AsRepStatesEnum.AS_REP_STATE, 107, new StoreKdcRep());
   }

   public static Grammar<AsRepContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new AsRepGrammar();
   }
}
