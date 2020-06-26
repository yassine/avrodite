<#-- @ftlvariable name="meta" type="org.avrodite.tools.template.TypeMetaContext" -->
<#macro fn_dump_value_codecs meta>
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
<#-- @ftlvariable name="valueCodecs" type="java.util.Map<String, String>" -->
  <#list meta.params as member>
    <#if member.valueType>
      private val ${member.memberMeta.name}_codec = ${valueCodecs[member.targetType().signature()]}()
    </#if>
  </#list>
  <#list meta.props as member>
    <#if member.valueType>
      private val ${member.memberMeta.name}_codec = ${valueCodecs[member.targetType().signature()]}()
    </#if>
  </#list>
</#macro>
