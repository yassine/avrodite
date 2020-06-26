<#macro list_decoder context member type reference>
<#-- @ftlvariable name="context" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="type" type="org.avrodite.meta.type.TypeInfo" -->
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
  <#local size = "size_${context.random(5)}"/>
  <#local list_ref = "list_${context.random(5)}"/>
  <#local current_ref = "${list_ref}_current"/>
  val ${size} = input.readInt()

  ${reference} = MutableList<${type.composite.signature()}>(${size}) { _ ->
  <#if !type.composite.native >
    var ${current_ref}: ${type.composite.signature()}? = null
    <#if type.composite.category != "TYPE">
      <@decoder context=context member=member type=type.composite reference="${current_ref}"/>
    <#else>
      <#if type.composite.type.markedNullable >
      if (input.readByte().equals(0.toByte())) {
        ${current_ref} = <@codec_fq_name context=context type=type.composite/>.decode(input)
      }
      <#else>
        ${current_ref} = <@codec_fq_name context=context type=type.composite/>.decode(input)
      </#if>
    </#if>
    <#if !type.composite.type.markedNullable >
    ${current_ref}!!
    <#else>
    ${current_ref}
    </#if>
  <#else>
    <@native_decoder member=member context=context type=type.composite/>
  </#if>
  }
  if (${size} > 0 && input.readByte() != 0.toByte()) {
    throw AvroditeAvroException.illegalArrayEnd(${size}, "${member.memberMeta.name}", "${member.memberMeta.typeInfo.signature()}")
  }
</#macro>
