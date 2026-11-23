package org.codeminers.standalone.adapter

import org.codeminers.standalone.ontology.CryptoOperationType

enum AdapterLanguage(val storedValue: String) {
  case Java   extends AdapterLanguage("JAVA")
  case Python extends AdapterLanguage("PYTHON")
  case Cpp    extends AdapterLanguage("C_CPP")
}

object AdapterLanguage {
  def parse(value: String): Either[String, AdapterLanguage] =
    AdapterLanguage.values
      .find(_.storedValue == value.trim.toUpperCase)
      .toRight(s"Unsupported adapter language: $value")
}

enum LifecycleRole(val storedValue: String) {
  case Factory       extends LifecycleRole("FACTORY")
  case Initialize    extends LifecycleRole("INITIALIZE")
  case ParameterSpec extends LifecycleRole("PARAMETER_SPEC")
  case Update        extends LifecycleRole("UPDATE")
  case Finalize      extends LifecycleRole("FINALIZE")
  case Generate      extends LifecycleRole("GENERATE")
  case Derive        extends LifecycleRole("DERIVE")
  case Hash          extends LifecycleRole("HASH")
  case Mac           extends LifecycleRole("MAC")
  case Sign          extends LifecycleRole("SIGN")
  case Verify        extends LifecycleRole("VERIFY")
  case Random        extends LifecycleRole("RANDOM")
}

object LifecycleRole {
  def parse(value: String): Either[String, LifecycleRole] =
    LifecycleRole.values
      .find(_.storedValue == value.trim.toUpperCase)
      .toRight(s"Unsupported lifecycle role: $value")
}

enum SensitiveParameter(val storedValue: String) {
  case Key      extends SensitiveParameter("KEY")
  case Iv       extends SensitiveParameter("IV")
  case Salt     extends SensitiveParameter("SALT")
  case Nonce    extends SensitiveParameter("NONCE")
  case Password extends SensitiveParameter("PASSWORD")
}

object SensitiveParameter {
  def parse(value: String): Either[String, SensitiveParameter] =
    SensitiveParameter.values
      .find(_.storedValue == value.trim.toUpperCase)
      .toRight(s"Unsupported sensitive parameter: $value")
}

enum ExtractionSource(val storedValue: String) {
  case Argument extends ExtractionSource("ARGUMENT")
  case Keyword  extends ExtractionSource("KEYWORD")
  case Code     extends ExtractionSource("CODE")
  case Constant extends ExtractionSource("CONSTANT")
}

object ExtractionSource {
  def parse(value: String): Either[String, ExtractionSource] =
    ExtractionSource.values
      .find(_.storedValue == value.trim.toUpperCase)
      .toRight(s"Unsupported extraction source: $value")
}

final case class ApiPattern(
  methodFullName: Vector[String] = Vector.empty,
  callName: Vector[String] = Vector.empty,
  code: Vector[String] = Vector.empty,
  localType: Vector[String] = Vector.empty
) {
  def isEmpty: Boolean = methodFullName.isEmpty && callName.isEmpty && code.isEmpty && localType.isEmpty
}

final case class AttributeExtraction(
  target: String,
  source: ExtractionSource,
  argumentIndex: Option[Int] = None,
  keyword: Option[String] = None,
  pattern: Option[String] = None,
  group: Int = 1,
  value: Option[String] = None,
  normalize: String = "UPPER"
)

final case class ParameterBinding(
  parameter: SensitiveParameter,
  argumentIndex: Option[Int] = None,
  keyword: Option[String] = None
)

final case class AdapterRule(
  id: String,
  language: AdapterLanguage,
  operationType: CryptoOperationType,
  operation: String,
  lifecycleRole: LifecycleRole,
  api: ApiPattern,
  extract: Vector[AttributeExtraction] = Vector.empty,
  sensitiveParameters: Vector[ParameterBinding] = Vector.empty,
  priority: Int = 0
)

final case class AdapterConfig(version: Int, language: AdapterLanguage, rules: Vector[AdapterRule])

/** A frontend-neutral view of a CPG CALL. Joern nodes are adapted to this view by the enrichment layer, keeping this
  * package independently testable.
  */
final case class CallDescriptor(
  language: AdapterLanguage,
  name: String,
  methodFullName: String,
  code: String,
  arguments: Map[Int, String] = Map.empty,
  keywordArguments: Map[String, String] = Map.empty
)

/** C/C++ template-heavy libraries can encode the algorithm in a LOCAL type without emitting a useful constructor CALL.
  * Keeping that frontend quirk in the adapter layer prevents it from leaking into CryptoQL.
  */
final case class LocalDescriptor(language: AdapterLanguage, name: String, typeFullName: String, code: String)

final case class AdapterMatch(
  rule: AdapterRule,
  attributes: Map[String, String],
  sensitiveParameters: Map[SensitiveParameter, String]
)
