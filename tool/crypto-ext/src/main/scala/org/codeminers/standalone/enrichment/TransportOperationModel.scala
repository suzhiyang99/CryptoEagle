package org.codeminers.standalone.enrichment

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{AstNode, Call, Expression, Identifier, Literal, Method, StoredNode}
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.adapter.AdapterLanguage
import org.codeminers.standalone.enrichment.{AnalysisAblation, JavaConstantCollections, JavaConstantPathEvaluator, ValueTracking}

import scala.collection.mutable


import OperationObservationKind.*

/** Resolves verification and network configuration into language-independent operation attributes. */
private[enrichment] object TransportOperationModel {
  private val VerificationDisabled = Verification
  private val InsecureTransport = Transport

  def observations(cpg: Cpg, language: AdapterLanguage, sharedIndex: Option[JavaValueIndex] = None): Vector[OperationObservation] = {
    val transport = (OperationModelTiming.measure("transport.http")(http(cpg, language, sharedIndex)) ++
      OperationModelTiming.measure("transport.tls")(tls(cpg, language)) ++
      OperationModelTiming.measure("transport.javaProtocols")(javaProtocols(cpg, language, sharedIndex)))
      // `new URL("http://...").openConnection()` matches both the construction and the sink: one decision per line.
      .distinctBy(m => (m.operationType, sourceFile(m.node), sourceLine(m.node)))
    val verification = OperationModelTiming.measure("transport.certificate")(certificate(cpg, language)) ++
      OperationModelTiming.measure("transport.hostname")(javaHostnameVerification(cpg, language)) ++
      OperationModelTiming.measure("transport.jwt")(jwt(cpg))
    transport ++ verification ++ enabledVerification(cpg, language) ++ modernTls(cpg, language)
  }

  // ---------------------------------------------------------------- C18: cleartext HTTP

  private def http(cpg: Cpg, language: AdapterLanguage, sharedIndex: Option[JavaValueIndex]): Vector[OperationObservation] = {
    // A URL assigned in one branch and overwritten in another is only cleartext if the http assignment survives.
    val javaIndex = Option.when(language == AdapterLanguage.Java)(sharedIndex.getOrElse(new JavaValueIndex(cpg)))
    val constantPath = javaIndex.map(_.paths)
    val javaValues = javaIndex.map(index => new JavaValueSummaries(cpg,
      includeRuntimeFieldWrites = true, sharedIndex = Some(index), transportText = true))
    val javaDefaults = javaIndex.filter(_ => AnalysisAblation.full)
      .map(index => new JavaHttpDefaults(cpg, index, urlSchemes(_).contains("http")))
    val schemeCache  = new UrlSchemeCache(cpg, javaValues)
    cpg.call.l.flatMap { sink =>
      Option.when(isNetworkSink(sink, language)) {
        val useLine = sink.lineNumber.map(_.toInt).getOrElse(Int.MaxValue)
        val inputs = networkInputs(sink, language)
        // Only URL-bearing operands contribute a scheme. Proxy settings, HTTP bodies and the file component
        // of a multipart URL are not destinations, even when their text happens to contain a URL.
        val directSchemes = inputs.flatMap { input =>
          if (javaValues.isDefined && containsArrayIdentity(input)) Set.empty[String]
          else urlSchemes(input.code) ++ input.ast.isLiteral.code.l.flatMap(urlSchemes)
        }.toSet
        // HTTP sorts before every other scheme this model recognizes. Once proven, later arguments
        // cannot change the operation's selected scheme; nested resolver results remain complete and cached.
        var schemes = directSchemes
        val arguments = inputs.iterator
        while (arguments.hasNext && !schemes.contains("http")) {
          val argument = arguments.next()
          val resolved = if (java.lang.Boolean.getBoolean("crypto.profile")) {
            val start = System.nanoTime()
            val result = resolveUrlSchemes(cpg, schemeCache, argument, useLine, 0, Set.empty, constantPath)
            val millis = (System.nanoTime() - start) / 1e6
            if (millis > 20) System.err.println(f"crypto.profile transport.http.argument ${sourceFile(sink)}:$useLine ${sink.methodFullName} $millis%.3f ms")
            result
          } else resolveUrlSchemes(cpg, schemeCache, argument, useLine, 0, Set.empty, constantPath)
          schemes ++= resolved
        }
        val fallback = Option.when(!schemes.contains("http"))(javaDefaults.flatMap(_.at(sink))).flatten
        val selected = if (fallback.isDefined) Some("http") else schemes.toVector.sorted.headOption
        selected.map { scheme =>
          OperationObservation(InsecureTransport, sink, "TRANSPORT", "CONNECT", scheme.toUpperCase,
            detail = fallback.fold(sink.code)(write =>
              s"${sink.code} uses ${write.code} when configuration is missing or empty"),
            transportScheme = scheme.toUpperCase, evidenceNodes = fallback.toVector)
        }
      }.flatten
    }.toVector
  }

  /** A URL literal is configuration or documentation until it reaches an API which can actually open a connection. */
  private def isNetworkSink(call: Call, language: AdapterLanguage): Boolean = {
    if (call.name.startsWith("<operator>")) return false
    lazy val code = compactLower(call.code)
    language match {
      case AdapterLanguage.Python =>
        val requestMethod = Set("get", "post", "put", "delete", "patch", "head", "options", "request", "send")
          .contains(call.name.toLowerCase)
        call.name.equalsIgnoreCase("urlopen") || (requestMethod && hasPythonHttpReceiver(code))
      case AdapterLanguage.Java =>
        Set("openConnection", "openStream").contains(call.name) ||
        call.methodFullName.toLowerCase.contains("httpclient") || compactLower(call.code.takeWhile(_ != '(')).contains("httpclient") ||
        isJavaUrlConstruction(call, code)
      case AdapterLanguage.Cpp =>
        Set("curl_easy_setopt", "curl_easy_perform", "curl_multi_add_handle").contains(call.name) ||
        code.contains("curl_easy_setopt(")
    }
  }

  private def networkInputs(call: Call, language: AdapterLanguage): List[Expression] = {
    val arguments = call.argument.l
    language match {
      case AdapterLanguage.Java if Set("openConnection", "openStream").contains(call.name) =>
        arguments.filter(_.argumentIndex == 0)
      case AdapterLanguage.Java if isJavaUrlConstruction(call, compactLower(call.code)) =>
        val explicit = arguments.filter(_.argumentIndex > 0)
        // URL(context, spec[, handler]) uses the context when spec is relative; the other multipart
        // constructors take the protocol first. Neither host, port, path nor handler determines it.
        val hasContext = call.methodFullName.contains("(java.net.URL,") || explicit.headOption.exists {
          case i: Identifier => i.typeFullName == "java.net.URL"
          case c: Call => c.typeFullName == "java.net.URL"
          case e => e.code == "null" && explicit.sizeIs >= 2 && compactLower(call.code).startsWith("newurl(")
        }
        if (hasContext) explicit.take(2)
        else explicit.take(1)
      case AdapterLanguage.Python =>
        arguments.filter(a => a.argumentName.contains("url") || a.argumentName.isEmpty &&
          a.argumentIndex == (if (call.name.equalsIgnoreCase("request")) 2 else 1))
      case _ => arguments.filter(_.argumentIndex > 0)
    }
  }

  /** Constructing a JDK `URL`/`URI` fixes the scheme the JDK will dial. A user-defined class named `URL` does not. */
  private def isJavaUrlConstruction(call: Call, code: => String): Boolean = {
    val fullName = call.methodFullName
    val jdkType = fullName.startsWith("java.net.URL.") || fullName.startsWith("java.net.URI.")
    val constructor = call.name == "<init>" || call.name == "create"
    (jdkType && constructor) ||
    // javasrc2cpg leaves the constructor unresolved when the JDK is not on the analysis classpath.
    (call.name == "<init>" && fullName.contains("<unresolved") && (code.startsWith("newurl(") || code.startsWith("newuri(")))
  }

  private def hasPythonHttpReceiver(code: String): Boolean =
    Vector("requests.", "httpx.", "aiohttp.", "urllib.", "urllib2.", "urllib3.", "httplib.", "http.client.",
      "tornado.httpclient.", "session.", "client.").exists(code.contains)

  /** `x.toString()` / `String.valueOf(x)` / `x.toCharArray()` carry the value through, so the walk continues at `x`. */
  private def valuePreservingSymbols(code: String): Vector[String] = {
    val compact = code.replaceAll("\\s+", "")
    val receiver = "([A-Za-z_$][A-Za-z0-9_$]*)\\.(?:toCharArray|toString)\\(\\)".r.findAllMatchIn(compact).map(_.group(1))
    val argument = "(?:String\\.)?valueOf\\(([A-Za-z_$][A-Za-z0-9_$]*)\\)".r.findAllMatchIn(compact).map(_.group(1))
    (receiver ++ argument).toVector.distinct
  }

  private val UrlScheme = """(?i)(?:[rubf]*)["'](https?)://(\[[^\]]*\]|[^/:?#"'\s]*)""".r
  private val LoopbackHost = """(?i)localhost|127(?:\.\d{1,3}){3}|\[::1\]""".r

  private def containsArrayIdentity(expression: Expression): Boolean =
    expression.ast.isCall.exists { call =>
      call.name == "toString" && call.argument.argumentIndex(0).exists {
        case identifier: Identifier => identifier.typeFullName.endsWith("[]")
        case receiver: Call => receiver.typeFullName.endsWith("[]")
        case _ => false
      }
    }

  /** Schemes of the URL literals in `code`. Cleartext HTTP to a loopback host never leaves the machine, so it is
    * reported as `http-loopback` and does not count as cleartext transport.
    */
  private def urlSchemes(code: String): Set[String] = UrlScheme.findAllMatchIn(code).map { m =>
    val scheme = m.group(1).toLowerCase
    if (scheme == "http" && LoopbackHost.matches(m.group(2))) "http-loopback" else scheme
  }.toSet

  /** Per-run indexes for `resolveUrlSchemes`; the walk only reads the CPG, so its results can be cached. */
  private final class UrlSchemeCache(cpg: Cpg, val javaValues: Option[JavaValueSummaries] = None) {
    val results = mutable.HashMap.empty[(Long, Int, Int, Set[String], Boolean), Set[String]]
    lazy val assignmentsByFile: Map[String, List[Call]] = cpg.call.nameExact("<operator>.assignment").l.groupBy(sourceFile)
    lazy val callsByName: Map[String, List[Call]] = cpg.call.l.groupBy(_.name)
    lazy val callsByFullName: Map[String, List[Call]] = cpg.call.l.groupBy(_.methodFullName)
  }

  private def resolveUrlSchemes(cpg: Cpg, cache: UrlSchemeCache, expression: Expression, useLine: Int, depth: Int,
    seen: Set[String], constantPath: Option[JavaConstantPathEvaluator] = None): Set[String] = {
    val key = (expression.id, useLine, depth, seen, constantPath.isDefined)
    cache.results.getOrElseUpdate(key, computeUrlSchemes(cpg, cache, expression, useLine, depth, seen, constantPath))
  }

  private def computeUrlSchemes(cpg: Cpg, cache: UrlSchemeCache, expression: Expression, useLine: Int, depth: Int,
    seen: Set[String], constantPath: Option[JavaConstantPathEvaluator]): Set[String] = {
    if (depth > 5) return Set.empty
    if (constantPath.isDefined && containsArrayIdentity(expression)) return Set.empty
    val direct = urlSchemes(expression.code) ++
      expression.ast.isLiteral.code.l.flatMap(urlSchemes).toSet
    if (direct.nonEmpty || AnalysisAblation.valueTracking == ValueTracking.None) return direct
    val exact = Option.when(expression.code.trim.matches("[A-Za-z_$][A-Za-z0-9_$]*"))(expression.code.trim).toSet
    // A field read contributes its FIELD_IDENTIFIER, not an IDENTIFIER; without it a field-to-field copy ends the walk.
    val symbols = exact ++ expression.ast.isIdentifier.name.l ++ expression.ast.isFieldIdentifier.canonicalName.l ++
      valuePreservingSymbols(expression.code)
    // Cycle protection is scoped: a caller and a callee routinely use the same parameter name.
    val scope = expression.method.id
    val provenSchemes = constantPath.toSet.flatMap { evaluator =>
      symbols.flatMap(symbol => evaluator.provenText(symbol, expression.method, expression))
    }.flatMap(text => urlSchemes("\"" + text))
    if (provenSchemes.nonEmpty) return provenSchemes
    val typedSchemes = cache.javaValues.filter(_ => depth == 0 && AnalysisAblation.full).flatMap(_.resolve(expression))
      .filter(summary => !summary.defaultPath && summary.sources.nonEmpty && summary.sources.forall(_.isInstanceOf[Literal]))
      .map { summary =>
        val schemes = summary.sources.collect { case literal: Literal =>
          urlSchemes(literal.code)
        }.distinct
        if (schemes.sizeIs == 1) schemes.head else Set.empty[String]
      }.getOrElse(Set.empty)
    if (typedSchemes.nonEmpty) return typedSchemes
    symbols.filterNot(symbol => seen(s"$scope#$symbol")).flatMap { symbol =>
      val candidates = cache.assignmentsByFile.getOrElse(sourceFile(expression), Nil)
        .filter(_.lineNumber.map(_.toInt).getOrElse(Int.MinValue) <= useLine)
        // Method scope bounds the walk: one method's `url = "http://..."` must not decide an unrelated helper.
        .filter(a => a.method.id == expression.method.id || a.method.name == "<module>")
        .filter(_.argument.l.find(_.argumentIndex == 1).exists(_.code.trim == symbol))
        .filterNot(assignment => constantPath.exists(_.execution(assignment).contains(false)))
      val assigned = candidates.flatMap(_.argument.l.find(_.argumentIndex == 2)).flatMap { value =>
        resolveUrlSchemes(cpg, cache, value, useLine, depth + 1, seen + s"$scope#$symbol", constantPath)
      }
      val parameters = expression.method.parameter.nameExact(symbol).l.filter(_ => AnalysisAblation.full).flatMap { parameter =>
        val exactCallers = cache.callsByFullName.getOrElse(expression.method.fullName, Nil)
        val callers = if (exactCallers.nonEmpty) exactCallers else cache.callsByName.getOrElse(expression.method.name, Nil)
        val callerSchemes = callers
          .filter(call => sourceFile(call) == sourceFile(expression))
          .filterNot(_.method.id == expression.method.id)
          .flatMap(_.argument.l).filter(_.argumentIndex == parameter.index)
          .map(value => resolveUrlSchemes(cpg, cache, value, useLine, depth + 1, seen + s"$scope#$symbol", constantPath))
          .filter(_.nonEmpty).distinct
        // A generic `fetch(url)` helper only has a scheme when every resolvable caller agrees on it.
        if (callerSchemes.sizeIs == 1) callerSchemes.head else Set.empty[String]
      }
      assigned ++ parameters ++ (if (AnalysisAblation.full) resolvePythonFieldSchemes(cpg, cache, expression, symbol,
        useLine, depth, seen) else Set.empty)
    }
  }

  private def resolvePythonFieldSchemes(cpg: Cpg, cache: UrlSchemeCache, expression: Expression, reference: String,
    useLine: Int, depth: Int, seen: Set[String]): Set[String] =
    pythonConstructorArguments(cpg, expression, reference)
      .flatMap(value => resolveUrlSchemes(cpg, cache, value, useLine, depth + 1, seen + reference)).toSet

  /** `obj.field` where `obj = Cls(arg)` and `Cls.__init__` stores a parameter in `self.field`: that argument. */
  private def pythonConstructorArguments(cpg: Cpg, expression: Expression, reference: String): List[Expression] = {
    val parts = reference.split("\\.").toVector
    if (parts.length != 2) return Nil
    val (objectName, fieldName) = (parts.head, parts.last)
    val constructors = cpg.call.nameExact("<operator>.assignment")
      .filter(a => sourceFile(a) == sourceFile(expression))
      .filter(_.argument.l.find(_.argumentIndex == 1).exists(_.code.trim == objectName)).l
      .flatMap(_.argument.l.find(_.argumentIndex == 2).toVector.flatMap(_.ast.isCall.l))
      .filterNot(_.name.startsWith("<operator>"))
    constructors.flatMap { constructor =>
      cpg.method.nameExact("__init__").filter(m => sourceFile(m) == sourceFile(expression))
        .filter(_.fullName.contains(s".${constructor.name}.__init__")).l.flatMap { init =>
          cpg.call.nameExact("<operator>.assignment").filter(_.method.id == init.id)
            .filter(_.argument.l.find(_.argumentIndex == 1).exists(_.code.trim.endsWith(s".$fieldName"))).l
            .flatMap(_.argument.l.find(_.argumentIndex == 2))
            .flatMap(rhs => init.parameter.nameExact(rhs.code.trim).l.flatMap(p =>
              constructor.argument.l.filter(_.argumentIndex == p.index)))
        }
    }
  }

  // ---------------------------------------------------------------- C18: deprecated SSL/TLS versions

  private def tls(cpg: Cpg, language: AdapterLanguage): Vector[OperationObservation] = cpg.call.l.collect {
    case call if weakTlsCall(call, language) =>
      OperationObservation(InsecureTransport, call, "TRANSPORT", "CONFIGURE_TLS", weakTlsToken(call.code), detail = call.code, protocolVersion = canonicalProtocol(call.code))
  }.toVector

  private def weakTlsCall(call: Call, language: AdapterLanguage): Boolean = {
    val exactC = Set("SSLv2_method", "SSLv3_method", "TLSv1_method", "TLSv1_1_method", "SSLv2_client_method",
      "SSLv3_client_method", "TLSv1_client_method")
    def code = call.code.toUpperCase.replace(".", "_")
    language match {
      case AdapterLanguage.Cpp =>
        exactC.contains(call.name) ||
        (Set("SSL_CTX_set_min_proto_version", "SSL_set_min_proto_version").contains(call.name) && containsWeakTlsToken(code))
      case AdapterLanguage.Java =>
        call.name == "getInstance" && call.methodFullName.contains("SSLContext") && containsWeakTlsToken(code)
      case AdapterLanguage.Python =>
        Set("wrap_socket", "SSLContext").contains(call.name) && containsWeakTlsToken(code) &&
        !weakTlsIsOnlyAGetattrFallback(call)
    }
  }

  /** `getattr(ssl, "PROTOCOL_TLSv1_2", ssl.PROTOCOL_TLSv1)` names the old constant only as a fallback. */
  private def weakTlsIsOnlyAGetattrFallback(call: Call): Boolean = {
    val compact = call.code.replaceAll("\\s+", "")
    "getattr\\([^)]*?,[\"']([A-Za-z0-9_]+)[\"'],[^)]*?(PROTOCOL_[A-Za-z0-9_]+|SSLv[0-9]+_method|TLSv1(?:_[0-9])?_method)[^)]*\\)".r
      .findAllMatchIn(compact).exists(found => strengthOf(found.group(1)) > strengthOf(found.group(2)))
  }

  private def strengthOf(token: String): Int = token.toUpperCase match {
    case value if value.contains("TLSV1_3")                          => 5
    case value if value.contains("TLSV1_2")                          => 4
    case value if value.contains("TLSV1_1")                          => 3
    case value if value.contains("TLSV1")                            => 2
    case value if value.contains("SSLV3") || value.contains("SSLV2") => 1
    case _                                                           => 0
  }

  private def containsWeakTlsToken(value: String): Boolean =
    "(?i).*(?:SSLV?2|SSLV?3|TLSV?1(?:_1)?)(?:_METHOD|_CLIENT_METHOD|_VERSION|\\b).*".r.matches(value)

  private def weakTlsToken(value: String): String =
    "(?i)(SSLV?2|SSLV?3|TLSV?1(?:_1)?)".r.findFirstMatchIn(value).map(_.group(1).toUpperCase).getOrElse("LEGACY")

  /** Normalize API spelling without changing the legacy report's algorithm label. */
  private def canonicalProtocol(value: String): String = {
    "(?i)(SSLV?[23]|TLSV?1(?:[._][0-3])?)".r.findFirstIn(value).map { token =>
      val version = token.toUpperCase.replace("V", "").replace('_', '.')
      if (version == "TLS1") "TLS1.0" else version
    }.getOrElse("LEGACY")
  }

  /** Java protocol lists (`setEnabledProtocols`, `SSLParameters`, Apache `SSLConnectionSocketFactory`, Elasticsearch
    * defaults) enabling SSLv2/SSLv3/TLSv1.0/TLSv1.1, with exact or reachable-default values. */
  private def javaProtocols(cpg: Cpg, language: AdapterLanguage, sharedIndex: Option[JavaValueIndex]): Vector[OperationObservation] = {
    if (language != AdapterLanguage.Java) return Vector.empty
    val protocolValues = new JavaConstantCollections(cpg, sharedIndex)
    cpg.call.l.toVector.flatMap { call =>
      val api = call.methodFullName
      val protocolArguments =
        if (call.name == "<init>" && api.startsWith("org.apache.http.conn.ssl.SSLConnectionSocketFactory."))
          call.argument.l.filter(_.argumentIndex == 2)
        else if (call.name == "setEnabledProtocols" && (api.startsWith("javax.net.ssl.SSLSocket.") ||
          api.startsWith("javax.net.ssl.SSLEngine.") || api.startsWith("javax.net.ssl.SSLServerSocket.")))
          call.argument.l.filter(_.argumentIndex == 1)
        else if (call.name == "<init>" && api.startsWith("javax.net.ssl.SSLParameters."))
          call.argument.l.filter(_.argumentIndex == 2)
        else if ((call.name == "setProtocols" && api.startsWith("javax.net.ssl.SSLParameters.")) ||
          (call.name == "setDefaultProtocols" && api.startsWith("org.elasticsearch.common.ssl.SslConfigurationLoader.")))
          call.argument.l.filter(_.argumentIndex == 1)
        else Nil
      val weak = protocolArguments.flatMap(argument => protocolValues.values(argument).getOrElse(Vector.empty))
        .filter(value => Set("SSLv2", "SSLv3", "TLSv1", "TLSv1.0", "TLSv1.1").contains(value))
      Option.when(weak.nonEmpty)(OperationObservation(InsecureTransport, call, "TRANSPORT", "CONFIGURE_TLS", weak.distinct.mkString(","),
        protocolVersion = weak.map(canonicalProtocol).distinct.mkString(","),
        detail = call.code + protocolArguments.flatMap(protocolValues.summary).flatMap { summary =>
          val label = if (summary.defaultPath) "reachable configuration default" else "exact configuration value"
          summary.trace.map(step => s"; $label: ${step.file.name.headOption.getOrElse("UNKNOWN")}:${step.lineNumber.getOrElse(-1)} ${step.code}")
        }.distinct.mkString)).toVector
    }
  }

  // ---------------------------------------------------------------- C17: certificate / hostname verification

  private def certificate(cpg: Cpg, language: AdapterLanguage): Vector[OperationObservation] = {
    def disabled(node: StoredNode, how: String, detail: String) =
      OperationObservation(VerificationDisabled, node, "VERIFICATION", how, detail = detail, verificationKind = "CERTIFICATE", verificationEnabled = Some(false))
    val callMatches = cpg.call.l.collect {
      case call if Set("SSL_CTX_set_verify", "SSL_set_verify").contains(call.name) &&
          call.argument.code.l.exists(_.contains("SSL_VERIFY_NONE")) => disabled(call, "DISABLE", call.code)
      case call if language == AdapterLanguage.Python &&
          ((hasDisabledPythonVerifyArgument(cpg, call) && isPythonTlsClientCall(cpg, call)) ||
            (call.name == "_create_unverified_context" && call.code.contains("(")) ||
            passesUnverifiedContextToInvokedParameter(cpg, call)) => disabled(call, "DISABLE", call.code)
    }
    val assignmentMatches = cpg.call.nameExact("<operator>.assignment").l.collect {
      case assignment if language == AdapterLanguage.Python && {
            val code = assignment.code.replace(" ", "").toUpperCase
            code.contains("VERIFY_MODE=SSL_CERT_NONE") ||
            (code.contains("CHECK_HOSTNAME=FALSE") && hasPythonTlsContextProof(cpg, assignment)) ||
            (code.contains("CURL_CA_BUNDLE") && hasEmptyPythonAssignmentValue(cpg, assignment)) ||
            (code.contains("_CREATE_DEFAULT_HTTPS_CONTEXT") && code.contains("_CREATE_UNVERIFIED_CONTEXT"))
          } => disabled(assignment, "DISABLE", assignment.code).copy(
            verificationKind = if (compactLower(assignment.code).contains("check_hostname=")) "HOSTNAME" else "CERTIFICATE")
    }
    val methodMatches = cpg.method.l.flatMap { method =>
      lazy val calls = method.ast.isCall.name.l
      val directSuccess = language match {
        case AdapterLanguage.Java => !method.isExternal && method.lineNumber.nonEmpty &&
          Set("checkServerTrusted", "checkClientTrusted").contains(method.name) && calls.isEmpty &&
          !clientOnlyTrustCallback(cpg, method)
        case AdapterLanguage.Cpp =>
          method.name.toLowerCase.contains("verify") && calls.isEmpty &&
          method.ast.isLiteral.code.l.exists(code => Set("1", "true").contains(code.toLowerCase))
        case AdapterLanguage.Python => false
      }
      val missingHostname = language == AdapterLanguage.Cpp && calls.contains("SSL_get_peer_certificate") &&
        !calls.exists(name => name.contains("check_host") || name.contains("set1_host"))
      Option.when(directSuccess || missingHostname)(method -> disabled(method, "BYPASS", method.name))
    }
    // One decision per trust manager: an X509ExtendedTrustManager has six callbacks that are emptied together.
    val perTrustManager = methodMatches.groupBy { case (method, _) =>
        if (language == AdapterLanguage.Java) ownerType(method).getOrElse(method.fullName) else method.id.toString }
      .values.map(_.minBy { case (method, _) =>
        (method.name != "checkServerTrusted", method.lineNumber.map(_.toInt).getOrElse(Int.MaxValue)) }._2)
    (callMatches ++ assignmentMatches ++ perTrustManager).toVector
  }

  /** A trust manager only installed for client sockets never validates incoming client certificates, so an empty
    * `checkClientTrusted` there is not a disabled check. */
  private def clientOnlyTrustCallback(cpg: Cpg, method: Method): Boolean = {
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

  /** Java hostname checks: a `HostnameVerifier.verify(String, SSLSession)` that unconditionally returns true, and an
    * `SSLSocket` opened to an explicit host by `SSLSocketFactory.createSocket` in a class that never verifies the
    * hostname (no HostnameVerifier use and no `setEndpointIdentificationAlgorithm`). Raw SSLSocket does not check it. */
  private def javaHostnameVerification(cpg: Cpg, language: AdapterLanguage): Vector[OperationObservation] = {
    if (language != AdapterLanguage.Java) return Vector.empty
    val alwaysTrue = cpg.method.l.filter { method =>
      !method.isExternal && method.lineNumber.nonEmpty && method.name == "verify" &&
      method.signature.contains("javax.net.ssl.SSLSession") && method.ast.isCall.filterNot(_.name.startsWith("<operator>")).isEmpty &&
      { val returns = method.ast.isReturn.l
        returns.nonEmpty && returns.forall(_.astChildren.code.l == List("true")) }
    }.map(method => OperationObservation(VerificationDisabled, method, "VERIFICATION", "BYPASS", detail = method.fullName, verificationKind = "HOSTNAME", verificationEnabled = Some(false)))
    val verifyingTypes = cpg.call.l.filter(c => c.name == "setEndpointIdentificationAlgorithm" ||
      c.name == "setHostnameVerifier" || c.name == "setDefaultHostnameVerifier" ||
      c.methodFullName.startsWith("javax.net.ssl.HostnameVerifier.verify")).flatMap(c => ownerType(c.method)).toSet
    val socketFactories = cpg.typeDecl.filter(_.inheritsFromTypeFullName.exists(_.endsWith("SSLSocketFactory")))
      .fullName.toSet
    val manualSockets = cpg.call.nameExact("createSocket").l.filter { call =>
      call.methodFullName.startsWith("javax.net.ssl.SSLSocketFactory.") &&
      call.argument.l.exists(a => a.argumentIndex > 0 && a.evalType.l.contains("java.lang.String")) &&
      !ownerType(call.method).exists(verifyingTypes.contains) &&
      // A socket factory that forwards `createSocket` to a wrapped factory (also from a lambda) is itself a factory:
      // the caller that uses the returned socket owns hostname verification.
      call.method.name != "createSocket" && !ownerType(call.method).exists(socketFactories.contains)
    }.map(call => OperationObservation(VerificationDisabled, call, "VERIFICATION", "MISSING", detail = call.code, verificationKind = "HOSTNAME", verificationEnabled = Some(false)))
    // Library and lambda forms: `ALLOW_ALL_HOSTNAME_VERIFIER`, `NoopHostnameVerifier`, `hostnameVerifier((h, s) -> true)`,
    // and Apache HttpClient's `loadTrustMaterial(store, (chain, authType) -> true)` / `TrustAllStrategy`.
    val verifierSetters = Set("setHostnameVerifier", "setDefaultHostnameVerifier", "hostnameVerifier", "setSSLHostnameVerifier")
    val libraryBypass = cpg.call.l.filterNot(_.name.startsWith("<operator>")).flatMap { call =>
      val arguments = call.argument.l.filter(_.argumentIndex > 0)
      if (arguments.exists(a => AllowAllVerifier.matches(compactLower(a.code))) ||
          (verifierSetters(call.name) && arguments.exists(returnsOnlyTrue)))
        Some(OperationObservation(VerificationDisabled, call, "VERIFICATION", "BYPASS", detail = call.code, verificationKind = "HOSTNAME", verificationEnabled = Some(false)))
      else if (call.name == "loadTrustMaterial" &&
          arguments.exists(a => compactLower(a.code).contains("trustallstrategy") || returnsOnlyTrue(a)))
        Some(OperationObservation(VerificationDisabled, call, "VERIFICATION", "BYPASS", detail = call.code, verificationKind = "CERTIFICATE", verificationEnabled = Some(false)))
      else None
    }
    (alwaysTrue ++ manualSockets ++ libraryBypass).toVector
  }

  private val AllowAllVerifier =
    """(?:\w+\.)*(?:allow_all_hostname_verifier|noophostnameverifier\.instance)|new(?:\w+\.)*(?:noop|allowall)hostnameverifier\(\)""".r

  /** A lambda (or method reference to a local method) whose every return is the literal `true`. */
  private def returnsOnlyTrue(argument: Expression): Boolean = {
    val lambdaText = compactLower(argument.code).matches("""(?s)(?:\([\w.]+\))?\(?[\w\s,]*\)?->true""")
    lambdaText || argument.ast.isMethodRef.referencedMethod.l.exists { method =>
      val returns = method.ast.isReturn.l
      method.ast.isCall.filterNot(_.name.startsWith("<operator>")).isEmpty &&
      returns.nonEmpty && returns.forall(_.astChildren.code.l == List("true"))
    }
  }

  private def ownerType(method: Method): Option[String] =
    Option(method.fullName.takeWhile(_ != ':').stripSuffix("." + method.name)).filter(_.nonEmpty)

  /** Prove a first-class `_create_unverified_context` value is actually invoked by the receiving function. */
  private def passesUnverifiedContextToInvokedParameter(cpg: Cpg, call: Call): Boolean =
    call.argument.l.filter(a => a.argumentIndex > 0 &&
      compactLower(a.code).matches("(?s).*(?:ssl\\.)?_create_unverified_context.*")).exists { argument =>
      cpg.method.nameExact(call.name).filter(m => sourceFile(m) == sourceFile(call)).l.exists { method =>
        method.parameter.index(argument.argumentIndex).l.exists { parameter =>
          val invoked = ("(?s).*\\b" + java.util.regex.Pattern.quote(parameter.name) + "\\s*\\(.*").r
          method.ast.isCall.code.l.exists(invoked.matches)
        }
      }
    }

  private def hasDisabledPythonVerifyArgument(cpg: Cpg, call: Call): Boolean = {
    val useLine = call.lineNumber.map(_.toInt).getOrElse(Int.MaxValue)
    call.argument.l.find(_.argumentName.exists(_.equalsIgnoreCase("verify")))
      .exists(argument => resolvePythonConstants(cpg, argument, useLine, 0, Set.empty).contains("FALSE"))
  }

  private def hasEmptyPythonAssignmentValue(cpg: Cpg, assignment: Call): Boolean = {
    val useLine = assignment.lineNumber.map(_.toInt).getOrElse(Int.MaxValue)
    assignment.argument.l.find(_.argumentIndex == 2)
      .exists(value => resolvePythonConstants(cpg, value, useLine, 0, Set.empty).contains("EMPTY"))
  }

  /** Security-relevant Python constants (False / empty) through exact assignments, parameters and constructor fields. */
  private def resolvePythonConstants(cpg: Cpg, expression: Expression, useLine: Int, depth: Int, seen: Set[String]): Set[String] = {
    if (depth > 6) return Set.empty
    val compact = compactLower(expression.code)
    if (Set("false", "bool(0)").contains(compact)) return Set("FALSE")
    if (Set("none", "\"\"", "''").contains(compact)) return Set("EMPTY")
    val symbol = Option.when(compact.matches("[a-z_$][a-z0-9_$]*(?:\\.[a-z_$][a-z0-9_$]*)*"))(expression.code.trim.replaceAll("\\s+", ""))
    symbol.filterNot(seen).toSet.flatMap { exact =>
      val assignments = cpg.call.nameExact("<operator>.assignment")
        .filter(a => sourceFile(a) == sourceFile(expression))
        .filter(_.lineNumber.map(_.toInt).getOrElse(Int.MinValue) <= useLine)
        .filter(_.argument.l.find(_.argumentIndex == 1).exists(_.code.replaceAll("\\s+", "") == exact)).l
        .flatMap(_.argument.l.find(_.argumentIndex == 2))
        .flatMap(value => resolvePythonConstants(cpg, value, useLine, depth + 1, seen + exact)).toSet
      val tail = exact.split("\\.").lastOption.getOrElse(exact)
      val parameters = expression.method.parameter.nameExact(tail).l.flatMap { parameter =>
        cpg.call.nameExact(expression.method.name).filter(c => sourceFile(c) == sourceFile(expression))
          .filterNot(_.method.id == expression.method.id).argument.filter(_.argumentIndex == parameter.index).l
          .flatMap(value => resolvePythonConstants(cpg, value, useLine, depth + 1, seen + exact))
      }.toSet
      val fields = pythonConstructorArguments(cpg, expression, exact)
        .flatMap(value => resolvePythonConstants(cpg, value, useLine, depth + 1, seen + exact)).toSet
      assignments ++ parameters ++ fields
    }
  }

  private def isPythonTlsClientCall(cpg: Cpg, call: Call): Boolean = {
    val code = compactLower(call.code)
    val requestMethod = Set("get", "post", "put", "delete", "patch", "head", "options", "request", "send")
      .contains(call.name.toLowerCase)
    requestMethod && resolvedPythonUrlScheme(cpg, call).contains("https") &&
    (hasPythonHttpReceiver(code) || code.contains("verify="))
  }

  /** `verify=False` matters only once the target is proven HTTPS; an ambiguous scheme is not promoted. */
  private def resolvedPythonUrlScheme(cpg: Cpg, call: Call): Option[String] = {
    val arguments = call.argument.l.filter(_.argumentIndex > 0).sortBy(_.argumentIndex)
    val urlArgument = arguments.find(_.argumentName.exists(_.equalsIgnoreCase("url"))).orElse {
      val positional = arguments.filter(_.argumentName.isEmpty)
      if (call.name.equalsIgnoreCase("request")) positional.drop(1).headOption else positional.headOption
    }
    val cache = new UrlSchemeCache(cpg)
    urlArgument.toSet.flatMap(argument =>
      resolveUrlSchemes(cpg, cache, argument, call.lineNumber.map(_.toInt).getOrElse(Int.MaxValue), 0, Set.empty, None)
    ).toVector match {
      case Vector(single) => Some(single)
      case _              => None
    }
  }

  /** `check_hostname` exists on arbitrary objects too: require a TLS-context factory for its receiver. */
  private def hasPythonTlsContextProof(cpg: Cpg, assignment: Call): Boolean = {
    val compact = compactLower(assignment.code)
    val tlsFactories = Vector("ssl.sslcontext(", "ssl.create_default_context(", "ssl._create_unverified_context(",
      "make_ssl_context(", "create_ssl_context(", "create_client_context(")
    if (tlsFactories.exists(compact.contains)) return true
    "(?i)([a-z_$][a-z0-9_$.]*)\\.check_hostname\\s*=\\s*false".r.findFirstMatchIn(assignment.code).map(_.group(1).trim)
      .exists { symbol =>
        val useLine = assignment.lineNumber.map(_.toInt).getOrElse(Int.MaxValue)
        cpg.call.nameExact("<operator>.assignment").filter(c => sourceFile(c) == sourceFile(assignment))
          .filter(_.method.id == assignment.method.id).filter(_.lineNumber.map(_.toInt).getOrElse(Int.MinValue) <= useLine)
          .exists { candidate =>
            val lhsMatches = candidate.argument.l.find(_.argumentIndex == 1).exists { lhs =>
              val normalized = lhs.code.trim
              normalized == symbol || symbol.endsWith(s".$normalized") || normalized.endsWith(s".$symbol")
            }
            lhsMatches && tlsFactories.exists(compactLower(candidate.code).contains)
          }
      }
  }

  // ---------------------------------------------------------------- C17: token signature verification

  private def jwt(cpg: Cpg): Vector[OperationObservation] = cpg.call.l.collect {
    case call if call.name == "decode" && call.code.toLowerCase.contains("jwt") && {
          val scopeCode = call.method.ast.isCall.code.l.mkString(" ").replace(" ", "").toLowerCase
          val code      = call.code.replace(" ", "").toLowerCase
          (code.contains("verify=false") || code.contains("verify_signature\":false") ||
          code.contains("verify_signature']=false") || scopeCode.contains("verify_signature\"]=false") ||
          scopeCode.contains("verify_signature']=false")) && !hasVerifiedJwtDecode(call)
        } => OperationObservation(VerificationDisabled, call, "VERIFICATION", "TOKEN_SIGNATURE", detail = call.code, verificationKind = "TOKEN_SIGNATURE", verificationEnabled = Some(false))
  }.toVector

  /** Multi-tenant JWT reads the unverified issuer first and verifies afterwards; only a lone unverified decode counts. */
  private def hasVerifiedJwtDecode(call: Call): Boolean =
    call.method.ast.isCall.nameExact("decode").filterNot(_.id == call.id).exists { sibling =>
      val code = sibling.code.replace(" ", "").toLowerCase
      code.contains("jwt") && !code.contains("verify=false") && sibling.argument.l.count(_.argumentIndex > 0) >= 2
    }

  /** Explicit secure settings are materialized too. Unknown/default settings are not asserted secure. */
  private def enabledVerification(cpg: Cpg, language: AdapterLanguage): Vector[OperationObservation] =
    if (language != AdapterLanguage.Python) Vector.empty
    else cpg.call.l.filter(call => isPythonTlsClientCall(cpg, call) &&
      call.argument.l.exists(a => a.argumentName.contains("verify") && compactLower(a.code) == "true"))
      .map(call => OperationObservation(Verification, call, "VERIFICATION", "CONFIGURE",
        detail = call.code, verificationKind = "CERTIFICATE", verificationEnabled = Some(true))).toVector

  private def modernTls(cpg: Cpg, language: AdapterLanguage): Vector[OperationObservation] =
    cpg.call.l.filter(call => !weakTlsCall(call, language) && (language match {
      case AdapterLanguage.Java => call.name == "getInstance" && call.methodFullName.contains("SSLContext")
      case AdapterLanguage.Python => Set("wrap_socket", "SSLContext").contains(call.name)
      case _ => false
    })).flatMap { call =>
      "(?i)TLS[v_]*1[._]([23])".r.findFirstMatchIn(call.code).map { m =>
        val version = "TLS1." + m.group(1)
        OperationObservation(Transport, call, "TRANSPORT", "CONFIGURE_TLS", version,
          detail = call.code, protocolVersion = version)
      }
    }.toVector

  // ---------------------------------------------------------------- helpers

  private val Whitespace = java.util.regex.Pattern.compile("\\s+")
  private def compactLower(value: String): String = Whitespace.matcher(value).replaceAll("").toLowerCase

  private def sourceFile(node: StoredNode): String = node match {
    case method: Method => method.file.name.headOption.getOrElse("UNKNOWN")
    case ast: AstNode   => ast.file.name.headOption.getOrElse("UNKNOWN")
    case _              => "UNKNOWN"
  }

  private def sourceLine(node: StoredNode): Int = node match {
    case method: Method => method.lineNumber.map(_.toInt).getOrElse(-1)
    case ast: AstNode   => ast.lineNumber.map(_.toInt).getOrElse(-1)
    case _              => -1
  }
}
