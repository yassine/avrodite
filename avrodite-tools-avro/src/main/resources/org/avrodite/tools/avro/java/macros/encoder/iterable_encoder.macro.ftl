<#macro iterable_encoder context member type reference>
<#-- @ftlvariable name="context" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="type" type="org.avrodite.meta.type.TypeInfo" -->
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
  output.writeInt(${reference}.size)
  <#assign ref = "item_${context.random(5)}"/>
  for (${type.composite.signature()} ${ref} : ${reference}) {
    <@encoder context=context member=member type=type.composite reference="item_${ref}"/>
  }
  if(${reference}.isNotEmpty()) {
    output.writeByte((byte) 0)
  }
</#macro>
