package org.apache.directory.shared.kerberos.codec.encTgsRepPart;

import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.kerberos.codec.encTgsRepPart.actions.StoreEncTgsRepPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EncTgsRepPartGrammar extends AbstractGrammar<EncTgsRepPartContainer> {
   static final Logger LOG = LoggerFactory.getLogger(EncTgsRepPartGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<EncTgsRepPartContainer> instance;

   private EncTgsRepPartGrammar() {
      this.setName(EncTgsRepPartGrammar.class.getName());
      super.transitions = new GrammarTransition[EncTgsRepPartStatesEnum.LAST_ENC_TGS_REP_PART_STATE.ordinal()][256];
      super.transitions[EncTgsRepPartStatesEnum.START_STATE.ordinal()][122] = new GrammarTransition(EncTgsRepPartStatesEnum.START_STATE, EncTgsRepPartStatesEnum.ENC_TGS_REP_PART_STATE, 122, new StoreEncTgsRepPart());
   }

   public static Grammar<EncTgsRepPartContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new EncTgsRepPartGrammar();
   }
}
