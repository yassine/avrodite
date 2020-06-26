<#include "./macros/macros.ftl">
<#-- @ftlvariable name="meta" type="org.avrodite.tools.template.TypeMetaContext" -->
<#-- @ftlvariable name="member" type="org.avrodite.tools.template.MemberMetaContext" -->
package ${meta.type.namespace()};

import org.avrodite.avro.*;
import org.avrodite.avro.error.*;
import org.avrodite.avro.error.AvroditeAvroException;

public class <@codec_name type=meta.type.info context=meta/> implements AvroCodec<${meta.typeMeta.typeInfo.signature()}> {

  @Override
  public void encode(${meta.typeMeta.typeInfo.signature()} m, AvroOutput output) throws AvroditeAvroException {
    throw new RuntimeException();
  }

  @Override
  public ${meta.typeMeta.typeInfo.signature()} decode(${meta.typeMeta.typeInfo.signature()} m, AvroInput input) throws AvroditeAvroException {
    throw new RuntimeException();
  }

  @Override
  public ${meta.typeMeta.typeInfo.signature()} decode(AvroInput input) throws AvroditeAvroException {
    throw new RuntimeException();
  }

  public static void encode_static(${meta.typeMeta.typeInfo.signature()} m, AvroOutput output) throws AvroditeAvroException {
    throw new RuntimeException();
  }

  public static ${meta.typeMeta.typeInfo.signature()} decode_static(${meta.typeMeta.typeInfo.signature()} m, AvroInput input) throws AvroditeAvroException {
    throw new RuntimeException();
  }

  public static ${meta.typeMeta.typeInfo.signature()} decode_static(AvroInput input) throws AvroditeAvroException {
    throw new RuntimeException();
  }
/*
  <#list meta.params as member>
  static <@fn_name_member_encoder member=member/>(${meta.typeMeta.typeInfo.signature()} model, AvroOutput output){
    <@encoder context=meta member=member type=member.memberMeta.typeInfo reference="model.${member.memberMeta.name}"/>
  }
  </#list>

  <#list meta.props as member>
  static <@fn_name_member_encoder member=member/>(${meta.typeMeta.typeInfo.signature()} model, AvroOutput output){
    <@encoder context=meta member=member type=member.memberMeta.typeInfo reference="model.${member.memberMeta.name}"/>
  }
  </#list>

  <#list meta.props as member>
  static <@fn_name_member_decoder member=member/>(${meta.typeMeta.typeInfo.signature()} model, AvroInput input) {
    <@decoder context=meta member=member type=member.memberMeta.typeInfo reference="model.${member.memberMeta.name}"/>
  }
  </#list>
*/
}
