package org.apache.directory.shared.kerberos.codec.etypeInfo2Entry;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.etypeInfo2Entry.actions.ETypeInfo2EntryInit;
import org.apache.directory.shared.kerberos.codec.etypeInfo2Entry.actions.StoreEType;
import org.apache.directory.shared.kerberos.codec.etypeInfo2Entry.actions.StoreS2KParams;
import org.apache.directory.shared.kerberos.codec.etypeInfo2Entry.actions.StoreSalt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ETypeInfo2EntryGrammar extends AbstractGrammar<ETypeInfo2EntryContainer> {
   static final Logger LOG = LoggerFactory.getLogger(ETypeInfo2EntryGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<ETypeInfo2EntryContainer> instance;

   private ETypeInfo2EntryGrammar() {
      this.setName(ETypeInfo2EntryGrammar.class.getName());
      super.transitions = new GrammarTransition[ETypeInfo2EntryStatesEnum.LAST_ETYPE_INFO2_ENTRY_STATE.ordinal()][256];
      super.transitions[ETypeInfo2EntryStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(ETypeInfo2EntryStatesEnum.START_STATE, ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_SEQ_STATE, UniversalTag.SEQUENCE, new ETypeInfo2EntryInit());
      super.transitions[ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_SEQ_STATE.ordinal()][160] = new GrammarTransition(ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_SEQ_STATE, ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_ETYPE_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_ETYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_ETYPE_TAG_STATE, ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_ETYPE_STATE, UniversalTag.INTEGER, new StoreEType());
      super.transitions[ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_ETYPE_STATE.ordinal()][161] = new GrammarTransition(ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_ETYPE_STATE, ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_SALT_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_ETYPE_STATE.ordinal()][162] = new GrammarTransition(ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_ETYPE_STATE, ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_S2KPARAMS_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_SALT_TAG_STATE.ordinal()][UniversalTag.GENERAL_STRING.getValue()] = new GrammarTransition(ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_SALT_TAG_STATE, ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_SALT_STATE, UniversalTag.GENERAL_STRING, new StoreSalt());
      super.transitions[ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_SALT_STATE.ordinal()][162] = new GrammarTransition(ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_SALT_STATE, ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_S2KPARAMS_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_S2KPARAMS_TAG_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_S2KPARAMS_TAG_STATE, ETypeInfo2EntryStatesEnum.ETYPE_INFO2_ENTRY_S2KPARAMS_STATE, UniversalTag.OCTET_STRING, new StoreS2KParams());
   }

   public static Grammar<ETypeInfo2EntryContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new ETypeInfo2EntryGrammar();
   }
}
