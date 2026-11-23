package org.apache.directory.shared.kerberos.codec.changePwdData;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.ber.AbstractContainer;
import org.apache.directory.shared.kerberos.messages.ChangePasswdData;

public class ChangePasswdDataContainer extends AbstractContainer {
   private ChangePasswdData chngPwdData;

   public ChangePasswdDataContainer(ByteBuffer stream) {
      super(stream);
      this.setGrammar(ChangePasswdDataGrammar.getInstance());
      this.setTransition(ChangePasswdDataStatesEnum.START_STATE);
   }

   public ChangePasswdData getChngPwdData() {
      return this.chngPwdData;
   }

   public void setChngPwdData(ChangePasswdData chngPwdData) {
      this.chngPwdData = chngPwdData;
   }
}
