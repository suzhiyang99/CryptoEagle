package org.apache.directory.shared.kerberos.codec.hostAddresses;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.hostAddresses.actions.AddHostAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HostAddressesGrammar extends AbstractGrammar<HostAddressesContainer> {
   static final Logger LOG = LoggerFactory.getLogger(HostAddressesGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<HostAddressesContainer> instance;

   private HostAddressesGrammar() {
      this.setName(HostAddressesGrammar.class.getName());
      super.transitions = new GrammarTransition[HostAddressesStatesEnum.LAST_HOST_ADDRESSES_STATE.ordinal()][256];
      super.transitions[HostAddressesStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(HostAddressesStatesEnum.START_STATE, HostAddressesStatesEnum.HOST_ADDRESSES_ADDRESS_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[HostAddressesStatesEnum.HOST_ADDRESSES_ADDRESS_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(HostAddressesStatesEnum.HOST_ADDRESSES_ADDRESS_STATE, HostAddressesStatesEnum.HOST_ADDRESSES_ADDRESS_STATE, UniversalTag.SEQUENCE, new AddHostAddress());
   }

   public static Grammar<HostAddressesContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new HostAddressesGrammar();
   }
}
