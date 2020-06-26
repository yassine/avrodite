<#macro map_encoder context member type reference>
<#-- @ftlvariable name="context" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="type" type="org.avrodite.meta.type.TypeInfo" -->
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
  output.writeLong(${reference}.size());
  <#assign entry = "entry_${context.random(5)}"/>
  <#assign value = "map_val_${context.random(5)}"/>
  ${type.composite.signature()} ${value} = null;
  for( java.util.Map.Entry< String, ${type.composite.signature()} > ${entry} : ${reference}.entrySet() ) {
    ${value} = ${entry}.getValue();
    if(${value} != null){
      output.writeString(${entry}.getKey());
      <@encoder context=context member=member type=type.composite reference="${value}"/>
    }
  }
</#macro>
