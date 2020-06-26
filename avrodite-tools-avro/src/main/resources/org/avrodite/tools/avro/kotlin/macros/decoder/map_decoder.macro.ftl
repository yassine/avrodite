<#macro map_decoder context member type reference>
<#-- @ftlvariable name="context" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="type" type="org.avrodite.meta.type.TypeInfo" -->
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
  <#assign size = "size_${context.random(8)}"/>
  <#assign index = "idx_${context.random(8)}"/>
  <#assign key = "key_${context.random(8)}"/>
  <#assign map = "map_${context.random(8)}"/>
  <#assign cur = "cur_${context.random(8)}"/>
  val ${size} = input.readInt()
  val ${map}  = HashMap< String, ${type.composite.signature()} >(${size})
  var ${key}: String
  var ${cur}: ${type.composite.signature()}
  for (${index} in 0 until ${size}) {
    ${key} = input.readString()
    <@decoder context=context member=member type=type.composite reference="${cur}"/>
    ${map}[${key}]= ${cur}
  }
  ${reference} = ${map}
</#macro>
