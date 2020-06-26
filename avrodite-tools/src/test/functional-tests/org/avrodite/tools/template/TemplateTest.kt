package org.avrodite.tools.template

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TemplateTest : Spek({

  describe("when a template is configured correctly") {
    it("should render the target correctly") {
      val template = Template.of {
        templateLocation = "/org/avrodite/tools/template/test-fixture.ftlh"
        withContextVar("target", "world")
      }
      expectThat(template.render()).isEqualTo("Hello world!\n")
    }
  }

})
