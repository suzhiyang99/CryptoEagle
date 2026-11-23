package org.apache.directory.server.kerberos.shared.crypto.checksum;

import org.apache.directory.server.kerberos.shared.crypto.encryption.KeyUsage;
import org.apache.directory.shared.kerberos.crypto.checksum.ChecksumType;

public interface ChecksumEngine {
   ChecksumType checksumType();

   byte[] calculateChecksum(byte[] var1, byte[] var2, KeyUsage var3);
}
