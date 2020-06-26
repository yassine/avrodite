<#macro encoder context member type reference>
<#-- @ftlvariable name="param" type="org.avrodite.tools.template.ParamMetaContext" -->
<#-- @ftlvariable name="context" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="type" type="org.avrodite.meta.type.TypeInfo" -->
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
  <#switch type.category>
    <#case "STRING">
      output.writeString(${reference});
      <#break/>
    <#case "BYTE">
      output.writeByte(${reference});
      <#break/>
    <#case "INT">
      output.writeInt(${reference});
      <#break/>
    <#case "LONG">
      output.writeLong(${reference});
      <#break/>
    <#case "DOUBLE">
      output.writeDouble(${reference});
      <#break/>
    <#case "FLOAT">
      output.writeFloat(${reference});
      <#break/>
    <#case "ARRAY">
      <@array_encoder context=context member=member type=type reference="${reference}"/>
      <#break/>
    <#case "LIST">
      <@list_encoder context=context member=member type=type reference="${reference}"/>
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

