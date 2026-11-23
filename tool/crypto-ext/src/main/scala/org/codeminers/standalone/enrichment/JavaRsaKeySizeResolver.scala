package org.codeminers.standalone.enrichment

import io.shiftleft.codepropertygraph.generated.nodes.{Literal, StoredNode}
import io.shiftleft.codepropertygraph.generated.language.*
import java.security.KeyFactory
import java.security.interfaces.RSAKey
import java.security.spec.{PKCS8EncodedKeySpec, X509EncodedKeySpec}
import java.util.Base64
import scala.collection.mutable
import scala.util.Try

/** Reads the modulus of actual DER keys, never estimates a size from the length of Base64 text.
  * Only literals already resolved as key material are inspected. A weak reachable default is relevant even when
  * callers may replace it. Malformed encodings and non-RSA keys stay unknown.
  */
private[enrichment] final class JavaRsaKeySizeResolver {
  private lazy val factory = KeyFactory.getInstance("RSA")
  private val cache = mutable.HashMap.empty[String, Option[Int]]

  def resolve(sources: Iterable[StoredNode]): Option[Int] = {
    val sizes = sources.collect { case literal: Literal => literal.code.trim }
      .flatMap(code => cache.getOrElseUpdate(code, parse(code))).toVector
    sizes.minOption
  }

  private def parse(code: String): Option[Int] = {
    if (!code.startsWith("\"") || !code.endsWith("\"")) return None
    val text = code.substring(1, code.length - 1)
    if (text.length < 64 || text.length > 16384 || !text.matches("[A-Za-z0-9+/=]+")) return None
    Try(Base64.getDecoder.decode(text)).toOption.flatMap { bytes =>
      Try(factory.generatePublic(new X509EncodedKeySpec(bytes)).asInstanceOf[RSAKey].getModulus.bitLength).toOption
        .orElse(Try(factory.generatePrivate(new PKCS8EncodedKeySpec(bytes)).asInstanceOf[RSAKey].getModulus.bitLength).toOption)
    }
  }
}
