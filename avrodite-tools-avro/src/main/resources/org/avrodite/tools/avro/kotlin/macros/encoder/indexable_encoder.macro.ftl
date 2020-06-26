<#macro indexable_encoder context member type reference>
<#-- @ftlvariable name="context" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="type" type="org.avrodite.meta.type.TypeInfo" -->
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
  output.writeInt(${reference}.size)
  <#assign element = "element_${context.random(5)}"/>
  for (${element} in ${reference}) {
    <@encoder context=context member=member type=type.composite reference="${element}"/>
  }
  if(${reference}.isNotEmpty()) {
    output.writeByte(0)
  }
</#macro>
