package org.apache.directory.shared.kerberos.codec.asReq;

import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.kerberos.codec.asReq.actions.StoreKdcReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AsReqGrammar extends AbstractGrammar<AsReqContainer> {
   static final Logger LOG = LoggerFactory.getLogger(AsReqGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<AsReqContainer> instance;

   private AsReqGrammar() {
      this.setName(AsReqGrammar.class.getName());
      super.transitions = new GrammarTransition[AsReqStatesEnum.LAST_AS_REQ_STATE.ordinal()][256];
      super.transitions[AsReqStatesEnum.START_STATE.ordinal()][106] = new GrammarTransition(AsReqStatesEnum.START_STATE, AsReqStatesEnum.AS_REQ_STATE, 106, new StoreKdcReq());
   }

   public static Grammar<AsReqContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new AsReqGrammar();
   }
}
