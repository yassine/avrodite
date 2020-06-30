<#include "./macros/macros.ftl">
<#-- @ftlvariable name="meta" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
<#-- @ftlvariable name="schema" type="String" -->
package ${meta.type.namespace()}

import org.apache.avro.Schema
import org.avrodite.avro.*
import org.avrodite.avro.error.*
import kotlin.reflect.typeOf

class <@codec_name type=meta.type.info context=meta/> : AvroCodec<${meta.typeMeta.typeInfo.signature()}>{

  private val schema: Schema = Schema.parse(String(byteArrayOf(${schema}), Charsets.UTF_8))
  <@fn_dump_value_codecs meta = meta/>

  override fun schema(): Schema = schema

  override fun encode(m: ${meta.typeMeta.typeInfo.signature()}, output: AvroOutput) {
    <#list meta.params as member>
      <@fn_name_member_encoder member=member/>(m, output)
    </#list>
    <#list meta.props as member>
      <@fn_name_member_encoder member=member/>(m, output)
    </#list>
  }

  override fun decode(m: ${meta.typeMeta.typeInfo.signature()}, input: AvroInput): ${meta.typeMeta.typeInfo.signature()} {
    <#if meta.params?size != 0 >
      return decode(input)
    <#else>
      <#list meta.props as member>
        <@fn_name_member_decoder member=member/>(m, input)
      </#list>
      return m;
    </#if>
  }

  override fun decode(input: AvroInput): ${meta.typeMeta.typeInfo.signature()} {
    <#list meta.params as member>
      <#if !member.memberMeta.typeInfo.native >
        <#if member.memberMeta.typeInfo.category == "TYPE">
          <#if member.memberMeta.typeInfo.isNullable()>
            if (input.readByte().equals(0.toByte())) {
              val ${member.memberMeta.name} = <@codec_fq_name context=meta type=member.memberMeta.typeInfo/>.decode(input)
            } else {
              val ${member.memberMeta.name} = null
            }
          <#else >
            val ${member.memberMeta.name} = <@codec_fq_name context=meta type=member.memberMeta.typeInfo/>.decode(input)
          </#if>
        <#else>
          <@decoder member=member context=meta type=member.memberMeta.typeInfo reference="val ${member.memberMeta.name}"/>
        </#if>
      <#else>
        val ${member.memberMeta.name}: ${member.memberMeta.typeInfo.signature()} = <@native_decoder context=meta member=member type=member.memberMeta.typeInfo/>
      </#if>
    </#list>
    val model: ${meta.typeMeta.typeInfo.signature()} = ${meta.typeMeta.typeInfo.signature()}(<#list meta.params as member>${member.memberMeta.name}<#if member?index != meta.params?size - 1>, </#if></#list>)
    <#list meta.props as member>
      <@fn_name_member_decoder member=member/>(model, input)
    </#list>
    return model
  }

  override fun format() : AvroFormat = AvroFormat

  @ExperimentalStdlibApi
  override fun target() = typeOf<${meta.typeMeta.typeInfo.getType()}>()

  companion object {

    <@fn_dump_value_codecs meta = meta/>

    fun encode(model: ${meta.typeMeta.typeInfo.signature()}, output: AvroOutput) {
    <#list meta.params as member>
      <@fn_name_member_encoder member=member/>(model, output)
    </#list>
    <#list meta.props as member>
      <@fn_name_member_encoder member=member/>(model, output)
    </#list>
    }

    fun decode(input: AvroInput): ${meta.typeMeta.typeInfo.signature()} {
      <#list meta.params as member>
        <#if !member.memberMeta.typeInfo.native >
          <#if member.memberMeta.typeInfo.category == "TYPE">
            <#if member.memberMeta.typeInfo.isNullable()>
              if (input.readByte().equals(0.toByte())) {
              val ${member.memberMeta.name} = <@codec_fq_name context=meta type=member.memberMeta.typeInfo/>.decode(input)
              }else{
              val ${member.memberMeta.name} = null
              }
            <#else >
              val ${member.memberMeta.name} = <@codec_fq_name context=meta type=member.memberMeta.typeInfo/>.decode(input)
            </#if>
          <#else>
            <@decoder member=member context=meta type=member.memberMeta.typeInfo reference="val ${member.memberMeta.name}"/>
          </#if>
        <#else>
          val ${member.memberMeta.name}: ${member.memberMeta.typeInfo.signature()} = <@native_decoder context=meta member=member type=member.memberMeta.typeInfo/>
        </#if>
      </#list>
      val model: ${meta.typeMeta.typeInfo.signature()} = ${meta.typeMeta.typeInfo.signature()}(<#list meta.params as member>${member.memberMeta.name}<#if member?index != meta.params?size - 1>, </#if></#list>)
      <#list meta.props as member>
        <@fn_name_member_decoder member=member/>(model, input)
      </#list>
      return model
    }

    @Suppress("UNUSED_PARAMETER")
    fun decode(m: ${meta.typeMeta.typeInfo.signature()}, input: AvroInput): ${meta.typeMeta.typeInfo.signature()} {
    <#if meta.params?size != 0 >
      return decode(input)
    <#else>
      if(m != null){
        <#list meta.props as member>
          <@fn_name_member_decoder member=member/>(m, input)
        </#list>
        return m;
      }else{
        return decode(input)
      }
    </#if>
    }
  <#list meta.params as member>
    fun <@fn_name_member_encoder member=member/>(model: ${meta.typeMeta.typeInfo.signature()}, output: AvroOutput){
    <@encoder context=meta member=member type=member.memberMeta.typeInfo reference="model.${member.memberMeta.name}"/>
    }
  </#list>

  <#list meta.props as member>
    fun <@fn_name_member_encoder member=member/>(model: ${meta.typeMeta.typeInfo.signature()}, output: AvroOutput){
    <@encoder context=meta member=member type=member.memberMeta.typeInfo reference="model.${member.memberMeta.name}"/>
    }
  </#list>

  <#list meta.props as member>
    fun <@fn_name_member_decoder member=member/>(model: ${meta.typeMeta.typeInfo.signature()}, input: AvroInput) {
    <@decoder context=meta member=member type=member.memberMeta.typeInfo reference="model.${member.memberMeta.name}"/>
    }
  </#list>

  }


}
