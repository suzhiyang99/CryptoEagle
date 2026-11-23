package org.apache.directory.shared.kerberos.codec.etypeInfoEntry;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.etypeInfoEntry.actions.ETypeInfoEntryInit;
import org.apache.directory.shared.kerberos.codec.etypeInfoEntry.actions.StoreEType;
import org.apache.directory.shared.kerberos.codec.etypeInfoEntry.actions.StoreSalt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ETypeInfoEntryGrammar extends AbstractGrammar<ETypeInfoEntryContainer> {
   static final Logger LOG = LoggerFactory.getLogger(ETypeInfoEntryGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<ETypeInfoEntryContainer> instance;

   private ETypeInfoEntryGrammar() {
      this.setName(ETypeInfoEntryGrammar.class.getName());
      super.transitions = new GrammarTransition[ETypeInfoEntryStatesEnum.LAST_ETYPE_INFO_ENTRY_STATE.ordinal()][256];
      super.transitions[ETypeInfoEntryStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(ETypeInfoEntryStatesEnum.START_STATE, ETypeInfoEntryStatesEnum.ETYPE_INFO_ENTRY_SEQ_STATE, UniversalTag.SEQUENCE, new ETypeInfoEntryInit());
      super.transitions[ETypeInfoEntryStatesEnum.ETYPE_INFO_ENTRY_SEQ_STATE.ordinal()][160] = new GrammarTransition(ETypeInfoEntryStatesEnum.ETYPE_INFO_ENTRY_SEQ_STATE, ETypeInfoEntryStatesEnum.ETYPE_INFO_ENTRY_ETYPE_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[ETypeInfoEntryStatesEnum.ETYPE_INFO_ENTRY_ETYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(ETypeInfoEntryStatesEnum.ETYPE_INFO_ENTRY_ETYPE_TAG_STATE, ETypeInfoEntryStatesEnum.ETYPE_INFO_ENTRY_ETYPE_STATE, UniversalTag.INTEGER, new StoreEType());
      super.transitions[ETypeInfoEntryStatesEnum.ETYPE_INFO_ENTRY_ETYPE_STATE.ordinal()][161] = new GrammarTransition(ETypeInfoEntryStatesEnum.ETYPE_INFO_ENTRY_ETYPE_STATE, ETypeInfoEntryStatesEnum.ETYPE_INFO_ENTRY_SALT_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[ETypeInfoEntryStatesEnum.ETYPE_INFO_ENTRY_SALT_TAG_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(ETypeInfoEntryStatesEnum.ETYPE_INFO_ENTRY_SALT_TAG_STATE, ETypeInfoEntryStatesEnum.ETYPE_INFO_ENTRY_SALT_STATE, UniversalTag.OCTET_STRING, new StoreSalt());
   }

   public static Grammar<ETypeInfoEntryContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new ETypeInfoEntryGrammar();
   }
}
