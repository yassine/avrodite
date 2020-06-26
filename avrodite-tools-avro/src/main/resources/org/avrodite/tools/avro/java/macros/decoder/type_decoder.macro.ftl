<#macro type_decoder context member type reference>
<#-- @ftlvariable name="context" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="type" type="org.avrodite.meta.type.TypeInfo" -->
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
  <#if type.type.markedNullable>
    <#assign ref = "type_${context.random(5)}"/>
    if (input.readByte().equals((byte) 0)) {
      if(${reference} != null){
        <@codec_fq_name context=context type=type/>.decode(${reference}, input);
      } else {
        ${reference} = <@codec_fq_name context=context type=type/>.decode(input);
      }
    } else {
      ${reference} = null;
    }
  <#else >
    ${reference} = <@codec_fq_name context=context type=type/>.decode(${reference}, input)
  </#if>
</#macro>
