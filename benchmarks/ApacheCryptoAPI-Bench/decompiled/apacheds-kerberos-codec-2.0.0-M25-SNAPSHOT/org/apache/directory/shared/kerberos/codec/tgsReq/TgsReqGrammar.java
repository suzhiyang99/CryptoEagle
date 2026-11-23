package org.apache.directory.shared.kerberos.codec.tgsReq;

import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.kerberos.codec.tgsReq.actions.StoreKdcReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TgsReqGrammar extends AbstractGrammar<TgsReqContainer> {
   static final Logger LOG = LoggerFactory.getLogger(TgsReqGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<TgsReqContainer> instance;

   private TgsReqGrammar() {
      this.setName(TgsReqGrammar.class.getName());
      super.transitions = new GrammarTransition[TgsReqStatesEnum.LAST_TGS_REQ_STATE.ordinal()][256];
      super.transitions[TgsReqStatesEnum.START_STATE.ordinal()][108] = new GrammarTransition(TgsReqStatesEnum.START_STATE, TgsReqStatesEnum.TGS_REQ_STATE, 108, new StoreKdcReq());
   }

   public static Grammar<TgsReqContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new TgsReqGrammar();
   }
}
