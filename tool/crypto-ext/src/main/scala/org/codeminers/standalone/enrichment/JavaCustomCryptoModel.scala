package org.codeminers.standalone.enrichment

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.{Call, Expression, Method}
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.adapter.*
import org.codeminers.standalone.ontology.CryptoOperationType

/** Recognize a custom repeating-key XOR from its body, not the class/function name.
  * A same-name helper, a checksum reduction, or a one-off xor does not satisfy this model. */
private[enrichment] final class JavaCustomCryptoModel(cpg: Cpg, sharedIndex: Option[JavaValueIndex] = None) {
  private val index = sharedIndex.getOrElse(new JavaValueIndex(cpg))
  private lazy val methods = index.methodsByName
  private def arg(c: Call, i: Int): Option[Expression] = c.argument.l.find(_.argumentIndex == i)
  private def owner(full: String, name: String) = full.takeWhile(_ != ':').stripSuffix("." + name)
  private def matches(c: Call, m: Method) = c.methodFullName == m.fullName ||
    (c.methodFullName.contains("<unresolvedSignature>") && owner(c.methodFullName, c.name) == owner(m.fullName, m.name) &&
      c.argument.count(_.argumentIndex > 0) == m.parameter.count(_.index > 0))
  private val summaries = new JavaValueSummaries(cpg, sharedIndex = Some(index))
  private lazy val keyProducerReturns = {
    val cryptoOwners = index.calls.filter(c => c.methodFullName.startsWith("javax.crypto.spec.SecretKeySpec.<init>:") ||
      c.methodFullName.startsWith("javax.crypto.Cipher.getInstance:")).groupBy(c => owner(c.method.fullName, c.method.name))
      .collect { case (typ, calls) if calls.exists(_.methodFullName.startsWith("javax.crypto.spec.SecretKeySpec.")) &&
        calls.exists(_.methodFullName.startsWith("javax.crypto.Cipher.")) => typ }.toSet
    methods.valuesIterator.flatten.filter(m => m.name.matches("generate(?:Random)?Key") &&
      cryptoOwners(owner(m.fullName, m.name))).flatMap { method =>
      method.ast.isReturn.astChildren.collect { case c: Call if arg(c, 0).nonEmpty &&
        Set("substring", "getBytes", "toString", "replace").contains(c.name) => c.id }
    }.toSet
  }
  private lazy val xorMethods = methods.valuesIterator.flatten.filter { m =>
    val params = m.parameter.l.filter(_.index > 0).sortBy(_.index)
    params.size == 2 && params.forall(_.typeFullName == "byte[]") &&
      m.ast.isCall.nameExact("<operator>.assignmentXor", "<operators>.assignmentXor").l.exists { write =>
        val lhs = arg(write, 1).collect { case c: Call if c.name == "<operator>.indexAccess" => c }
        val rhs = arg(write, 2).collect { case c: Call if c.name == "<operator>.indexAccess" => c }
        lhs.exists(c => arg(c, 1).exists(_.code == params.head.name)) && rhs.exists { access =>
          arg(access, 1).exists(_.code == params.last.name) && arg(access, 2).exists(_.ast.isCall
            .nameExact("<operator>.modulo").argument.argumentIndex(2).code.l
            .exists(_.replace(" ", "") == params.last.name + ".length"))
        }
      }
  }.toVector

  def matchCall(call: Call): Option[AdapterMatch] = {
    if (SourceClassifier.isJavaMersenneTwisterOutput(call)) {
      val rule = AdapterRule("java-structural-mt-output", AdapterLanguage.Java, CryptoOperationType.Randomness,
        "GENERATE", LifecycleRole.Random, ApiPattern(callName = Vector(call.name)))
      Some(AdapterMatch(rule, Map("source" -> "WEAK_RANDOM"), Map.empty))
    } else if (keyProducerReturns(call.id) && summaries.resolve(call, material = true).exists(_.sources.exists(source =>
      SourceClassifier.classify(source) == org.codeminers.standalone.ontology.CryptoSourceType.WeakRandom))) {
      // Explicit key-generator API in a class with actual JCE key/cipher use. Library callers can be outside the snapshot.
      val binding = ParameterBinding(SensitiveParameter.Key, argumentIndex = Some(0))
      val rule = AdapterRule("java-declared-key-generator", AdapterLanguage.Java, CryptoOperationType.SymmetricEncryption,
        "KEY_GENERATION", LifecycleRole.Generate, ApiPattern(callName = Vector(call.name)), sensitiveParameters = Vector(binding))
      Some(AdapterMatch(rule, Map("algorithm" -> "UNKNOWN"), Map(SensitiveParameter.Key -> arg(call, 0).get.code)))
    } else {
      val matched = xorMethods.filter(matches(call, _))
      if (matched.size != 1) None else {
        val binding = ParameterBinding(SensitiveParameter.Key, argumentIndex = Some(2))
        val rule = AdapterRule("java-structural-repeating-xor", AdapterLanguage.Java, CryptoOperationType.SymmetricEncryption,
          "TRANSFORM", LifecycleRole.Finalize, ApiPattern(callName = Vector(call.name)), sensitiveParameters = Vector(binding))
        Some(AdapterMatch(rule, Map("algorithm" -> "XOR", "mode" -> "REPEATING_KEY"),
          arg(call, 2).map(e => Map(SensitiveParameter.Key -> e.code)).getOrElse(Map.empty)))
      }
    }
  }
}
