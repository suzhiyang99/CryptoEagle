package org.apache.directory.shared.kerberos.codec.encryptionKey;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.encryptionKey.actions.EncryptionKeyInit;
import org.apache.directory.shared.kerberos.codec.encryptionKey.actions.StoreKeyType;
import org.apache.directory.shared.kerberos.codec.encryptionKey.actions.StoreKeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EncryptionKeyGrammar extends AbstractGrammar<EncryptionKeyContainer> {
   static final Logger LOG = LoggerFactory.getLogger(EncryptionKeyGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<EncryptionKeyContainer> instance;

   private EncryptionKeyGrammar() {
      this.setName(EncryptionKeyGrammar.class.getName());
      super.transitions = new GrammarTransition[EncryptionKeyStatesEnum.LAST_ENCRYPTION_KEY_STATE.ordinal()][256];
      super.transitions[EncryptionKeyStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(EncryptionKeyStatesEnum.START_STATE, EncryptionKeyStatesEnum.ENCRYPTION_KEY_SEQ_STATE, UniversalTag.SEQUENCE, new EncryptionKeyInit());
      super.transitions[EncryptionKeyStatesEnum.ENCRYPTION_KEY_SEQ_STATE.ordinal()][160] = new GrammarTransition(EncryptionKeyStatesEnum.ENCRYPTION_KEY_SEQ_STATE, EncryptionKeyStatesEnum.ENCRYPTION_KEY_TYPE_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[EncryptionKeyStatesEnum.ENCRYPTION_KEY_TYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(EncryptionKeyStatesEnum.ENCRYPTION_KEY_TYPE_TAG_STATE, EncryptionKeyStatesEnum.ENCRYPTION_KEY_TYPE_STATE, UniversalTag.INTEGER, new StoreKeyType());
      super.transitions[EncryptionKeyStatesEnum.ENCRYPTION_KEY_TYPE_STATE.ordinal()][161] = new GrammarTransition(EncryptionKeyStatesEnum.ENCRYPTION_KEY_TYPE_STATE, EncryptionKeyStatesEnum.ENCRYPTION_KEY_VALUE_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[EncryptionKeyStatesEnum.ENCRYPTION_KEY_VALUE_TAG_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(EncryptionKeyStatesEnum.ENCRYPTION_KEY_VALUE_TAG_STATE, EncryptionKeyStatesEnum.ENCRYPTION_KEY_VALUE_STATE, UniversalTag.OCTET_STRING, new StoreKeyValue());
   }

   public static Grammar<EncryptionKeyContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new EncryptionKeyGrammar();
   }
}
