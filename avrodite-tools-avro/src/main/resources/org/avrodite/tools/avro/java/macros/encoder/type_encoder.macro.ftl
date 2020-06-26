<#macro type_encoder context member type reference>
<#-- @ftlvariable name="context" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="type" type="org.avrodite.meta.type.TypeInfo" -->
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
  <#if type.type.markedNullable || type.nullable >
    if (${reference} != null) {
      output.writeByte(0);
      <@codec_fq_name context=context type=type/>.encode(${reference}, output)
    } else {
      output.writeByte(2);
    }
  <#else >
    <@codec_fq_name context=context type=type/>.encode(${reference}, output)
  </#if>
</#macro>
