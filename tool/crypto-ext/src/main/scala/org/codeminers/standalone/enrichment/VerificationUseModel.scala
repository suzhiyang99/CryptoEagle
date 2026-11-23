package org.codeminers.standalone.enrichment

import io.shiftleft.codepropertygraph.generated.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.*
import io.shiftleft.semanticcpg.language.*
import org.codeminers.standalone.adapter.AdapterLanguage

/** Boolean verification results must constrain a later use of the data actually checked.
  * Exception-based verify APIs are deliberately not Boolean verifiers. Unknown data relationships produce no fact.
  */
private[enrichment] object VerificationUseModel {
  def observations(cpg: Cpg, language: AdapterLanguage, index: Option[JavaValueIndex]): Vector[OperationObservation] = {
    if (language == AdapterLanguage.Cpp) return Vector.empty
    val flow = new SecurityFlowContext(cpg, index)
    val candidates = flow.calls.filter(c =>
      (language == AdapterLanguage.Java &&
        ((c.name == "verify" && c.methodFullName.startsWith("java.security.Signature.")) ||
          (c.name == "isEqual" && c.methodFullName.startsWith("java.security.MessageDigest.")) ||
          (c.name == "equals" && c.methodFullName.startsWith("java.util.Arrays.")))) ||
        (language == AdapterLanguage.Python && c.name == "compare_digest" && c.code.trim.startsWith("hmac.compare_digest(")))
    if (candidates.isEmpty) return Vector.empty
    lazy val byId = flow.calls.map(c => c.id -> c).toMap
    def args(c: Call) = c.argument.l.filter(_.argumentIndex > 0).toVector
    def genuine(c: Call, module: String): Boolean = {
      val file = c.file.name.headOption
      flow.calls.exists(i => i.name == "import" && i.file.name.headOption == file && i.code == s"import(, $module)") &&
        c.method.parameter.nameExact(module).isEmpty && !flow.calls.exists(a => a.name == "<operator>.assignment" &&
          a.file.name.headOption == file && (a.method.id == c.method.id || a.method.name == "<module>") &&
          flow.arg(a, 1).exists(e => e.code.trim == module || e.code.trim.startsWith(module + ".")) && !flow.arg(a, 2).exists {
            case imported: Call => imported.name == "import"
            case _ => false
          })
    }
    def pythonInputs(tag: Call): Vector[Expression] = {
      if (!Set("digest", "hexdigest")(tag.name)) return Vector.empty
      val factory = flow.receiver(tag).flatMap(flow.origin(_)).flatMap(byId.get)
        .filter(c => c.name == "new" && c.code.trim.startsWith("hmac.new(") && genuine(c, "hmac"))
      factory.toVector.flatMap { f =>
        val updates = flow.callsByMethod.getOrElse(tag.method.id, Vector.empty).filter(c => c.name == "update" &&
          flow.before(f, c) && flow.before(c, tag) && flow.dominates(c, tag) &&
          flow.receiver(c).exists(e => flow.origin(e).contains(f.id)))
        f.argument.l.find(a => a.argumentName.contains("msg") || a.argumentIndex == 2).toVector ++
          updates.flatMap(flow.arg(_, 1))
      }
    }
    def macInputs(e: Expression): Vector[Expression] = flow.origin(e).flatMap(byId.get).toVector.flatMap { tag =>
      if (java.lang.Boolean.getBoolean("crypto.context.trace"))
        System.err.println(s"context.mac ${e.code} origin=${tag.name}:${tag.code} args=" + tag.argument.l.map(a => s"${a.argumentIndex}:${a.code}").mkString(" | "))
      if (tag.name == "doFinal" && tag.methodFullName.startsWith("javax.crypto.Mac.")) flow.javaMacInputs(tag)
      else if (language == AdapterLanguage.Python) pythonInputs(tag)
      else Vector.empty
    }
    val checkedInputs = candidates.map { check =>
      if (java.lang.Boolean.getBoolean("crypto.context.trace"))
        System.err.println(s"context.check ${check.methodFullName} ${check.code}; imports=" +
          flow.calls.filter(_.name == "import").map(_.code).mkString(" | "))
      val local = flow.callsByMethod.getOrElse(check.method.id, Vector.empty)
      val signature = language == AdapterLanguage.Java && check.name == "verify" &&
        check.methodFullName.startsWith("java.security.Signature.")
      val macComparison = if (language == AdapterLanguage.Java)
        (check.name == "isEqual" && check.methodFullName.startsWith("java.security.MessageDigest.")) ||
          (check.name == "equals" && check.methodFullName.startsWith("java.util.Arrays."))
      else check.code.trim.startsWith("hmac.compare_digest(") && genuine(check, "hmac")
      if (java.lang.Boolean.getBoolean("crypto.context.trace"))
        System.err.println(s"context.comparison $macComparison args=" + args(check).map(e => s"${e.argumentIndex}:${e.code}:${flow.origin(e)}").mkString(" | ") +
          "; assignments=" + flow.calls.filter(_.name == "<operator>.assignment").map(a => a.code + " RHS=" + flow.arg(a, 2).map(e => s"${e.label}:${e.code}")).mkString(" | "))
      val inputs = if (signature) {
        val receiver = flow.arg(check, 0)
        val owned = local.filter(c => c.methodFullName.startsWith("java.security.Signature.") &&
          flow.before(c, check) && flow.active(c) && receiver.exists(r => flow.arg(c, 0).exists(flow.same(r, _))))
        val resets = owned.filter(c => Set("initVerify", "initSign", "verify")(c.name))
        owned.filter(c => c.name == "update" && resets.forall(flow.before(_, c)) &&
          flow.dominates(c, check)).flatMap(flow.wholeInput)
      } else if (macComparison) args(check).flatMap(macInputs) else Vector.empty
      check.id -> inputs
    }.toMap
    val checkedTargets = checkedInputs.view.mapValues(_.flatMap(flow.origin(_)).toSet).toMap
    candidates.flatMap { check =>
      val local = flow.callsByMethod.getOrElse(check.method.id, Vector.empty)
      val signature = check.methodFullName.startsWith("java.security.Signature.")
      val inputs = checkedInputs(check.id)
      val targets = checkedTargets(check.id)
      if (java.lang.Boolean.getBoolean("crypto.context.trace"))
        System.err.println(s"context.inputs ${check.code} ${inputs.map(_.code)} targets=$targets")
      if (targets.isEmpty || !flow.active(check)) Vector.empty
      else {
        val uses: Vector[CfgNode] = (local.filter(c => flow.before(check, c) && flow.active(c) &&
          !c.name.startsWith("<") && !Set("verify", "isEqual", "compare_digest", "equals", "getBytes", "encode", "decode")(c.name) &&
          !c.methodFullName.startsWith("java.security.Signature.") && !c.methodFullName.startsWith("javax.crypto.Mac.") &&
          !c.methodFullName.startsWith("org.slf4j.") && !c.methodFullName.startsWith("java.util.logging.") &&
          args(c).exists(e => flow.depends(e, targets))) ++
          check.method.ast.isReturn.l.filter(r => flow.before(check, r) &&
            r.astChildren.collect { case e: Expression => e }.exists(e => flow.depends(e, targets))))
          .distinctBy(_.id).sortBy(flow.order).map(n => n: CfgNode).toVector
        // Alternative verifiers over the same full data may legitimately accept either signature/tag.
        // Diagnose an unauthenticated use only when all such checks fail, not merely when one fails.
        val peers = candidates.filter(c => c.method.id == check.method.id && targets.subsetOf(checkedTargets(c.id))).map(_.id).toSet
        val outcomes = uses.map(use => use -> flow.failureReaches(check, use, peers))
        val unsafe = outcomes.find(_._2.contains(true))
        val state = if (unsafe.nonEmpty) Some(false)
          else Option.when(outcomes.nonEmpty && outcomes.forall(_._2.contains(false)))(true)
        state.toVector.map { enforced =>
          val use = unsafe.map(_._1).orElse(uses.headOption).toVector
          val controls = check.method.controlStructure.l.filter(_.controlStructureType == "IF")
          OperationObservation(OperationObservationKind.VerificationUse, check, "VERIFICATION", "VERIFY_RESULT",
            detail = if (enforced) "Verification failure blocks the checked data's later use on the bounded local CFG."
              else "The checked data reaches a later use even when Boolean verification returns false.",
            verificationKind = if (signature) "SIGNATURE_RESULT" else "MAC_RESULT",
            verificationEnabled = Some(enforced), evidenceNodes = (inputs ++ use ++ controls).map(n => n: StoredNode).distinctBy(_.id))
        }
      }
    }
  }
}
