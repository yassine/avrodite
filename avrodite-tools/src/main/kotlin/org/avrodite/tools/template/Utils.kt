package org.avrodite.tools.template

import java.security.MessageDigest

object Utils {

  fun packageNameOf(fqClassName: String)
    = fqClassName.split(".")
        .let {
          it.filterIndexed { index, _ -> index != it.lastIndex }
        }.joinToString(".")

  fun hashSha(s: String)
    = MessageDigest.getInstance("SHA-256")
        .digest(s.toByteArray())
        .joinToString("") { String.format("%02x", it) }

  fun hashSha(s: String, len: Int): String
    = MessageDigest.getInstance("SHA-256")
        .digest(s.toByteArray())
        .joinToString("") { String.format("%02x", it) }
        .substring(0, len)

  fun codecFqName(info: TypeInfoContext): String
    = "${info.namespace()}.${info.simpleName()}__${hashSha(info.info.signature(), 8)}"

}

