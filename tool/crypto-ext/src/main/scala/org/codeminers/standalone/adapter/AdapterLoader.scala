package org.codeminers.standalone.adapter

import org.codeminers.standalone.ontology.CryptoOperationType
import org.yaml.snakeyaml.Yaml

import java.io.{InputStream, Reader}
import java.nio.file.{Files, Path}
import scala.jdk.CollectionConverters.*
import scala.util.{Try, Using}

object AdapterLoader {
  private type YMap = java.util.Map[String, Object]

  def load(path: Path): Either[Vector[String], AdapterConfig] =
    Using(Files.newBufferedReader(path)) { reader => decode(reader, path.toString) }.toEither.left
      .map(error => Vector(s"Unable to read adapter $path: ${error.getMessage}"))
      .flatMap(identity)

  def loadResource(resource: String): Either[Vector[String], AdapterConfig] = {
    val stream = Option(getClass.getClassLoader.getResourceAsStream(resource))
    stream.toRight(Vector(s"Adapter resource not found: $resource")).flatMap { input =>
      Using(input)(in => decode(in, resource)).toEither.left
        .map(error => Vector(s"Unable to read adapter resource $resource: ${error.getMessage}"))
        .flatMap(identity)
    }
  }

  private def decode(reader: Reader, source: String): Either[Vector[String], AdapterConfig] =
    decodeRoot(new Yaml().load[Object](reader), source)

  private def decode(input: InputStream, source: String): Either[Vector[String], AdapterConfig] =
    decodeRoot(new Yaml().load[Object](input), source)

  private def decodeRoot(raw: Object, source: String): Either[Vector[String], AdapterConfig] = {
    val root = asMap(raw).toRight(Vector(s"$source: root value must be a mapping"))
    root.flatMap { values =>
      val versionResult = int(values, "version").toRight(Vector(s"$source: missing integer 'version'"))
      val languageResult = string(values, "language")
        .toRight(Vector(s"$source: missing 'language'"))
        .flatMap(value => AdapterLanguage.parse(value).left.map(Vector(_)))
      val rulesRaw = list(values, "rules").toRight(Vector(s"$source: missing list 'rules'"))

      for {
        version  <- versionResult
        _        <- Either.cond(version == 1, (), Vector(s"$source: unsupported adapter version $version"))
        language <- languageResult
        entries  <- rulesRaw
        rules <- collect(entries.zipWithIndex.map { case (entry, index) =>
          asMap(entry)
            .toRight(Vector(s"$source: rules[$index] must be a mapping"))
            .flatMap(decodeRule(_, language, s"$source:rules[$index]"))
        })
        _ <- validateRuleIds(rules, source)
      } yield AdapterConfig(version, language, rules.sortBy(rule => (-rule.priority, rule.id)))
    }
  }

  private def decodeRule(
    values: YMap,
    language: AdapterLanguage,
    location: String
  ): Either[Vector[String], AdapterRule] = {
    val idResult = requiredString(values, "id", location)
    val typeResult = requiredString(values, "operationType", location)
      .flatMap(value =>
        CryptoOperationType.fromStoredValue(value).toRight(Vector(s"$location: unsupported operationType '$value'"))
      )
    val operationResult = requiredString(values, "operation", location).map(_.trim.toUpperCase)
    val lifecycleResult = requiredString(values, "lifecycleRole", location)
      .flatMap(value => LifecycleRole.parse(value).left.map(Vector(_)))
    val apiResult =
      map(values, "api").toRight(Vector(s"$location: missing mapping 'api'")).flatMap(decodeApi(_, location))
    val extractResult   = decodeMany(values, "extract", location)(decodeExtraction)
    val sensitiveResult = decodeMany(values, "sensitiveParameters", location)(decodeBinding)

    for {
      id        <- idResult
      opType    <- typeResult
      operation <- operationResult
      lifecycle <- lifecycleResult
      api       <- apiResult
      extract   <- extractResult
      sensitive <- sensitiveResult
    } yield AdapterRule(
      id = id,
      language = language,
      operationType = opType,
      operation = operation,
      lifecycleRole = lifecycle,
      api = api,
      extract = extract,
      sensitiveParameters = sensitive,
      priority = int(values, "priority").getOrElse(0)
    )
  }

  private def decodeApi(values: YMap, location: String): Either[Vector[String], ApiPattern] = {
    val api = ApiPattern(
      methodFullName = strings(values, "methodFullName"),
      callName = strings(values, "callName"),
      code = strings(values, "code"),
      localType = strings(values, "localType")
    )
    val invalidPatterns = Vector(
      "methodFullName" -> api.methodFullName,
      "callName"       -> api.callName,
      "code"           -> api.code,
      "localType"      -> api.localType
    ).flatMap { case (field, patterns) =>
      patterns.collect {
        case pattern if Try(pattern.r).isFailure =>
          s"$location: invalid $field regex '$pattern'"
      }
    }
    if (api.isEmpty) Left(Vector(s"$location: api must define at least one pattern"))
    else if (invalidPatterns.nonEmpty) Left(invalidPatterns)
    else Right(api)
  }

  private def decodeExtraction(values: YMap, location: String): Either[Vector[String], AttributeExtraction] =
    for {
      target <- requiredString(values, "target", location)
      source <- requiredString(values, "source", location)
        .flatMap(value => ExtractionSource.parse(value).left.map(Vector(_)))
      extraction <- validateExtraction(
        AttributeExtraction(
          target = target.trim,
          source = source,
          argumentIndex = int(values, "argumentIndex"),
          keyword = string(values, "keyword"),
          pattern = string(values, "pattern"),
          group = int(values, "group").getOrElse(1),
          value = string(values, "value"),
          normalize = string(values, "normalize").getOrElse("UPPER").trim.toUpperCase
        ),
        location
      )
    } yield extraction

  private def validateExtraction(
    extraction: AttributeExtraction,
    location: String
  ): Either[Vector[String], AttributeExtraction] = {
    val errors = Vector(
      Option.when(extraction.source == ExtractionSource.Argument && extraction.argumentIndex.isEmpty)(
        s"$location: ARGUMENT source requires argumentIndex"
      ),
      Option.when(extraction.source == ExtractionSource.Keyword && extraction.keyword.isEmpty)(
        s"$location: KEYWORD source requires keyword"
      ),
      Option.when(extraction.source == ExtractionSource.Constant && extraction.value.isEmpty)(
        s"$location: CONSTANT source requires value"
      ),
      Option.when(extraction.group < 0)(s"$location: regex group cannot be negative"),
      extraction.pattern
        .filter(pattern => Try(pattern.r).isFailure)
        .map(pattern => s"$location: invalid extraction regex '$pattern'"),
      Option.when(!Set("UPPER", "LOWER", "NONE").contains(extraction.normalize))(
        s"$location: normalize must be UPPER, LOWER, or NONE"
      )
    ).flatten
    Either.cond(errors.isEmpty, extraction, errors)
  }

  private def decodeBinding(values: YMap, location: String): Either[Vector[String], ParameterBinding] =
    requiredString(values, "parameter", location)
      .flatMap(value => SensitiveParameter.parse(value).left.map(Vector(_)))
      .flatMap { parameter =>
        val binding = ParameterBinding(parameter, int(values, "argumentIndex"), string(values, "keyword"))
        Either.cond(
          binding.argumentIndex.nonEmpty || binding.keyword.nonEmpty,
          binding,
          Vector(s"$location: sensitive parameter requires argumentIndex or keyword")
        )
      }

  private def decodeMany[A](values: YMap, key: String, location: String)(
    decoder: (YMap, String) => Either[Vector[String], A]
  ): Either[Vector[String], Vector[A]] =
    list(values, key) match {
      case None => Right(Vector.empty)
      case Some(entries) =>
        collect(entries.zipWithIndex.map { case (entry, index) =>
          asMap(entry)
            .toRight(Vector(s"$location:$key[$index] must be a mapping"))
            .flatMap(decoder(_, s"$location:$key[$index]"))
        })
    }

  private def validateRuleIds(rules: Vector[AdapterRule], source: String): Either[Vector[String], Unit] = {
    val duplicates = rules.groupBy(_.id).collect { case (id, matches) if matches.size > 1 => id }.toVector.sorted
    Either.cond(duplicates.isEmpty, (), Vector(s"$source: duplicate rule ids: ${duplicates.mkString(", ")}"))
  }

  private def collect[A](results: Seq[Either[Vector[String], A]]): Either[Vector[String], Vector[A]] = {
    val errors = results.collect { case Left(messages) => messages }.flatten.toVector
    if (errors.nonEmpty) Left(errors) else Right(results.collect { case Right(value) => value }.toVector)
  }

  private def asMap(value: Object): Option[YMap] = value match {
    case map: java.util.Map[?, ?] => Some(map.asInstanceOf[YMap])
    case _                        => None
  }

  private def map(values: YMap, key: String): Option[YMap] = Option(values.get(key)).flatMap(asMap)
  private def list(values: YMap, key: String): Option[Vector[Object]] = Option(values.get(key)).collect {
    case entries: java.util.List[?] => entries.asScala.toVector.map(_.asInstanceOf[Object])
  }
  private def string(values: YMap, key: String): Option[String] = Option(values.get(key)).collect {
    case value: String => value
  }
  private def strings(values: YMap, key: String): Vector[String] = Option(values.get(key)) match {
    case Some(value: String)              => Vector(value)
    case Some(entries: java.util.List[?]) => entries.asScala.collect { case value: String => value }.toVector
    case _                                => Vector.empty
  }
  private def int(values: YMap, key: String): Option[Int] = Option(values.get(key)).flatMap {
    case value: java.lang.Number => Some(value.intValue())
    case value: String           => value.toIntOption
    case _                       => None
  }
  private def requiredString(values: YMap, key: String, location: String): Either[Vector[String], String] =
    string(values, key).filter(_.trim.nonEmpty).toRight(Vector(s"$location: missing '$key'"))
}
