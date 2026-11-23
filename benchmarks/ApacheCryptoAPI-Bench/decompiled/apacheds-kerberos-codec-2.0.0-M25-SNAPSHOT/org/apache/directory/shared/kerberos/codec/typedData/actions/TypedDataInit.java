package org.apache.directory.shared.kerberos.codec.typedData.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.typedData.TypedDataContainer;
import org.apache.directory.shared.kerberos.components.TypedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypedDataInit extends GrammarAction<TypedDataContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(TypedDataInit.class);
   private static final boolean IS_DEBUG;

   public TypedDataInit() {
      super("Creates a TypedData instance");
   }

   public void action(TypedDataContainer typedDataContainer) throws DecoderException {
      TLV tlv = typedDataContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         if (typedDataContainer.getTypedData() == null) {
            TypedData typedData = new TypedData();
            typedDataContainer.setTypedData(typedData);
            if (IS_DEBUG) {
               LOG.debug("TypedData created");
            }
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
