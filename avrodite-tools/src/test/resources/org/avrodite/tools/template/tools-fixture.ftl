<#-- @ftlvariable name="meta" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="prop" type="org.avrodite.tools.template.PropMetaContext" -->
package ${meta.type.namespace()}

Hello ${meta.type.info.signature()}!
<#list meta.props as prop>
${prop.memberMeta.field.name} : ${prop.memberMeta.typeInfo.signature()} : ${prop.memberMeta.typeInfo.type.markedNullable?string}
</#list>
