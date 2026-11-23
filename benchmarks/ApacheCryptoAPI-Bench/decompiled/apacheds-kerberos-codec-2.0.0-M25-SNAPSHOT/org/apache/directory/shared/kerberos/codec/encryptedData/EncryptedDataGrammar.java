package org.apache.directory.shared.kerberos.codec.encryptedData;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.encryptedData.actions.EncryptedDataInit;
import org.apache.directory.shared.kerberos.codec.encryptedData.actions.StoreCipher;
import org.apache.directory.shared.kerberos.codec.encryptedData.actions.StoreEType;
import org.apache.directory.shared.kerberos.codec.encryptedData.actions.StoreKvno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EncryptedDataGrammar extends AbstractGrammar<EncryptedDataContainer> {
   static final Logger LOG = LoggerFactory.getLogger(EncryptedDataGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<EncryptedDataContainer> instance;

   private EncryptedDataGrammar() {
      this.setName(EncryptedDataGrammar.class.getName());
      super.transitions = new GrammarTransition[EncryptedDataStatesEnum.LAST_ENCRYPTED_DATA_STATE.ordinal()][256];
      super.transitions[EncryptedDataStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(EncryptedDataStatesEnum.START_STATE, EncryptedDataStatesEnum.ENCRYPTED_DATA_SEQ_STATE, UniversalTag.SEQUENCE, new EncryptedDataInit());
      super.transitions[EncryptedDataStatesEnum.ENCRYPTED_DATA_SEQ_STATE.ordinal()][160] = new GrammarTransition(EncryptedDataStatesEnum.ENCRYPTED_DATA_SEQ_STATE, EncryptedDataStatesEnum.ENCRYPTED_DATA_ETYPE_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[EncryptedDataStatesEnum.ENCRYPTED_DATA_ETYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(EncryptedDataStatesEnum.ENCRYPTED_DATA_ETYPE_TAG_STATE, EncryptedDataStatesEnum.ENCRYPTED_DATA_ETYPE_STATE, UniversalTag.INTEGER, new StoreEType());
      super.transitions[EncryptedDataStatesEnum.ENCRYPTED_DATA_ETYPE_STATE.ordinal()][161] = new GrammarTransition(EncryptedDataStatesEnum.ENCRYPTED_DATA_ETYPE_STATE, EncryptedDataStatesEnum.ENCRYPTED_DATA_KVNO_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[EncryptedDataStatesEnum.ENCRYPTED_DATA_ETYPE_STATE.ordinal()][162] = new GrammarTransition(EncryptedDataStatesEnum.ENCRYPTED_DATA_ETYPE_STATE, EncryptedDataStatesEnum.ENCRYPTED_DATA_CIPHER_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[EncryptedDataStatesEnum.ENCRYPTED_DATA_KVNO_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(EncryptedDataStatesEnum.ENCRYPTED_DATA_KVNO_TAG_STATE, EncryptedDataStatesEnum.ENCRYPTED_DATA_KVNO_STATE, UniversalTag.INTEGER, new StoreKvno());
      super.transitions[EncryptedDataStatesEnum.ENCRYPTED_DATA_KVNO_STATE.ordinal()][162] = new GrammarTransition(EncryptedDataStatesEnum.ENCRYPTED_DATA_KVNO_STATE, EncryptedDataStatesEnum.ENCRYPTED_DATA_CIPHER_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[EncryptedDataStatesEnum.ENCRYPTED_DATA_CIPHER_TAG_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(EncryptedDataStatesEnum.ENCRYPTED_DATA_CIPHER_TAG_STATE, EncryptedDataStatesEnum.ENCRYPTED_DATA_CIPHER_STATE, UniversalTag.OCTET_STRING, new StoreCipher());
   }

   public static Grammar<EncryptedDataContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new EncryptedDataGrammar();
   }
}
