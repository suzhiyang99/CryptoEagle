package org.apache.directory.shared.kerberos.codec.tgsRep;

import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.kerberos.codec.tgsRep.actions.StoreKdcRep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TgsRepGrammar extends AbstractGrammar<TgsRepContainer> {
   static final Logger LOG = LoggerFactory.getLogger(TgsRepGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<TgsRepContainer> instance;

   private TgsRepGrammar() {
      this.setName(TgsRepGrammar.class.getName());
      super.transitions = new GrammarTransition[TgsRepStatesEnum.LAST_TGS_REP_STATE.ordinal()][256];
      super.transitions[TgsRepStatesEnum.START_STATE.ordinal()][109] = new GrammarTransition(TgsRepStatesEnum.START_STATE, TgsRepStatesEnum.TGS_REP_STATE, 109, new StoreKdcRep());
   }

   public static Grammar<TgsRepContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new TgsRepGrammar();
   }
}
