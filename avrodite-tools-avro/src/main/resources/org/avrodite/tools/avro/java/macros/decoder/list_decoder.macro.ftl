<#macro list_decoder context member type reference>
<#-- @ftlvariable name="context" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="type" type="org.avrodite.meta.type.TypeInfo" -->
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
  <#assign size = "size_${context.random(5)}"/>
  <#assign list_ref = "list_${context.random(5)}"/>
  <#assign current_ref = "${list_ref}_current"/>
  int ${size} = input.readInt();
  ${reference} = new java.util.ArrayList<${type.composite.signature()}>(${size});
  <#assign index = "index_${context.random(5)}"/>
  <#if type.composite.category == "TYPE">
    ${type.composite.signature()} ${current_ref} = null;
  </#if>
  for(int ${index} = 0; ${index} < size; ${index}++) {
    <@decoder context=context member=member type=type.composite reference="${current_ref}"/>
    ${reference}.add(${index}, ${current_ref});
  }
  if (${size} > 0 && input.readByte() != 0.toByte()) {
    throw AvroditeAvroException.illegalArrayEnd(${size}, "${member.memberMeta.name}", "${member.memberMeta.typeInfo.signature()}");
  }
</#macro>
