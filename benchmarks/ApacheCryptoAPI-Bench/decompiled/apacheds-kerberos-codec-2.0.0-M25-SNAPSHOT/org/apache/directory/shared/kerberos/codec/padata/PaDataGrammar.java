package org.apache.directory.shared.kerberos.codec.padata;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.padata.actions.PaDataInit;
import org.apache.directory.shared.kerberos.codec.padata.actions.StoreDataType;
import org.apache.directory.shared.kerberos.codec.padata.actions.StorePaDataValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PaDataGrammar extends AbstractGrammar<PaDataContainer> {
   static final Logger LOG = LoggerFactory.getLogger(PaDataGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<PaDataContainer> instance;

   private PaDataGrammar() {
      this.setName(PaDataGrammar.class.getName());
      super.transitions = new GrammarTransition[PaDataStatesEnum.LAST_PADATA_STATE.ordinal()][256];
      super.transitions[PaDataStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(PaDataStatesEnum.START_STATE, PaDataStatesEnum.PADATA_SEQ_STATE, UniversalTag.SEQUENCE, new PaDataInit());
      super.transitions[PaDataStatesEnum.PADATA_SEQ_STATE.ordinal()][161] = new GrammarTransition(PaDataStatesEnum.PADATA_SEQ_STATE, PaDataStatesEnum.PADATA_TYPE_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[PaDataStatesEnum.PADATA_TYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(PaDataStatesEnum.PADATA_TYPE_TAG_STATE, PaDataStatesEnum.PADATA_TYPE_STATE, UniversalTag.INTEGER, new StoreDataType());
      super.transitions[PaDataStatesEnum.PADATA_TYPE_STATE.ordinal()][162] = new GrammarTransition(PaDataStatesEnum.PADATA_TYPE_STATE, PaDataStatesEnum.PADATA_VALUE_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[PaDataStatesEnum.PADATA_VALUE_TAG_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(PaDataStatesEnum.PADATA_VALUE_TAG_STATE, PaDataStatesEnum.PADATA_VALUE_STATE, UniversalTag.OCTET_STRING, new StorePaDataValue());
   }

   public static Grammar<PaDataContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new PaDataGrammar();
   }
}
