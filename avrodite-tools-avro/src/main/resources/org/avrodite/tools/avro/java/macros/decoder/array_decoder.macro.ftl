<#macro array_decoder context member type reference>
<#-- @ftlvariable name="context" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="type" type="org.avrodite.meta.type.TypeInfo" -->
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
  <#assign size = "size_${context.random(5)}"/>
  <#assign array_ref = "array_${context.random(5)}"/>
  <#assign current_ref = "${array_ref}_current"/>
  int ${size} = input.readInt();
  ${reference} = new ${type.composite.signature()}[${size}];
  <#assign index = "index_${context.random(5)}"/>
  <#if type.composite.category == "TYPE">
    ${type.composite.signature()} ${current_ref} = null;
  </#if>
  for(int ${index} = 0; ${index} < size; ${index}++) {
    <#if type.composite.category != "TYPE">
      <@decoder context=context member=member type=type.composite reference="${reference}[${index}]"/>
    <#else>
      <@decoder context=context member=member type=type.composite reference="${current_ref}"/>
      ${reference}[${index}] = ${current_ref};
    </#if>
  }
  if (${size} > 0 && input.readByte() != 0.toByte()) {
    throw AvroditeAvroException.illegalArrayEnd(${size}, "${member.memberMeta.name}", "${member.memberMeta.typeInfo.signature()}");
  }
</#macro>
