package org.apache.directory.shared.kerberos.codec.hostAddresses;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum HostAddressesStatesEnum implements States {
   START_STATE,
   HOST_ADDRESSES_SEQ_STATE,
   HOST_ADDRESSES_ADDRESS_STATE,
   LAST_HOST_ADDRESSES_STATE;

   public String getGrammarName(int grammar) {
      return "HOST_ADDRESSES_GRAMMAR";
   }

   public String getGrammarName(Grammar<HostAddressesContainer> grammar) {
      return grammar instanceof HostAddressesGrammar ? "HOST_ADDRESSES_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_HOST_ADDRESSES_STATE.ordinal() ? "HOST_ADDRESSES_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_HOST_ADDRESSES_STATE;
   }

   public HostAddressesStatesEnum getStartState() {
      return START_STATE;
   }
}
