package org.codeminers.standalone.enrichment

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{AstNode, Call, Expression, Method}
import io.shiftleft.semanticcpg.language.*

/** Proves the identity of Python callable values forwarded through unique globals, parameters, and object fields. A
  * synthetic direct call is returned only when every discovered definition denotes the same supported callable.
  */
private[enrichment] final class PythonFunctionValueResolver(cpg: Cpg) {
  private sealed trait CallableTarget
  private final case class HashConstructor(algorithm: String) extends CallableTarget
  private final case class CipherModule(algorithm: String)    extends CallableTarget
  private case object Pbkdf2Hmac                              extends CallableTarget
  private case object RsaModule                               extends CallableTarget
  private case object RsaGeneratePrivateKey                   extends CallableTarget
  private case object DsaModule                               extends CallableTarget
  private case object DsaGeneratePrivateKey                   extends CallableTarget

  final case class SyntheticCall(name: String, code: String)

  private val HashConstructorPattern =
    "(?i)^(?:Crypto\\.Hash\\.)?(MD2|MD4|MD5|SHA|SHA1|SHA224|SHA256|SHA384|SHA512)\\.new$".r
  private val HashModulePattern =
    "(?i)^\\s*(MD2|MD4|MD5|SHA|SHA1|SHA224|SHA256|SHA384|SHA512)\\s*$".r
  private val CipherModulePattern =
    "(?i)^\\s*(AES|DES|DES3|ARC2|ARC4|Blowfish)\\s*$".r
  private val RsaImport =
    "(?i).*from\\s+cryptography\\.hazmat\\.primitives\\.asymmetric\\s+import\\s+.*\\brsa(?:\\s+as\\s+([A-Za-z_][A-Za-z0-9_]*))?.*".r
  private val RsaImportCall =
    "(?i)import\\(cryptography\\.hazmat\\.primitives\\.asymmetric,\\s*rsa(?:\\s+as\\s+([A-Za-z_][A-Za-z0-9_]*))?\\)".r

  private lazy val assignmentsByFile: Map[String, Vector[Call]] = cpg.call
    .nameExact("<operator>.assignment")
    .l
    .groupBy(fileName)
    .view
    .mapValues(_.toVector)
    .toMap
  private lazy val callsByFileAndName: Map[(String, String), Vector[Call]] = cpg.call.l
    .groupBy(call => fileName(call) -> call.name)
    .view
    .mapValues(_.toVector)
    .toMap
  private lazy val callsByFile: Map[String, Vector[Call]] =
    cpg.call.l.groupBy(fileName).view.mapValues(_.toVector).toMap
  private lazy val rsaAliasesByFile: Map[String, Set[String]] = {
    val importNodes = cpg.imports.l.flatMap { imported =>
      imported.code match {
        case RsaImport(alias) => Some(fileName(imported) -> Option(alias).getOrElse("rsa"))
        case _                => None
      }
    }
    val importCalls = cpg.call.nameExact("import").l.flatMap { imported =>
      imported.code match {
        case RsaImportCall(alias) => Some(fileName(imported) -> Option(alias).getOrElse("rsa"))
        case _                    => None
      }
    }
    (importNodes ++ importCalls).groupMap(_._1)(_._2).view.mapValues(_.toSet).toMap
  }
  private lazy val dsaAliasesByFile: Map[String, Set[String]] = {
    val sourcePattern = "from\\s+cryptography\\.hazmat\\.primitives\\.asymmetric\\s+import\\s+.*\\bdsa(?:\\s+as\\s+([A-Za-z_][A-Za-z0-9_]*))?".r
    val callPattern = "import\\(cryptography\\.hazmat\\.primitives\\.asymmetric,\\s*dsa(?:,\\s*([A-Za-z_][A-Za-z0-9_]*))?\\)".r
    val source = cpg.imports.l.flatMap(n => sourcePattern.findFirstMatchIn(n.code)
      .map(m => fileName(n) -> Option(m.group(1)).getOrElse("dsa")))
    val calls = cpg.call.nameExact("import").l.flatMap(n => callPattern.findFirstMatchIn(n.code)
      .map(m => fileName(n) -> Option(m.group(1)).getOrElse("dsa")))
    (source ++ calls).groupMap(_._1)(_._2).view.mapValues(_.toSet).toMap
  }

  // A same-name local binding must not inherit an imported DSA module's identity.
  private def dsaBindingIsUnchanged(reference: String, context: Method, file: String): Boolean = {
    val base = reference.takeWhile(_ != '.')
    !context.parameter.nameExact(base).nonEmpty &&
      !assignments(file).exists(a => lhsCode(a) == base &&
        (a.method.id == context.id || a.method.name == "<module>") &&
        !rhs(a).exists { case c: Call => c.name == "import"; case _ => false })
  }
  private lazy val importCodeByFile: Map[String, String] = {
    val imports = cpg.imports.l.map(node => fileName(node) -> node.code)
    val calls   = cpg.call.nameExact("import").l.map(node => fileName(node) -> node.code)
    (imports ++ calls).groupMap(_._1)(_._2).view.mapValues(_.mkString("\n").toLowerCase).toMap
  }

  def syntheticCall(call: Call): Option[SyntheticCall] = {
    val target =
      invocationTarget(call).flatMap(reference => unique(resolveReference(reference, call.method, fileName(call), 0)))
    target.filterNot(_ == DsaModule).map {
      case HashConstructor(algorithm) => SyntheticCall("new", s"$algorithm.new()")
      case CipherModule(algorithm) =>
        val arguments = call.code.dropWhile(_ != '(')
        SyntheticCall("new", s"$algorithm.new$arguments")
      case Pbkdf2Hmac =>
        val arguments = call.code.dropWhile(_ != '(')
        SyntheticCall("pbkdf2_hmac", s"hashlib.pbkdf2_hmac$arguments")
      case RsaGeneratePrivateKey =>
        SyntheticCall("generate_private_key", "__cryptoaware_rsa__.generate_private_key()")
      case RsaModule => SyntheticCall("generate_private_key", "__cryptoaware_rsa__.generate_private_key()")
      case DsaGeneratePrivateKey => SyntheticCall("generate_private_key", "__cryptoaware_dsa__.generate_private_key()")
      case DsaModule => SyntheticCall("generate_private_key", "__cryptoaware_dsa__.generate_private_key()")
    }
  }

  private def invocationTarget(call: Call): Option[String] = {
    val prefix = call.code.takeWhile(_ != '(').trim
    if (call.code.contains('(') && prefix.nonEmpty && prefix.matches("[A-Za-z_][A-Za-z0-9_.]*")) Some(prefix) else None
  }

  private def resolveReference(reference: String, context: Method, file: String, depth: Int): Vector[CallableTarget] = {
    if (depth > 4) Vector.empty
    else if (directTarget(reference, file).exists(t => t == DsaModule || t == DsaGeneratePrivateKey) &&
      !dsaBindingIsUnchanged(reference, context, file)) Vector.empty
    else
      directTarget(reference, file).filter {
        case DsaModule | DsaGeneratePrivateKey => dsaBindingIsUnchanged(reference, context, file)
        case _ => true
      }.toVector match {
        case direct if direct.nonEmpty => direct
        case _ if reference.endsWith(".generate_private_key") =>
          resolveReference(reference.stripSuffix(".generate_private_key"), context, file, depth + 1).collect {
            case RsaModule => RsaGeneratePrivateKey
            case DsaModule => DsaGeneratePrivateKey
          }
        case _ if reference.endsWith(".new") =>
          resolveReference(reference.stripSuffix(".new"), context, file, depth + 1).collect {
            case target: HashConstructor => target
            case target: CipherModule    => target
          }
        case _ if reference.matches("[A-Za-z_][A-Za-z0-9_]*") =>
          resolveSymbol(reference, context, file, depth + 1)
        case _ if reference.contains('.') => resolveField(reference, context, file, depth + 1)
        case _                            => Vector.empty
      }
  }

  private def resolveSymbol(name: String, context: Method, file: String, depth: Int): Vector[CallableTarget] = {
    if (depth > 4 || !name.matches("[A-Za-z_][A-Za-z0-9_]*")) Vector.empty
    else {
      val localAssignments = assignments(file)
        .filter(_.method.id == context.id)
        .filter(lhsCode(_) == name)
        .flatMap(rhs(_).toVector.flatMap(resolveExpression(_, context, file, depth + 1)))
      val globalAssignments = assignments(file)
        .filter(_.method.name == "<module>")
        .filter(lhsCode(_) == name)
        .flatMap(rhs(_).toVector.flatMap(resolveExpression(_, context, file, depth + 1)))
      val parameters = context.parameter.nameExact(name).l.flatMap { parameter =>
        val directSites = callsByFileAndName.getOrElse(file -> context.name, Vector.empty)
        val sites       = if (directSites.nonEmpty) directSites else returnedCallableSites(context, file)
        sites
          .filterNot(_.method.id == context.id)
          .flatMap(_.argument.l.filter(_.argumentIndex == parameter.index))
          .flatMap(argument => resolveExpression(argument, argument.method, file, depth + 1))
      }
      uniqueVector(localAssignments.toVector ++ globalAssignments.toVector ++ parameters.toVector)
    }
  }

  private def resolveField(reference: String, context: Method, file: String, depth: Int): Vector[CallableTarget] = {
    if (depth > 4) Vector.empty
    else {
      val parts = reference.split("\\.").toVector
      if (parts.length < 2) Vector.empty
      else {
        val objectName = parts.head
        val fieldName  = parts(1)
        val suffix     = parts.drop(2)
        val constructors = assignments(file)
          .filter(lhsCode(_) == objectName)
          .flatMap(rhs(_).toVector.flatMap(_.ast.isCall.l))
          .filterNot(_.name.startsWith("<operator>"))
        val fieldValues = constructors.flatMap { constructor =>
          cpg.method
            .nameExact("__init__")
            .filter(method => method.fullName.contains(s".${constructor.name}.__init__"))
            .l
            .flatMap { init =>
              assignments(file)
                .filter(_.method.id == init.id)
                .filter(assignment => lhsCode(assignment).endsWith(s".$fieldName"))
                .flatMap(rhs(_).toVector.map(_.code.trim))
                .flatMap { parameterName =>
                  init.parameter.nameExact(parameterName).l.flatMap { parameter =>
                    constructor.argument.l
                      .filter(_.argumentIndex == parameter.index)
                      .flatMap(argument => resolveExpression(argument, constructor.method, file, depth + 1))
                  }
                }
            }
        }.toVector
        val base = uniqueVector(fieldValues)
        suffix.foldLeft(base) { (targets, member) =>
          if (member == "generate_private_key") targets.collect {
            case RsaModule => RsaGeneratePrivateKey
            case DsaModule => DsaGeneratePrivateKey
          }
          else Vector.empty
        }
      }
    }
  }

  private def resolveExpression(
    expression: Expression,
    context: Method,
    file: String,
    depth: Int
  ): Vector[CallableTarget] = {
    def proven(reference: String): Option[CallableTarget] = directTarget(reference, file).filter {
      case DsaModule | DsaGeneratePrivateKey => dsaBindingIsUnchanged(reference, context, file)
      case _ => true
    }
    val whole = proven(expression.code.trim).toVector
    val nested =
      expression.ast.map(_.code.trim).filter(_.nonEmpty).flatMap(reference => proven(reference)).toVector
    if (whole.nonEmpty) whole
    else if (nested.exists(t => t == DsaModule || t == DsaGeneratePrivateKey) &&
      !expression.isInstanceOf[Call]) Vector.empty
    else if (nested.exists(t => t == DsaModule || t == DsaGeneratePrivateKey) &&
      expression.asInstanceOf[Call].name != "import") Vector.empty
    else if (nested.nonEmpty) uniqueVector(nested)
    else if (expression.code.trim.matches("[A-Za-z_][A-Za-z0-9_.]*"))
      resolveReference(expression.code.trim, context, file, depth)
    else Vector.empty
  }

  private def directTarget(code: String, file: String): Option[CallableTarget] = code.trim match {
    case HashConstructorPattern(value) => Some(HashConstructor(value.toUpperCase))
    case HashModulePattern(value)      => Some(HashConstructor(value.toUpperCase))
    case CipherModulePattern(value) if imported(file, "crypto.cipher") => Some(CipherModule(value.toUpperCase))
    case "pbkdf2_hmac" if imported(file, "pbkdf2_hmac") || imported(file, "hashlib") => Some(Pbkdf2Hmac)
    case "hashlib.pbkdf2_hmac" if imported(file, "hashlib") => Some(Pbkdf2Hmac)
    case value if rsaAliasesByFile.getOrElse(file, Set.empty).contains(value) => Some(RsaModule)
    case value
        if value.endsWith(".generate_private_key") &&
          rsaAliasesByFile.getOrElse(file, Set.empty).contains(value.stripSuffix(".generate_private_key")) =>
      Some(RsaGeneratePrivateKey)
    case value if dsaAliasesByFile.getOrElse(file, Set.empty).contains(value) => Some(DsaModule)
    case value if value.endsWith(".generate_private_key") &&
      dsaAliasesByFile.getOrElse(file, Set.empty).contains(value.stripSuffix(".generate_private_key")) =>
      Some(DsaGeneratePrivateKey)
    case _ => None
  }

  private def imported(file: String, value: String): Boolean =
    importCodeByFile.getOrElse(file, "").contains(value.toLowerCase)

  /** Python represents `factory_provider()(argument)` as an anonymous CALL. Accept it only if the provider's method has
    * a unique return of the nested function currently being resolved.
    */
  private def returnedCallableSites(context: Method, file: String): Vector[Call] = {
    val providers = cpg.method
      .filter(method => fileName(method) == file)
      .filterNot(_.id == context.id)
      .filter(_.ast.isReturn.codeExact(s"return ${context.name}").nonEmpty)
      .name
      .toSet
    if (providers.isEmpty) Vector.empty
    else
      callsByFile.getOrElse(file, Vector.empty).filter { call =>
        call.name.isEmpty && providers.exists(provider =>
          call.code.trim.startsWith(s"$provider()(") ||
            call.ast.isCall.nameExact(provider).nonEmpty
        )
      }
  }

  private def assignments(file: String): Vector[Call] = assignmentsByFile.getOrElse(file, Vector.empty)
  private def lhsCode(assignment: Call): String =
    assignment.argument.l.find(_.argumentIndex == 1).map(_.code.trim).getOrElse("")
  private def rhs(assignment: Call): Option[Expression] = assignment.argument.l.find(_.argumentIndex == 2)

  private def unique(values: Vector[CallableTarget]): Option[CallableTarget] = values.distinct match {
    case Vector(single) => Some(single)
    case _              => None
  }
  private def uniqueVector(values: Vector[CallableTarget]): Vector[CallableTarget] = unique(values).toVector
  private def fileName(node: AstNode): String = node.file.name.headOption.getOrElse("")
}
