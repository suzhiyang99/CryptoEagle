package org.apache.directory.shared.kerberos.codec.typedData;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.typedData.actions.StoreDataValue;
import org.apache.directory.shared.kerberos.codec.typedData.actions.StoreTdType;
import org.apache.directory.shared.kerberos.codec.typedData.actions.TypedDataInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TypedDataGrammar extends AbstractGrammar<TypedDataContainer> {
   static final Logger LOG = LoggerFactory.getLogger(TypedDataGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<TypedDataContainer> instance;

   private TypedDataGrammar() {
      this.setName(TypedDataGrammar.class.getName());
      super.transitions = new GrammarTransition[TypedDataStatesEnum.LAST_TYPED_DATA_STATE.ordinal()][256];
      super.transitions[TypedDataStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(TypedDataStatesEnum.START_STATE, TypedDataStatesEnum.TYPED_DATA_SEQ_SEQ_STATE, UniversalTag.SEQUENCE, new TypedDataInit());
      super.transitions[TypedDataStatesEnum.TYPED_DATA_SEQ_SEQ_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(TypedDataStatesEnum.TYPED_DATA_SEQ_SEQ_STATE, TypedDataStatesEnum.TYPED_DATA_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[TypedDataStatesEnum.TYPED_DATA_SEQ_STATE.ordinal()][160] = new GrammarTransition(TypedDataStatesEnum.TYPED_DATA_SEQ_STATE, TypedDataStatesEnum.TYPED_DATA_TDTYPE_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[TypedDataStatesEnum.TYPED_DATA_TDTYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(TypedDataStatesEnum.TYPED_DATA_TDTYPE_TAG_STATE, TypedDataStatesEnum.TYPED_DATA_TDTYPE_STATE, UniversalTag.INTEGER, new StoreTdType());
      super.transitions[TypedDataStatesEnum.TYPED_DATA_TDTYPE_STATE.ordinal()][161] = new GrammarTransition(TypedDataStatesEnum.TYPED_DATA_TDTYPE_STATE, TypedDataStatesEnum.TYPED_DATA_TDDATA_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[TypedDataStatesEnum.TYPED_DATA_TDDATA_TAG_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(TypedDataStatesEnum.TYPED_DATA_TDDATA_TAG_STATE, TypedDataStatesEnum.TYPED_DATA_TDDATA_STATE, UniversalTag.OCTET_STRING, new StoreDataValue());
      super.transitions[TypedDataStatesEnum.TYPED_DATA_TDDATA_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(TypedDataStatesEnum.TYPED_DATA_TDDATA_STATE, TypedDataStatesEnum.TYPED_DATA_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
   }

   public static Grammar<TypedDataContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new TypedDataGrammar();
   }
}
