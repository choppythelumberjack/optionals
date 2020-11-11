package com.github.ctl.optionals;

import org.junit.Test;
import static com.github.ctl.optionals.Optionals.*;
import static org.junit.Assert.*;

public class TestOptionalAndGet {
  public String throwNull() {
    if (true) throw new NullPointerException("msg");
    return "fakeValue";
  }

  @Test
  public void testOfAndThenExist() {
    Optionals2<String, String> str2 = Optionals.of(() -> "foo").and(() -> "bar");
    assertEquals(str2.value1(), "foo");
    assertEquals(str2.value2(), "bar");
    assertEquals(str2.isDefined(), true);
    assertEquals(str2.isEmpty(), false);
    assertEquals(str2.message(), null);
  }

  @Test
  public void testOfAndThenNotExist1() {
    Optionals2<String, String> str2 = Optionals.of(() -> (String) null).and(() -> "bar");
    assertEquals(str2.value1(), null);
    assertEquals(str2.value2(), null);
    assertEquals(str2.isDefined(), false);
    assertEquals(str2.isEmpty(), true);
    assertEquals(str2.message(), null);
  }

  @Test
  public void testOfAndThenNotExist2() {
    Optionals2<String, String> str2 = Optionals.of(() -> "foo").and(() -> null);
    assertEquals(str2.value1(), "foo");
    assertEquals(str2.value2(), null);
    assertEquals(str2.isDefined(), false);
    assertEquals(str2.isEmpty(), true);
    assertEquals(str2.message(), null);
  }

  @Test
  public void testOfAndThenNotExist1_Throws() {
    Optionals2<String, String> str2 = Optionals.of(() -> throwNull()).and(() -> "bar");
    assertEquals(str2.value1(), null);
    assertEquals(str2.value2(), null);
    assertEquals(str2.isDefined(), false);
    assertEquals(str2.isEmpty(), true);
    assertEquals(str2.message(), null);
  }

  @Test
  public void testOfAndThenNotExist2_Throws() {
    Optionals2<String, String> str2 = Optionals.of(() -> "foo").and(() -> throwNull());
    assertEquals(str2.value1(), "foo");
    assertEquals(str2.value2(), null);
    assertEquals(str2.isDefined(), false);
    assertEquals(str2.isEmpty(), true);
    assertEquals(str2.message(), null);
  }

  @Test
  public void testOfAndThenNotExist1_Msg() { // same for 2
    Optionals2<String, String> str2 = Optionals.of(() -> (String) null, "FooMessage").and(() -> "bar");
    assertEquals(str2.value1(), null);
    assertEquals(str2.value2(), null);
    assertEquals(str2.isDefined(), false);
    assertEquals(str2.isEmpty(), true);
    assertEquals(str2.message(), "FooMessage");
  }

  @Test
  public void testOfAndThenNotExist2_Msg() { // same for 2
    Optionals2<String, String> str2 = Optionals.of(() -> "foo", "FooMessage").and(() -> throwNull(), "BarMessage");
    assertEquals(str2.value1(), "foo");
    assertEquals(str2.value2(), null);
    assertEquals(str2.isDefined(), false);
    assertEquals(str2.isEmpty(), true);
    assertEquals(str2.message(), "BarMessage");
  }
}
