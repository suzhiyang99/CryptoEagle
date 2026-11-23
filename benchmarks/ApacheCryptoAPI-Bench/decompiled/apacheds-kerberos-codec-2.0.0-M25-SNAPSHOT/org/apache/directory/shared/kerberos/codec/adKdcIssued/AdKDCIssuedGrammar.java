package org.apache.directory.shared.kerberos.codec.adKdcIssued;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.adKdcIssued.actions.AdKdcIssuedInit;
import org.apache.directory.shared.kerberos.codec.adKdcIssued.actions.StoreChecksum;
import org.apache.directory.shared.kerberos.codec.adKdcIssued.actions.StoreElements;
import org.apache.directory.shared.kerberos.codec.adKdcIssued.actions.StoreIRealm;
import org.apache.directory.shared.kerberos.codec.adKdcIssued.actions.StoreISName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AdKDCIssuedGrammar extends AbstractGrammar<AdKdcIssuedContainer> {
   static final Logger LOG = LoggerFactory.getLogger(AdKDCIssuedGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<AdKdcIssuedContainer> instance;

   private AdKDCIssuedGrammar() {
      this.setName(AdKDCIssuedGrammar.class.getName());
      super.transitions = new GrammarTransition[AdKDCIssuedStatesEnum.LAST_AD_KDC_ISSUED_STATE.ordinal()][256];
      super.transitions[AdKDCIssuedStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(AdKDCIssuedStatesEnum.START_STATE, AdKDCIssuedStatesEnum.AD_KDC_ISSUED_SEQ_STATE, UniversalTag.SEQUENCE, new AdKdcIssuedInit());
      super.transitions[AdKDCIssuedStatesEnum.AD_KDC_ISSUED_SEQ_STATE.ordinal()][160] = new GrammarTransition(AdKDCIssuedStatesEnum.AD_KDC_ISSUED_SEQ_STATE, AdKDCIssuedStatesEnum.AD_KDC_ISSUED_AD_CHECKSUM_TAG_STATE, 160, new StoreChecksum());
      super.transitions[AdKDCIssuedStatesEnum.AD_KDC_ISSUED_AD_CHECKSUM_TAG_STATE.ordinal()][161] = new GrammarTransition(AdKDCIssuedStatesEnum.AD_KDC_ISSUED_AD_CHECKSUM_TAG_STATE, AdKDCIssuedStatesEnum.AD_KDC_ISSUED_I_REALM_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[AdKDCIssuedStatesEnum.AD_KDC_ISSUED_AD_CHECKSUM_TAG_STATE.ordinal()][162] = new GrammarTransition(AdKDCIssuedStatesEnum.AD_KDC_ISSUED_AD_CHECKSUM_TAG_STATE, AdKDCIssuedStatesEnum.AD_KDC_ISSUED_I_SNAME_TAG_STATE, 162, new StoreISName());
      super.transitions[AdKDCIssuedStatesEnum.AD_KDC_ISSUED_AD_CHECKSUM_TAG_STATE.ordinal()][163] = new GrammarTransition(AdKDCIssuedStatesEnum.AD_KDC_ISSUED_AD_CHECKSUM_TAG_STATE, AdKDCIssuedStatesEnum.AD_KDC_ISSUED_ELEMENTS_TAG_STATE, 163, new StoreElements());
      super.transitions[AdKDCIssuedStatesEnum.AD_KDC_ISSUED_I_REALM_TAG_STATE.ordinal()][UniversalTag.GENERAL_STRING.getValue()] = new GrammarTransition(AdKDCIssuedStatesEnum.AD_KDC_ISSUED_I_REALM_TAG_STATE, AdKDCIssuedStatesEnum.AD_KDC_ISSUED_I_REALM_STATE, UniversalTag.GENERAL_STRING, new StoreIRealm());
      super.transitions[AdKDCIssuedStatesEnum.AD_KDC_ISSUED_I_REALM_STATE.ordinal()][162] = new GrammarTransition(AdKDCIssuedStatesEnum.AD_KDC_ISSUED_I_REALM_STATE, AdKDCIssuedStatesEnum.AD_KDC_ISSUED_I_SNAME_TAG_STATE, 162, new StoreISName());
      super.transitions[AdKDCIssuedStatesEnum.AD_KDC_ISSUED_I_REALM_STATE.ordinal()][163] = new GrammarTransition(AdKDCIssuedStatesEnum.AD_KDC_ISSUED_I_REALM_STATE, AdKDCIssuedStatesEnum.AD_KDC_ISSUED_ELEMENTS_TAG_STATE, 163, new StoreElements());
      super.transitions[AdKDCIssuedStatesEnum.AD_KDC_ISSUED_I_SNAME_TAG_STATE.ordinal()][163] = new GrammarTransition(AdKDCIssuedStatesEnum.AD_KDC_ISSUED_I_SNAME_TAG_STATE, AdKDCIssuedStatesEnum.AD_KDC_ISSUED_ELEMENTS_TAG_STATE, 163, new StoreElements());
   }

   public static Grammar<AdKdcIssuedContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new AdKDCIssuedGrammar();
   }
}
