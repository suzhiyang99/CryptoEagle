package io.shiftleft.codepropertygraph.generated

import flatgraph.FormalQtyType

object GraphSchema extends flatgraph.Schema {
  private val nodeLabels = IndexedSeq(
    "ANNOTATION",
    "ANNOTATION_LITERAL",
    "ANNOTATION_PARAMETER",
    "ANNOTATION_PARAMETER_ASSIGN",
    "ARRAY_INITIALIZER",
    "BINDING",
    "BLOCK",
    "CALL",
    "CLOSURE_BINDING",
    "COMMENT",
    "CONFIG_FILE",
    "CONTROL_STRUCTURE",
    "CRYPTO_OPERATION",
    "DEPENDENCY",
    "FIELD_IDENTIFIER",
    "FILE",
    "FINDING",
    "IDENTIFIER",
    "IMPORT",
    "JUMP_LABEL",
    "JUMP_TARGET",
    "KEY_VALUE_PAIR",
    "LITERAL",
    "LOCAL",
    "LOCATION",
    "MEMBER",
    "META_DATA",
    "METHOD",
    "METHOD_PARAMETER_IN",
    "METHOD_PARAMETER_OUT",
    "METHOD_REF",
    "METHOD_RETURN",
    "MODIFIER",
    "NAMESPACE",
    "NAMESPACE_BLOCK",
    "RETURN",
    "TAG",
    "TAG_NODE_PAIR",
    "TEMPLATE_DOM",
    "TYPE",
    "TYPE_ARGUMENT",
    "TYPE_DECL",
    "TYPE_PARAMETER",
    "TYPE_REF",
    "UNKNOWN"
  )
  val nodeKindByLabel = nodeLabels.zipWithIndex.toMap
  val edgeLabels: Array[String] = Array(
    "ALIAS_OF",
    "ARGUMENT",
    "AST",
    "BINDS",
    "BINDS_TO",
    "CALL",
    "CAPTURE",
    "CAPTURED_BY",
    "CDG",
    "CFG",
    "CONDITION",
    "CONTAINS",
    "DOMINATE",
    "EVAL_TYPE",
    "HAS_SECURITY_EVIDENCE",
    "IMPORTS",
    "INHERITS_FROM",
    "IS_CALL_FOR_IMPORT",
    "PARAMETER_LINK",
    "POST_DOMINATE",
    "REACHING_DEF",
    "REALIZES_CRYPTO",
    "RECEIVER",
    "REF",
    "SOURCE_FILE",
    "TAGGED_BY",
    "USES_IV_FROM",
    "USES_KEY_FROM",
    "USES_NONCE_FROM",
    "USES_SALT_FROM"
  )
  val edgeKindByLabel = edgeLabels.zipWithIndex.toMap
  val edgePropertyAllocators: Array[Int => Array[?]] = Array(
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => Array.fill(size)("<empty>") /* label = REACHING_DEF, id = 20 */,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null,
    size => null
  )
  val nodeFactories: Array[(flatgraph.Graph, Int) => nodes.StoredNode] = Array(
    (g, seq) => new nodes.Annotation(g, seq),
    (g, seq) => new nodes.AnnotationLiteral(g, seq),
    (g, seq) => new nodes.AnnotationParameter(g, seq),
    (g, seq) => new nodes.AnnotationParameterAssign(g, seq),
    (g, seq) => new nodes.ArrayInitializer(g, seq),
    (g, seq) => new nodes.Binding(g, seq),
    (g, seq) => new nodes.Block(g, seq),
    (g, seq) => new nodes.Call(g, seq),
    (g, seq) => new nodes.ClosureBinding(g, seq),
    (g, seq) => new nodes.Comment(g, seq),
    (g, seq) => new nodes.ConfigFile(g, seq),
    (g, seq) => new nodes.ControlStructure(g, seq),
    (g, seq) => new nodes.CryptoOperation(g, seq),
    (g, seq) => new nodes.Dependency(g, seq),
    (g, seq) => new nodes.FieldIdentifier(g, seq),
    (g, seq) => new nodes.File(g, seq),
    (g, seq) => new nodes.Finding(g, seq),
    (g, seq) => new nodes.Identifier(g, seq),
    (g, seq) => new nodes.Import(g, seq),
    (g, seq) => new nodes.JumpLabel(g, seq),
    (g, seq) => new nodes.JumpTarget(g, seq),
    (g, seq) => new nodes.KeyValuePair(g, seq),
    (g, seq) => new nodes.Literal(g, seq),
    (g, seq) => new nodes.Local(g, seq),
    (g, seq) => new nodes.Location(g, seq),
    (g, seq) => new nodes.Member(g, seq),
    (g, seq) => new nodes.MetaData(g, seq),
    (g, seq) => new nodes.Method(g, seq),
    (g, seq) => new nodes.MethodParameterIn(g, seq),
    (g, seq) => new nodes.MethodParameterOut(g, seq),
    (g, seq) => new nodes.MethodRef(g, seq),
    (g, seq) => new nodes.MethodReturn(g, seq),
    (g, seq) => new nodes.Modifier(g, seq),
    (g, seq) => new nodes.Namespace(g, seq),
    (g, seq) => new nodes.NamespaceBlock(g, seq),
    (g, seq) => new nodes.Return(g, seq),
    (g, seq) => new nodes.Tag(g, seq),
    (g, seq) => new nodes.TagNodePair(g, seq),
    (g, seq) => new nodes.TemplateDom(g, seq),
    (g, seq) => new nodes.Type(g, seq),
    (g, seq) => new nodes.TypeArgument(g, seq),
    (g, seq) => new nodes.TypeDecl(g, seq),
    (g, seq) => new nodes.TypeParameter(g, seq),
    (g, seq) => new nodes.TypeRef(g, seq),
    (g, seq) => new nodes.Unknown(g, seq)
  )
  val edgeFactories: Array[(flatgraph.GNode, flatgraph.GNode, Int, Any) => flatgraph.Edge] = Array(
    (s, d, subseq, p) => new edges.AliasOf(s, d, subseq, p),
    (s, d, subseq, p) => new edges.Argument(s, d, subseq, p),
    (s, d, subseq, p) => new edges.Ast(s, d, subseq, p),
    (s, d, subseq, p) => new edges.Binds(s, d, subseq, p),
    (s, d, subseq, p) => new edges.BindsTo(s, d, subseq, p),
    (s, d, subseq, p) => new edges.Call(s, d, subseq, p),
    (s, d, subseq, p) => new edges.Capture(s, d, subseq, p),
    (s, d, subseq, p) => new edges.CapturedBy(s, d, subseq, p),
    (s, d, subseq, p) => new edges.Cdg(s, d, subseq, p),
    (s, d, subseq, p) => new edges.Cfg(s, d, subseq, p),
    (s, d, subseq, p) => new edges.Condition(s, d, subseq, p),
    (s, d, subseq, p) => new edges.Contains(s, d, subseq, p),
    (s, d, subseq, p) => new edges.Dominate(s, d, subseq, p),
    (s, d, subseq, p) => new edges.EvalType(s, d, subseq, p),
    (s, d, subseq, p) => new edges.HasSecurityEvidence(s, d, subseq, p),
    (s, d, subseq, p) => new edges.Imports(s, d, subseq, p),
    (s, d, subseq, p) => new edges.InheritsFrom(s, d, subseq, p),
    (s, d, subseq, p) => new edges.IsCallForImport(s, d, subseq, p),
    (s, d, subseq, p) => new edges.ParameterLink(s, d, subseq, p),
    (s, d, subseq, p) => new edges.PostDominate(s, d, subseq, p),
    (s, d, subseq, p) => new edges.ReachingDef(s, d, subseq, p),
    (s, d, subseq, p) => new edges.RealizesCrypto(s, d, subseq, p),
    (s, d, subseq, p) => new edges.Receiver(s, d, subseq, p),
    (s, d, subseq, p) => new edges.Ref(s, d, subseq, p),
    (s, d, subseq, p) => new edges.SourceFile(s, d, subseq, p),
    (s, d, subseq, p) => new edges.TaggedBy(s, d, subseq, p),
    (s, d, subseq, p) => new edges.UsesIvFrom(s, d, subseq, p),
    (s, d, subseq, p) => new edges.UsesKeyFrom(s, d, subseq, p),
    (s, d, subseq, p) => new edges.UsesNonceFrom(s, d, subseq, p),
    (s, d, subseq, p) => new edges.UsesSaltFrom(s, d, subseq, p)
  )
  val nodePropertyAllocators: Array[Int => Array[?]] = Array(
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[Int](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[Int](size),
    size => new Array[Int](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[Boolean](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[Int](size),
    size => new Array[String](size),
    size => new Array[Boolean](size),
    size => new Array[Boolean](size),
    size => new Array[Boolean](size),
    size => new Array[Boolean](size),
    size => new Array[Boolean](size),
    size => new Array[Int](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[Int](size),
    size => new Array[Int](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[Int](size),
    size => new Array[Int](size),
    size => new Array[Int](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[Int](size),
    size => new Array[Int](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[Int](size),
    size => new Array[Int](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[Boolean](size),
    size => new Array[Boolean](size),
    size => new Array[Boolean](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[Boolean](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[Int](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[String](size),
    size => new Array[Boolean](size),
    size => new Array[String](size),
    size => new Array[Boolean](size),
    size => new Array[String](size),
    size => new Array[Boolean](size),
    size => new Array[String](size),
    size => new Array[flatgraph.GNode](size),
    size => new Array[flatgraph.GNode](size),
    size => new Array[flatgraph.GNode](size),
    size => new Array[flatgraph.GNode](size)
  )
  val normalNodePropertyNames: Array[String] = Array(
    "ALGORITHM",
    "ALIAS_TYPE_FULL_NAME",
    "ARGUMENT_INDEX",
    "ARGUMENT_NAME",
    "AST_PARENT_FULL_NAME",
    "AST_PARENT_TYPE",
    "CANONICAL_NAME",
    "CLASS_NAME",
    "CLASS_SHORT_NAME",
    "CLOSURE_BINDING_ID",
    "CLOSURE_ORIGINAL_NAME",
    "CODE",
    "COLUMN_NUMBER",
    "COLUMN_NUMBER_END",
    "CONTAINED_REF",
    "CONTENT",
    "CONTROL_STRUCTURE_TYPE",
    "DEPENDENCY_GROUP_ID",
    "DISPATCH_TYPE",
    "DYNAMIC_TYPE_HINT_FULL_NAME",
    "EVALUATION_STRATEGY",
    "EXPLICIT_AS",
    "FILENAME",
    "FULL_NAME",
    "GENERIC_SIGNATURE",
    "HASH",
    "IMPORTED_AS",
    "IMPORTED_ENTITY",
    "INDEX",
    "INHERITS_FROM_TYPE_FULL_NAME",
    "IS_DEPRECATED",
    "IS_EXPLICIT",
    "IS_EXTERNAL",
    "IS_VARIADIC",
    "IS_WILDCARD",
    "ITERATIONS",
    "IV_SOURCE_TYPE",
    "KEY",
    "KEY_LENGTH",
    "KEY_SIZE",
    "KEY_SOURCE",
    "KEY_SOURCE_TYPE",
    "LANGUAGE",
    "LINE_NO",
    "LINE_NUMBER",
    "LINE_NUMBER_END",
    "METHOD_FULL_NAME",
    "METHOD_SHORT_NAME",
    "MODE",
    "MODIFIER_TYPE",
    "NAME",
    "NODE_LABEL",
    "NONCE_SOURCE_TYPE",
    "OFFSET",
    "OFFSET_END",
    "OPERATION",
    "OP_TYPE",
    "ORDER",
    "OUTPUT_LENGTH",
    "OVERLAYS",
    "PACKAGE_NAME",
    "PADDING",
    "PARSER_TYPE_NAME",
    "POSSIBLE_TYPES",
    "PROTOCOL_VERSION",
    "PROVIDES_INTEGRITY",
    "REQUIRES_CSPRNG_SOURCE",
    "REQUIRES_UNIQUE_NONCE",
    "RESOLUTION",
    "ROOT",
    "SALT_SOURCE_TYPE",
    "SALT_USED",
    "SEMANTIC_DETAIL",
    "SEMANTIC_KIND",
    "SIGNATURE",
    "SOURCE",
    "SRC_FILE",
    "SYMBOL",
    "TAG_LENGTH",
    "TRANSPORT_SCHEME",
    "TYPE_DECL_FULL_NAME",
    "TYPE_FULL_NAME",
    "USED_FOR_CRYPTO",
    "VALUE",
    "VERIFICATION_ENABLED",
    "VERIFICATION_KIND",
    "VERIFIED",
    "VERSION"
  )
  val nodePropertyByLabel = normalNodePropertyNames.zipWithIndex.toMap
    .updated("evidence", 88)
    .updated("keyValuePairs", 89)
    .updated("node", 90)
    .updated("tag", 91)
  val nodePropertyDescriptors: Array[FormalQtyType.FormalQuantity | FormalQtyType.FormalType] = {
    val nodePropertyDescriptors = new Array[FormalQtyType.FormalQuantity | FormalQtyType.FormalType](8280)
    for (idx <- Range(0, 8280)) {
      nodePropertyDescriptors(idx) =
        if ((idx & 1) == 0) FormalQtyType.NothingType
        else FormalQtyType.QtyNone
    }

    nodePropertyDescriptors(180) = FormalQtyType.IntType // ANNOTATION.ARGUMENT_INDEX
    nodePropertyDescriptors(181) = FormalQtyType.QtyOne
    nodePropertyDescriptors(270) = FormalQtyType.StringType // ANNOTATION.ARGUMENT_NAME
    nodePropertyDescriptors(271) = FormalQtyType.QtyOption
    nodePropertyDescriptors(990) = FormalQtyType.StringType // ANNOTATION.CODE
    nodePropertyDescriptors(991) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1080) = FormalQtyType.IntType // ANNOTATION.COLUMN_NUMBER
    nodePropertyDescriptors(1081) = FormalQtyType.QtyOption
    nodePropertyDescriptors(2070) = FormalQtyType.StringType // ANNOTATION.FULL_NAME
    nodePropertyDescriptors(2071) = FormalQtyType.QtyOne
    nodePropertyDescriptors(3960) = FormalQtyType.IntType // ANNOTATION.LINE_NUMBER
    nodePropertyDescriptors(3961) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4500) = FormalQtyType.StringType // ANNOTATION.NAME
    nodePropertyDescriptors(4501) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5130) = FormalQtyType.IntType // ANNOTATION.ORDER
    nodePropertyDescriptors(5131) = FormalQtyType.QtyOne
    nodePropertyDescriptors(182) = FormalQtyType.IntType // ANNOTATION_LITERAL.ARGUMENT_INDEX
    nodePropertyDescriptors(183) = FormalQtyType.QtyOne
    nodePropertyDescriptors(272) = FormalQtyType.StringType // ANNOTATION_LITERAL.ARGUMENT_NAME
    nodePropertyDescriptors(273) = FormalQtyType.QtyOption
    nodePropertyDescriptors(992) = FormalQtyType.StringType // ANNOTATION_LITERAL.CODE
    nodePropertyDescriptors(993) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1082) = FormalQtyType.IntType // ANNOTATION_LITERAL.COLUMN_NUMBER
    nodePropertyDescriptors(1083) = FormalQtyType.QtyOption
    nodePropertyDescriptors(3962) = FormalQtyType.IntType // ANNOTATION_LITERAL.LINE_NUMBER
    nodePropertyDescriptors(3963) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4502) = FormalQtyType.StringType // ANNOTATION_LITERAL.NAME
    nodePropertyDescriptors(4503) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5132) = FormalQtyType.IntType // ANNOTATION_LITERAL.ORDER
    nodePropertyDescriptors(5133) = FormalQtyType.QtyOne
    nodePropertyDescriptors(994) = FormalQtyType.StringType // ANNOTATION_PARAMETER.CODE
    nodePropertyDescriptors(995) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1084) = FormalQtyType.IntType // ANNOTATION_PARAMETER.COLUMN_NUMBER
    nodePropertyDescriptors(1085) = FormalQtyType.QtyOption
    nodePropertyDescriptors(3964) = FormalQtyType.IntType // ANNOTATION_PARAMETER.LINE_NUMBER
    nodePropertyDescriptors(3965) = FormalQtyType.QtyOption
    nodePropertyDescriptors(5134) = FormalQtyType.IntType // ANNOTATION_PARAMETER.ORDER
    nodePropertyDescriptors(5135) = FormalQtyType.QtyOne
    nodePropertyDescriptors(996) = FormalQtyType.StringType // ANNOTATION_PARAMETER_ASSIGN.CODE
    nodePropertyDescriptors(997) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1086) = FormalQtyType.IntType // ANNOTATION_PARAMETER_ASSIGN.COLUMN_NUMBER
    nodePropertyDescriptors(1087) = FormalQtyType.QtyOption
    nodePropertyDescriptors(3966) = FormalQtyType.IntType // ANNOTATION_PARAMETER_ASSIGN.LINE_NUMBER
    nodePropertyDescriptors(3967) = FormalQtyType.QtyOption
    nodePropertyDescriptors(5136) = FormalQtyType.IntType // ANNOTATION_PARAMETER_ASSIGN.ORDER
    nodePropertyDescriptors(5137) = FormalQtyType.QtyOne
    nodePropertyDescriptors(188) = FormalQtyType.IntType // ARRAY_INITIALIZER.ARGUMENT_INDEX
    nodePropertyDescriptors(189) = FormalQtyType.QtyOne
    nodePropertyDescriptors(278) = FormalQtyType.StringType // ARRAY_INITIALIZER.ARGUMENT_NAME
    nodePropertyDescriptors(279) = FormalQtyType.QtyOption
    nodePropertyDescriptors(998) = FormalQtyType.StringType // ARRAY_INITIALIZER.CODE
    nodePropertyDescriptors(999) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1088) = FormalQtyType.IntType // ARRAY_INITIALIZER.COLUMN_NUMBER
    nodePropertyDescriptors(1089) = FormalQtyType.QtyOption
    nodePropertyDescriptors(3968) = FormalQtyType.IntType // ARRAY_INITIALIZER.LINE_NUMBER
    nodePropertyDescriptors(3969) = FormalQtyType.QtyOption
    nodePropertyDescriptors(5138) = FormalQtyType.IntType // ARRAY_INITIALIZER.ORDER
    nodePropertyDescriptors(5139) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4150) = FormalQtyType.StringType // BINDING.METHOD_FULL_NAME
    nodePropertyDescriptors(4151) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4510) = FormalQtyType.StringType // BINDING.NAME
    nodePropertyDescriptors(4511) = FormalQtyType.QtyOne
    nodePropertyDescriptors(6670) = FormalQtyType.StringType // BINDING.SIGNATURE
    nodePropertyDescriptors(6671) = FormalQtyType.QtyOne
    nodePropertyDescriptors(192) = FormalQtyType.IntType // BLOCK.ARGUMENT_INDEX
    nodePropertyDescriptors(193) = FormalQtyType.QtyOne
    nodePropertyDescriptors(282) = FormalQtyType.StringType // BLOCK.ARGUMENT_NAME
    nodePropertyDescriptors(283) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1002) = FormalQtyType.StringType // BLOCK.CODE
    nodePropertyDescriptors(1003) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1092) = FormalQtyType.IntType // BLOCK.COLUMN_NUMBER
    nodePropertyDescriptors(1093) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1722) = FormalQtyType.StringType // BLOCK.DYNAMIC_TYPE_HINT_FULL_NAME
    nodePropertyDescriptors(1723) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(3972) = FormalQtyType.IntType // BLOCK.LINE_NUMBER
    nodePropertyDescriptors(3973) = FormalQtyType.QtyOption
    nodePropertyDescriptors(5142) = FormalQtyType.IntType // BLOCK.ORDER
    nodePropertyDescriptors(5143) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5682) = FormalQtyType.StringType // BLOCK.POSSIBLE_TYPES
    nodePropertyDescriptors(5683) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(7302) = FormalQtyType.StringType // BLOCK.TYPE_FULL_NAME
    nodePropertyDescriptors(7303) = FormalQtyType.QtyOne
    nodePropertyDescriptors(194) = FormalQtyType.IntType // CALL.ARGUMENT_INDEX
    nodePropertyDescriptors(195) = FormalQtyType.QtyOne
    nodePropertyDescriptors(284) = FormalQtyType.StringType // CALL.ARGUMENT_NAME
    nodePropertyDescriptors(285) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1004) = FormalQtyType.StringType // CALL.CODE
    nodePropertyDescriptors(1005) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1094) = FormalQtyType.IntType // CALL.COLUMN_NUMBER
    nodePropertyDescriptors(1095) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1634) = FormalQtyType.StringType // CALL.DISPATCH_TYPE
    nodePropertyDescriptors(1635) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1724) = FormalQtyType.StringType // CALL.DYNAMIC_TYPE_HINT_FULL_NAME
    nodePropertyDescriptors(1725) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(3974) = FormalQtyType.IntType // CALL.LINE_NUMBER
    nodePropertyDescriptors(3975) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4154) = FormalQtyType.StringType // CALL.METHOD_FULL_NAME
    nodePropertyDescriptors(4155) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4514) = FormalQtyType.StringType // CALL.NAME
    nodePropertyDescriptors(4515) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5144) = FormalQtyType.IntType // CALL.ORDER
    nodePropertyDescriptors(5145) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5684) = FormalQtyType.StringType // CALL.POSSIBLE_TYPES
    nodePropertyDescriptors(5685) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(6674) = FormalQtyType.StringType // CALL.SIGNATURE
    nodePropertyDescriptors(6675) = FormalQtyType.QtyOne
    nodePropertyDescriptors(7304) = FormalQtyType.StringType // CALL.TYPE_FULL_NAME
    nodePropertyDescriptors(7305) = FormalQtyType.QtyOne
    nodePropertyDescriptors(826) = FormalQtyType.StringType // CLOSURE_BINDING.CLOSURE_BINDING_ID
    nodePropertyDescriptors(827) = FormalQtyType.QtyOption
    nodePropertyDescriptors(916) = FormalQtyType.StringType // CLOSURE_BINDING.CLOSURE_ORIGINAL_NAME
    nodePropertyDescriptors(917) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1816) = FormalQtyType.StringType // CLOSURE_BINDING.EVALUATION_STRATEGY
    nodePropertyDescriptors(1817) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1008) = FormalQtyType.StringType // COMMENT.CODE
    nodePropertyDescriptors(1009) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1098) = FormalQtyType.IntType // COMMENT.COLUMN_NUMBER
    nodePropertyDescriptors(1099) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1998) = FormalQtyType.StringType // COMMENT.FILENAME
    nodePropertyDescriptors(1999) = FormalQtyType.QtyOne
    nodePropertyDescriptors(3978) = FormalQtyType.IntType // COMMENT.LINE_NUMBER
    nodePropertyDescriptors(3979) = FormalQtyType.QtyOption
    nodePropertyDescriptors(5148) = FormalQtyType.IntType // COMMENT.ORDER
    nodePropertyDescriptors(5149) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1370) = FormalQtyType.StringType // CONFIG_FILE.CONTENT
    nodePropertyDescriptors(1371) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4520) = FormalQtyType.StringType // CONFIG_FILE.NAME
    nodePropertyDescriptors(4521) = FormalQtyType.QtyOne
    nodePropertyDescriptors(202) = FormalQtyType.IntType // CONTROL_STRUCTURE.ARGUMENT_INDEX
    nodePropertyDescriptors(203) = FormalQtyType.QtyOne
    nodePropertyDescriptors(292) = FormalQtyType.StringType // CONTROL_STRUCTURE.ARGUMENT_NAME
    nodePropertyDescriptors(293) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1012) = FormalQtyType.StringType // CONTROL_STRUCTURE.CODE
    nodePropertyDescriptors(1013) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1102) = FormalQtyType.IntType // CONTROL_STRUCTURE.COLUMN_NUMBER
    nodePropertyDescriptors(1103) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1462) = FormalQtyType.StringType // CONTROL_STRUCTURE.CONTROL_STRUCTURE_TYPE
    nodePropertyDescriptors(1463) = FormalQtyType.QtyOne
    nodePropertyDescriptors(3982) = FormalQtyType.IntType // CONTROL_STRUCTURE.LINE_NUMBER
    nodePropertyDescriptors(3983) = FormalQtyType.QtyOption
    nodePropertyDescriptors(5152) = FormalQtyType.IntType // CONTROL_STRUCTURE.ORDER
    nodePropertyDescriptors(5153) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5602) = FormalQtyType.StringType // CONTROL_STRUCTURE.PARSER_TYPE_NAME
    nodePropertyDescriptors(5603) = FormalQtyType.QtyOne
    nodePropertyDescriptors(24) = FormalQtyType.StringType // CRYPTO_OPERATION.ALGORITHM
    nodePropertyDescriptors(25) = FormalQtyType.QtyOne
    nodePropertyDescriptors(2724) = FormalQtyType.BoolType // CRYPTO_OPERATION.IS_DEPRECATED
    nodePropertyDescriptors(2725) = FormalQtyType.QtyOne
    nodePropertyDescriptors(3174) = FormalQtyType.IntType // CRYPTO_OPERATION.ITERATIONS
    nodePropertyDescriptors(3175) = FormalQtyType.QtyOne
    nodePropertyDescriptors(3264) = FormalQtyType.StringType // CRYPTO_OPERATION.IV_SOURCE_TYPE
    nodePropertyDescriptors(3265) = FormalQtyType.QtyOne
    nodePropertyDescriptors(3444) = FormalQtyType.IntType // CRYPTO_OPERATION.KEY_LENGTH
    nodePropertyDescriptors(3445) = FormalQtyType.QtyOne
    nodePropertyDescriptors(3534) = FormalQtyType.IntType // CRYPTO_OPERATION.KEY_SIZE
    nodePropertyDescriptors(3535) = FormalQtyType.QtyOne
    nodePropertyDescriptors(3624) = FormalQtyType.StringType // CRYPTO_OPERATION.KEY_SOURCE
    nodePropertyDescriptors(3625) = FormalQtyType.QtyOne
    nodePropertyDescriptors(3714) = FormalQtyType.StringType // CRYPTO_OPERATION.KEY_SOURCE_TYPE
    nodePropertyDescriptors(3715) = FormalQtyType.QtyOne
    nodePropertyDescriptors(3894) = FormalQtyType.IntType // CRYPTO_OPERATION.LINE_NO
    nodePropertyDescriptors(3895) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4344) = FormalQtyType.StringType // CRYPTO_OPERATION.MODE
    nodePropertyDescriptors(4345) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4704) = FormalQtyType.StringType // CRYPTO_OPERATION.NONCE_SOURCE_TYPE
    nodePropertyDescriptors(4705) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5064) = FormalQtyType.StringType // CRYPTO_OPERATION.OP_TYPE
    nodePropertyDescriptors(5065) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4974) = FormalQtyType.StringType // CRYPTO_OPERATION.OPERATION
    nodePropertyDescriptors(4975) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5244) = FormalQtyType.IntType // CRYPTO_OPERATION.OUTPUT_LENGTH
    nodePropertyDescriptors(5245) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5514) = FormalQtyType.StringType // CRYPTO_OPERATION.PADDING
    nodePropertyDescriptors(5515) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5784) = FormalQtyType.StringType // CRYPTO_OPERATION.PROTOCOL_VERSION
    nodePropertyDescriptors(5785) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5874) = FormalQtyType.BoolType // CRYPTO_OPERATION.PROVIDES_INTEGRITY
    nodePropertyDescriptors(5875) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5964) = FormalQtyType.BoolType // CRYPTO_OPERATION.REQUIRES_CSPRNG_SOURCE
    nodePropertyDescriptors(5965) = FormalQtyType.QtyOne
    nodePropertyDescriptors(6054) = FormalQtyType.BoolType // CRYPTO_OPERATION.REQUIRES_UNIQUE_NONCE
    nodePropertyDescriptors(6055) = FormalQtyType.QtyOne
    nodePropertyDescriptors(6144) = FormalQtyType.StringType // CRYPTO_OPERATION.RESOLUTION
    nodePropertyDescriptors(6145) = FormalQtyType.QtyOne
    nodePropertyDescriptors(6324) = FormalQtyType.StringType // CRYPTO_OPERATION.SALT_SOURCE_TYPE
    nodePropertyDescriptors(6325) = FormalQtyType.QtyOne
    nodePropertyDescriptors(6414) = FormalQtyType.BoolType // CRYPTO_OPERATION.SALT_USED
    nodePropertyDescriptors(6415) = FormalQtyType.QtyOption
    nodePropertyDescriptors(6504) = FormalQtyType.StringType // CRYPTO_OPERATION.SEMANTIC_DETAIL
    nodePropertyDescriptors(6505) = FormalQtyType.QtyOne
    nodePropertyDescriptors(6594) = FormalQtyType.StringType // CRYPTO_OPERATION.SEMANTIC_KIND
    nodePropertyDescriptors(6595) = FormalQtyType.QtyOne
    nodePropertyDescriptors(6774) = FormalQtyType.StringType // CRYPTO_OPERATION.SOURCE
    nodePropertyDescriptors(6775) = FormalQtyType.QtyOne
    nodePropertyDescriptors(6864) = FormalQtyType.StringType // CRYPTO_OPERATION.SRC_FILE
    nodePropertyDescriptors(6865) = FormalQtyType.QtyOne
    nodePropertyDescriptors(7044) = FormalQtyType.IntType // CRYPTO_OPERATION.TAG_LENGTH
    nodePropertyDescriptors(7045) = FormalQtyType.QtyOne
    nodePropertyDescriptors(7134) = FormalQtyType.StringType // CRYPTO_OPERATION.TRANSPORT_SCHEME
    nodePropertyDescriptors(7135) = FormalQtyType.QtyOne
    nodePropertyDescriptors(7404) = FormalQtyType.BoolType // CRYPTO_OPERATION.USED_FOR_CRYPTO
    nodePropertyDescriptors(7405) = FormalQtyType.QtyOption
    nodePropertyDescriptors(7584) = FormalQtyType.BoolType // CRYPTO_OPERATION.VERIFICATION_ENABLED
    nodePropertyDescriptors(7585) = FormalQtyType.QtyOption
    nodePropertyDescriptors(7674) = FormalQtyType.StringType // CRYPTO_OPERATION.VERIFICATION_KIND
    nodePropertyDescriptors(7675) = FormalQtyType.QtyOne
    nodePropertyDescriptors(7764) = FormalQtyType.BoolType // CRYPTO_OPERATION.VERIFIED
    nodePropertyDescriptors(7765) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1556) = FormalQtyType.StringType // DEPENDENCY.DEPENDENCY_GROUP_ID
    nodePropertyDescriptors(1557) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4526) = FormalQtyType.StringType // DEPENDENCY.NAME
    nodePropertyDescriptors(4527) = FormalQtyType.QtyOne
    nodePropertyDescriptors(7856) = FormalQtyType.StringType // DEPENDENCY.VERSION
    nodePropertyDescriptors(7857) = FormalQtyType.QtyOne
    nodePropertyDescriptors(208) = FormalQtyType.IntType // FIELD_IDENTIFIER.ARGUMENT_INDEX
    nodePropertyDescriptors(209) = FormalQtyType.QtyOne
    nodePropertyDescriptors(298) = FormalQtyType.StringType // FIELD_IDENTIFIER.ARGUMENT_NAME
    nodePropertyDescriptors(299) = FormalQtyType.QtyOption
    nodePropertyDescriptors(568) = FormalQtyType.StringType // FIELD_IDENTIFIER.CANONICAL_NAME
    nodePropertyDescriptors(569) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1018) = FormalQtyType.StringType // FIELD_IDENTIFIER.CODE
    nodePropertyDescriptors(1019) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1108) = FormalQtyType.IntType // FIELD_IDENTIFIER.COLUMN_NUMBER
    nodePropertyDescriptors(1109) = FormalQtyType.QtyOption
    nodePropertyDescriptors(3988) = FormalQtyType.IntType // FIELD_IDENTIFIER.LINE_NUMBER
    nodePropertyDescriptors(3989) = FormalQtyType.QtyOption
    nodePropertyDescriptors(5158) = FormalQtyType.IntType // FIELD_IDENTIFIER.ORDER
    nodePropertyDescriptors(5159) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1020) = FormalQtyType.StringType // FILE.CODE
    nodePropertyDescriptors(1021) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1110) = FormalQtyType.IntType // FILE.COLUMN_NUMBER
    nodePropertyDescriptors(1111) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1380) = FormalQtyType.StringType // FILE.CONTENT
    nodePropertyDescriptors(1381) = FormalQtyType.QtyOne
    nodePropertyDescriptors(2280) = FormalQtyType.StringType // FILE.HASH
    nodePropertyDescriptors(2281) = FormalQtyType.QtyOption
    nodePropertyDescriptors(3990) = FormalQtyType.IntType // FILE.LINE_NUMBER
    nodePropertyDescriptors(3991) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4530) = FormalQtyType.StringType // FILE.NAME
    nodePropertyDescriptors(4531) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5160) = FormalQtyType.IntType // FILE.ORDER
    nodePropertyDescriptors(5161) = FormalQtyType.QtyOne
    nodePropertyDescriptors(7952) = FormalQtyType.RefType // FINDING.evidence
    nodePropertyDescriptors(7953) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(8042) = FormalQtyType.RefType // FINDING.keyValuePairs
    nodePropertyDescriptors(8043) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(214) = FormalQtyType.IntType // IDENTIFIER.ARGUMENT_INDEX
    nodePropertyDescriptors(215) = FormalQtyType.QtyOne
    nodePropertyDescriptors(304) = FormalQtyType.StringType // IDENTIFIER.ARGUMENT_NAME
    nodePropertyDescriptors(305) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1024) = FormalQtyType.StringType // IDENTIFIER.CODE
    nodePropertyDescriptors(1025) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1114) = FormalQtyType.IntType // IDENTIFIER.COLUMN_NUMBER
    nodePropertyDescriptors(1115) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1744) = FormalQtyType.StringType // IDENTIFIER.DYNAMIC_TYPE_HINT_FULL_NAME
    nodePropertyDescriptors(1745) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(3994) = FormalQtyType.IntType // IDENTIFIER.LINE_NUMBER
    nodePropertyDescriptors(3995) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4534) = FormalQtyType.StringType // IDENTIFIER.NAME
    nodePropertyDescriptors(4535) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5164) = FormalQtyType.IntType // IDENTIFIER.ORDER
    nodePropertyDescriptors(5165) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5704) = FormalQtyType.StringType // IDENTIFIER.POSSIBLE_TYPES
    nodePropertyDescriptors(5705) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(7324) = FormalQtyType.StringType // IDENTIFIER.TYPE_FULL_NAME
    nodePropertyDescriptors(7325) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1026) = FormalQtyType.StringType // IMPORT.CODE
    nodePropertyDescriptors(1027) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1116) = FormalQtyType.IntType // IMPORT.COLUMN_NUMBER
    nodePropertyDescriptors(1117) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1926) = FormalQtyType.BoolType // IMPORT.EXPLICIT_AS
    nodePropertyDescriptors(1927) = FormalQtyType.QtyOption
    nodePropertyDescriptors(2376) = FormalQtyType.StringType // IMPORT.IMPORTED_AS
    nodePropertyDescriptors(2377) = FormalQtyType.QtyOption
    nodePropertyDescriptors(2466) = FormalQtyType.StringType // IMPORT.IMPORTED_ENTITY
    nodePropertyDescriptors(2467) = FormalQtyType.QtyOption
    nodePropertyDescriptors(2826) = FormalQtyType.BoolType // IMPORT.IS_EXPLICIT
    nodePropertyDescriptors(2827) = FormalQtyType.QtyOption
    nodePropertyDescriptors(3096) = FormalQtyType.BoolType // IMPORT.IS_WILDCARD
    nodePropertyDescriptors(3097) = FormalQtyType.QtyOption
    nodePropertyDescriptors(3996) = FormalQtyType.IntType // IMPORT.LINE_NUMBER
    nodePropertyDescriptors(3997) = FormalQtyType.QtyOption
    nodePropertyDescriptors(5166) = FormalQtyType.IntType // IMPORT.ORDER
    nodePropertyDescriptors(5167) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1028) = FormalQtyType.StringType // JUMP_LABEL.CODE
    nodePropertyDescriptors(1029) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1118) = FormalQtyType.IntType // JUMP_LABEL.COLUMN_NUMBER
    nodePropertyDescriptors(1119) = FormalQtyType.QtyOption
    nodePropertyDescriptors(3998) = FormalQtyType.IntType // JUMP_LABEL.LINE_NUMBER
    nodePropertyDescriptors(3999) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4538) = FormalQtyType.StringType // JUMP_LABEL.NAME
    nodePropertyDescriptors(4539) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5168) = FormalQtyType.IntType // JUMP_LABEL.ORDER
    nodePropertyDescriptors(5169) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5618) = FormalQtyType.StringType // JUMP_LABEL.PARSER_TYPE_NAME
    nodePropertyDescriptors(5619) = FormalQtyType.QtyOne
    nodePropertyDescriptors(220) = FormalQtyType.IntType // JUMP_TARGET.ARGUMENT_INDEX
    nodePropertyDescriptors(221) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1030) = FormalQtyType.StringType // JUMP_TARGET.CODE
    nodePropertyDescriptors(1031) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1120) = FormalQtyType.IntType // JUMP_TARGET.COLUMN_NUMBER
    nodePropertyDescriptors(1121) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4000) = FormalQtyType.IntType // JUMP_TARGET.LINE_NUMBER
    nodePropertyDescriptors(4001) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4540) = FormalQtyType.StringType // JUMP_TARGET.NAME
    nodePropertyDescriptors(4541) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5170) = FormalQtyType.IntType // JUMP_TARGET.ORDER
    nodePropertyDescriptors(5171) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5620) = FormalQtyType.StringType // JUMP_TARGET.PARSER_TYPE_NAME
    nodePropertyDescriptors(5621) = FormalQtyType.QtyOne
    nodePropertyDescriptors(3372) = FormalQtyType.StringType // KEY_VALUE_PAIR.KEY
    nodePropertyDescriptors(3373) = FormalQtyType.QtyOne
    nodePropertyDescriptors(7512) = FormalQtyType.StringType // KEY_VALUE_PAIR.VALUE
    nodePropertyDescriptors(7513) = FormalQtyType.QtyOne
    nodePropertyDescriptors(224) = FormalQtyType.IntType // LITERAL.ARGUMENT_INDEX
    nodePropertyDescriptors(225) = FormalQtyType.QtyOne
    nodePropertyDescriptors(314) = FormalQtyType.StringType // LITERAL.ARGUMENT_NAME
    nodePropertyDescriptors(315) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1034) = FormalQtyType.StringType // LITERAL.CODE
    nodePropertyDescriptors(1035) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1124) = FormalQtyType.IntType // LITERAL.COLUMN_NUMBER
    nodePropertyDescriptors(1125) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1754) = FormalQtyType.StringType // LITERAL.DYNAMIC_TYPE_HINT_FULL_NAME
    nodePropertyDescriptors(1755) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(4004) = FormalQtyType.IntType // LITERAL.LINE_NUMBER
    nodePropertyDescriptors(4005) = FormalQtyType.QtyOption
    nodePropertyDescriptors(5174) = FormalQtyType.IntType // LITERAL.ORDER
    nodePropertyDescriptors(5175) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5714) = FormalQtyType.StringType // LITERAL.POSSIBLE_TYPES
    nodePropertyDescriptors(5715) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(7334) = FormalQtyType.StringType // LITERAL.TYPE_FULL_NAME
    nodePropertyDescriptors(7335) = FormalQtyType.QtyOne
    nodePropertyDescriptors(856) = FormalQtyType.StringType // LOCAL.CLOSURE_BINDING_ID
    nodePropertyDescriptors(857) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1036) = FormalQtyType.StringType // LOCAL.CODE
    nodePropertyDescriptors(1037) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1126) = FormalQtyType.IntType // LOCAL.COLUMN_NUMBER
    nodePropertyDescriptors(1127) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1756) = FormalQtyType.StringType // LOCAL.DYNAMIC_TYPE_HINT_FULL_NAME
    nodePropertyDescriptors(1757) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(2206) = FormalQtyType.StringType // LOCAL.GENERIC_SIGNATURE
    nodePropertyDescriptors(2207) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4006) = FormalQtyType.IntType // LOCAL.LINE_NUMBER
    nodePropertyDescriptors(4007) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4546) = FormalQtyType.StringType // LOCAL.NAME
    nodePropertyDescriptors(4547) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5176) = FormalQtyType.IntType // LOCAL.ORDER
    nodePropertyDescriptors(5177) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5716) = FormalQtyType.StringType // LOCAL.POSSIBLE_TYPES
    nodePropertyDescriptors(5717) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(7336) = FormalQtyType.StringType // LOCAL.TYPE_FULL_NAME
    nodePropertyDescriptors(7337) = FormalQtyType.QtyOne
    nodePropertyDescriptors(678) = FormalQtyType.StringType // LOCATION.CLASS_NAME
    nodePropertyDescriptors(679) = FormalQtyType.QtyOne
    nodePropertyDescriptors(768) = FormalQtyType.StringType // LOCATION.CLASS_SHORT_NAME
    nodePropertyDescriptors(769) = FormalQtyType.QtyOne
    nodePropertyDescriptors(2028) = FormalQtyType.StringType // LOCATION.FILENAME
    nodePropertyDescriptors(2029) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4008) = FormalQtyType.IntType // LOCATION.LINE_NUMBER
    nodePropertyDescriptors(4009) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4188) = FormalQtyType.StringType // LOCATION.METHOD_FULL_NAME
    nodePropertyDescriptors(4189) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4278) = FormalQtyType.StringType // LOCATION.METHOD_SHORT_NAME
    nodePropertyDescriptors(4279) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4638) = FormalQtyType.StringType // LOCATION.NODE_LABEL
    nodePropertyDescriptors(4639) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5448) = FormalQtyType.StringType // LOCATION.PACKAGE_NAME
    nodePropertyDescriptors(5449) = FormalQtyType.QtyOne
    nodePropertyDescriptors(6978) = FormalQtyType.StringType // LOCATION.SYMBOL
    nodePropertyDescriptors(6979) = FormalQtyType.QtyOne
    nodePropertyDescriptors(8148) = FormalQtyType.RefType // LOCATION.node
    nodePropertyDescriptors(8149) = FormalQtyType.QtyOption
    nodePropertyDescriptors(410) = FormalQtyType.StringType // MEMBER.AST_PARENT_FULL_NAME
    nodePropertyDescriptors(411) = FormalQtyType.QtyOne
    nodePropertyDescriptors(500) = FormalQtyType.StringType // MEMBER.AST_PARENT_TYPE
    nodePropertyDescriptors(501) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1040) = FormalQtyType.StringType // MEMBER.CODE
    nodePropertyDescriptors(1041) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1130) = FormalQtyType.IntType // MEMBER.COLUMN_NUMBER
    nodePropertyDescriptors(1131) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1760) = FormalQtyType.StringType // MEMBER.DYNAMIC_TYPE_HINT_FULL_NAME
    nodePropertyDescriptors(1761) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(2210) = FormalQtyType.StringType // MEMBER.GENERIC_SIGNATURE
    nodePropertyDescriptors(2211) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4010) = FormalQtyType.IntType // MEMBER.LINE_NUMBER
    nodePropertyDescriptors(4011) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4550) = FormalQtyType.StringType // MEMBER.NAME
    nodePropertyDescriptors(4551) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5180) = FormalQtyType.IntType // MEMBER.ORDER
    nodePropertyDescriptors(5181) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5720) = FormalQtyType.StringType // MEMBER.POSSIBLE_TYPES
    nodePropertyDescriptors(5721) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(7340) = FormalQtyType.StringType // MEMBER.TYPE_FULL_NAME
    nodePropertyDescriptors(7341) = FormalQtyType.QtyOne
    nodePropertyDescriptors(2302) = FormalQtyType.StringType // META_DATA.HASH
    nodePropertyDescriptors(2303) = FormalQtyType.QtyOption
    nodePropertyDescriptors(3832) = FormalQtyType.StringType // META_DATA.LANGUAGE
    nodePropertyDescriptors(3833) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5362) = FormalQtyType.StringType // META_DATA.OVERLAYS
    nodePropertyDescriptors(5363) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(6262) = FormalQtyType.StringType // META_DATA.ROOT
    nodePropertyDescriptors(6263) = FormalQtyType.QtyOne
    nodePropertyDescriptors(7882) = FormalQtyType.StringType // META_DATA.VERSION
    nodePropertyDescriptors(7883) = FormalQtyType.QtyOne
    nodePropertyDescriptors(414) = FormalQtyType.StringType // METHOD.AST_PARENT_FULL_NAME
    nodePropertyDescriptors(415) = FormalQtyType.QtyOne
    nodePropertyDescriptors(504) = FormalQtyType.StringType // METHOD.AST_PARENT_TYPE
    nodePropertyDescriptors(505) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1044) = FormalQtyType.StringType // METHOD.CODE
    nodePropertyDescriptors(1045) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1134) = FormalQtyType.IntType // METHOD.COLUMN_NUMBER
    nodePropertyDescriptors(1135) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1224) = FormalQtyType.IntType // METHOD.COLUMN_NUMBER_END
    nodePropertyDescriptors(1225) = FormalQtyType.QtyOption
    nodePropertyDescriptors(2034) = FormalQtyType.StringType // METHOD.FILENAME
    nodePropertyDescriptors(2035) = FormalQtyType.QtyOne
    nodePropertyDescriptors(2124) = FormalQtyType.StringType // METHOD.FULL_NAME
    nodePropertyDescriptors(2125) = FormalQtyType.QtyOne
    nodePropertyDescriptors(2214) = FormalQtyType.StringType // METHOD.GENERIC_SIGNATURE
    nodePropertyDescriptors(2215) = FormalQtyType.QtyOne
    nodePropertyDescriptors(2304) = FormalQtyType.StringType // METHOD.HASH
    nodePropertyDescriptors(2305) = FormalQtyType.QtyOption
    nodePropertyDescriptors(2934) = FormalQtyType.BoolType // METHOD.IS_EXTERNAL
    nodePropertyDescriptors(2935) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4014) = FormalQtyType.IntType // METHOD.LINE_NUMBER
    nodePropertyDescriptors(4015) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4104) = FormalQtyType.IntType // METHOD.LINE_NUMBER_END
    nodePropertyDescriptors(4105) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4554) = FormalQtyType.StringType // METHOD.NAME
    nodePropertyDescriptors(4555) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4824) = FormalQtyType.IntType // METHOD.OFFSET
    nodePropertyDescriptors(4825) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4914) = FormalQtyType.IntType // METHOD.OFFSET_END
    nodePropertyDescriptors(4915) = FormalQtyType.QtyOption
    nodePropertyDescriptors(5184) = FormalQtyType.IntType // METHOD.ORDER
    nodePropertyDescriptors(5185) = FormalQtyType.QtyOne
    nodePropertyDescriptors(6714) = FormalQtyType.StringType // METHOD.SIGNATURE
    nodePropertyDescriptors(6715) = FormalQtyType.QtyOne
    nodePropertyDescriptors(866) = FormalQtyType.StringType // METHOD_PARAMETER_IN.CLOSURE_BINDING_ID
    nodePropertyDescriptors(867) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1046) = FormalQtyType.StringType // METHOD_PARAMETER_IN.CODE
    nodePropertyDescriptors(1047) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1136) = FormalQtyType.IntType // METHOD_PARAMETER_IN.COLUMN_NUMBER
    nodePropertyDescriptors(1137) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1766) = FormalQtyType.StringType // METHOD_PARAMETER_IN.DYNAMIC_TYPE_HINT_FULL_NAME
    nodePropertyDescriptors(1767) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(1856) = FormalQtyType.StringType // METHOD_PARAMETER_IN.EVALUATION_STRATEGY
    nodePropertyDescriptors(1857) = FormalQtyType.QtyOne
    nodePropertyDescriptors(2576) = FormalQtyType.IntType // METHOD_PARAMETER_IN.INDEX
    nodePropertyDescriptors(2577) = FormalQtyType.QtyOne
    nodePropertyDescriptors(3026) = FormalQtyType.BoolType // METHOD_PARAMETER_IN.IS_VARIADIC
    nodePropertyDescriptors(3027) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4016) = FormalQtyType.IntType // METHOD_PARAMETER_IN.LINE_NUMBER
    nodePropertyDescriptors(4017) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4556) = FormalQtyType.StringType // METHOD_PARAMETER_IN.NAME
    nodePropertyDescriptors(4557) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5186) = FormalQtyType.IntType // METHOD_PARAMETER_IN.ORDER
    nodePropertyDescriptors(5187) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5726) = FormalQtyType.StringType // METHOD_PARAMETER_IN.POSSIBLE_TYPES
    nodePropertyDescriptors(5727) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(7346) = FormalQtyType.StringType // METHOD_PARAMETER_IN.TYPE_FULL_NAME
    nodePropertyDescriptors(7347) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1048) = FormalQtyType.StringType // METHOD_PARAMETER_OUT.CODE
    nodePropertyDescriptors(1049) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1138) = FormalQtyType.IntType // METHOD_PARAMETER_OUT.COLUMN_NUMBER
    nodePropertyDescriptors(1139) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1858) = FormalQtyType.StringType // METHOD_PARAMETER_OUT.EVALUATION_STRATEGY
    nodePropertyDescriptors(1859) = FormalQtyType.QtyOne
    nodePropertyDescriptors(2578) = FormalQtyType.IntType // METHOD_PARAMETER_OUT.INDEX
    nodePropertyDescriptors(2579) = FormalQtyType.QtyOne
    nodePropertyDescriptors(3028) = FormalQtyType.BoolType // METHOD_PARAMETER_OUT.IS_VARIADIC
    nodePropertyDescriptors(3029) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4018) = FormalQtyType.IntType // METHOD_PARAMETER_OUT.LINE_NUMBER
    nodePropertyDescriptors(4019) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4558) = FormalQtyType.StringType // METHOD_PARAMETER_OUT.NAME
    nodePropertyDescriptors(4559) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5188) = FormalQtyType.IntType // METHOD_PARAMETER_OUT.ORDER
    nodePropertyDescriptors(5189) = FormalQtyType.QtyOne
    nodePropertyDescriptors(7348) = FormalQtyType.StringType // METHOD_PARAMETER_OUT.TYPE_FULL_NAME
    nodePropertyDescriptors(7349) = FormalQtyType.QtyOne
    nodePropertyDescriptors(240) = FormalQtyType.IntType // METHOD_REF.ARGUMENT_INDEX
    nodePropertyDescriptors(241) = FormalQtyType.QtyOne
    nodePropertyDescriptors(330) = FormalQtyType.StringType // METHOD_REF.ARGUMENT_NAME
    nodePropertyDescriptors(331) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1050) = FormalQtyType.StringType // METHOD_REF.CODE
    nodePropertyDescriptors(1051) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1140) = FormalQtyType.IntType // METHOD_REF.COLUMN_NUMBER
    nodePropertyDescriptors(1141) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1770) = FormalQtyType.StringType // METHOD_REF.DYNAMIC_TYPE_HINT_FULL_NAME
    nodePropertyDescriptors(1771) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(4020) = FormalQtyType.IntType // METHOD_REF.LINE_NUMBER
    nodePropertyDescriptors(4021) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4200) = FormalQtyType.StringType // METHOD_REF.METHOD_FULL_NAME
    nodePropertyDescriptors(4201) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5190) = FormalQtyType.IntType // METHOD_REF.ORDER
    nodePropertyDescriptors(5191) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5730) = FormalQtyType.StringType // METHOD_REF.POSSIBLE_TYPES
    nodePropertyDescriptors(5731) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(7350) = FormalQtyType.StringType // METHOD_REF.TYPE_FULL_NAME
    nodePropertyDescriptors(7351) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1052) = FormalQtyType.StringType // METHOD_RETURN.CODE
    nodePropertyDescriptors(1053) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1142) = FormalQtyType.IntType // METHOD_RETURN.COLUMN_NUMBER
    nodePropertyDescriptors(1143) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1772) = FormalQtyType.StringType // METHOD_RETURN.DYNAMIC_TYPE_HINT_FULL_NAME
    nodePropertyDescriptors(1773) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(1862) = FormalQtyType.StringType // METHOD_RETURN.EVALUATION_STRATEGY
    nodePropertyDescriptors(1863) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4022) = FormalQtyType.IntType // METHOD_RETURN.LINE_NUMBER
    nodePropertyDescriptors(4023) = FormalQtyType.QtyOption
    nodePropertyDescriptors(5192) = FormalQtyType.IntType // METHOD_RETURN.ORDER
    nodePropertyDescriptors(5193) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5732) = FormalQtyType.StringType // METHOD_RETURN.POSSIBLE_TYPES
    nodePropertyDescriptors(5733) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(7352) = FormalQtyType.StringType // METHOD_RETURN.TYPE_FULL_NAME
    nodePropertyDescriptors(7353) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1054) = FormalQtyType.StringType // MODIFIER.CODE
    nodePropertyDescriptors(1055) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1144) = FormalQtyType.IntType // MODIFIER.COLUMN_NUMBER
    nodePropertyDescriptors(1145) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4024) = FormalQtyType.IntType // MODIFIER.LINE_NUMBER
    nodePropertyDescriptors(4025) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4474) = FormalQtyType.StringType // MODIFIER.MODIFIER_TYPE
    nodePropertyDescriptors(4475) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5194) = FormalQtyType.IntType // MODIFIER.ORDER
    nodePropertyDescriptors(5195) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1056) = FormalQtyType.StringType // NAMESPACE.CODE
    nodePropertyDescriptors(1057) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1146) = FormalQtyType.IntType // NAMESPACE.COLUMN_NUMBER
    nodePropertyDescriptors(1147) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4026) = FormalQtyType.IntType // NAMESPACE.LINE_NUMBER
    nodePropertyDescriptors(4027) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4566) = FormalQtyType.StringType // NAMESPACE.NAME
    nodePropertyDescriptors(4567) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5196) = FormalQtyType.IntType // NAMESPACE.ORDER
    nodePropertyDescriptors(5197) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1058) = FormalQtyType.StringType // NAMESPACE_BLOCK.CODE
    nodePropertyDescriptors(1059) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1148) = FormalQtyType.IntType // NAMESPACE_BLOCK.COLUMN_NUMBER
    nodePropertyDescriptors(1149) = FormalQtyType.QtyOption
    nodePropertyDescriptors(2048) = FormalQtyType.StringType // NAMESPACE_BLOCK.FILENAME
    nodePropertyDescriptors(2049) = FormalQtyType.QtyOne
    nodePropertyDescriptors(2138) = FormalQtyType.StringType // NAMESPACE_BLOCK.FULL_NAME
    nodePropertyDescriptors(2139) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4028) = FormalQtyType.IntType // NAMESPACE_BLOCK.LINE_NUMBER
    nodePropertyDescriptors(4029) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4568) = FormalQtyType.StringType // NAMESPACE_BLOCK.NAME
    nodePropertyDescriptors(4569) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5198) = FormalQtyType.IntType // NAMESPACE_BLOCK.ORDER
    nodePropertyDescriptors(5199) = FormalQtyType.QtyOne
    nodePropertyDescriptors(250) = FormalQtyType.IntType // RETURN.ARGUMENT_INDEX
    nodePropertyDescriptors(251) = FormalQtyType.QtyOne
    nodePropertyDescriptors(340) = FormalQtyType.StringType // RETURN.ARGUMENT_NAME
    nodePropertyDescriptors(341) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1060) = FormalQtyType.StringType // RETURN.CODE
    nodePropertyDescriptors(1061) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1150) = FormalQtyType.IntType // RETURN.COLUMN_NUMBER
    nodePropertyDescriptors(1151) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4030) = FormalQtyType.IntType // RETURN.LINE_NUMBER
    nodePropertyDescriptors(4031) = FormalQtyType.QtyOption
    nodePropertyDescriptors(5200) = FormalQtyType.IntType // RETURN.ORDER
    nodePropertyDescriptors(5201) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4572) = FormalQtyType.StringType // TAG.NAME
    nodePropertyDescriptors(4573) = FormalQtyType.QtyOne
    nodePropertyDescriptors(7542) = FormalQtyType.StringType // TAG.VALUE
    nodePropertyDescriptors(7543) = FormalQtyType.QtyOne
    nodePropertyDescriptors(8174) = FormalQtyType.RefType // TAG_NODE_PAIR.node
    nodePropertyDescriptors(8175) = FormalQtyType.QtyOne
    nodePropertyDescriptors(8264) = FormalQtyType.RefType // TAG_NODE_PAIR.tag
    nodePropertyDescriptors(8265) = FormalQtyType.QtyOne
    nodePropertyDescriptors(256) = FormalQtyType.IntType // TEMPLATE_DOM.ARGUMENT_INDEX
    nodePropertyDescriptors(257) = FormalQtyType.QtyOne
    nodePropertyDescriptors(346) = FormalQtyType.StringType // TEMPLATE_DOM.ARGUMENT_NAME
    nodePropertyDescriptors(347) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1066) = FormalQtyType.StringType // TEMPLATE_DOM.CODE
    nodePropertyDescriptors(1067) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1156) = FormalQtyType.IntType // TEMPLATE_DOM.COLUMN_NUMBER
    nodePropertyDescriptors(1157) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4036) = FormalQtyType.IntType // TEMPLATE_DOM.LINE_NUMBER
    nodePropertyDescriptors(4037) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4576) = FormalQtyType.StringType // TEMPLATE_DOM.NAME
    nodePropertyDescriptors(4577) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5206) = FormalQtyType.IntType // TEMPLATE_DOM.ORDER
    nodePropertyDescriptors(5207) = FormalQtyType.QtyOne
    nodePropertyDescriptors(2148) = FormalQtyType.StringType // TYPE.FULL_NAME
    nodePropertyDescriptors(2149) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4578) = FormalQtyType.StringType // TYPE.NAME
    nodePropertyDescriptors(4579) = FormalQtyType.QtyOne
    nodePropertyDescriptors(7278) = FormalQtyType.StringType // TYPE.TYPE_DECL_FULL_NAME
    nodePropertyDescriptors(7279) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1070) = FormalQtyType.StringType // TYPE_ARGUMENT.CODE
    nodePropertyDescriptors(1071) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1160) = FormalQtyType.IntType // TYPE_ARGUMENT.COLUMN_NUMBER
    nodePropertyDescriptors(1161) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4040) = FormalQtyType.IntType // TYPE_ARGUMENT.LINE_NUMBER
    nodePropertyDescriptors(4041) = FormalQtyType.QtyOption
    nodePropertyDescriptors(5210) = FormalQtyType.IntType // TYPE_ARGUMENT.ORDER
    nodePropertyDescriptors(5211) = FormalQtyType.QtyOne
    nodePropertyDescriptors(172) = FormalQtyType.StringType // TYPE_DECL.ALIAS_TYPE_FULL_NAME
    nodePropertyDescriptors(173) = FormalQtyType.QtyOption
    nodePropertyDescriptors(442) = FormalQtyType.StringType // TYPE_DECL.AST_PARENT_FULL_NAME
    nodePropertyDescriptors(443) = FormalQtyType.QtyOne
    nodePropertyDescriptors(532) = FormalQtyType.StringType // TYPE_DECL.AST_PARENT_TYPE
    nodePropertyDescriptors(533) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1072) = FormalQtyType.StringType // TYPE_DECL.CODE
    nodePropertyDescriptors(1073) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1162) = FormalQtyType.IntType // TYPE_DECL.COLUMN_NUMBER
    nodePropertyDescriptors(1163) = FormalQtyType.QtyOption
    nodePropertyDescriptors(2062) = FormalQtyType.StringType // TYPE_DECL.FILENAME
    nodePropertyDescriptors(2063) = FormalQtyType.QtyOne
    nodePropertyDescriptors(2152) = FormalQtyType.StringType // TYPE_DECL.FULL_NAME
    nodePropertyDescriptors(2153) = FormalQtyType.QtyOne
    nodePropertyDescriptors(2242) = FormalQtyType.StringType // TYPE_DECL.GENERIC_SIGNATURE
    nodePropertyDescriptors(2243) = FormalQtyType.QtyOne
    nodePropertyDescriptors(2692) = FormalQtyType.StringType // TYPE_DECL.INHERITS_FROM_TYPE_FULL_NAME
    nodePropertyDescriptors(2693) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(2962) = FormalQtyType.BoolType // TYPE_DECL.IS_EXTERNAL
    nodePropertyDescriptors(2963) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4042) = FormalQtyType.IntType // TYPE_DECL.LINE_NUMBER
    nodePropertyDescriptors(4043) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4582) = FormalQtyType.StringType // TYPE_DECL.NAME
    nodePropertyDescriptors(4583) = FormalQtyType.QtyOne
    nodePropertyDescriptors(4852) = FormalQtyType.IntType // TYPE_DECL.OFFSET
    nodePropertyDescriptors(4853) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4942) = FormalQtyType.IntType // TYPE_DECL.OFFSET_END
    nodePropertyDescriptors(4943) = FormalQtyType.QtyOption
    nodePropertyDescriptors(5212) = FormalQtyType.IntType // TYPE_DECL.ORDER
    nodePropertyDescriptors(5213) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1074) = FormalQtyType.StringType // TYPE_PARAMETER.CODE
    nodePropertyDescriptors(1075) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1164) = FormalQtyType.IntType // TYPE_PARAMETER.COLUMN_NUMBER
    nodePropertyDescriptors(1165) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4044) = FormalQtyType.IntType // TYPE_PARAMETER.LINE_NUMBER
    nodePropertyDescriptors(4045) = FormalQtyType.QtyOption
    nodePropertyDescriptors(4584) = FormalQtyType.StringType // TYPE_PARAMETER.NAME
    nodePropertyDescriptors(4585) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5214) = FormalQtyType.IntType // TYPE_PARAMETER.ORDER
    nodePropertyDescriptors(5215) = FormalQtyType.QtyOne
    nodePropertyDescriptors(266) = FormalQtyType.IntType // TYPE_REF.ARGUMENT_INDEX
    nodePropertyDescriptors(267) = FormalQtyType.QtyOne
    nodePropertyDescriptors(356) = FormalQtyType.StringType // TYPE_REF.ARGUMENT_NAME
    nodePropertyDescriptors(357) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1076) = FormalQtyType.StringType // TYPE_REF.CODE
    nodePropertyDescriptors(1077) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1166) = FormalQtyType.IntType // TYPE_REF.COLUMN_NUMBER
    nodePropertyDescriptors(1167) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1796) = FormalQtyType.StringType // TYPE_REF.DYNAMIC_TYPE_HINT_FULL_NAME
    nodePropertyDescriptors(1797) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(4046) = FormalQtyType.IntType // TYPE_REF.LINE_NUMBER
    nodePropertyDescriptors(4047) = FormalQtyType.QtyOption
    nodePropertyDescriptors(5216) = FormalQtyType.IntType // TYPE_REF.ORDER
    nodePropertyDescriptors(5217) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5756) = FormalQtyType.StringType // TYPE_REF.POSSIBLE_TYPES
    nodePropertyDescriptors(5757) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(7376) = FormalQtyType.StringType // TYPE_REF.TYPE_FULL_NAME
    nodePropertyDescriptors(7377) = FormalQtyType.QtyOne
    nodePropertyDescriptors(268) = FormalQtyType.IntType // UNKNOWN.ARGUMENT_INDEX
    nodePropertyDescriptors(269) = FormalQtyType.QtyOne
    nodePropertyDescriptors(358) = FormalQtyType.StringType // UNKNOWN.ARGUMENT_NAME
    nodePropertyDescriptors(359) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1078) = FormalQtyType.StringType // UNKNOWN.CODE
    nodePropertyDescriptors(1079) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1168) = FormalQtyType.IntType // UNKNOWN.COLUMN_NUMBER
    nodePropertyDescriptors(1169) = FormalQtyType.QtyOption
    nodePropertyDescriptors(1348) = FormalQtyType.StringType // UNKNOWN.CONTAINED_REF
    nodePropertyDescriptors(1349) = FormalQtyType.QtyOne
    nodePropertyDescriptors(1798) = FormalQtyType.StringType // UNKNOWN.DYNAMIC_TYPE_HINT_FULL_NAME
    nodePropertyDescriptors(1799) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(4048) = FormalQtyType.IntType // UNKNOWN.LINE_NUMBER
    nodePropertyDescriptors(4049) = FormalQtyType.QtyOption
    nodePropertyDescriptors(5218) = FormalQtyType.IntType // UNKNOWN.ORDER
    nodePropertyDescriptors(5219) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5668) = FormalQtyType.StringType // UNKNOWN.PARSER_TYPE_NAME
    nodePropertyDescriptors(5669) = FormalQtyType.QtyOne
    nodePropertyDescriptors(5758) = FormalQtyType.StringType // UNKNOWN.POSSIBLE_TYPES
    nodePropertyDescriptors(5759) = FormalQtyType.QtyMulti
    nodePropertyDescriptors(7378) = FormalQtyType.StringType // UNKNOWN.TYPE_FULL_NAME
    nodePropertyDescriptors(7379) = FormalQtyType.QtyOne
    nodePropertyDescriptors
  }
  private val newNodeInsertionHelpers: Array[flatgraph.NewNodePropertyInsertionHelper] = {
    val _newNodeInserters = new Array[flatgraph.NewNodePropertyInsertionHelper](8280)
    _newNodeInserters(180) = nodes.NewAnnotation.InsertionHelpers.NewNodeInserter_Annotation_argumentIndex
    _newNodeInserters(270) = nodes.NewAnnotation.InsertionHelpers.NewNodeInserter_Annotation_argumentName
    _newNodeInserters(990) = nodes.NewAnnotation.InsertionHelpers.NewNodeInserter_Annotation_code
    _newNodeInserters(1080) = nodes.NewAnnotation.InsertionHelpers.NewNodeInserter_Annotation_columnNumber
    _newNodeInserters(2070) = nodes.NewAnnotation.InsertionHelpers.NewNodeInserter_Annotation_fullName
    _newNodeInserters(3960) = nodes.NewAnnotation.InsertionHelpers.NewNodeInserter_Annotation_lineNumber
    _newNodeInserters(4500) = nodes.NewAnnotation.InsertionHelpers.NewNodeInserter_Annotation_name
    _newNodeInserters(5130) = nodes.NewAnnotation.InsertionHelpers.NewNodeInserter_Annotation_order
    _newNodeInserters(182) = nodes.NewAnnotationLiteral.InsertionHelpers.NewNodeInserter_AnnotationLiteral_argumentIndex
    _newNodeInserters(272) = nodes.NewAnnotationLiteral.InsertionHelpers.NewNodeInserter_AnnotationLiteral_argumentName
    _newNodeInserters(992) = nodes.NewAnnotationLiteral.InsertionHelpers.NewNodeInserter_AnnotationLiteral_code
    _newNodeInserters(1082) = nodes.NewAnnotationLiteral.InsertionHelpers.NewNodeInserter_AnnotationLiteral_columnNumber
    _newNodeInserters(3962) = nodes.NewAnnotationLiteral.InsertionHelpers.NewNodeInserter_AnnotationLiteral_lineNumber
    _newNodeInserters(4502) = nodes.NewAnnotationLiteral.InsertionHelpers.NewNodeInserter_AnnotationLiteral_name
    _newNodeInserters(5132) = nodes.NewAnnotationLiteral.InsertionHelpers.NewNodeInserter_AnnotationLiteral_order
    _newNodeInserters(994) = nodes.NewAnnotationParameter.InsertionHelpers.NewNodeInserter_AnnotationParameter_code
    _newNodeInserters(1084) =
      nodes.NewAnnotationParameter.InsertionHelpers.NewNodeInserter_AnnotationParameter_columnNumber
    _newNodeInserters(3964) =
      nodes.NewAnnotationParameter.InsertionHelpers.NewNodeInserter_AnnotationParameter_lineNumber
    _newNodeInserters(5134) = nodes.NewAnnotationParameter.InsertionHelpers.NewNodeInserter_AnnotationParameter_order
    _newNodeInserters(996) =
      nodes.NewAnnotationParameterAssign.InsertionHelpers.NewNodeInserter_AnnotationParameterAssign_code
    _newNodeInserters(1086) =
      nodes.NewAnnotationParameterAssign.InsertionHelpers.NewNodeInserter_AnnotationParameterAssign_columnNumber
    _newNodeInserters(3966) =
      nodes.NewAnnotationParameterAssign.InsertionHelpers.NewNodeInserter_AnnotationParameterAssign_lineNumber
    _newNodeInserters(5136) =
      nodes.NewAnnotationParameterAssign.InsertionHelpers.NewNodeInserter_AnnotationParameterAssign_order
    _newNodeInserters(188) = nodes.NewArrayInitializer.InsertionHelpers.NewNodeInserter_ArrayInitializer_argumentIndex
    _newNodeInserters(278) = nodes.NewArrayInitializer.InsertionHelpers.NewNodeInserter_ArrayInitializer_argumentName
    _newNodeInserters(998) = nodes.NewArrayInitializer.InsertionHelpers.NewNodeInserter_ArrayInitializer_code
    _newNodeInserters(1088) = nodes.NewArrayInitializer.InsertionHelpers.NewNodeInserter_ArrayInitializer_columnNumber
    _newNodeInserters(3968) = nodes.NewArrayInitializer.InsertionHelpers.NewNodeInserter_ArrayInitializer_lineNumber
    _newNodeInserters(5138) = nodes.NewArrayInitializer.InsertionHelpers.NewNodeInserter_ArrayInitializer_order
    _newNodeInserters(4150) = nodes.NewBinding.InsertionHelpers.NewNodeInserter_Binding_methodFullName
    _newNodeInserters(4510) = nodes.NewBinding.InsertionHelpers.NewNodeInserter_Binding_name
    _newNodeInserters(6670) = nodes.NewBinding.InsertionHelpers.NewNodeInserter_Binding_signature
    _newNodeInserters(192) = nodes.NewBlock.InsertionHelpers.NewNodeInserter_Block_argumentIndex
    _newNodeInserters(282) = nodes.NewBlock.InsertionHelpers.NewNodeInserter_Block_argumentName
    _newNodeInserters(1002) = nodes.NewBlock.InsertionHelpers.NewNodeInserter_Block_code
    _newNodeInserters(1092) = nodes.NewBlock.InsertionHelpers.NewNodeInserter_Block_columnNumber
    _newNodeInserters(1722) = nodes.NewBlock.InsertionHelpers.NewNodeInserter_Block_dynamicTypeHintFullName
    _newNodeInserters(3972) = nodes.NewBlock.InsertionHelpers.NewNodeInserter_Block_lineNumber
    _newNodeInserters(5142) = nodes.NewBlock.InsertionHelpers.NewNodeInserter_Block_order
    _newNodeInserters(5682) = nodes.NewBlock.InsertionHelpers.NewNodeInserter_Block_possibleTypes
    _newNodeInserters(7302) = nodes.NewBlock.InsertionHelpers.NewNodeInserter_Block_typeFullName
    _newNodeInserters(194) = nodes.NewCall.InsertionHelpers.NewNodeInserter_Call_argumentIndex
    _newNodeInserters(284) = nodes.NewCall.InsertionHelpers.NewNodeInserter_Call_argumentName
    _newNodeInserters(1004) = nodes.NewCall.InsertionHelpers.NewNodeInserter_Call_code
    _newNodeInserters(1094) = nodes.NewCall.InsertionHelpers.NewNodeInserter_Call_columnNumber
    _newNodeInserters(1634) = nodes.NewCall.InsertionHelpers.NewNodeInserter_Call_dispatchType
    _newNodeInserters(1724) = nodes.NewCall.InsertionHelpers.NewNodeInserter_Call_dynamicTypeHintFullName
    _newNodeInserters(3974) = nodes.NewCall.InsertionHelpers.NewNodeInserter_Call_lineNumber
    _newNodeInserters(4154) = nodes.NewCall.InsertionHelpers.NewNodeInserter_Call_methodFullName
    _newNodeInserters(4514) = nodes.NewCall.InsertionHelpers.NewNodeInserter_Call_name
    _newNodeInserters(5144) = nodes.NewCall.InsertionHelpers.NewNodeInserter_Call_order
    _newNodeInserters(5684) = nodes.NewCall.InsertionHelpers.NewNodeInserter_Call_possibleTypes
    _newNodeInserters(6674) = nodes.NewCall.InsertionHelpers.NewNodeInserter_Call_signature
    _newNodeInserters(7304) = nodes.NewCall.InsertionHelpers.NewNodeInserter_Call_typeFullName
    _newNodeInserters(826) = nodes.NewClosureBinding.InsertionHelpers.NewNodeInserter_ClosureBinding_closureBindingId
    _newNodeInserters(916) = nodes.NewClosureBinding.InsertionHelpers.NewNodeInserter_ClosureBinding_closureOriginalName
    _newNodeInserters(1816) = nodes.NewClosureBinding.InsertionHelpers.NewNodeInserter_ClosureBinding_evaluationStrategy
    _newNodeInserters(1008) = nodes.NewComment.InsertionHelpers.NewNodeInserter_Comment_code
    _newNodeInserters(1098) = nodes.NewComment.InsertionHelpers.NewNodeInserter_Comment_columnNumber
    _newNodeInserters(1998) = nodes.NewComment.InsertionHelpers.NewNodeInserter_Comment_filename
    _newNodeInserters(3978) = nodes.NewComment.InsertionHelpers.NewNodeInserter_Comment_lineNumber
    _newNodeInserters(5148) = nodes.NewComment.InsertionHelpers.NewNodeInserter_Comment_order
    _newNodeInserters(1370) = nodes.NewConfigFile.InsertionHelpers.NewNodeInserter_ConfigFile_content
    _newNodeInserters(4520) = nodes.NewConfigFile.InsertionHelpers.NewNodeInserter_ConfigFile_name
    _newNodeInserters(202) = nodes.NewControlStructure.InsertionHelpers.NewNodeInserter_ControlStructure_argumentIndex
    _newNodeInserters(292) = nodes.NewControlStructure.InsertionHelpers.NewNodeInserter_ControlStructure_argumentName
    _newNodeInserters(1012) = nodes.NewControlStructure.InsertionHelpers.NewNodeInserter_ControlStructure_code
    _newNodeInserters(1102) = nodes.NewControlStructure.InsertionHelpers.NewNodeInserter_ControlStructure_columnNumber
    _newNodeInserters(1462) =
      nodes.NewControlStructure.InsertionHelpers.NewNodeInserter_ControlStructure_controlStructureType
    _newNodeInserters(3982) = nodes.NewControlStructure.InsertionHelpers.NewNodeInserter_ControlStructure_lineNumber
    _newNodeInserters(5152) = nodes.NewControlStructure.InsertionHelpers.NewNodeInserter_ControlStructure_order
    _newNodeInserters(5602) = nodes.NewControlStructure.InsertionHelpers.NewNodeInserter_ControlStructure_parserTypeName
    _newNodeInserters(24) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_algorithm
    _newNodeInserters(2724) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_isDeprecated
    _newNodeInserters(3174) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_iterations
    _newNodeInserters(3264) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_ivSourceType
    _newNodeInserters(3444) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_keyLength
    _newNodeInserters(3534) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_keySize
    _newNodeInserters(3624) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_keySource
    _newNodeInserters(3714) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_keySourceType
    _newNodeInserters(3894) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_lineNo
    _newNodeInserters(4344) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_mode
    _newNodeInserters(4704) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_nonceSourceType
    _newNodeInserters(5064) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_opType
    _newNodeInserters(4974) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_operation
    _newNodeInserters(5244) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_outputLength
    _newNodeInserters(5514) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_padding
    _newNodeInserters(5784) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_protocolVersion
    _newNodeInserters(5874) =
      nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_providesIntegrity
    _newNodeInserters(5964) =
      nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_requiresCsprngSource
    _newNodeInserters(6054) =
      nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_requiresUniqueNonce
    _newNodeInserters(6144) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_resolution
    _newNodeInserters(6324) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_saltSourceType
    _newNodeInserters(6414) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_saltUsed
    _newNodeInserters(6504) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_semanticDetail
    _newNodeInserters(6594) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_semanticKind
    _newNodeInserters(6774) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_source
    _newNodeInserters(6864) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_srcFile
    _newNodeInserters(7044) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_tagLength
    _newNodeInserters(7134) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_transportScheme
    _newNodeInserters(7404) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_usedForCrypto
    _newNodeInserters(7584) =
      nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_verificationEnabled
    _newNodeInserters(7674) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_verificationKind
    _newNodeInserters(7764) = nodes.NewCryptoOperation.InsertionHelpers.NewNodeInserter_CryptoOperation_verified
    _newNodeInserters(1556) = nodes.NewDependency.InsertionHelpers.NewNodeInserter_Dependency_dependencyGroupId
    _newNodeInserters(4526) = nodes.NewDependency.InsertionHelpers.NewNodeInserter_Dependency_name
    _newNodeInserters(7856) = nodes.NewDependency.InsertionHelpers.NewNodeInserter_Dependency_version
    _newNodeInserters(208) = nodes.NewFieldIdentifier.InsertionHelpers.NewNodeInserter_FieldIdentifier_argumentIndex
    _newNodeInserters(298) = nodes.NewFieldIdentifier.InsertionHelpers.NewNodeInserter_FieldIdentifier_argumentName
    _newNodeInserters(568) = nodes.NewFieldIdentifier.InsertionHelpers.NewNodeInserter_FieldIdentifier_canonicalName
    _newNodeInserters(1018) = nodes.NewFieldIdentifier.InsertionHelpers.NewNodeInserter_FieldIdentifier_code
    _newNodeInserters(1108) = nodes.NewFieldIdentifier.InsertionHelpers.NewNodeInserter_FieldIdentifier_columnNumber
    _newNodeInserters(3988) = nodes.NewFieldIdentifier.InsertionHelpers.NewNodeInserter_FieldIdentifier_lineNumber
    _newNodeInserters(5158) = nodes.NewFieldIdentifier.InsertionHelpers.NewNodeInserter_FieldIdentifier_order
    _newNodeInserters(1020) = nodes.NewFile.InsertionHelpers.NewNodeInserter_File_code
    _newNodeInserters(1110) = nodes.NewFile.InsertionHelpers.NewNodeInserter_File_columnNumber
    _newNodeInserters(1380) = nodes.NewFile.InsertionHelpers.NewNodeInserter_File_content
    _newNodeInserters(2280) = nodes.NewFile.InsertionHelpers.NewNodeInserter_File_hash
    _newNodeInserters(3990) = nodes.NewFile.InsertionHelpers.NewNodeInserter_File_lineNumber
    _newNodeInserters(4530) = nodes.NewFile.InsertionHelpers.NewNodeInserter_File_name
    _newNodeInserters(5160) = nodes.NewFile.InsertionHelpers.NewNodeInserter_File_order
    _newNodeInserters(7952) = nodes.NewFinding.InsertionHelpers.NewNodeInserter_Finding_evidence
    _newNodeInserters(8042) = nodes.NewFinding.InsertionHelpers.NewNodeInserter_Finding_keyValuePairs
    _newNodeInserters(214) = nodes.NewIdentifier.InsertionHelpers.NewNodeInserter_Identifier_argumentIndex
    _newNodeInserters(304) = nodes.NewIdentifier.InsertionHelpers.NewNodeInserter_Identifier_argumentName
    _newNodeInserters(1024) = nodes.NewIdentifier.InsertionHelpers.NewNodeInserter_Identifier_code
    _newNodeInserters(1114) = nodes.NewIdentifier.InsertionHelpers.NewNodeInserter_Identifier_columnNumber
    _newNodeInserters(1744) = nodes.NewIdentifier.InsertionHelpers.NewNodeInserter_Identifier_dynamicTypeHintFullName
    _newNodeInserters(3994) = nodes.NewIdentifier.InsertionHelpers.NewNodeInserter_Identifier_lineNumber
    _newNodeInserters(4534) = nodes.NewIdentifier.InsertionHelpers.NewNodeInserter_Identifier_name
    _newNodeInserters(5164) = nodes.NewIdentifier.InsertionHelpers.NewNodeInserter_Identifier_order
    _newNodeInserters(5704) = nodes.NewIdentifier.InsertionHelpers.NewNodeInserter_Identifier_possibleTypes
    _newNodeInserters(7324) = nodes.NewIdentifier.InsertionHelpers.NewNodeInserter_Identifier_typeFullName
    _newNodeInserters(1026) = nodes.NewImport.InsertionHelpers.NewNodeInserter_Import_code
    _newNodeInserters(1116) = nodes.NewImport.InsertionHelpers.NewNodeInserter_Import_columnNumber
    _newNodeInserters(1926) = nodes.NewImport.InsertionHelpers.NewNodeInserter_Import_explicitAs
    _newNodeInserters(2376) = nodes.NewImport.InsertionHelpers.NewNodeInserter_Import_importedAs
    _newNodeInserters(2466) = nodes.NewImport.InsertionHelpers.NewNodeInserter_Import_importedEntity
    _newNodeInserters(2826) = nodes.NewImport.InsertionHelpers.NewNodeInserter_Import_isExplicit
    _newNodeInserters(3096) = nodes.NewImport.InsertionHelpers.NewNodeInserter_Import_isWildcard
    _newNodeInserters(3996) = nodes.NewImport.InsertionHelpers.NewNodeInserter_Import_lineNumber
    _newNodeInserters(5166) = nodes.NewImport.InsertionHelpers.NewNodeInserter_Import_order
    _newNodeInserters(1028) = nodes.NewJumpLabel.InsertionHelpers.NewNodeInserter_JumpLabel_code
    _newNodeInserters(1118) = nodes.NewJumpLabel.InsertionHelpers.NewNodeInserter_JumpLabel_columnNumber
    _newNodeInserters(3998) = nodes.NewJumpLabel.InsertionHelpers.NewNodeInserter_JumpLabel_lineNumber
    _newNodeInserters(4538) = nodes.NewJumpLabel.InsertionHelpers.NewNodeInserter_JumpLabel_name
    _newNodeInserters(5168) = nodes.NewJumpLabel.InsertionHelpers.NewNodeInserter_JumpLabel_order
    _newNodeInserters(5618) = nodes.NewJumpLabel.InsertionHelpers.NewNodeInserter_JumpLabel_parserTypeName
    _newNodeInserters(220) = nodes.NewJumpTarget.InsertionHelpers.NewNodeInserter_JumpTarget_argumentIndex
    _newNodeInserters(1030) = nodes.NewJumpTarget.InsertionHelpers.NewNodeInserter_JumpTarget_code
    _newNodeInserters(1120) = nodes.NewJumpTarget.InsertionHelpers.NewNodeInserter_JumpTarget_columnNumber
    _newNodeInserters(4000) = nodes.NewJumpTarget.InsertionHelpers.NewNodeInserter_JumpTarget_lineNumber
    _newNodeInserters(4540) = nodes.NewJumpTarget.InsertionHelpers.NewNodeInserter_JumpTarget_name
    _newNodeInserters(5170) = nodes.NewJumpTarget.InsertionHelpers.NewNodeInserter_JumpTarget_order
    _newNodeInserters(5620) = nodes.NewJumpTarget.InsertionHelpers.NewNodeInserter_JumpTarget_parserTypeName
    _newNodeInserters(3372) = nodes.NewKeyValuePair.InsertionHelpers.NewNodeInserter_KeyValuePair_key
    _newNodeInserters(7512) = nodes.NewKeyValuePair.InsertionHelpers.NewNodeInserter_KeyValuePair_value
    _newNodeInserters(224) = nodes.NewLiteral.InsertionHelpers.NewNodeInserter_Literal_argumentIndex
    _newNodeInserters(314) = nodes.NewLiteral.InsertionHelpers.NewNodeInserter_Literal_argumentName
    _newNodeInserters(1034) = nodes.NewLiteral.InsertionHelpers.NewNodeInserter_Literal_code
    _newNodeInserters(1124) = nodes.NewLiteral.InsertionHelpers.NewNodeInserter_Literal_columnNumber
    _newNodeInserters(1754) = nodes.NewLiteral.InsertionHelpers.NewNodeInserter_Literal_dynamicTypeHintFullName
    _newNodeInserters(4004) = nodes.NewLiteral.InsertionHelpers.NewNodeInserter_Literal_lineNumber
    _newNodeInserters(5174) = nodes.NewLiteral.InsertionHelpers.NewNodeInserter_Literal_order
    _newNodeInserters(5714) = nodes.NewLiteral.InsertionHelpers.NewNodeInserter_Literal_possibleTypes
    _newNodeInserters(7334) = nodes.NewLiteral.InsertionHelpers.NewNodeInserter_Literal_typeFullName
    _newNodeInserters(856) = nodes.NewLocal.InsertionHelpers.NewNodeInserter_Local_closureBindingId
    _newNodeInserters(1036) = nodes.NewLocal.InsertionHelpers.NewNodeInserter_Local_code
    _newNodeInserters(1126) = nodes.NewLocal.InsertionHelpers.NewNodeInserter_Local_columnNumber
    _newNodeInserters(1756) = nodes.NewLocal.InsertionHelpers.NewNodeInserter_Local_dynamicTypeHintFullName
    _newNodeInserters(2206) = nodes.NewLocal.InsertionHelpers.NewNodeInserter_Local_genericSignature
    _newNodeInserters(4006) = nodes.NewLocal.InsertionHelpers.NewNodeInserter_Local_lineNumber
    _newNodeInserters(4546) = nodes.NewLocal.InsertionHelpers.NewNodeInserter_Local_name
    _newNodeInserters(5176) = nodes.NewLocal.InsertionHelpers.NewNodeInserter_Local_order
    _newNodeInserters(5716) = nodes.NewLocal.InsertionHelpers.NewNodeInserter_Local_possibleTypes
    _newNodeInserters(7336) = nodes.NewLocal.InsertionHelpers.NewNodeInserter_Local_typeFullName
    _newNodeInserters(678) = nodes.NewLocation.InsertionHelpers.NewNodeInserter_Location_className
    _newNodeInserters(768) = nodes.NewLocation.InsertionHelpers.NewNodeInserter_Location_classShortName
    _newNodeInserters(2028) = nodes.NewLocation.InsertionHelpers.NewNodeInserter_Location_filename
    _newNodeInserters(4008) = nodes.NewLocation.InsertionHelpers.NewNodeInserter_Location_lineNumber
    _newNodeInserters(4188) = nodes.NewLocation.InsertionHelpers.NewNodeInserter_Location_methodFullName
    _newNodeInserters(4278) = nodes.NewLocation.InsertionHelpers.NewNodeInserter_Location_methodShortName
    _newNodeInserters(4638) = nodes.NewLocation.InsertionHelpers.NewNodeInserter_Location_nodeLabel
    _newNodeInserters(5448) = nodes.NewLocation.InsertionHelpers.NewNodeInserter_Location_packageName
    _newNodeInserters(6978) = nodes.NewLocation.InsertionHelpers.NewNodeInserter_Location_symbol
    _newNodeInserters(8148) = nodes.NewLocation.InsertionHelpers.NewNodeInserter_Location_node
    _newNodeInserters(410) = nodes.NewMember.InsertionHelpers.NewNodeInserter_Member_astParentFullName
    _newNodeInserters(500) = nodes.NewMember.InsertionHelpers.NewNodeInserter_Member_astParentType
    _newNodeInserters(1040) = nodes.NewMember.InsertionHelpers.NewNodeInserter_Member_code
    _newNodeInserters(1130) = nodes.NewMember.InsertionHelpers.NewNodeInserter_Member_columnNumber
    _newNodeInserters(1760) = nodes.NewMember.InsertionHelpers.NewNodeInserter_Member_dynamicTypeHintFullName
    _newNodeInserters(2210) = nodes.NewMember.InsertionHelpers.NewNodeInserter_Member_genericSignature
    _newNodeInserters(4010) = nodes.NewMember.InsertionHelpers.NewNodeInserter_Member_lineNumber
    _newNodeInserters(4550) = nodes.NewMember.InsertionHelpers.NewNodeInserter_Member_name
    _newNodeInserters(5180) = nodes.NewMember.InsertionHelpers.NewNodeInserter_Member_order
    _newNodeInserters(5720) = nodes.NewMember.InsertionHelpers.NewNodeInserter_Member_possibleTypes
    _newNodeInserters(7340) = nodes.NewMember.InsertionHelpers.NewNodeInserter_Member_typeFullName
    _newNodeInserters(2302) = nodes.NewMetaData.InsertionHelpers.NewNodeInserter_MetaData_hash
    _newNodeInserters(3832) = nodes.NewMetaData.InsertionHelpers.NewNodeInserter_MetaData_language
    _newNodeInserters(5362) = nodes.NewMetaData.InsertionHelpers.NewNodeInserter_MetaData_overlays
    _newNodeInserters(6262) = nodes.NewMetaData.InsertionHelpers.NewNodeInserter_MetaData_root
    _newNodeInserters(7882) = nodes.NewMetaData.InsertionHelpers.NewNodeInserter_MetaData_version
    _newNodeInserters(414) = nodes.NewMethod.InsertionHelpers.NewNodeInserter_Method_astParentFullName
    _newNodeInserters(504) = nodes.NewMethod.InsertionHelpers.NewNodeInserter_Method_astParentType
    _newNodeInserters(1044) = nodes.NewMethod.InsertionHelpers.NewNodeInserter_Method_code
    _newNodeInserters(1134) = nodes.NewMethod.InsertionHelpers.NewNodeInserter_Method_columnNumber
    _newNodeInserters(1224) = nodes.NewMethod.InsertionHelpers.NewNodeInserter_Method_columnNumberEnd
    _newNodeInserters(2034) = nodes.NewMethod.InsertionHelpers.NewNodeInserter_Method_filename
    _newNodeInserters(2124) = nodes.NewMethod.InsertionHelpers.NewNodeInserter_Method_fullName
    _newNodeInserters(2214) = nodes.NewMethod.InsertionHelpers.NewNodeInserter_Method_genericSignature
    _newNodeInserters(2304) = nodes.NewMethod.InsertionHelpers.NewNodeInserter_Method_hash
    _newNodeInserters(2934) = nodes.NewMethod.InsertionHelpers.NewNodeInserter_Method_isExternal
    _newNodeInserters(4014) = nodes.NewMethod.InsertionHelpers.NewNodeInserter_Method_lineNumber
    _newNodeInserters(4104) = nodes.NewMethod.InsertionHelpers.NewNodeInserter_Method_lineNumberEnd
    _newNodeInserters(4554) = nodes.NewMethod.InsertionHelpers.NewNodeInserter_Method_name
    _newNodeInserters(4824) = nodes.NewMethod.InsertionHelpers.NewNodeInserter_Method_offset
    _newNodeInserters(4914) = nodes.NewMethod.InsertionHelpers.NewNodeInserter_Method_offsetEnd
    _newNodeInserters(5184) = nodes.NewMethod.InsertionHelpers.NewNodeInserter_Method_order
    _newNodeInserters(6714) = nodes.NewMethod.InsertionHelpers.NewNodeInserter_Method_signature
    _newNodeInserters(866) =
      nodes.NewMethodParameterIn.InsertionHelpers.NewNodeInserter_MethodParameterIn_closureBindingId
    _newNodeInserters(1046) = nodes.NewMethodParameterIn.InsertionHelpers.NewNodeInserter_MethodParameterIn_code
    _newNodeInserters(1136) = nodes.NewMethodParameterIn.InsertionHelpers.NewNodeInserter_MethodParameterIn_columnNumber
    _newNodeInserters(1766) =
      nodes.NewMethodParameterIn.InsertionHelpers.NewNodeInserter_MethodParameterIn_dynamicTypeHintFullName
    _newNodeInserters(1856) =
      nodes.NewMethodParameterIn.InsertionHelpers.NewNodeInserter_MethodParameterIn_evaluationStrategy
    _newNodeInserters(2576) = nodes.NewMethodParameterIn.InsertionHelpers.NewNodeInserter_MethodParameterIn_index
    _newNodeInserters(3026) = nodes.NewMethodParameterIn.InsertionHelpers.NewNodeInserter_MethodParameterIn_isVariadic
    _newNodeInserters(4016) = nodes.NewMethodParameterIn.InsertionHelpers.NewNodeInserter_MethodParameterIn_lineNumber
    _newNodeInserters(4556) = nodes.NewMethodParameterIn.InsertionHelpers.NewNodeInserter_MethodParameterIn_name
    _newNodeInserters(5186) = nodes.NewMethodParameterIn.InsertionHelpers.NewNodeInserter_MethodParameterIn_order
    _newNodeInserters(5726) =
      nodes.NewMethodParameterIn.InsertionHelpers.NewNodeInserter_MethodParameterIn_possibleTypes
    _newNodeInserters(7346) = nodes.NewMethodParameterIn.InsertionHelpers.NewNodeInserter_MethodParameterIn_typeFullName
    _newNodeInserters(1048) = nodes.NewMethodParameterOut.InsertionHelpers.NewNodeInserter_MethodParameterOut_code
    _newNodeInserters(1138) =
      nodes.NewMethodParameterOut.InsertionHelpers.NewNodeInserter_MethodParameterOut_columnNumber
    _newNodeInserters(1858) =
      nodes.NewMethodParameterOut.InsertionHelpers.NewNodeInserter_MethodParameterOut_evaluationStrategy
    _newNodeInserters(2578) = nodes.NewMethodParameterOut.InsertionHelpers.NewNodeInserter_MethodParameterOut_index
    _newNodeInserters(3028) = nodes.NewMethodParameterOut.InsertionHelpers.NewNodeInserter_MethodParameterOut_isVariadic
    _newNodeInserters(4018) = nodes.NewMethodParameterOut.InsertionHelpers.NewNodeInserter_MethodParameterOut_lineNumber
    _newNodeInserters(4558) = nodes.NewMethodParameterOut.InsertionHelpers.NewNodeInserter_MethodParameterOut_name
    _newNodeInserters(5188) = nodes.NewMethodParameterOut.InsertionHelpers.NewNodeInserter_MethodParameterOut_order
    _newNodeInserters(7348) =
      nodes.NewMethodParameterOut.InsertionHelpers.NewNodeInserter_MethodParameterOut_typeFullName
    _newNodeInserters(240) = nodes.NewMethodRef.InsertionHelpers.NewNodeInserter_MethodRef_argumentIndex
    _newNodeInserters(330) = nodes.NewMethodRef.InsertionHelpers.NewNodeInserter_MethodRef_argumentName
    _newNodeInserters(1050) = nodes.NewMethodRef.InsertionHelpers.NewNodeInserter_MethodRef_code
    _newNodeInserters(1140) = nodes.NewMethodRef.InsertionHelpers.NewNodeInserter_MethodRef_columnNumber
    _newNodeInserters(1770) = nodes.NewMethodRef.InsertionHelpers.NewNodeInserter_MethodRef_dynamicTypeHintFullName
    _newNodeInserters(4020) = nodes.NewMethodRef.InsertionHelpers.NewNodeInserter_MethodRef_lineNumber
    _newNodeInserters(4200) = nodes.NewMethodRef.InsertionHelpers.NewNodeInserter_MethodRef_methodFullName
    _newNodeInserters(5190) = nodes.NewMethodRef.InsertionHelpers.NewNodeInserter_MethodRef_order
    _newNodeInserters(5730) = nodes.NewMethodRef.InsertionHelpers.NewNodeInserter_MethodRef_possibleTypes
    _newNodeInserters(7350) = nodes.NewMethodRef.InsertionHelpers.NewNodeInserter_MethodRef_typeFullName
    _newNodeInserters(1052) = nodes.NewMethodReturn.InsertionHelpers.NewNodeInserter_MethodReturn_code
    _newNodeInserters(1142) = nodes.NewMethodReturn.InsertionHelpers.NewNodeInserter_MethodReturn_columnNumber
    _newNodeInserters(1772) =
      nodes.NewMethodReturn.InsertionHelpers.NewNodeInserter_MethodReturn_dynamicTypeHintFullName
    _newNodeInserters(1862) = nodes.NewMethodReturn.InsertionHelpers.NewNodeInserter_MethodReturn_evaluationStrategy
    _newNodeInserters(4022) = nodes.NewMethodReturn.InsertionHelpers.NewNodeInserter_MethodReturn_lineNumber
    _newNodeInserters(5192) = nodes.NewMethodReturn.InsertionHelpers.NewNodeInserter_MethodReturn_order
    _newNodeInserters(5732) = nodes.NewMethodReturn.InsertionHelpers.NewNodeInserter_MethodReturn_possibleTypes
    _newNodeInserters(7352) = nodes.NewMethodReturn.InsertionHelpers.NewNodeInserter_MethodReturn_typeFullName
    _newNodeInserters(1054) = nodes.NewModifier.InsertionHelpers.NewNodeInserter_Modifier_code
    _newNodeInserters(1144) = nodes.NewModifier.InsertionHelpers.NewNodeInserter_Modifier_columnNumber
    _newNodeInserters(4024) = nodes.NewModifier.InsertionHelpers.NewNodeInserter_Modifier_lineNumber
    _newNodeInserters(4474) = nodes.NewModifier.InsertionHelpers.NewNodeInserter_Modifier_modifierType
    _newNodeInserters(5194) = nodes.NewModifier.InsertionHelpers.NewNodeInserter_Modifier_order
    _newNodeInserters(1056) = nodes.NewNamespace.InsertionHelpers.NewNodeInserter_Namespace_code
    _newNodeInserters(1146) = nodes.NewNamespace.InsertionHelpers.NewNodeInserter_Namespace_columnNumber
    _newNodeInserters(4026) = nodes.NewNamespace.InsertionHelpers.NewNodeInserter_Namespace_lineNumber
    _newNodeInserters(4566) = nodes.NewNamespace.InsertionHelpers.NewNodeInserter_Namespace_name
    _newNodeInserters(5196) = nodes.NewNamespace.InsertionHelpers.NewNodeInserter_Namespace_order
    _newNodeInserters(1058) = nodes.NewNamespaceBlock.InsertionHelpers.NewNodeInserter_NamespaceBlock_code
    _newNodeInserters(1148) = nodes.NewNamespaceBlock.InsertionHelpers.NewNodeInserter_NamespaceBlock_columnNumber
    _newNodeInserters(2048) = nodes.NewNamespaceBlock.InsertionHelpers.NewNodeInserter_NamespaceBlock_filename
    _newNodeInserters(2138) = nodes.NewNamespaceBlock.InsertionHelpers.NewNodeInserter_NamespaceBlock_fullName
    _newNodeInserters(4028) = nodes.NewNamespaceBlock.InsertionHelpers.NewNodeInserter_NamespaceBlock_lineNumber
    _newNodeInserters(4568) = nodes.NewNamespaceBlock.InsertionHelpers.NewNodeInserter_NamespaceBlock_name
    _newNodeInserters(5198) = nodes.NewNamespaceBlock.InsertionHelpers.NewNodeInserter_NamespaceBlock_order
    _newNodeInserters(250) = nodes.NewReturn.InsertionHelpers.NewNodeInserter_Return_argumentIndex
    _newNodeInserters(340) = nodes.NewReturn.InsertionHelpers.NewNodeInserter_Return_argumentName
    _newNodeInserters(1060) = nodes.NewReturn.InsertionHelpers.NewNodeInserter_Return_code
    _newNodeInserters(1150) = nodes.NewReturn.InsertionHelpers.NewNodeInserter_Return_columnNumber
    _newNodeInserters(4030) = nodes.NewReturn.InsertionHelpers.NewNodeInserter_Return_lineNumber
    _newNodeInserters(5200) = nodes.NewReturn.InsertionHelpers.NewNodeInserter_Return_order
    _newNodeInserters(4572) = nodes.NewTag.InsertionHelpers.NewNodeInserter_Tag_name
    _newNodeInserters(7542) = nodes.NewTag.InsertionHelpers.NewNodeInserter_Tag_value
    _newNodeInserters(8174) = nodes.NewTagNodePair.InsertionHelpers.NewNodeInserter_TagNodePair_node
    _newNodeInserters(8264) = nodes.NewTagNodePair.InsertionHelpers.NewNodeInserter_TagNodePair_tag
    _newNodeInserters(256) = nodes.NewTemplateDom.InsertionHelpers.NewNodeInserter_TemplateDom_argumentIndex
    _newNodeInserters(346) = nodes.NewTemplateDom.InsertionHelpers.NewNodeInserter_TemplateDom_argumentName
    _newNodeInserters(1066) = nodes.NewTemplateDom.InsertionHelpers.NewNodeInserter_TemplateDom_code
    _newNodeInserters(1156) = nodes.NewTemplateDom.InsertionHelpers.NewNodeInserter_TemplateDom_columnNumber
    _newNodeInserters(4036) = nodes.NewTemplateDom.InsertionHelpers.NewNodeInserter_TemplateDom_lineNumber
    _newNodeInserters(4576) = nodes.NewTemplateDom.InsertionHelpers.NewNodeInserter_TemplateDom_name
    _newNodeInserters(5206) = nodes.NewTemplateDom.InsertionHelpers.NewNodeInserter_TemplateDom_order
    _newNodeInserters(2148) = nodes.NewType.InsertionHelpers.NewNodeInserter_Type_fullName
    _newNodeInserters(4578) = nodes.NewType.InsertionHelpers.NewNodeInserter_Type_name
    _newNodeInserters(7278) = nodes.NewType.InsertionHelpers.NewNodeInserter_Type_typeDeclFullName
    _newNodeInserters(1070) = nodes.NewTypeArgument.InsertionHelpers.NewNodeInserter_TypeArgument_code
    _newNodeInserters(1160) = nodes.NewTypeArgument.InsertionHelpers.NewNodeInserter_TypeArgument_columnNumber
    _newNodeInserters(4040) = nodes.NewTypeArgument.InsertionHelpers.NewNodeInserter_TypeArgument_lineNumber
    _newNodeInserters(5210) = nodes.NewTypeArgument.InsertionHelpers.NewNodeInserter_TypeArgument_order
    _newNodeInserters(172) = nodes.NewTypeDecl.InsertionHelpers.NewNodeInserter_TypeDecl_aliasTypeFullName
    _newNodeInserters(442) = nodes.NewTypeDecl.InsertionHelpers.NewNodeInserter_TypeDecl_astParentFullName
    _newNodeInserters(532) = nodes.NewTypeDecl.InsertionHelpers.NewNodeInserter_TypeDecl_astParentType
    _newNodeInserters(1072) = nodes.NewTypeDecl.InsertionHelpers.NewNodeInserter_TypeDecl_code
    _newNodeInserters(1162) = nodes.NewTypeDecl.InsertionHelpers.NewNodeInserter_TypeDecl_columnNumber
    _newNodeInserters(2062) = nodes.NewTypeDecl.InsertionHelpers.NewNodeInserter_TypeDecl_filename
    _newNodeInserters(2152) = nodes.NewTypeDecl.InsertionHelpers.NewNodeInserter_TypeDecl_fullName
    _newNodeInserters(2242) = nodes.NewTypeDecl.InsertionHelpers.NewNodeInserter_TypeDecl_genericSignature
    _newNodeInserters(2692) = nodes.NewTypeDecl.InsertionHelpers.NewNodeInserter_TypeDecl_inheritsFromTypeFullName
    _newNodeInserters(2962) = nodes.NewTypeDecl.InsertionHelpers.NewNodeInserter_TypeDecl_isExternal
    _newNodeInserters(4042) = nodes.NewTypeDecl.InsertionHelpers.NewNodeInserter_TypeDecl_lineNumber
    _newNodeInserters(4582) = nodes.NewTypeDecl.InsertionHelpers.NewNodeInserter_TypeDecl_name
    _newNodeInserters(4852) = nodes.NewTypeDecl.InsertionHelpers.NewNodeInserter_TypeDecl_offset
    _newNodeInserters(4942) = nodes.NewTypeDecl.InsertionHelpers.NewNodeInserter_TypeDecl_offsetEnd
    _newNodeInserters(5212) = nodes.NewTypeDecl.InsertionHelpers.NewNodeInserter_TypeDecl_order
    _newNodeInserters(1074) = nodes.NewTypeParameter.InsertionHelpers.NewNodeInserter_TypeParameter_code
    _newNodeInserters(1164) = nodes.NewTypeParameter.InsertionHelpers.NewNodeInserter_TypeParameter_columnNumber
    _newNodeInserters(4044) = nodes.NewTypeParameter.InsertionHelpers.NewNodeInserter_TypeParameter_lineNumber
    _newNodeInserters(4584) = nodes.NewTypeParameter.InsertionHelpers.NewNodeInserter_TypeParameter_name
    _newNodeInserters(5214) = nodes.NewTypeParameter.InsertionHelpers.NewNodeInserter_TypeParameter_order
    _newNodeInserters(266) = nodes.NewTypeRef.InsertionHelpers.NewNodeInserter_TypeRef_argumentIndex
    _newNodeInserters(356) = nodes.NewTypeRef.InsertionHelpers.NewNodeInserter_TypeRef_argumentName
    _newNodeInserters(1076) = nodes.NewTypeRef.InsertionHelpers.NewNodeInserter_TypeRef_code
    _newNodeInserters(1166) = nodes.NewTypeRef.InsertionHelpers.NewNodeInserter_TypeRef_columnNumber
    _newNodeInserters(1796) = nodes.NewTypeRef.InsertionHelpers.NewNodeInserter_TypeRef_dynamicTypeHintFullName
    _newNodeInserters(4046) = nodes.NewTypeRef.InsertionHelpers.NewNodeInserter_TypeRef_lineNumber
    _newNodeInserters(5216) = nodes.NewTypeRef.InsertionHelpers.NewNodeInserter_TypeRef_order
    _newNodeInserters(5756) = nodes.NewTypeRef.InsertionHelpers.NewNodeInserter_TypeRef_possibleTypes
    _newNodeInserters(7376) = nodes.NewTypeRef.InsertionHelpers.NewNodeInserter_TypeRef_typeFullName
    _newNodeInserters(268) = nodes.NewUnknown.InsertionHelpers.NewNodeInserter_Unknown_argumentIndex
    _newNodeInserters(358) = nodes.NewUnknown.InsertionHelpers.NewNodeInserter_Unknown_argumentName
    _newNodeInserters(1078) = nodes.NewUnknown.InsertionHelpers.NewNodeInserter_Unknown_code
    _newNodeInserters(1168) = nodes.NewUnknown.InsertionHelpers.NewNodeInserter_Unknown_columnNumber
    _newNodeInserters(1348) = nodes.NewUnknown.InsertionHelpers.NewNodeInserter_Unknown_containedRef
    _newNodeInserters(1798) = nodes.NewUnknown.InsertionHelpers.NewNodeInserter_Unknown_dynamicTypeHintFullName
    _newNodeInserters(4048) = nodes.NewUnknown.InsertionHelpers.NewNodeInserter_Unknown_lineNumber
    _newNodeInserters(5218) = nodes.NewUnknown.InsertionHelpers.NewNodeInserter_Unknown_order
    _newNodeInserters(5668) = nodes.NewUnknown.InsertionHelpers.NewNodeInserter_Unknown_parserTypeName
    _newNodeInserters(5758) = nodes.NewUnknown.InsertionHelpers.NewNodeInserter_Unknown_possibleTypes
    _newNodeInserters(7378) = nodes.NewUnknown.InsertionHelpers.NewNodeInserter_Unknown_typeFullName
    _newNodeInserters
  }
  override def getNumberOfNodeKinds: Int              = 45
  override def getNumberOfEdgeKinds: Int              = 30
  override def getNodeLabel(nodeKind: Int): String    = nodeLabels(nodeKind)
  override def getNodeKindByLabel(label: String): Int = nodeKindByLabel.getOrElse(label, flatgraph.Schema.UndefinedKind)
  override def getEdgeLabel(nodeKind: Int, edgeKind: Int): String = edgeLabels(edgeKind)
  override def getEdgeKindByLabel(label: String): Int = edgeKindByLabel.getOrElse(label, flatgraph.Schema.UndefinedKind)
  override def getNodePropertyNames(nodeLabel: String): Set[String] = {
    nodeLabel match {
      case "ANNOTATION" =>
        Set("ARGUMENT_INDEX", "ARGUMENT_NAME", "CODE", "COLUMN_NUMBER", "FULL_NAME", "LINE_NUMBER", "NAME", "ORDER")
      case "ANNOTATION_LITERAL" =>
        Set("ARGUMENT_INDEX", "ARGUMENT_NAME", "CODE", "COLUMN_NUMBER", "LINE_NUMBER", "NAME", "ORDER")
      case "ANNOTATION_PARAMETER"        => Set("CODE", "COLUMN_NUMBER", "LINE_NUMBER", "ORDER")
      case "ANNOTATION_PARAMETER_ASSIGN" => Set("CODE", "COLUMN_NUMBER", "LINE_NUMBER", "ORDER")
      case "ARRAY_INITIALIZER" =>
        Set("ARGUMENT_INDEX", "ARGUMENT_NAME", "CODE", "COLUMN_NUMBER", "LINE_NUMBER", "ORDER")
      case "BINDING" => Set("METHOD_FULL_NAME", "NAME", "SIGNATURE")
      case "BLOCK" =>
        Set(
          "ARGUMENT_INDEX",
          "ARGUMENT_NAME",
          "CODE",
          "COLUMN_NUMBER",
          "DYNAMIC_TYPE_HINT_FULL_NAME",
          "LINE_NUMBER",
          "ORDER",
          "POSSIBLE_TYPES",
          "TYPE_FULL_NAME"
        )
      case "CALL" =>
        Set(
          "ARGUMENT_INDEX",
          "ARGUMENT_NAME",
          "CODE",
          "COLUMN_NUMBER",
          "DISPATCH_TYPE",
          "DYNAMIC_TYPE_HINT_FULL_NAME",
          "LINE_NUMBER",
          "METHOD_FULL_NAME",
          "NAME",
          "ORDER",
          "POSSIBLE_TYPES",
          "SIGNATURE",
          "TYPE_FULL_NAME"
        )
      case "CLOSURE_BINDING" => Set("CLOSURE_BINDING_ID", "CLOSURE_ORIGINAL_NAME", "EVALUATION_STRATEGY")
      case "COMMENT"         => Set("CODE", "COLUMN_NUMBER", "FILENAME", "LINE_NUMBER", "ORDER")
      case "CONFIG_FILE"     => Set("CONTENT", "NAME")
      case "CONTROL_STRUCTURE" =>
        Set(
          "ARGUMENT_INDEX",
          "ARGUMENT_NAME",
          "CODE",
          "COLUMN_NUMBER",
          "CONTROL_STRUCTURE_TYPE",
          "LINE_NUMBER",
          "ORDER",
          "PARSER_TYPE_NAME"
        )
      case "CRYPTO_OPERATION" =>
        Set(
          "ALGORITHM",
          "IS_DEPRECATED",
          "ITERATIONS",
          "IV_SOURCE_TYPE",
          "KEY_LENGTH",
          "KEY_SIZE",
          "KEY_SOURCE",
          "KEY_SOURCE_TYPE",
          "LINE_NO",
          "MODE",
          "NONCE_SOURCE_TYPE",
          "OP_TYPE",
          "OPERATION",
          "OUTPUT_LENGTH",
          "PADDING",
          "PROTOCOL_VERSION",
          "PROVIDES_INTEGRITY",
          "REQUIRES_CSPRNG_SOURCE",
          "REQUIRES_UNIQUE_NONCE",
          "RESOLUTION",
          "SALT_SOURCE_TYPE",
          "SALT_USED",
          "SEMANTIC_DETAIL",
          "SEMANTIC_KIND",
          "SOURCE",
          "SRC_FILE",
          "TAG_LENGTH",
          "TRANSPORT_SCHEME",
          "USED_FOR_CRYPTO",
          "VERIFICATION_ENABLED",
          "VERIFICATION_KIND",
          "VERIFIED"
        )
      case "DEPENDENCY" => Set("DEPENDENCY_GROUP_ID", "NAME", "VERSION")
      case "FIELD_IDENTIFIER" =>
        Set("ARGUMENT_INDEX", "ARGUMENT_NAME", "CANONICAL_NAME", "CODE", "COLUMN_NUMBER", "LINE_NUMBER", "ORDER")
      case "FILE"    => Set("CODE", "COLUMN_NUMBER", "CONTENT", "HASH", "LINE_NUMBER", "NAME", "ORDER")
      case "FINDING" => Set()
      case "IDENTIFIER" =>
        Set(
          "ARGUMENT_INDEX",
          "ARGUMENT_NAME",
          "CODE",
          "COLUMN_NUMBER",
          "DYNAMIC_TYPE_HINT_FULL_NAME",
          "LINE_NUMBER",
          "NAME",
          "ORDER",
          "POSSIBLE_TYPES",
          "TYPE_FULL_NAME"
        )
      case "IMPORT" =>
        Set(
          "CODE",
          "COLUMN_NUMBER",
          "EXPLICIT_AS",
          "IMPORTED_AS",
          "IMPORTED_ENTITY",
          "IS_EXPLICIT",
          "IS_WILDCARD",
          "LINE_NUMBER",
          "ORDER"
        )
      case "JUMP_LABEL" => Set("CODE", "COLUMN_NUMBER", "LINE_NUMBER", "NAME", "ORDER", "PARSER_TYPE_NAME")
      case "JUMP_TARGET" =>
        Set("ARGUMENT_INDEX", "CODE", "COLUMN_NUMBER", "LINE_NUMBER", "NAME", "ORDER", "PARSER_TYPE_NAME")
      case "KEY_VALUE_PAIR" => Set("KEY", "VALUE")
      case "LITERAL" =>
        Set(
          "ARGUMENT_INDEX",
          "ARGUMENT_NAME",
          "CODE",
          "COLUMN_NUMBER",
          "DYNAMIC_TYPE_HINT_FULL_NAME",
          "LINE_NUMBER",
          "ORDER",
          "POSSIBLE_TYPES",
          "TYPE_FULL_NAME"
        )
      case "LOCAL" =>
        Set(
          "CLOSURE_BINDING_ID",
          "CODE",
          "COLUMN_NUMBER",
          "DYNAMIC_TYPE_HINT_FULL_NAME",
          "GENERIC_SIGNATURE",
          "LINE_NUMBER",
          "NAME",
          "ORDER",
          "POSSIBLE_TYPES",
          "TYPE_FULL_NAME"
        )
      case "LOCATION" =>
        Set(
          "CLASS_NAME",
          "CLASS_SHORT_NAME",
          "FILENAME",
          "LINE_NUMBER",
          "METHOD_FULL_NAME",
          "METHOD_SHORT_NAME",
          "NODE_LABEL",
          "PACKAGE_NAME",
          "SYMBOL"
        )
      case "MEMBER" =>
        Set(
          "AST_PARENT_FULL_NAME",
          "AST_PARENT_TYPE",
          "CODE",
          "COLUMN_NUMBER",
          "DYNAMIC_TYPE_HINT_FULL_NAME",
          "GENERIC_SIGNATURE",
          "LINE_NUMBER",
          "NAME",
          "ORDER",
          "POSSIBLE_TYPES",
          "TYPE_FULL_NAME"
        )
      case "META_DATA" => Set("HASH", "LANGUAGE", "OVERLAYS", "ROOT", "VERSION")
      case "METHOD" =>
        Set(
          "AST_PARENT_FULL_NAME",
          "AST_PARENT_TYPE",
          "CODE",
          "COLUMN_NUMBER",
          "COLUMN_NUMBER_END",
          "FILENAME",
          "FULL_NAME",
          "GENERIC_SIGNATURE",
          "HASH",
          "IS_EXTERNAL",
          "LINE_NUMBER",
          "LINE_NUMBER_END",
          "NAME",
          "OFFSET",
          "OFFSET_END",
          "ORDER",
          "SIGNATURE"
        )
      case "METHOD_PARAMETER_IN" =>
        Set(
          "CLOSURE_BINDING_ID",
          "CODE",
          "COLUMN_NUMBER",
          "DYNAMIC_TYPE_HINT_FULL_NAME",
          "EVALUATION_STRATEGY",
          "INDEX",
          "IS_VARIADIC",
          "LINE_NUMBER",
          "NAME",
          "ORDER",
          "POSSIBLE_TYPES",
          "TYPE_FULL_NAME"
        )
      case "METHOD_PARAMETER_OUT" =>
        Set(
          "CODE",
          "COLUMN_NUMBER",
          "EVALUATION_STRATEGY",
          "INDEX",
          "IS_VARIADIC",
          "LINE_NUMBER",
          "NAME",
          "ORDER",
          "TYPE_FULL_NAME"
        )
      case "METHOD_REF" =>
        Set(
          "ARGUMENT_INDEX",
          "ARGUMENT_NAME",
          "CODE",
          "COLUMN_NUMBER",
          "DYNAMIC_TYPE_HINT_FULL_NAME",
          "LINE_NUMBER",
          "METHOD_FULL_NAME",
          "ORDER",
          "POSSIBLE_TYPES",
          "TYPE_FULL_NAME"
        )
      case "METHOD_RETURN" =>
        Set(
          "CODE",
          "COLUMN_NUMBER",
          "DYNAMIC_TYPE_HINT_FULL_NAME",
          "EVALUATION_STRATEGY",
          "LINE_NUMBER",
          "ORDER",
          "POSSIBLE_TYPES",
          "TYPE_FULL_NAME"
        )
      case "MODIFIER"        => Set("CODE", "COLUMN_NUMBER", "LINE_NUMBER", "MODIFIER_TYPE", "ORDER")
      case "NAMESPACE"       => Set("CODE", "COLUMN_NUMBER", "LINE_NUMBER", "NAME", "ORDER")
      case "NAMESPACE_BLOCK" => Set("CODE", "COLUMN_NUMBER", "FILENAME", "FULL_NAME", "LINE_NUMBER", "NAME", "ORDER")
      case "RETURN"          => Set("ARGUMENT_INDEX", "ARGUMENT_NAME", "CODE", "COLUMN_NUMBER", "LINE_NUMBER", "ORDER")
      case "TAG"             => Set("NAME", "VALUE")
      case "TAG_NODE_PAIR"   => Set()
      case "TEMPLATE_DOM" =>
        Set("ARGUMENT_INDEX", "ARGUMENT_NAME", "CODE", "COLUMN_NUMBER", "LINE_NUMBER", "NAME", "ORDER")
      case "TYPE"          => Set("FULL_NAME", "NAME", "TYPE_DECL_FULL_NAME")
      case "TYPE_ARGUMENT" => Set("CODE", "COLUMN_NUMBER", "LINE_NUMBER", "ORDER")
      case "TYPE_DECL" =>
        Set(
          "ALIAS_TYPE_FULL_NAME",
          "AST_PARENT_FULL_NAME",
          "AST_PARENT_TYPE",
          "CODE",
          "COLUMN_NUMBER",
          "FILENAME",
          "FULL_NAME",
          "GENERIC_SIGNATURE",
          "INHERITS_FROM_TYPE_FULL_NAME",
          "IS_EXTERNAL",
          "LINE_NUMBER",
          "NAME",
          "OFFSET",
          "OFFSET_END",
          "ORDER"
        )
      case "TYPE_PARAMETER" => Set("CODE", "COLUMN_NUMBER", "LINE_NUMBER", "NAME", "ORDER")
      case "TYPE_REF" =>
        Set(
          "ARGUMENT_INDEX",
          "ARGUMENT_NAME",
          "CODE",
          "COLUMN_NUMBER",
          "DYNAMIC_TYPE_HINT_FULL_NAME",
          "LINE_NUMBER",
          "ORDER",
          "POSSIBLE_TYPES",
          "TYPE_FULL_NAME"
        )
      case "UNKNOWN" =>
        Set(
          "ARGUMENT_INDEX",
          "ARGUMENT_NAME",
          "CODE",
          "COLUMN_NUMBER",
          "CONTAINED_REF",
          "DYNAMIC_TYPE_HINT_FULL_NAME",
          "LINE_NUMBER",
          "ORDER",
          "PARSER_TYPE_NAME",
          "POSSIBLE_TYPES",
          "TYPE_FULL_NAME"
        )
      case _ => Set.empty
    }
  }
  override def getEdgePropertyName(label: String): Option[String] = {
    label match {
      case "REACHING_DEF" => Some("VARIABLE")
      case _              => None
    }
  }

  override def getPropertyLabel(nodeKind: Int, propertyKind: Int): String = {
    if (propertyKind < 88) normalNodePropertyNames(propertyKind)
    else if (propertyKind == 88 && nodeKind == 16) "evidence"      /*on node FINDING*/
    else if (propertyKind == 89 && nodeKind == 16) "keyValuePairs" /*on node FINDING*/
    else if (propertyKind == 90 && nodeKind == 24) "node"          /*on node LOCATION*/
    else if (propertyKind == 90 && nodeKind == 37) "node"          /*on node TAG_NODE_PAIR*/
    else if (propertyKind == 91 && nodeKind == 37) "tag"           /*on node TAG_NODE_PAIR*/
    else null
  }

  override def getPropertyKindByName(label: String): Int =
    nodePropertyByLabel.getOrElse(label, flatgraph.Schema.UndefinedKind)
  override def getNumberOfPropertyKinds: Int = 92
  override def makeNode(graph: flatgraph.Graph, nodeKind: Short, seq: Int): nodes.StoredNode =
    nodeFactories(nodeKind)(graph, seq)
  override def makeEdge(
    src: flatgraph.GNode,
    dst: flatgraph.GNode,
    edgeKind: Short,
    subSeq: Int,
    property: Any
  ): flatgraph.Edge = edgeFactories(edgeKind)(src, dst, subSeq, property)
  override def allocateEdgeProperty(
    nodeKind: Int,
    direction: flatgraph.Edge.Direction,
    edgeKind: Int,
    size: Int
  ): Array[?] = edgePropertyAllocators(edgeKind)(size)
  override def getNodePropertyFormalType(nodeKind: Int, propertyKind: Int): FormalQtyType.FormalType =
    nodePropertyDescriptors(propertyOffsetArrayIndex(nodeKind, propertyKind)).asInstanceOf[FormalQtyType.FormalType]
  override def getNodePropertyFormalQuantity(nodeKind: Int, propertyKind: Int): FormalQtyType.FormalQuantity =
    nodePropertyDescriptors(1 + propertyOffsetArrayIndex(nodeKind, propertyKind))
      .asInstanceOf[FormalQtyType.FormalQuantity]

  override def getNewNodePropertyInserter(nodeKind: Int, propertyKind: Int): flatgraph.NewNodePropertyInsertionHelper =
    newNodeInsertionHelpers(propertyOffsetArrayIndex(nodeKind, propertyKind))
}
