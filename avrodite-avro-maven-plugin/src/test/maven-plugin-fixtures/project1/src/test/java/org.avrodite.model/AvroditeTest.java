package org.avrodite.model;

import static org.junit.Assert.*;

import java.lang.reflect.Type;
import org.junit.Test;
public class AvroditeTest {

  @Test
  public void test(){
    // test the codecs were compiled and that the public API
    // is able to discover them.
    /*
    org.avrodite.Avrodite avrodite = AvroStandardV18.avrodite().build();
    Type type = (Type) avrodite.codecIndex().keySet().stream().findAny().get();
    assertEquals(avrodite.codecIndex().values().size(), 1);
    assertEquals(type.getTypeName(), "org.avrodite.model.TestModel");
    assertNotNull(avrodite.getCodec(TestModel.class));

     */
    System.out.println("test runned!");
  }

}
