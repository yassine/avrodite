<#macro fn_name_member_encoder member>
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
<#compress>
  encode_${member.memberMeta.name}
</#compress>
</#macro>
<#macro fn_name_member_decoder member>
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
  <#compress>
    decode_${member.memberMeta.name}
  </#compress>
</#macro>
<#macro codec_name context type>
<#-- @ftlvariable name="context" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="type" type="org.avrodite.meta.type.TypeInfo" -->
<#compress>
  ${type.simpleName()}_Avro_Codec_${context.sha(type.eraseNullability().signature(), 8)}
</#compress>
</#macro>

<#macro codec_fq_name context type>
<#-- @ftlvariable name="context" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="type" type="org.avrodite.meta.type.TypeInfo" -->
<#compress>
  ${type.packageName()}.<@codec_name context=context type=type/>
</#compress>
</#macro>
