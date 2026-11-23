package org.apache.directory.shared.kerberos.codec.encAsRepPart;

import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.kerberos.codec.encAsRepPart.actions.StoreEncAsRepPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EncAsRepPartGrammar extends AbstractGrammar<EncAsRepPartContainer> {
   static final Logger LOG = LoggerFactory.getLogger(EncAsRepPartGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<EncAsRepPartContainer> instance;

   private EncAsRepPartGrammar() {
      this.setName(EncAsRepPartGrammar.class.getName());
      super.transitions = new GrammarTransition[EncAsRepPartStatesEnum.LAST_ENC_AS_REP_PART_STATE.ordinal()][256];
      super.transitions[EncAsRepPartStatesEnum.START_STATE.ordinal()][121] = new GrammarTransition(EncAsRepPartStatesEnum.START_STATE, EncAsRepPartStatesEnum.ENC_AS_REP_PART_STATE, 121, new StoreEncAsRepPart());
   }

   public static Grammar<EncAsRepPartContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new EncAsRepPartGrammar();
   }
}
