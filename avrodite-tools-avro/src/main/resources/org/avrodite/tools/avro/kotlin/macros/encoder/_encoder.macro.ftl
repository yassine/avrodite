<#macro encoder context member type reference>
<#-- @ftlvariable name="param" type="org.avrodite.tools.template.ParamMetaContext" -->
<#-- @ftlvariable name="context" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="type" type="org.avrodite.meta.type.TypeInfo" -->
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
  <#assign ref = "ref${context.random(15)}"/>

  <#if type.isNullable()>
    val ${ref} = ${reference}
    if(${ref} == null){
      output.writeByte(2)
    } else {
      output.writeByte(0)
      <@internalEncoder context=context member=member type=type reference="${ref}"/>
  <#else>
    <@internalEncoder context=context member=member type=type reference="${reference}"/>
  </#if>

  <#if type.isNullable()>
    }
  </#if>

</#macro>

<#macro internalEncoder context member type reference>
<#-- @ftlvariable name="param" type="org.avrodite.tools.template.ParamMetaContext" -->
<#-- @ftlvariable name="context" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="type" type="org.avrodite.meta.type.TypeInfo" -->
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
  <#switch type.category>
    <#case "STRING">
      output.writeString(${reference})
      <#break/>
    <#case "BYTE">
      output.writeByte(${reference})
      <#break/>
    <#case "INT">
      output.writeInt(${reference})
      <#break/>
    <#case "SHORT">
      output.writeInt(${reference}.toInt())
    <#break/>
    <#case "LONG">
      output.writeLong(${reference})
      <#break/>
    <#case "DOUBLE">
      output.writeDouble(${reference})
      <#break/>
    <#case "FLOAT">
      output.writeFloat(${reference})
      <#break/>
    <#case "VALUE">
      ${member.memberMeta.name}_codec.encode(output, ${reference})
      <#break/>
    <#case "ARRAY">
    <#case "LIST">
      <@indexable_encoder context=context member=member type=type reference="${reference}"/>
      <#break/>
    <#case "SET">
      <@iterable_encoder context=context member=member type=type reference="${reference}"/>
      <#break/>
    <#case "MAP">
      <@map_encoder context=context member=member type=type reference="${reference}"/>
      <#break/>
    <#case "TYPE">
      <@type_encoder context=context member=member type=type reference="${reference}"/>
      <#break/>
  </#switch>
</#macro>
