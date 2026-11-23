package org.codeminers.standalone.rules

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{Call, CryptoOperation, Expression, Identifier, Method}
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.enrichment.{AnalysisAblation, JavaValueSummaries}

/** Bounded Java API-use evidence. No project/file allowlists or changes to C/X rule identifiers. */
private[standalone] final class JavaUsageEvidence(cpg: Cpg) {
  private val values = new JavaValueSummaries(cpg)
  private def arg(c: Call, i: Int) = c.argument.l.find(_.argumentIndex == i)
  private def owner(full: String, name: String) = full.takeWhile(_ != ':').stripSuffix("." + name)
  private lazy val sitesByName = cpg.call.l.groupBy(_.name)
  private def callers(m: Method) = sitesByName.getOrElse(m.name, Nil).filter(c =>
    c.methodFullName == m.fullName || (c.methodFullName.contains("<unresolvedSignature>") &&
      owner(c.methodFullName, c.name) == owner(m.fullName, m.name) &&
      c.argument.count(_.argumentIndex > 0) == m.parameter.count(_.index > 0))).filterNot(_.method.id == m.id)
  private def methods(o: CryptoOperation) = o.realizingCall.method.l.distinctBy(_.id)
  private def enclosing(m: Method): Vector[Method] = {
    val prefix = owner(m.fullName, m.name).split("\\.").dropRight(1).mkString(".")
    cpg.method.l.filter(x => x.file.name.headOption == m.file.name.headOption &&
      owner(x.fullName, x.name).startsWith(prefix)).toVector
  }
  private def reachableConsumers(m: Method, depth: Int = 0, seen: Set[Long] = Set.empty): Vector[Method] =
    if (depth > 3 || seen(m.id)) Vector.empty else {
      val next = callers(m).map(_.method).distinctBy(_.id).take(32)
      next.toVector ++ next.toVector.flatMap(x => reachableConsumers(x, depth + 1, seen + m.id))
    }
  private def text(m: Method) = m.ast.isCall.code.l.mkString(" ")

  /** RFC 8429 deprecates these Kerberos checksum suites, not every use of HMAC-SHA1.
    * Require a typed enum returned by the checksum selector in this exact owner and an
    * actual calculateChecksum/calculateIntegrity caller chain to this MAC operation. */
  def deprecatedKerberosChecksum(o: CryptoOperation): Option[String] = methods(o).iterator.flatMap { m =>
    val consumers = m +: reachableConsumers(m)
    val checksumUse = consumers.exists(x => Set("calculateChecksum", "calculateIntegrity")(x.name) &&
      owner(x.fullName, x.name) == owner(m.fullName, m.name))
    if (!checksumUse) Iterator.empty else cpg.method.l.iterator.filter(selector =>
      selector.name == "checksumType" && owner(selector.fullName, selector.name) == owner(m.fullName, m.name))
      .flatMap(_.ast.isReturn.ast.isCall.l.iterator)
      .filter(c => c.name == "<operator>.fieldAccess" && c.typeFullName.endsWith(".ChecksumType"))
      .flatMap(_.ast.isFieldIdentifier.canonicalName.l.iterator)
      .filter(Set("HMAC_SHA1_DES3_KD", "HMAC_SHA1_DES3"))
  }.take(1).toVector.headOption
  private def normalized(e: Expression) = e.code.replaceAll("\\.getBytes\\([^)]*\\)", "").replaceAll("\\s+", "")

  /** The actual Cipher key is the result of generating a key from a PBEKeySpec.
    * Preserve this proof when lifecycle source substitution retained only the password. */
  private def derivedKey(o: CryptoOperation): Boolean = {
    def pbeResult(e: Expression): Boolean = e match {
      case c: Call if c.name == "generateSecret" && c.methodFullName.startsWith("javax.crypto.SecretKeyFactory.") =>
        arg(c, 1).exists(a => a.ast.isCall.l.exists(_.methodFullName.startsWith("javax.crypto.spec.PBEKeySpec.<init>:")) ||
          (a match { case i: Identifier => i.typeFullName == "javax.crypto.spec.PBEKeySpec"; case _ => false }))
      case _ => false
    }
    o.realizingCall.l.filter(c => c.name == "init" && c.methodFullName.startsWith("javax.crypto.Cipher.")).exists { init =>
      arg(init, 2).exists { key =>
        pbeResult(key) || (key match {
          case i: Identifier =>
            init.method.ast.isCall.nameExact("<operator>.assignment").l
              .filter(a => a.lineNumber.getOrElse(Int.MaxValue) <= init.lineNumber.getOrElse(-1) &&
                arg(a, 1).exists(_.code.trim == i.name))
              .sortBy(a => (a.lineNumber.getOrElse(-1), a.id)).lastOption.flatMap(a => arg(a, 2)).exists(pbeResult)
          case _ => false
        })
      }
    }
  }

  private def authenticatedHutool(o: CryptoOperation): Boolean = methods(o).exists { m =>
    val calls = m.ast.isCall.l
    val enc = calls.filter(c => c.name.startsWith("encrypt") && c.methodFullName.startsWith("cn.hutool.crypto.symmetric."))
    val signatures = calls.filter(c => c.name == "sign" && c.methodFullName.startsWith("cn.hutool.crypto.asymmetric.Sign."))
    enc.exists(e => signatures.exists(s => arg(e, 1).nonEmpty && arg(s, 1).exists(a => normalized(a) == normalized(arg(e, 1).get)))) &&
      m.ast.isReturn.nonEmpty
  }
  private def hutoolDirection(o: CryptoOperation): Option[String] = {
    val factories = o.realizingCall.l.filter(c => c.methodFullName.startsWith("cn.hutool.crypto.SecureUtil.aes:"))
    val uses = factories.flatMap { f =>
      val names = f.method.ast.isCall.nameExact("<operator>.assignment").l.filter(a =>
        arg(a, 2).exists(_.ast.exists(_.id == f.id))).flatMap(a => arg(a, 1).map(_.code.trim))
      f.method.ast.isCall.l.filter(c => arg(c, 0).exists(a => names.contains(a.code.trim)))
    }
    if (uses.exists(_.name.startsWith("encrypt"))) Some("ENCRYPT")
    else if (uses.exists(_.name.startsWith("decrypt"))) Some("DECRYPT") else None
  }
  private def mockSeed(o: CryptoOperation): Boolean = o.operation == "SEED" && methods(o).exists { m =>
    val typ = owner(m.fullName, m.name)
    val mockType = typ.split("[.$]").exists(_.matches("(?:Mock|Stub|Fake)(?:.*)"))
    mockType && enclosing(m).exists(x => text(x).contains("getInstanceOrNull()") &&
      x.ast.isReturn.code.l.exists(_.contains("Mock.INSTANCE")))
  }
  private def challengeDigest(m: Method): Boolean = {
    val calls = m.ast.isCall.l
    val bytes = m.parameter.l.filter(p => p.index > 0 && p.typeFullName == "byte[]")
    bytes.size >= 2 && calls.exists(_.name == "<operator>.xor") &&
      calls.exists(c => c.name == "update" && c.methodFullName.startsWith("java.security.MessageDigest.") &&
        arg(c, 1).exists(a => bytes.exists(_.name == a.code.trim)))
  }
  private def expectedHashInput(m: Method): Boolean = m.ast.isCall.l.exists { c =>
    c.name == "equals" && c.methodFullName.startsWith("java.util.Arrays.") &&
      arg(c, 1).exists(a => m.parameter.l.exists(p => p.index > 0 && p.typeFullName == "byte[]" && p.name == a.code.trim))
  }
  private def credentialDigestUse(o: CryptoOperation): Option[FindingEvidence] = {
    val credential = "(?i).*(?:apiToken|accessToken|authToken|password|passwd|passphrase|credential|secretKey).*".r
    def forwarded(site: Call): Boolean = site.inAst.isReturn.nonEmpty || {
      // A factory result can become the receiver of a digest whose return value is used by callers.
      val aliases = site.inAst.isCall.nameExact("<operator>.assignment").l.filter(a =>
        arg(a, 2).exists(_.id == site.id)).flatMap(a => arg(a, 1).map(_.code)).toSet
      site.method.ast.isReturn.ast.isCall.l.exists(c => c.name == "digest" &&
        c.methodFullName.startsWith("java.security.MessageDigest.") &&
        arg(c, 0).exists(a => aliases(a.code)))
    }
    def find(method: Method, depth: Int, seen: Set[Long]): Option[Call] = {
      if (depth == 0 || seen(method.id)) return None
      val sites = callers(method).filter(_.methodFullName == method.fullName)
      sites.find(site => arg(site, 1).exists(input => credential.matches(input.code)) ||
        (site.inAst.isReturn.nonEmpty && credential.matches(site.method.name)))
        .orElse(sites.take(32).iterator.filter(forwarded).flatMap(site =>
          find(site.method, depth - 1, seen + method.id)).nextOption())
    }
    if (!AnalysisAblation.full) None
    else methods(o).iterator.filter(_.methodReturn.typeFullName == "java.security.MessageDigest")
      .flatMap(m => find(m, 4, Set.empty)).nextOption().map { site =>
        val location = site.lineNumber.map(line => FindingLocation(site.file.name.headOption.getOrElse(o.srcFile), line))
        FindingEvidence("DIGEST_CONSUMER", site.label, site.code, location)
      }
  }
  private def nonSecurityDigest(o: CryptoOperation): Boolean = methods(o).exists { m =>
    val consumers = reachableConsumers(m)
    val all = (m +: consumers) ++ enclosing(m)
    val directSites = callers(m)
    // An actual credential consumer overrides non-security consumers of a shared digest helper.
    val securityConsumer = consumers.exists(x => (x.fullName + " " + x.ast.isIdentifier.name.l.mkString(" ")).toLowerCase.matches(
      "(?s).*(?:apitoken|password|passwd|credential|signature|generatekey|encrypt).*"))
    val caching = directSites.nonEmpty && directSites.forall(site => text(site.method).matches(
      "(?s).*(?:cache[A-Za-z]*\\.(?:contains|add)|getContent\\(|contentMd5|config\\.content).*"))
    val securityOperand = directSites.exists(site => arg(site, 1).exists(a => a.code.toLowerCase.matches(
      "(?s).*(?:password|passwd|passphrase|credential|secret|token|signature|privatekey|encryptkey).*")))
    val boundedRandom = all.exists(_.ast.isCall.l.exists(_.methodFullName.startsWith("java.util.Random.<init>:"))) &&
      all.exists(_.ast.isCall.l.exists(_.methodFullName.startsWith("java.util.Random.nextInt:"))) &&
      !all.exists(_.ast.isCall.l.exists(_.methodFullName.startsWith("javax.crypto.")))
    val historical = m.ast.isCall.l.exists(c => JavaUsageEvidence.historicalSignatureVerification(cpg, c))
    val macOutput = m.ast.isCall.l.filter(c => Set("update", "digest")(c.name) &&
      c.methodFullName.startsWith("java.security.MessageDigest.")).flatMap(c => arg(c, 1)).exists(e =>
        values.resolve(e, material = true).exists(_.sources.exists {
          case c: Call => c.methodFullName.startsWith("javax.crypto.Mac.doFinal:")
          case _ => false
        }))
    historical || macOutput || challengeDigest(m) ||
      (caching && !securityOperand) || (!securityConsumer && boundedRandom)
  }

  def refine(o: CryptoOperation, f: CryptoOperationFact): CryptoOperationFact = {
    if (!f.srcFile.endsWith(".java")) return f
    val ms = methods(o)
    val credentialUse = Option.when(f.opType == CryptoQLRules.HashFunction && f.isDeprecated)(credentialDigestUse(o)).flatten
    f.copy(operation = hutoolDirection(o).getOrElse(f.operation),
      evidence = f.evidence ++ credentialUse,
      keyTransformed = f.keyTransformed || derivedKey(o),
      ciphertextAuthentication = if (authenticatedHutool(o)) Some(true) else f.ciphertextAuthentication,
      nonSecurityUse = mockSeed(o) || (f.opType == CryptoQLRules.HashFunction && credentialUse.isEmpty && nonSecurityDigest(o)),
      hashInputTransformed = f.hashInputTransformed || (f.opType == CryptoQLRules.HashFunction &&
        ms.exists(m => challengeDigest(m) && expectedHashInput(m))))
  }
}

private[standalone] object JavaUsageEvidence {
  private def arg(c: Call, i: Int) = c.argument.l.find(_.argumentIndex == i)
  def historicalSignatureVerification(cpg: Cpg, factory: Call): Boolean = {
    if (!factory.methodFullName.startsWith("java.security.Signature.getInstance:")) return false
    val m = factory.method
    val calls = m.ast.isCall.l
    val receiver = calls.filter(_.name == "<operator>.assignment").filter(c =>
      arg(c, 2).exists(_.ast.exists(_.id == factory.id))).flatMap(c => arg(c, 1).map(_.code.trim)).toSet
    val bound = calls.filter(c => c.methodFullName.startsWith("java.security.Signature.") &&
      arg(c, 0).exists(a => receiver(a.code.trim)))
    // A factory configured solely for verification may be a live protocol; require the
    // stronger digest branch in this verifier before suppressing historical compatibility.
    bound.exists(_.name == "initVerify") && !bound.exists(_.name == "initSign") &&
      calls.exists(c => c.methodFullName.startsWith("java.security.Signature.getInstance:") &&
        arg(c, 1).exists(_.code.toUpperCase.contains("SHA512")))
  }
  def pbeCipherConsumer(o: CryptoOperation): Boolean = !o.srcFile.endsWith(".java") ||
    o.realizingCall.l.flatMap(_.file.method.ast.isCall.realizedCryptoOperation.l).exists(x =>
      x.opType == CryptoQLRules.SymmetricEncryption && x.algorithm.equalsIgnoreCase(o.algorithm))

  def clientOnlyTrustCallback(cpg: Cpg, method: Method): Boolean = {
    if (method.name != "checkClientTrusted") return false
    val parts = method.fullName.takeWhile(_ != ':').split("\\.")
    val prefix = parts.takeWhile(p => !p.startsWith("X509TrustManager") && !p.startsWith("X509ExtendedTrustManager")).mkString(".")
    val files = method.file.name.l.toSet
    val declaredField = parts.dropWhile(p => !p.endsWith("TRUST_MANAGERS")).headOption
    val related = cpg.method.l.filter(m => files.exists(m.file.name.l.contains) ||
      declaredField.exists(field => m.ast.isCall.code.l.exists(_.contains(prefix.split("\\.").dropRight(1).lastOption.getOrElse("") + "." + field))))
    val calls = related.flatMap(_.ast.isCall.l)
    val server = calls.exists(c => Set("getServerSocketFactory", "setNeedClientAuth", "setWantClientAuth")(c.name))
    val client = calls.exists(c => Set("getSocketFactory", "setSSLSocketFactory")(c.name) ||
      c.methodFullName.startsWith("org.apache.http.impl.client.HttpClients."))
    !server && client
  }
}
