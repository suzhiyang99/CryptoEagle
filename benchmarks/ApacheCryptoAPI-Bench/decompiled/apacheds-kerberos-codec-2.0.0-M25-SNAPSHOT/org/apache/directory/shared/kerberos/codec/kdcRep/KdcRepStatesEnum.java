package org.apache.directory.shared.kerberos.codec.kdcRep;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum KdcRepStatesEnum implements States {
   START_STATE,
   KDC_REP_SEQ_STATE,
   KDC_REP_PVNO_TAG_STATE,
   KDC_REP_PVNO_STATE,
   KDC_REP_MSG_TYPE_TAG_STATE,
   KDC_REP_MSG_TYPE_STATE,
   KDC_REP_PA_DATA_TAG_STATE,
   KDC_REP_PA_DATA_STATE,
   KDC_REP_CREALM_TAG_STATE,
   KDC_REP_CREALM_STATE,
   KDC_REP_CNAME_STATE,
   KDC_REP_TICKET_STATE,
   KDC_REP_ENC_PART_STATE,
   LAST_KDC_REP_STATE;

   public String getGrammarName(int grammar) {
      return "KDC_REP_GRAMMAR";
   }

   public String getGrammarName(Grammar<KdcRepContainer> grammar) {
      return grammar instanceof KdcRepGrammar ? "KDC_REP_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_KDC_REP_STATE.ordinal() ? "KDC_REP_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_KDC_REP_STATE;
   }

   public KdcRepStatesEnum getStartState() {
      return START_STATE;
   }
}
