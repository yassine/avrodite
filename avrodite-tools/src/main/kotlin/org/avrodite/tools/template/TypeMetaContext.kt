package org.avrodite.tools.template

import org.avrodite.meta.type.*
import org.avrodite.tools.template.Utils.hashSha
import org.avrodite.tools.template.Utils.packageNameOf

import java.lang.String.format
import kotlin.random.Random.Default.nextBytes
import kotlin.reflect.KClass

class TypeMetaContext(val typeMeta: TypeMeta) {

  val type: TypeInfoContext = TypeInfoContext(typeMeta.typeInfo)

  val params: List<ParamMetaContext> = typeMeta.constructorParams
    .map { ParamMetaContext(this, it) }

  val props: List<PropMetaContext> = typeMeta.props
    .map { PropMetaContext(this, it) }

  fun random(len: Int): String = nextBytes(len).joinToString("") { format("%02x", it) }.substring(0, len)

  fun sha(s: String): String = hashSha(s)

  fun sha(s: String, len: Int): String = hashSha(s, len)

}

open class MemberMetaContext(val typeMeta: TypeMetaContext, val memberMeta: MemberMeta) {

  val memberMetaContext: TypeInfoContext = TypeInfoContext(memberMeta.typeInfo)

  fun targetType():TypeInfo
    = extractTargetType(memberMeta.typeInfo)

  fun isValueType(): Boolean
    = targetType().category == TypeCategory.VALUE

  private fun extractTargetType(typeInfo: TypeInfo) : TypeInfo
    = typeInfo.composite?.let { extractTargetType(it) } ?: typeInfo

}

class PropMetaContext(typeMeta: TypeMetaContext, propMeta: PropMeta)
  : MemberMetaContext(typeMeta, propMeta)

class ParamMetaContext(typeMeta: TypeMetaContext, paramMeta: ParamMeta)
  : MemberMetaContext(typeMeta, paramMeta)

class TypeInfoContext(val info: TypeInfo) {

  fun simpleName(): String = (info.type.classifier as KClass<*>).simpleName!!
  fun fqName(): String = (info.type.classifier as KClass<*>).qualifiedName!!
  fun namespace(): String = packageNameOf(fqName())
  fun isSimple(): Boolean = info.composite?.let { false } ?: true

}
