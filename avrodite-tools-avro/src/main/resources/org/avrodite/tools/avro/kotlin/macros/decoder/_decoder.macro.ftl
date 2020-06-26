<#macro decoder context member type reference>
<#-- @ftlvariable name="context" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="type" type="org.avrodite.meta.type.TypeInfo" -->
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
  <#switch type.category>
    <#case "STRING">
      ${reference} = input.readString()
      <#break/>
    <#case "BYTE">
      ${reference} = input.readByte()
      <#break/>
    <#case "INT">
      ${reference} = input.readInt()
      <#break/>
    <#case "LONG">
      ${reference} = input.readLong()
      <#break/>
    <#case "DOUBLE">
      ${reference} = input.readDouble()
      <#break/>
    <#case "FLOAT">
      ${reference} = input.readFloat()
      <#break/>
    <#case "ARRAY">
      <@array_decoder context=context member=member type=type reference="${reference}"/>
      <#break/>
    <#case "LIST">
      <@list_decoder context=context member=member type=type reference="${reference}"/>
      <#break/>
    <#case "SET">
      <@set_decoder context=context member=member type=type reference="${reference}"/>
      <#break/>
    <#case "MAP">
      <@map_decoder context=context member=member type=type reference="${reference}"/>
      <#break/>
    <#case "TYPE">
      <@type_decoder context=context member=member type=type reference="${reference}"/>
      <#break/>
    <#case "VALUE">
      ${reference} = ${member.memberMeta.name}_codec.decode(input)
      <#break/>
  </#switch>
</#macro>

<#macro native_decoder context  member type>
  <#compress>
  <#switch type.category>
    <#case "STRING">
      input.readString()
      <#break/>
    <#case "BYTE">
      input.readByte()
      <#break/>
    <#case "INT">
      input.readInt()
      <#break/>
    <#case "LONG">
      input.readLong()
      <#break/>
    <#case "DOUBLE">
      input.readDouble()
      <#break/>
    <#case "FLOAT">
      input.readFloat()
    <#break/>
    <#case "VALUE">
      ${member.memberMeta.name}_codec.decode(input)
    <#break/>
  </#switch>
  </#compress>
</#macro>

