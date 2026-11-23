package org.codeminers.standalone.adapter

import scala.util.Try
import scala.util.matching.Regex

final class CryptoCallMatcher(configs: Seq[AdapterConfig]) {
  private val rulesByLanguage: Map[AdapterLanguage, Vector[CompiledRule]] =
    configs
      .flatMap(_.rules)
      .groupBy(_.language)
      .view
      .mapValues(_.sortBy(rule => (-rule.priority, rule.id)).map(CompiledRule.apply).toVector)
      .toMap

  // A signature is reused by many calls, but code patterns and extracted arguments remain call-specific.
  private val signatureCandidates = scala.collection.concurrent.TrieMap.empty[
    (AdapterLanguage, String, String), Vector[CompiledRule]]

  private def candidates(language: AdapterLanguage, name: String, fullName: String): Vector[CompiledRule] =
    signatureCandidates.getOrElseUpdate((language, name, fullName),
      rulesByLanguage.getOrElse(language, Vector.empty).filter(_.matchesSignature(language, name, fullName)))

  /** Code-only rules have unconstrained signatures and must remain candidates here. */
  def mayMatch(language: AdapterLanguage, name: String, fullName: String): Boolean =
    candidates(language, name, fullName).nonEmpty

  def matchingRules(call: CallDescriptor): Vector[AdapterRule] =
    candidates(call.language, call.name, call.methodFullName)
      .filter(_.matchesCode(call.code)).map(_.rule)

  def matchAndExtract(call: CallDescriptor): Vector[AdapterMatch] =
    matchingRules(call).map { rule =>
      AdapterMatch(rule, AttributeExtractor.extract(rule, call), AttributeExtractor.sensitiveValues(rule, call))
    }

  def matchAndExtract(local: LocalDescriptor): Vector[AdapterMatch] =
    rulesByLanguage
      .getOrElse(local.language, Vector.empty)
      .filter(_.matches(local))
      .map { compiled =>
        val descriptor = CallDescriptor(local.language, local.name, "", s"${local.typeFullName} ${local.code}")
        AdapterMatch(compiled.rule, AttributeExtractor.extract(compiled.rule, descriptor), Map.empty)
      }
}

private final case class CompiledRule(
  rule: AdapterRule,
  methodFullName: Vector[Regex],
  callName: Vector[Regex],
  code: Vector[Regex],
  localType: Vector[Regex]
) {
  def matchesSignature(language: AdapterLanguage, name: String, fullName: String): Boolean =
    localType.isEmpty && language == rule.language &&
      matchesCategory(methodFullName, fullName) && matchesCategory(callName, name)

  def matchesCode(value: String): Boolean = matchesCategory(code, value)

  def matches(local: LocalDescriptor): Boolean =
    local.language == rule.language && localType.nonEmpty &&
      matchesCategory(localType, s"${local.typeFullName} ${local.code}")

  private def matchesCategory(patterns: Vector[Regex], value: String): Boolean =
    patterns.isEmpty || patterns.exists(_.pattern.matcher(value).matches())
}

private object CompiledRule {
  def apply(rule: AdapterRule): CompiledRule = CompiledRule(
    rule,
    compile(rule.api.methodFullName, rule.id, "methodFullName"),
    compile(rule.api.callName, rule.id, "callName"),
    compile(rule.api.code, rule.id, "code"),
    compile(rule.api.localType, rule.id, "localType")
  )

  /** Adapter patterns are matched against a call's whole source text, and a call written across several lines
    * keeps its newlines there.  Without DOTALL a leading `.*` cannot cross them, so every multi-line crypto call
    * was invisible to any rule carrying a `code` pattern — `hmac.new(\n  key, data,\n  digest)` among them.
    */
  private def compile(patterns: Vector[String], ruleId: String, field: String): Vector[Regex] =
    patterns.map { pattern =>
      Try(s"(?s)$pattern".r).getOrElse(
        throw new IllegalArgumentException(s"Adapter rule $ruleId has invalid $field regex: $pattern")
      )
    }
}

object AttributeExtractor {
  val Unknown = "UNKNOWN"
  private val patterns = scala.collection.concurrent.TrieMap.empty[String, Option[Regex]]

  def extract(rule: AdapterRule, call: CallDescriptor): Map[String, String] =
    rule.extract.map(spec => spec.target -> extract(spec, call).getOrElse(Unknown)).toMap

  def sensitiveValues(rule: AdapterRule, call: CallDescriptor): Map[SensitiveParameter, String] =
    rule.sensitiveParameters.map { binding =>
      val value = binding.keyword.flatMap(call.keywordArguments.get)
        .orElse(binding.argumentIndex.flatMap(call.arguments.get))
        .getOrElse(Unknown)
      binding.parameter -> value
    }.toMap

  /** Apply one adapter extraction to a value recovered from the CPG.  Keeping pattern handling here ensures that
    * direct call text and data-flow-resolved values have exactly the same normalization semantics. */
  def extractValue(spec: AttributeExtraction, value: String): Option[String] =
    applyPattern(value, spec).map(normalize(_, spec.normalize)).filter(_.nonEmpty)

  private def extract(spec: AttributeExtraction, call: CallDescriptor): Option[String] = {
    val raw = spec.source match {
      case ExtractionSource.Argument => spec.keyword.flatMap(call.keywordArguments.get)
        .orElse(spec.argumentIndex.flatMap(call.arguments.get))
      case ExtractionSource.Keyword  => spec.keyword.flatMap(call.keywordArguments.get)
      case ExtractionSource.Code     => Some(call.code)
      case ExtractionSource.Constant => spec.value
    }
    raw.flatMap(value => extractValue(spec, value))
  }

  private def applyPattern(value: String, spec: AttributeExtraction): Option[String] = spec.pattern match {
    case Some(pattern) =>
      patterns.getOrElseUpdate(pattern, Try(pattern.r).toOption).flatMap(_.findFirstMatchIn(value)).flatMap { matched =>
        Try(matched.group(spec.group)).toOption
      }
    case None => Some(stripQuotes(value.trim))
  }

  private def normalize(value: String, strategy: String): String = strategy match {
    case "NONE"  => value.trim
    case "LOWER" => value.trim.toLowerCase
    case _       => value.trim.toUpperCase
  }

  private def stripQuotes(value: String): String =
    if (value.length >= 2 && ((value.head == '"' && value.last == '"') || (value.head == '\'' && value.last == '\'')))
      value.substring(1, value.length - 1)
    else value
}
