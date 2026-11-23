package org.codeminers.standalone.rules

import io.shiftleft.codepropertygraph.generated.nodes.*
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.enrichment.SecurityFlowContext

/** Related C4 locations for a file format with an inline key-derivation header.
  * The writer and reader must share a storage boundary and a key derivation.
  */
private[rules] final class JavaFramedCipherStreams(
  flow: SecurityFlowContext, targets: Call => Vector[Method], callers: Method => Vector[Call]
) {
  import flow.{active, arg, before, origin, same}

  private case class Key(mac: String, accessor: String, secret: MethodParameterIn, header: Expression, derivation: Call)
  private case class Reader(stream: Call, entry: Method, file: MethodParameterIn, key: Key, size: Int)
  private case class Storage(owner: String, directory: String, provider: Vector[String])
  private case class Provider(signature: Vector[String], context: MethodParameterIn)
  private val ByteAllocation = """new\s+byte\s*\[\s*([0-9]+)\s*\]""".r
  private lazy val byId = flow.calls.map(call => call.id -> call).toMap

  private def calls(method: Method): Vector[Call] =
    flow.callsByMethod.getOrElse(method.id, Vector.empty).filter(active)
  private def owner(method: Method): String = method.fullName.takeWhile(_ != ':').stripSuffix("." + method.name)
  private def valueCall(value: Expression): Option[Call] = origin(value).flatMap(byId.get)
  private def parameter(value: Expression): Option[MethodParameterIn] =
    origin(value).flatMap(id => value.method.parameter.filter(_.id == id).headOption).filter(_.index > 0)
  private def returns(method: Method): Vector[Expression] =
    method.ast.isReturn.astChildren.collect { case value: Expression if value.code != "null" => value }.toVector
  private def unique[A](values: Vector[A]): Option[A] = Option.when(values.sizeIs == 1)(values.head)
  private def constructor(value: Expression, typ: String): Option[Call] = {
    def isConstructor(call: Call): Boolean = call.methodFullName.startsWith(typ + ".<init>:")
    val inline = value.ast.isCall.filter(isConstructor).l.toVector
    unique(inline).orElse(valueCall(value).flatMap { allocated =>
      unique(calls(allocated.method).filter(c => isConstructor(c) && c.lineNumber == allocated.lineNumber &&
        arg(c, 0).exists(receiver => origin(receiver).contains(allocated.id))))
    })
  }
  private def headerSize(value: Expression): Option[Int] = valueCall(value).flatMap { allocation =>
    allocation.code match {
      case ByteAllocation(size) => size.toIntOption.filter(_ > 0)
      case _ => None
    }
  }
  private def operationName(call: Call): String = call.methodFullName.takeWhile(_ != ':')
  private def ownsArgument(call: Call, value: Expression): Boolean =
    call.argument.l.exists(same(_, value))
  private def usesOnly(method: Method, value: Expression, allowed: Call => Boolean): Boolean =
    calls(method).forall(c => c.name.startsWith("<operator>") || !ownsArgument(c, value) || allowed(c))
  private def writesElements(method: Method, array: Expression): Boolean =
    calls(method).exists(c => c.name.startsWith("<operator>.assignment") && arg(c, 1).exists {
      case lhs: Call if lhs.name == "<operator>.indexAccess" => arg(lhs, 1).exists(same(_, array))
      case _ => false
    })
  private def isReturned(method: Method, value: Expression): Boolean = {
    val result = returns(method)
    result.nonEmpty && result.forall(same(_, value))
  }

  private def writerReturned(entry: Call): Boolean = {
    val result = returns(entry.method)
    isReturned(entry.method, entry) || (result.nonEmpty && result.forall {
      case access: Call if access.name == "<operator>.fieldAccess" &&
          access.ast.isFieldIdentifier.canonicalName.headOption.contains("second") =>
        arg(access, 1).exists(same(_, entry))
      case _ => false
    })
  }

  private def derivedKey(init: Call): Option[Key] = for {
    key <- arg(init, 2).flatMap(constructor(_, "javax.crypto.spec.SecretKeySpec"))
    derived <- arg(key, 1).flatMap(valueCall).filter(_.methodFullName == "javax.crypto.Mac.doFinal:byte[](byte[])")
    header <- arg(derived, 1)
    mac <- arg(derived, 0)
    factory <- valueCall(mac).filter(_.methodFullName.startsWith("javax.crypto.Mac.getInstance:"))
    algorithm <- arg(factory, 1).collect { case literal: Literal if literal.code.startsWith("\"Hmac") => literal.code }
    setup <- unique(calls(derived.method).filter(c => c.methodFullName.startsWith("javax.crypto.Mac.init:") &&
      flow.dominates(c, derived) && arg(c, 0).exists(same(_, mac))))
    if usesOnly(derived.method, mac, c => c.id == setup.id || c.id == derived.id)
    spec <- arg(setup, 1).flatMap(constructor(_, "javax.crypto.spec.SecretKeySpec"))
    getter <- arg(spec, 1).flatMap(valueCall).filter(c =>
      !c.methodFullName.contains("<unresolved") && c.argument.count(_.argumentIndex > 0) == 0)
    secret <- arg(getter, 0).flatMap(parameter)
  } yield Key(algorithm, getter.methodFullName, secret, header, derived)

  /** A prefix reader must return only after filling its buffer or throw on EOF.
    * Recognize a typed read loop with an offset, an EOF guard, and a full-length exit.
    */
  private def fullRead(call: Call): Boolean = targets(call) match {
    case Vector(method) =>
      val input = method.parameter.index(1).headOption
      val buffer = method.parameter.index(2).headOption
      val body = calls(method).filterNot(_.name.startsWith("<operator>"))
      (input, buffer, body.filter(_.name == "read")) match {
        case (Some(in), Some(bytes), Vector(read)) if
            read.methodFullName == "java.io.InputStream.read:int(byte[],int,int)" &&
            arg(read, 0).flatMap(parameter).contains(in) && arg(read, 1).flatMap(parameter).contains(bytes) =>
          val offset = arg(read, 2).collect { case id: Identifier => id.name }
          val assignment = read.inAst.isCall.nameExact("<operator>.assignment")
            .flatMap(a => arg(a, 1)).collect { case id: Identifier => id.name }.headOption
          (offset, assignment) match {
            case (Some(o), Some(n)) =>
              def compact(code: String): String = code.replaceAll("\\s+", "")
              val controls = method.controlStructure.l
              val testsFull = Set(s"$n+$o<${bytes.name}.length", s"$o+$n<${bytes.name}.length")
              val returns = method.ast.isReturn.l
              val offsetWrites = method.ast.isCall.filter(c => c.name.startsWith("<operator>.assignment") &&
                arg(c, 1).exists(_.code == o)).l
              val startAtZero = offsetWrites.count(a => a.name == "<operator>.assignment" &&
                arg(a, 2).exists(_.code == "0") && flow.dominates(a, read)) == 1
              arg(read, 3).exists(a => compact(a.code) == s"${bytes.name}.length-$o") &&
                startAtZero && offsetWrites.sizeIs == 2 &&
                controls.exists(c => compact(c.condition.code.headOption.getOrElse("")) == s"$n==-1" &&
                  c.whenTrue.ast.isCall.exists(_.code.trim.startsWith("throw "))) &&
                returns.sizeIs == 1 &&
                controls.exists(c => testsFull(compact(c.condition.code.headOption.getOrElse(""))) &&
                  c.whenFalse.ast.exists(_.id == returns.head.id) &&
                  c.whenTrue.ast.isCall.exists(a => compact(a.code) == s"$o+=$n")) &&
                read.inAst.isControlStructure.exists(_.controlStructureType == "FOR") &&
                body.forall(c => c.id == read.id || c.methodFullName.startsWith("java.io.IOException.<init>:"))
            case _ => false
          }
        case _ => false
      }
    case _ => false
  }

  private def readers(stream: Call, init: Call): Vector[Reader] = {
    val entry = for {
      input <- arg(stream, 1)
      streamParam <- parameter(input)
      key <- derivedKey(init)
      prefixParam <- parameter(key.header)
      wrapped <- arg(stream, 0)
      if isReturned(stream.method, wrapped)
      if usesOnly(stream.method, input, c => c.id == stream.id || operationName(c) == "java.io.InputStream.skip")
      if usesOnly(stream.method, wrapped, c => c.id == stream.id || fullRead(c))
    } yield (streamParam, prefixParam, key)
    entry.toVector.flatMap { (streamParam, prefixParam, key) =>
      callers(stream.method).flatMap { call =>
        for {
          input <- arg(call, streamParam.index)
          open <- constructor(input, "java.io.FileInputStream")
          fileExpr <- arg(open, 1)
          file <- parameter(fileExpr)
          header <- arg(call, prefixParam.index)
          size <- headerSize(header)
          secret <- arg(call, key.secret.index).flatMap(parameter)
          read <- unique(calls(call.method).filter(c => before(c, call) &&
            arg(c, 1).exists(same(_, input)) && arg(c, 2).exists(same(_, header)) && fullRead(c)))
          if flow.dominates(read, call) && isReturned(call.method, call)
          if usesOnly(call.method, input, c => c.id == read.id || c.id == call.id || c.id == open.id)
          if usesOnly(call.method, fileExpr, c => c.id == open.id)
          if usesOnly(call.method, header, c => c.id == read.id || c.id == call.id)
          if !writesElements(call.method, header)
        } yield Reader(stream, call.method, file, key.copy(secret = secret), size)
      }
    }
  }

  private def provider(value: Expression): Option[Provider] = for {
    access <- valueCall(value).filter(c => c.argument.count(_.argumentIndex > 0) == 0)
    receiver <- arg(access, 0).flatMap(valueCall)
    context <- arg(receiver, 1).flatMap(parameter)
    if receiver.argument.count(_.argumentIndex > 0) == 1
  } yield Provider(Vector(operationName(access), operationName(receiver)), context)

  private def directory(value: Expression): Option[(String, Expression)] = for {
    factory <- valueCall(value).filter(c => c.name == "getDir" && c.argument.count(_.argumentIndex > 0) == 2)
    context <- arg(factory, 0).filter(parameter(_).nonEmpty)
    _ <- arg(factory, 1).flatMap(parameter).filter(_.typeFullName == "java.lang.String")
    flags <- arg(factory, 2)
  } yield s"${operationName(factory)}(${flags.code})" -> context

  private def forwardsToOutput(site: Call): Boolean = calls(site.method).exists { copy =>
    if (copy.methodFullName == "java.io.InputStream.transferTo:long(java.io.OutputStream)")
      arg(copy, 1).exists(flow.depends(_, Set(site.id)))
    else targets(copy) match {
      case Vector(method) =>
        val output = method.parameter.typeFullNameExact("java.io.OutputStream").filter(p =>
          p.index > 0 && arg(copy, p.index).exists(flow.depends(_, Set(site.id)))).headOption
        output.exists { parameter =>
          calls(method).exists(c => operationName(c) == "java.io.OutputStream.write" &&
            arg(c, 0).flatMap(this.parameter).contains(parameter))
        }
      case _ => false
    }
  }

  /** Both public operations use the same storage namespace. Save returns the file's name and read
    * reconstructs a file from that name. The key provider must also match at the boundary.
    */
  private def storage(entry: Call, file: MethodParameterIn, secret: MethodParameterIn, writing: Boolean): Vector[Storage] = {
    val boundary = for {
      fileValue <- arg(entry, file.index)
      fileParam <- parameter(fileValue)
      keyProvider <- arg(entry, secret.index).flatMap(provider)
      if usesOnly(entry.method, fileValue, _.id == entry.id)
      if (if (writing) writerReturned(entry) else isReturned(entry.method, entry))
    } yield fileParam -> keyProvider
    boundary.toVector.flatMap { (fileParam, keyProvider) =>
      callers(entry.method).filter(site => owner(site.method) == owner(entry.method)).flatMap { site =>
        for {
          fileValue <- arg(site, fileParam.index)
          directoryValue <- if (writing) {
            valueCall(fileValue).filter(c => c.methodFullName.startsWith("java.io.File.createTempFile:") &&
              c.argument.count(_.argumentIndex > 0) == 3).flatMap(arg(_, 3)).filter { _ =>
              forwardsToOutput(site) && returns(site.method).exists(r => valueCall(r).exists(c =>
                c.methodFullName.startsWith("java.io.File.getName:") && arg(c, 0).exists(same(_, fileValue))))
            }
          } else {
            constructor(fileValue, "java.io.File").filter(c =>
              c.methodFullName == "java.io.File.<init>:void(java.io.File,java.lang.String)" &&
              arg(c, 2).flatMap(parameter).nonEmpty &&
              usesOnly(site.method, fileValue, use => use.id == site.id || use.id == c.id) &&
              isReturned(site.method, site)).flatMap(arg(_, 1))
          }
          signature <- directory(directoryValue)
          if arg(site, keyProvider.context.index).exists(same(_, signature._2))
        } yield Storage(owner(site.method), signature._1, keyProvider.signature)
      }
    }
  }

  private def headerEnabled(write: Call, stream: Call, entry: Call): Boolean =
    flow.dominates(write, stream) || (write.inAst.isControlStructure.l.filter(_.controlStructureType == "IF") match {
      case guard :: Nil =>
        guard.whenTrue.ast.exists(_.id == write.id) && guard.condition.headOption.flatMap(parameter)
          .flatMap(p => arg(entry, p.index)).exists(_.code.trim == "true")
      case _ => false
    })

  def related(writer: Call, init: Call, decryptions: => Vector[(Call, Call, String, String)],
              algorithm: String, mode: String): Vector[FindingEvidence] = {
    if (mode != "CTR" || !writer.methodFullName.startsWith("javax.crypto.CipherOutputStream.<init>:")) return Vector.empty
    val pair = for {
      output <- arg(writer, 1)
      open <- constructor(output, "java.io.FileOutputStream")
      if open.argument.count(_.argumentIndex > 0) == 1
      file <- arg(open, 1).flatMap(parameter)
      key <- derivedKey(init)
      size <- headerSize(key.header)
      write <- unique(calls(writer.method).filter(c => before(c, writer) &&
        Set("java.io.FileOutputStream.write", "java.io.OutputStream.write")(operationName(c)) &&
        arg(c, 0).exists(same(_, output)) && arg(c, 1).exists(same(_, key.header)) &&
        c.argument.count(_.argumentIndex > 0) == 1))
      if usesOnly(writer.method, output, c => c.id == open.id || c.id == writer.id || c.id == write.id)
      if usesOnly(writer.method, key.header, c => c.id == write.id || c.id == key.derivation.id ||
        (operationName(c) == "java.security.SecureRandom.nextBytes" && flow.dominates(c, key.derivation)) ||
        (operationName(c).endsWith("Pair.<init>") && c.ast.exists(_.id == writer.id)))
      if !writesElements(writer.method, key.header)
    } yield (file, key, size, write)
    pair.toVector.flatMap { (file, key, size, write) =>
      val stores = callers(writer.method).filter(headerEnabled(write, writer, _))
        .flatMap(storage(_, file, key.secret, writing = true)).toSet
      if (stores.isEmpty) Vector.empty
      else decryptions.filter { (_, _, alg, m) => alg == algorithm && m == mode }.flatMap { (reader, setup, _, _) =>
        readers(reader, setup).filter(r => r.size == size && r.key.mac == key.mac && r.key.accessor == key.accessor)
          .filter(r => callers(r.entry).exists(site =>
            storage(site, r.file, r.key.secret, writing = false).exists(stores)))
          .flatMap { r =>
            for {
              srcFile <- r.stream.file.name.headOption
              line <- r.stream.lineNumber
            } yield FindingEvidence("DECRYPTION_CONSUMER", "CALL", r.stream.code, Some(FindingLocation(srcFile, line)))
          }
      }
    }.distinct
  }
}
