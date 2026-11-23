package org.apache.directory.shared.kerberos.codec.hostAddress;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.hostAddress.actions.HostAddressInit;
import org.apache.directory.shared.kerberos.codec.hostAddress.actions.StoreAddrType;
import org.apache.directory.shared.kerberos.codec.hostAddress.actions.StoreAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HostAddressGrammar extends AbstractGrammar<HostAddressContainer> {
   static final Logger LOG = LoggerFactory.getLogger(HostAddressGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<HostAddressContainer> instance;

   private HostAddressGrammar() {
      this.setName(HostAddressGrammar.class.getName());
      super.transitions = new GrammarTransition[HostAddressStatesEnum.LAST_HOST_ADDRESS_STATE.ordinal()][256];
      super.transitions[HostAddressStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(HostAddressStatesEnum.START_STATE, HostAddressStatesEnum.HOST_ADDRESS_SEQ_STATE, UniversalTag.SEQUENCE, new HostAddressInit());
      super.transitions[HostAddressStatesEnum.HOST_ADDRESS_SEQ_STATE.ordinal()][160] = new GrammarTransition(HostAddressStatesEnum.HOST_ADDRESS_SEQ_STATE, HostAddressStatesEnum.HOST_ADDRESS_ADDR_TYPE_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[HostAddressStatesEnum.HOST_ADDRESS_ADDR_TYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(HostAddressStatesEnum.HOST_ADDRESS_ADDR_TYPE_TAG_STATE, HostAddressStatesEnum.HOST_ADDRESS_ADDR_TYPE_STATE, UniversalTag.INTEGER, new StoreAddrType());
      super.transitions[HostAddressStatesEnum.HOST_ADDRESS_ADDR_TYPE_STATE.ordinal()][161] = new GrammarTransition(HostAddressStatesEnum.HOST_ADDRESS_ADDR_TYPE_STATE, HostAddressStatesEnum.HOST_ADDRESS_ADDRESS_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[HostAddressStatesEnum.HOST_ADDRESS_ADDRESS_TAG_STATE.ordinal()][UniversalTag.OCTET_STRING.ordinal()] = new GrammarTransition(HostAddressStatesEnum.HOST_ADDRESS_ADDRESS_TAG_STATE, HostAddressStatesEnum.HOST_ADDRESS_ADDRESS_STATE, UniversalTag.OCTET_STRING, new StoreAddress());
   }

   public static Grammar<HostAddressContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new HostAddressGrammar();
   }
}
