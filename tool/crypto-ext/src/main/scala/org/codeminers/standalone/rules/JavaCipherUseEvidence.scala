package org.codeminers.standalone.rules

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.*
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.JavaFrontend
import org.codeminers.standalone.enrichment.{JavaConstantPathEvaluator, SecurityFlowContext}

/** C4-only lifecycle projection. Attribute uncertainty (such as a bare RC4 name)
  * must not erase a proven encryption consumer, and initializing an unused local
  * Cipher must not invent one. Other rules continue to use their original facts.
  */
private[rules] final class JavaCipherUseEvidence(cpg: Cpg) {
  private val paths = new JavaConstantPathEvaluator(cpg)
  private val flow = new SecurityFlowContext(cpg)
  private lazy val calls = cpg.call.l.toVector
  private lazy val callById = calls.map(c => c.id -> c).toMap
  private lazy val writes = calls.filter(_.name == "<operator>.assignment")
  private lazy val localWrites = writes.groupBy(_.method.id)
  private lazy val arrayCopies = calls.filter(c => c.name == "arraycopy" && c.methodFullName.startsWith("java.lang.System.arraycopy"))
    .groupBy(_.method.id)
  private lazy val methods = cpg.method.l.filterNot(_.isExternal).toVector.groupBy(_.name)
  private lazy val callSites = calls.groupBy(_.name)
  private def arg(c: Call, n: Int): Option[Expression] = c.argument.l.find(_.argumentIndex == n)
  private def file(n: AstNode) = n.file.name.headOption.getOrElse("")
  private def order(n: AstNode) = (n.lineNumber.getOrElse(Int.MaxValue), n.columnNumber.getOrElse(Int.MaxValue), n.id)
  private def before(a: AstNode, b: AstNode) = summon[Ordering[(Int, Int, Long)]].lt(order(a), order(b))
  private def active(c: Call) = !paths.execution(c).contains(false)
  private def cipher(c: Call) = c.methodFullName.startsWith("javax.crypto.Cipher.")
  private def factory(c: Call) = (cipher(c) && c.name == "getInstance") ||
    (c.name == "getCipher" && c.methodFullName.startsWith("hudson.util.Secret.getCipher:"))
  private def owner(m: Method) = m.fullName.takeWhile(_ != ':').stripSuffix("." + m.name)
  private def target(c: Call): Vector[Method] = methods.getOrElse(c.name, Vector.empty).filter(m =>
    m.fullName == c.methodFullName || (c.methodFullName.contains("<unresolvedSignature>") &&
      c.methodFullName.takeWhile(_ != ':').stripSuffix("." + c.name) == owner(m) &&
      c.argument.count(_.argumentIndex > 0) == m.parameter.count(_.index > 0)))
  private def callers(m: Method) = callSites.getOrElse(m.name, Vector.empty)
    .filter(c => active(c) && c.method.id != m.id && target(c).exists(_.id == m.id)).take(16)
  private def returns(m: Method) = m.ast.isReturn.astChildren.collect { case e: Expression => e }.toVector

  private case class Origin(factory: Call, instances: Vector[Long] = Vector.empty)
  private type Env = Map[Long, Expression]
  private def same(a: Origin, b: Origin): Boolean = a.factory.id == b.factory.id &&
    (a.instances == b.instances || a.instances.isEmpty || b.instances.isEmpty)
  private def field(e: Expression): Option[(String, String)] = e match {
    case c: Call if c.name == "<operator>.fieldAccess" && arg(c, 1).exists(_.code == "this") =>
      c.ast.isFieldIdentifier.canonicalName.headOption.map(name => owner(c.method) -> name)
    case _ => None
  }

  /** Resolve aliases and single constructor-field definitions. Distinct owners,
    * reassigned/ambiguous fields, and different helper invocations stay distinct.
    */
  private def origin(e: Expression, env: Env = Map.empty, depth: Int = 0,
    seen: Set[Long] = Set.empty): Option[Origin] = {
    if (depth > 10 || seen(e.id)) return None
    val next = seen + e.id
    def follow(x: Expression, bindings: Env = env) = origin(x, bindings, depth + 1, next)
    e match {
      case c: Call if factory(c) && active(c) => Some(Origin(c))
      case i: Identifier =>
        val assigned = localWrites.getOrElse(i.method.id, Vector.empty).filter(a =>
          before(a, i) && active(a) && arg(a, 1).exists(_.code.trim == i.name)).sortBy(order)
        assigned.lastOption match {
          case Some(a) if assigned.size == 1 || paths.execution(a).contains(true) => arg(a, 2).flatMap(follow(_))
          case Some(_) => None
          case None => i.method.parameter.nameExact(i.name).headOption.flatMap { p =>
            env.get(p.id).flatMap(x => follow(x, env - p.id)).orElse {
              val sites = callers(p.method)
              if (sites.size != 1) None else arg(sites.head, p.index).flatMap(follow(_))
            }
          }
        }
      case c: Call if field(c).nonEmpty =>
        val key = field(c)
        val all = writes.filter(a => active(a) && arg(a, 1).flatMap(field) == key &&
          file(a) == file(c))
        val local = all.filter(a => a.method.id == c.method.id && before(a, c)).sortBy(order)
        val reaching = if (local.nonEmpty && paths.execution(local.last).contains(true)) Vector(local.last)
          else all.filter(a => JavaFrontend.isInitializer(a.method.name))
        if (reaching.size != 1 || (local.isEmpty && all.exists(a => !JavaFrontend.isInitializer(a.method.name)))) None
        else arg(reaching.head, 2).flatMap(follow(_))
      case c: Call if c.name == "<operator>.cast" => c.argument.l.lastOption.flatMap(follow(_))
      case c: Call if !c.name.startsWith("<operator>.") =>
        val definitions = target(c)
        if (definitions.size != 1) None else {
          val method = definitions.head
          val bindings = env ++ method.parameter.l.flatMap(p => arg(c, p.index).map(p.id -> _))
          val result = returns(method).map(follow(_, bindings))
          if (result.size != 1) None else result.head.map(o => o.copy(instances = o.instances :+ c.id))
        }
      case _ => None
    }
  }

  private def stream(c: Call) = c.name == "<init>" &&
    Set("javax.crypto.CipherOutputStream.<init>", "javax.crypto.CipherInputStream.<init>").exists(c.methodFullName.startsWith)
  /** The Cipher a consumer uses: the receiver of update/doFinal, or the cipher wrapped by a Cipher stream. */
  private def receiver(c: Call) = arg(c, if (stream(c)) 2 else 0).flatMap(origin(_))
  private def direction(c: Call, o: Origin): Option[Boolean] = arg(c, 1).flatMap(mode(_, o))
  /** An init mode may be a parameter of the helper that initializes the cipher; it is then resolved
    * at the helper invocation recorded in the origin, so each invocation keeps its own direction.
    */
  private def mode(e: Expression, o: Origin, depth: Int = 0): Option[Boolean] = {
    val code = e.code.replaceAll("\\s+", "")
    if (code.endsWith("Cipher.ENCRYPT_MODE") || code == "1") Some(true)
    else if (code.endsWith("Cipher.DECRYPT_MODE") || code == "2") Some(false)
    else e match {
      case i: Identifier if depth < 4 && !localWrites.getOrElse(i.method.id, Vector.empty)
          .exists(a => arg(a, 1).exists(_.code.trim == i.name)) =>
        i.method.parameter.nameExact(i.name).headOption.flatMap { p =>
          o.instances.flatMap(callById.get).find(c => target(c).exists(_.id == p.method.id))
            .flatMap(arg(_, p.index)).flatMap(mode(_, o, depth + 1))
        }
      case _ => None
    }
  }
  private lazy val initializers = calls.filter(c => cipher(c) && c.name == "init" && active(c))
    .flatMap(c => receiver(c).map(c -> _))
  private def initializer(use: Call, o: Origin): Option[Call] = {
    val candidates = initializers.filter((c, found) => same(o, found))
    val local = candidates.filter((c, _) => c.method.id == use.method.id && before(c, use)).sortBy(p => order(p._1))
    if (local.nonEmpty && (local.size == 1 || paths.execution(local.last._1).contains(true))) Some(local.last._1)
    else {
      val other = candidates.filter((c, _) => c.method.id != use.method.id)
      Option.when(other.size == 1)(other.head._1)
    }
  }

  /** Local byte-output dependencies, with explicit transformation barriers for
    * slices. This does not promote a nearby HMAC/KDF to ciphertext authentication.
    */
  private def depends(e: Expression, ids: Set[Long], depth: Int = 0, seen: Set[Long] = Set.empty): Boolean = {
    if (ids(e.id)) return true
    if (depth > 10 || seen(e.id) || ids.isEmpty) return false
    val next = seen + e.id
    e match {
      case i: Identifier =>
        val assigned = localWrites.getOrElse(i.method.id, Vector.empty).filter(a => active(a) && before(a, i) &&
          arg(a, 1).exists(_.code.trim == i.name)).sortBy(order)
        // System.arraycopy(src, _, dst, _, _) writes src bytes into dst, e.g. IV/ciphertext/tag concatenation.
        val copied = arrayCopies.getOrElse(i.method.id, Vector.empty).filter(c => active(c) && before(c, i) &&
          arg(c, 3).exists(_.code.trim == i.name))
        (assigned.isEmpty && i.method.parameter.nameExact(i.name).exists(p => ids(p.id))) ||
        assigned.lastOption.exists(a => (assigned.size == 1 || paths.execution(a).contains(true)) &&
          arg(a, 2).exists(depends(_, ids, depth + 1, next))) ||
        copied.exists(c => arg(c, 1).exists(depends(_, ids, depth + 1, next)))
      case c: Call if Set("<operator>.indexAccess", "<operator>.fieldAccess", "<operator>.equals",
        "<operator>.notEquals", "<operator>.lessThan", "<operator>.greaterThan")(c.name) => false
      case c: Call if c.name == "<operator>.conditional" =>
        val branches = c.argument.l.filter(_.argumentIndex > 1)
        arg(c, 1).flatMap(flow.boolValue(_, c, false)) match {
          case Some(truth) => arg(c, if (truth) 2 else 3).exists(depends(_, ids, depth + 1, next))
          case None => branches.nonEmpty && branches.forall(depends(_, ids, depth + 1, next))
        }
      case c: Call if c.name.startsWith("<operator>.") || Set("encode", "encodeToString", "getBytes")(c.name) =>
        c.argument.l.exists(depends(_, ids, depth + 1, next))
      case b: Block => b.astChildren.collect { case x: Expression => x }.lastOption.exists(depends(_, ids, depth + 1, next))
      case _ => false
    }
  }
  private def protectedOutput(method: Method, output: Set[Long], depth: Int = 0): Boolean = {
    val local = method.ast.isCall.l.filter(active)
    val tags = local.filter(c => c.name == "doFinal" && c.methodFullName.startsWith("javax.crypto.Mac.")).filter { tag =>
      val input = flow.javaMacInputs(tag)
      output.forall(id => input.exists(depends(_, Set(id))))
    }.map(_.id).toSet
    // A helper that MACs one of its parameters and returns that parameter together with the tag
    // (getMacBody(mac, ciphertext)) protects whatever is passed in that position.
    val helperTags = if (depth >= 2) Set.empty[Long] else local.filter(c => !c.name.startsWith("<operator>.")).flatMap { h =>
      target(h) match {
        case Vector(m) => m.parameter.l.filter(_.index > 0).find(p =>
            arg(h, p.index).exists(a => output.forall(id => depends(a, Set(id)))) &&
            protectedOutput(m, Set(p.id), depth + 1)).map(_ => h.id)
        case _ => None
      }
    }.toSet
    // `return null` (typically an error path in a catch block) carries no ciphertext to authenticate.
    val results = returns(method).filterNot(_.code.trim == "null")
    results.nonEmpty && ((tags.nonEmpty && results.forall(e => depends(e, output) && depends(e, tags))) ||
      (helperTags.nonEmpty && results.forall(depends(_, helperTags))))
  }
  private def authenticated(use: Call, output: Set[Long]): Boolean =
    protectedOutput(use.method, output) || {
      val sites = callers(use.method)
      returns(use.method).exists(depends(_, output)) && sites.nonEmpty &&
        sites.forall(site => protectedOutput(site.method, Set(site.id)))
    }

  def forC4(facts: Vector[CryptoOperationFact]): Vector[CryptoOperationFact] = {
    val nodes = cpg.cryptoOperation.filter(_.semanticKind.isEmpty).l
    val byCall = nodes.flatMap(o => o.realizingCall.l.map(c => c.id -> o.id)).toMap
    val byId = facts.map(f => f.operationId -> f).toMap
    val jcaIds = nodes.filter(o => o.realizingCall.l.exists(cipher)).map(_.id).toSet
    // Unproven JCA lifecycles cannot create an absence finding. Their facts for
    // C1/C2/C3/etc. are not changed by this C4-only projection.
    val retained = facts.map(f => if (jcaIds(f.operationId)) f.copy(encryptionConsumer = Some(false)) else f)
    // Wrapping an encrypt-mode cipher in a Cipher stream hands it the data to encrypt, like update/doFinal.
    val uses = calls.filter(c => ((cipher(c) && Set("doFinal", "update")(c.name)) || stream(c)) &&
      active(c) && c.argument.exists(_.argumentIndex > 0))
    val framedStreams = new JavaFramedCipherStreams(flow, target, callers)
    lazy val decryptions = uses.filter(_.methodFullName.startsWith("javax.crypto.CipherInputStream.<init>:")).flatMap { use =>
      for {
        found <- receiver(use)
        init <- initializer(use, found) if direction(init, found).contains(false)
        configured <- byCall.get(found.factory.id).flatMap(byId.get)
      } yield (use, init, configured.algorithm, configured.mode)
    }
    val proven = uses.flatMap { use =>
      for {
        found <- receiver(use)
        init <- initializer(use, found) if direction(init, found).contains(true)
        template <- byCall.get(init.id).flatMap(byId.get).orElse(byCall.get(found.factory.id).flatMap(byId.get))
        configured <- byCall.get(found.factory.id).flatMap(byId.get)
        if !Set("", "UNKNOWN", "RSA")(configured.algorithm)
        // No guessed AES mode: partial resolution is sufficient only when the
        // primitive is intrinsically non-AEAD (including bare legacy ciphers).
        if !Set("", "UNKNOWN")(configured.mode) ||
          Set("RC4", "ARC4", "RC2", "DES", "DESEDE", "BLOWFISH", "IDEA")(configured.algorithm)
      } yield {
        val linked = Vector(found.factory, init, use)
        val integrity = authenticated(use, Set(use.id))
        val related = if (integrity || configured.providesIntegrity) Vector.empty
          else framedStreams.related(use, init, decryptions, configured.algorithm, configured.mode)
        template.copy(operationId = init.id, opType = CryptoQLRules.SymmetricEncryption, operation = "ENCRYPT",
          algorithm = configured.algorithm, mode = configured.mode, padding = configured.padding,
          providesIntegrity = configured.providesIntegrity, srcFile = file(use), lineNo = use.lineNumber.getOrElse(-1),
          scopes = Set(use.method.fullName), encryptionConsumer = Some(true),
          ciphertextAuthentication = Some(integrity),
          evidence = (template.evidence ++ configured.evidence ++ linked.map(c => FindingEvidence("CALL", "CALL", c.code)) ++
            Vector(FindingEvidence("ENCRYPTION_CONSUMER", "CALL", s"${file(use)}:${use.lineNumber.getOrElse(-1)} ${use.code}")) ++
            related)
            .distinct)
      }
    }.groupBy(_.operationId).values.map { group =>
      // update/doFinal on one initialized object describe one encryption lifecycle.
      val last = group.maxBy(_.lineNo)
      last.copy(evidence = group.flatMap(_.evidence).distinct.toVector)
    }.toVector
    retained ++ proven
  }
}
