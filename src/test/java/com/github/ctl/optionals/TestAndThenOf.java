package com.github.ctl.optionals;

import org.junit.Test;
import static com.github.ctl.optionals.Optionals.*;
import static org.junit.Assert.*;

public class TestAndThenOf {
  public String throwNull() {
    if (true) throw new NullPointerException("msg");
    return "fakeValue";
  }

  @Test
  public void testThen2() {
    Optionals3<String, String, String> str3 =
      Optionals
          .of(() -> "foo", () -> "bar")
          .andThenOf((foo, bar) -> foo + bar);

    assertEquals(str3.value1(), "foo");
    assertEquals(str3.value2(), "bar");
    assertEquals(str3.value3(), "foobar");
    assertEquals(str3.isDefined(), true);
    assertEquals(str3.isEmpty(), false);
    assertEquals(str3.message(), null);
  }

  @Test
  public void testThen2_Throw1() {
    Optionals3<String, String, String> str3 =
        Optionals
            .of(() -> throwNull(), () -> "bar")
            .andThenOf((foo, bar) -> foo + bar);

    assertEquals(str3.value1(), null);
    assertEquals(str3.value2(), null);
    assertEquals(str3.value3(), null);
    assertEquals(str3.isDefined(), false);
    assertEquals(str3.isEmpty(), true);
    assertEquals(str3.message(), null);
  }

  @Test
  public void testThen2_Throw2() {
    Optionals3<String, String, String> str3 =
        Optionals
            .of(() -> "foo", () -> throwNull())
            .andThenOf((foo, bar) -> foo + bar);

    assertEquals(str3.value1(), "foo");
    assertEquals(str3.value2(), null);
    assertEquals(str3.value2(), null);
    assertEquals(str3.isDefined(), false);
    assertEquals(str3.isEmpty(), true);
    assertEquals(str3.message(), null);
  }

  @Test
  public void testThen2_Throw3() {
    Optionals3<String, String, String> str3 =
        Optionals
            .of(() -> "foo", () -> "bar")
            .andThenOf((foo, bar) -> throwNull());

    assertEquals(str3.value1(), "foo");
    assertEquals(str3.value2(), "bar");
    assertEquals(str3.value3(), null);
    assertEquals(str3.isDefined(), false);
    assertEquals(str3.isEmpty(), true);
    assertEquals(str3.message(), null);
  }


  @Test
  public void testThen2_Throw2_Msg() {
    Optionals3<String, String, String> str3 =
        Optionals
            .of(
                () -> "foo", "FooMessage",
                () -> throwNull(), "BarMessage"
            )
            .andThenOf((foo, bar) -> foo + bar, "BazMessage");

    assertEquals(str3.value1(), "foo");
    assertEquals(str3.value2(), null);
    assertEquals(str3.value3(), null);
    assertEquals(str3.isDefined(), false);
    assertEquals(str3.isEmpty(), true);
    assertEquals(str3.message(), "BarMessage");
  }

  @Test
  public void testThen2_Throw3_Msg() {
    Optionals3<String, String, String> str3 =
        Optionals
            .of(() -> "foo", "FooMessage",
                () -> "bar", "BarMessage")
            .andThenOf((foo, bar) -> throwNull(), "BazMessage");

    assertEquals(str3.value1(), "foo");
    assertEquals(str3.value2(), "bar");
    assertEquals(str3.value3(), null);
    assertEquals(str3.isDefined(), false);
    assertEquals(str3.isEmpty(), true);
    assertEquals(str3.message(), "BazMessage");
  }
}
