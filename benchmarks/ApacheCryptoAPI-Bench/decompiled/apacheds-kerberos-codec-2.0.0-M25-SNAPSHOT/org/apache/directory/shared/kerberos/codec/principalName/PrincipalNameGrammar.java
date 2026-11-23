package org.apache.directory.shared.kerberos.codec.principalName;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.principalName.actions.PrincipalNameInit;
import org.apache.directory.shared.kerberos.codec.principalName.actions.StoreNameString;
import org.apache.directory.shared.kerberos.codec.principalName.actions.StoreNameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PrincipalNameGrammar extends AbstractGrammar<PrincipalNameContainer> {
   static final Logger LOG = LoggerFactory.getLogger(PrincipalNameGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<PrincipalNameContainer> instance;

   private PrincipalNameGrammar() {
      this.setName(PrincipalNameGrammar.class.getName());
      super.transitions = new GrammarTransition[PrincipalNameStatesEnum.LAST_PRINCIPAL_NAME_STATE.ordinal()][256];
      super.transitions[PrincipalNameStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(PrincipalNameStatesEnum.START_STATE, PrincipalNameStatesEnum.PRINCIPAL_NAME_SEQ_STATE, UniversalTag.SEQUENCE, new PrincipalNameInit());
      super.transitions[PrincipalNameStatesEnum.PRINCIPAL_NAME_SEQ_STATE.ordinal()][160] = new GrammarTransition(PrincipalNameStatesEnum.PRINCIPAL_NAME_SEQ_STATE, PrincipalNameStatesEnum.PRINCIPAL_NAME_NAME_TYPE_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[PrincipalNameStatesEnum.PRINCIPAL_NAME_NAME_TYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(PrincipalNameStatesEnum.PRINCIPAL_NAME_NAME_TYPE_TAG_STATE, PrincipalNameStatesEnum.PRINCIPAL_NAME_NAME_TYPE_STATE, UniversalTag.INTEGER, new StoreNameType());
      super.transitions[PrincipalNameStatesEnum.PRINCIPAL_NAME_NAME_TYPE_STATE.ordinal()][161] = new GrammarTransition(PrincipalNameStatesEnum.PRINCIPAL_NAME_NAME_TYPE_STATE, PrincipalNameStatesEnum.PRINCIPAL_NAME_NAME_STRING_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[PrincipalNameStatesEnum.PRINCIPAL_NAME_NAME_STRING_TAG_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(PrincipalNameStatesEnum.PRINCIPAL_NAME_NAME_STRING_TAG_STATE, PrincipalNameStatesEnum.PRINCIPAL_NAME_NAME_STRING_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[PrincipalNameStatesEnum.PRINCIPAL_NAME_NAME_STRING_SEQ_STATE.ordinal()][UniversalTag.GENERAL_STRING.getValue()] = new GrammarTransition(PrincipalNameStatesEnum.PRINCIPAL_NAME_NAME_STRING_SEQ_STATE, PrincipalNameStatesEnum.PRINCIPAL_NAME_NAME_STRING_SEQ_STATE, UniversalTag.GENERAL_STRING, new StoreNameString());
   }

   public static Grammar<PrincipalNameContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new PrincipalNameGrammar();
   }
}
