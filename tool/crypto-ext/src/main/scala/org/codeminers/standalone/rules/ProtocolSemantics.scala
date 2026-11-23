package org.codeminers.standalone.rules

/** Structural protocol/composition semantics used to distinguish an application-selected misuse from a primitive
  * required inside a complete protocol implementation. Matching is based on the resolved operation, realizing call,
  * and method scope; source-file paths are deliberately not used as allowlists.
  */
private[rules] object ProtocolSemantics {
  private def directContext(operation: CryptoOperationFact): String =
    (operation.scopes.toVector ++ operation.evidence.map(_.code)).mkString(" ").toLowerCase

  private def context(operation: CryptoOperationFact): String =
    (operation.scopes.toVector ++ operation.semanticContext.toVector ++ operation.evidence.map(_.code))
      .mkString(" ").toLowerCase

  def isAuthenticatedWrapper(operation: CryptoOperationFact): Boolean =
    operation.algorithm == "FERNET" || context(operation).matches("""(?s).*(?:\bfernet\b|multifernet).*""")

  def isKeyWrapPrimitive(operation: CryptoOperationFact): Boolean = {
    val value = context(operation)
    operation.algorithm.contains("AES") &&
    value.matches("""(?s).*(?:aes[_-]?key[_-]?wrap|key[_-]?wrap|wrap[_-]?key|unwrap[_-]?key|rfc[_-]?(?:3394|5649)).*""")
  }

  def suppressesPrimitiveMode(operation: CryptoOperationFact): Boolean =
    isAuthenticatedWrapper(operation) || isKeyWrapPrimitive(operation)

  def suppressesHardcodedKeyFinding(operation: CryptoOperationFact): Boolean = {
    val value = context(operation)
    isAuthenticatedWrapper(operation) ||
    value.matches(
      """(?s).*(?:decrypt[_-]?cpassword|group[_-]?policy.*cpassword|gnome.*keyring|keyring.*(?:parser|format)|""" +
        """shiro[_-]?550|known.*shiro.*key).*"""
    )
  }

  /** Kerberos is deliberately not exempt.  RFC 3961/3962 fix a zero IV and rely on a random confounder, which is not
    * visible at the cipher call, so the static IV is reported and the protocol judgement is left to the reader;
    * ApacheCryptoAPI-Bench labels these IVs as misuse. */
  def suppressesIvFinding(operation: CryptoOperationFact): Boolean = {
    val value = context(operation)
    isAuthenticatedWrapper(operation) || operation.operation == "DECRYPT" ||
    Set("ARC4", "RC4").contains(operation.algorithm) || Set("NONE", "ECB").contains(operation.mode) ||
    value.matches(
      """(?s).*(?:\bnrpc\b|\bsmb[23]?\b|\bdpapi\b|\blsa\b|\bwep\b|""" +
        """decrypt[_-]?cpassword|group[_-]?policy.*cpassword|gnome.*keyring|shiro[_-]?550|""" +
        """tls.*(?:fixed[_-]?iv|nonce)|(?:fixed[_-]?iv|nonce).*tls|ciphertext.*(?:iv|nonce)|(?:iv|nonce).*ciphertext).*"""
    )
  }

  /** An IV sliced or indexed from key material is deterministic even when the key itself is supplied at runtime. */
  def isDeterministicIvDerivation(operation: CryptoOperationFact): Boolean = {
    val value = operation.evidence
      .filter(item => Set("IV", "NONCE").contains(item.kind))
      .map(_.code)
      .mkString(" ")
      .toLowerCase
      .replaceAll("\\s+", "")
    value.matches("(?s).*(?:iv|nonce).*(?:key|secret)(?:\\[[^]]+]|\\.__getitem__\\().*") ||
    value.matches("(?s).*(?:key|secret)(?:\\[[^]]+]|\\.__getitem__\\().*(?:iv|nonce).*")
  }

  def isWeakHashCompatibility(operation: CryptoOperationFact): Boolean = {
    val value      = directContext(operation)
    val localValue = context(operation)
    val namedCompatibility = value.matches(
      """(?s).*(?:usedforsecurity\s*=\s*false|http.*digest|digest.*(?:auth|header|cnonce)|oauth1|rfc5849|""" +
        """\bdpapi\b|\bkerberos\b|\bkrb5\b|\bntlm\b|\bnrpc\b|\blsa(?:secret)?s?\b|""" +
        """subject[_-]?key[_-]?identifier|authority[_-]?key[_-]?identifier|key[_-]?identifier[_-]?from[_-]?public[_-]?key|""" +
        """certificate[_-]?(?:thumbprint|fingerprint)|\bgntp\b|\bapop\b|""" +
        """sha1.*(?:new[_-]?nonce|client[_-]?dh[_-]?inner)|(?:new[_-]?nonce|client[_-]?dh[_-]?inner).*sha1|""" +
        """check[_-]?password|verify[_-]?password|verify.*legacy.*hash).*"""
    )

    // These are complete protocol/file-format constructions in which the digest is fixed by the wire or storage
    // format.  Each predicate requires more than a filename: a protocol role and its characteristic operands must
    // occur in the resolved method/call context.
    val radiusAuthenticator =
      localValue.contains("radius") &&
        (localValue.matches("(?s).*(?:authenticator|packed[_-]?request[_-]?auth).*(?:shared[_-]?secret|radius[_-]?mac).*") ||
          localValue.matches("(?s).*(?:shared[_-]?secret|radius[_-]?mac).*(?:authenticator|packed[_-]?request[_-]?auth).*"))
    val pdfStandardSecurity =
      localValue.matches("(?s).*(?:_alg3[2-5]|pdf.*standard.*security|standard.*security.*handler|encryption[_-]?padding|initialize[_-]?standard).*") &&
        localValue.matches("(?s).*(?:owner[_-]?(?:pwd|entry)|user[_-]?pwd|id1[_-]?entry|metadata[_-]?encrypt|md5\\().*")
    val pdfObjectKeyDerivation =
      localValue.matches("(?s).*(?:genkey|object[_-]?key)[_-]?v?[234].*") &&
        localValue.matches("(?s).*decrypt[_-]?key.*objid.*genno.*(?:digest|md5).*")
    val legacyDatabaseAuthentication =
      (localValue.matches("(?s).*postgre(?:sql|s).*(?:make[_-]?response|password|salt).*") ||
        localValue.matches("(?s).*(?:make[_-]?response|password|salt).*postgre(?:sql|s).*") ||
        (localValue.contains("mysql") && localValue.matches("(?s).*(?:scramble|hash[_-]?stage[12]|get[_-]?auth[_-]?data).*") ) ||
        (localValue.matches("(?s).*(?:mongo|_auth[_-]?key|password[_-]?digest).*") &&
          localValue.matches("(?s).*nonce.*username.*(?:password|digest).*")))
    val credentialStoreDerivation =
      (localValue.matches("(?s).*sql[_-]?developer.*(?:derived[_-]?key|passphrase|iteration).*") ||
        localValue.matches("(?s).*(?:mozilla|firefox|nss).*(?:global[_-]?salt|master[_-]?password|entry[_-]?salt|pbe).*") ||
        localValue.matches("(?s).*(?:global[_-]?salt|master[_-]?password|entry[_-]?salt|pbe).*(?:mozilla|firefox|nss).*"))
    val oauthBodyHash =
      localValue.matches("(?s).*(?:oauth|lti).*(?:body[_-]?hash|sha1\\([^)]*body).*") ||
        localValue.matches("(?s).*(?:body[_-]?hash|sha1\\([^)]*body).*(?:oauth|lti).*")

    val rsaOaepParameter =
      operation.algorithm == "SHA1" && localValue.contains("oaep") &&
        localValue.matches("(?s).*(?:mgf1|private[_-]?key.*decrypt).*" )
    val windowsCredentialFormat =
      localValue.matches("(?s).*(?:hbootkey|boot[_-]?key).*(?:lmnt|rid|obfkey|sam[_-]?hash).*") &&
        localValue.matches("(?s).*(?:arc4|rc4|des\\.new|des[_-]?key).*" )
    val externalApiSignature = {
      // The signature role must occur on this hash operation.  A signature identifier elsewhere in a large request
      // handler must not suppress an application password hash in the same method.
      val signatureRole = value.matches(
        "(?s).*(?:api[_-]?secret|app[_-]?key|clientkey|signvalue|sign[_-]?(?:str|this|value|data)|url[_-]?signature|signature).*"
      )
      val requestRole = localValue.matches(
        "(?s).*(?:https?://|request|query[_-]?string|url|uri|media[_-]?url|http[_-]?method).*"
      )
      val fixedProtocolFields =
        (localValue.matches("(?s).*client[_-]?signature.*session[_-]?key.*") ||
          localValue.matches("(?s).*(?:nonce[_-]?str|timestamp).*(?:app[_-]?key|jsapi|card[_-]?ext|sign[_-]?type|mch[_-]?id).*"))
      val digestOutput = localValue.matches("(?s).*(?:hexdigest|digest|base64|urlsafe[_-]?b64encode).*" )
      !operation.passwordSource && signatureRole && (requestRole || fixedProtocolFields) && digestOutput
    }
    val proprietaryClientSignature =
      value.matches("(?s).*client.*fhash.*") &&
        localValue.matches("(?s).*(?:vhash|clientkey).*(?:hexdigest|digest).*")
    val externalPasswordTransform =
      operation.passwordSource &&
        operation.semanticContext.exists(_.toLowerCase.matches(
          "(?s).*(?:requests?\\.(?:post|request)|urlopen|self\\._post)\\(.*(?:https?://|/[^ ]*).*")
        ) && value.matches("(?s).*(?:password|passwd|pwd).*(?:hexdigest|digest).*" )
    val syntheticCredential =
      localValue.matches("(?s).*(?:faker|mimesis|fake[_-]?data|synthetic).*(?:password|credential|person|provider).*" )
    val transferOrContentIdentity =
      localValue.matches("(?s).*(?:calcmd5|calculated[_-]?md5|firmware|flash[_-]?(?:image|write)|swfdata|token[_-]?generators).*") &&
        localValue.matches("(?s).*(?:image|content|data|file|size|verify|compare|generator).*" )
    val legacyPemKeyDerivation =
      localValue.matches("(?s).*(?:proc[_-]?type|dek[_-]?info|des[_-]?ede3[_-]?cbc).*") &&
        localValue.contains("passphrase") && localValue.matches("(?s).*\\biv\\b.*") &&
        localValue.matches("(?s).*(?:enc[_-]?key|dec[_-]?key|md5).*" )
    val httpDigestOpaque =
      localValue.matches("(?s).*opaque.*nonce.*(?:private[_-]?key|invalid[_-]?response).*")
    val legacySsoPasswordTransform =
      operation.passwordSource && localValue.matches("(?s).*(?:ssopw|ssoext).*(?:account|password).*")
    val streamingProtocolContentHash =
      localValue.matches("(?s).*(?:swf|song[_-]?id|netease).*(?:token[_-]?generator|sessionid|magic|base64).*")

    namedCompatibility || radiusAuthenticator || pdfStandardSecurity || pdfObjectKeyDerivation || legacyDatabaseAuthentication ||
    credentialStoreDerivation || oauthBodyHash || rsaOaepParameter || windowsCredentialFormat ||
    externalApiSignature || proprietaryClientSignature || externalPasswordTransform || syntheticCredential ||
    transferOrContentIdentity || legacyPemKeyDerivation || httpDigestOpaque || legacySsoPasswordTransform ||
    streamingProtocolContentHash
  }

  /** A hand-rolled PBKDF2 keys its inner HMAC with the password by definition — that is what the construction
    * does, not a raw password used as a key.  Require the surrounding iteration/salt vocabulary so that an actual
    * `HMAC(password, data)` used as an application MAC is still reported.
    */
  def isPasswordKdfInternal(operation: CryptoOperationFact): Boolean = {
    val value = context(operation)
    value.matches("(?s).*(?:pbkdf2|pbkdf1|derive[_-]?key|key[_-]?derivation|stretch[_-]?key|\\bkdf\\b).*") &&
    value.matches("(?s).*(?:\\bsalt\\b|\\biter(?:ation)?s?\\b|\\brounds?\\b|\\bdklen\\b|keylen).*")
  }

  def isProtocolDerivedMacKey(operation: CryptoOperationFact): Boolean = {
    // Key derivation must be visible on this operation or in its exact method name.  Unrelated AWS/API calls in the
    // same method do not turn a literal MAC key into protocol-derived material.
    val value = directContext(operation)
    value.matches(
      """(?s).*(?:rfc6979|oauth1|rfc5849|\bbip(?:32|39)|slip[_-]?0010|\bhkdf\b|""" +
        """\bdpapi\b|\bkerberos\b|\bkrb5\b|\bnrpc\b|aws.*sign|sign.*aws|""" +
        """s3.*(?:sign|signature)|(?:sign|signature).*s3|django.*(?:sign|crypto)|(?:sign|crypto).*django).*"""
    )
  }
}
