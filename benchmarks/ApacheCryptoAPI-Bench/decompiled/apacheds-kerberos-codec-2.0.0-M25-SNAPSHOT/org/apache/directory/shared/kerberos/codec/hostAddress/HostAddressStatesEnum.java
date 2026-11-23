package org.apache.directory.shared.kerberos.codec.hostAddress;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum HostAddressStatesEnum implements States {
   START_STATE,
   HOST_ADDRESS_SEQ_STATE,
   HOST_ADDRESS_ADDR_TYPE_TAG_STATE,
   HOST_ADDRESS_ADDR_TYPE_STATE,
   HOST_ADDRESS_ADDRESS_TAG_STATE,
   HOST_ADDRESS_ADDRESS_STATE,
   LAST_HOST_ADDRESS_STATE;

   public String getGrammarName(int grammar) {
      return "HOST_ADDRESS_GRAMMAR";
   }

   public String getGrammarName(Grammar<HostAddressContainer> grammar) {
      return grammar instanceof HostAddressGrammar ? "HOST_ADDRESS_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_HOST_ADDRESS_STATE.ordinal() ? "HOST_ADDRESS_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_HOST_ADDRESS_STATE;
   }

   public HostAddressStatesEnum getStartState() {
      return START_STATE;
   }
}
