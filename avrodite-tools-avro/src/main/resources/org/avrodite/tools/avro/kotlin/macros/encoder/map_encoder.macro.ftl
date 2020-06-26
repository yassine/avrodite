<#macro map_encoder context member type reference>
<#-- @ftlvariable name="context" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="type" type="org.avrodite.meta.type.TypeInfo" -->
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
  output.writeInt(${reference}.size)
  <#assign entry = "entry_${context.random(5)}"/>
  <#assign value = "map_val_${context.random(5)}"/>
  var ${value}: ${type.composite.signature()}
  for(${entry} in ${reference}.entries) {
    output.writeString(${entry}.key)
    ${value} = ${entry}.value
    <@encoder context=context member=member type=type.composite reference="${value}"/>
  }
</#macro>
