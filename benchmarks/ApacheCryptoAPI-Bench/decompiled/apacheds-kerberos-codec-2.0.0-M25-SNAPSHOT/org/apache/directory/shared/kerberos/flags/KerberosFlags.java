package org.apache.directory.shared.kerberos.flags;

public interface KerberosFlags {
   int getIntValue();

   boolean isFlagSet(KerberosFlag var1);

   boolean isFlagSet(int var1);

   void setFlag(KerberosFlag var1);

   void setFlag(int var1);

   void clearFlag(KerberosFlag var1);

   void clearFlag(int var1);
}
