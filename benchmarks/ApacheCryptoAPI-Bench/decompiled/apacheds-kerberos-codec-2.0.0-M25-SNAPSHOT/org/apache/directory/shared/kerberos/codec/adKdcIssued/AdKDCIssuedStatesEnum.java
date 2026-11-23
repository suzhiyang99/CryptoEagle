package org.apache.directory.shared.kerberos.codec.adKdcIssued;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum AdKDCIssuedStatesEnum implements States {
   START_STATE,
   AD_KDC_ISSUED_SEQ_STATE,
   AD_KDC_ISSUED_AD_CHECKSUM_TAG_STATE,
   AD_KDC_ISSUED_I_REALM_TAG_STATE,
   AD_KDC_ISSUED_I_REALM_STATE,
   AD_KDC_ISSUED_I_SNAME_TAG_STATE,
   AD_KDC_ISSUED_ELEMENTS_TAG_STATE,
   LAST_AD_KDC_ISSUED_STATE;

   public String getGrammarName(int grammar) {
      return "AD_KDC_ISSUED_GRAMMAR";
   }

   public String getGrammarName(Grammar<AdKdcIssuedContainer> grammar) {
      return grammar instanceof AdKDCIssuedGrammar ? "AD_KDC_ISSUED_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_AD_KDC_ISSUED_STATE.ordinal() ? "AD_KDC_ISSUED_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_AD_KDC_ISSUED_STATE;
   }

   public AdKDCIssuedStatesEnum getStartState() {
      return START_STATE;
   }
}
