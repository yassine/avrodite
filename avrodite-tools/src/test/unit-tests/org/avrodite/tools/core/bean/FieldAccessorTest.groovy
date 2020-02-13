package org.avrodite.tools.core.bean

import org.avrodite.fixtures.bean.FieldAccessBean
import spock.lang.Specification

import java.lang.reflect.Field
import java.util.stream.Collectors

class FieldAccessorTest extends Specification {

  static def visibilityBeanFields = Arrays.stream(FieldAccessBean.class.declaredFields)
    .collect(Collectors.toMap({ ((Field) it).name }, { new FieldAccessor((Field) it)}))

  def "Field accessibility should be correctly mapped for a given Field"(){
    expect:
      !visibilityBeanFields['accessible1'].visible
      visibilityBeanFields['accessible1'].readable
      visibilityBeanFields['accessible1'].writable

      !visibilityBeanFields['accessible2'].visible
      visibilityBeanFields['accessible2'].readable
      visibilityBeanFields['accessible2'].writable

      visibilityBeanFields['accessible3'].visible
      visibilityBeanFields['accessible3'].readable
      visibilityBeanFields['accessible3'].writable

      visibilityBeanFields['accessible4'].visible
      visibilityBeanFields['accessible4'].readable
      visibilityBeanFields['accessible4'].writable

      !visibilityBeanFields['accessible5'].visible
      visibilityBeanFields['accessible5'].readable
      visibilityBeanFields['accessible5'].writable

      !visibilityBeanFields['inaccessible1'].visible
      !visibilityBeanFields['inaccessible1'].readable
      !visibilityBeanFields['inaccessible1'].writable

      !visibilityBeanFields['inaccessible2'].visible
      !visibilityBeanFields['inaccessible2'].readable
      !visibilityBeanFields['inaccessible2'].writable

      !visibilityBeanFields['inaccessible3'].visible
      !visibilityBeanFields['inaccessible3'].readable
      !visibilityBeanFields['inaccessible3'].writable
  }

}
