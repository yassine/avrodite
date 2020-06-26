<#macro list_encoder context member type reference>
<#-- @ftlvariable name="context" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="type" type="org.avrodite.meta.type.TypeInfo" -->
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
  output.writeInt(${reference}.size)
  <#assign index = "index_${context.random(5)}"/>
  <#assign item = "array_item_${context.random(5)}"/>
  ${type.composite.signature()} ${item};
  for (int ${index} = 0; ${index}<${reference}.size(); ${index}++) {
    ${item} = ${reference}.get(${index});
    <@encoder context=context member=member type=type.composite reference="${item}"/>
  }
  if(!${reference}.isEmpty()) {
    output.writeByte(0);
  }
</#macro>

<#macro array_encoder context member type reference>
<#-- @ftlvariable name="context" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="type" type="org.avrodite.meta.type.TypeInfo" -->
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
  output.writeInt(${reference}.size);
  <#assign index = "index_${context.random(5)}"/>
  for (int ${index} = 0; ${index}<${reference}.size(); ${index}++) {
    <@encoder context=context member=member type=type.composite reference="${reference}[${index}]"/>
  }
  if(!${reference}.isEmpty()) {
    output.writeByte(0);
  }
</#macro>
