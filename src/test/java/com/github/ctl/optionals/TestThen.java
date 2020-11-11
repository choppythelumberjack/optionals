package com.github.ctl.optionals;

import org.junit.Test;
import static com.github.ctl.optionals.Optionals.*;
import static org.junit.Assert.assertEquals;

public class TestThen {
  public String throwNull() {
    if (true) throw new NullPointerException("msg");
    return "fakeValue";
  }

  @Test
  public void testThen2() {
    Optionals1<String> str3 =
      Optionals
          .of(() -> "foo", () -> "bar")
          .then((foo, bar) -> foo + bar);

    assertEquals(str3.value1(), "foobar");
    assertEquals(str3.isDefined(), true);
    assertEquals(str3.isEmpty(), false);
    assertEquals(str3.message(), null);
  }

  @Test
  public void testThen2_Throw1() {
    Optionals1<String> str3 =
        Optionals
            .of(() -> throwNull(), () -> "bar")
            .then((foo, bar) -> foo + bar);

    assertEquals(str3.value1(), null);
    assertEquals(str3.isDefined(), false);
    assertEquals(str3.isEmpty(), true);
    assertEquals(str3.message(), null);
  }

  @Test
  public void testThen2_Throw2() {
    Optionals1<String> str3 =
        Optionals
            .of(() -> "foo", () -> throwNull())
            .then((foo, bar) -> foo + bar);

    assertEquals(str3.value1(), null);
    assertEquals(str3.isDefined(), false);
    assertEquals(str3.isEmpty(), true);
    assertEquals(str3.message(), null);
  }

  @Test
  public void testThen2_Throw3() {
    Optionals1<String> str3 =
        Optionals
            .of(() -> "foo", () -> "bar")
            .then((foo, bar) -> throwNull());

    assertEquals(str3.value1(), null);
    assertEquals(str3.isDefined(), false);
    assertEquals(str3.isEmpty(), true);
    assertEquals(str3.message(), null);
  }


  @Test
  public void testThen2_Throw2_Msg() {
    Optionals1<String> str3 =
        Optionals
            .of(
                () -> "foo", "FooMessage",
                () -> throwNull(), "BarMessage"
            )
            .then((foo, bar) -> foo + bar, "BazMessage");

    assertEquals(str3.value1(), null);
    assertEquals(str3.isDefined(), false);
    assertEquals(str3.isEmpty(), true);
    assertEquals(str3.message(), "BarMessage");
  }

  @Test
  public void testThen2_Throw3_Msg() {
    Optionals1<String> str3 =
        Optionals
            .of(() -> "foo", "FooMessage",
                () -> "bar", "BarMessage")
            .then((foo, bar) -> throwNull(), "BazMessage");

    assertEquals(str3.value1(), null);
    assertEquals(str3.isDefined(), false);
    assertEquals(str3.isEmpty(), true);
    assertEquals(str3.message(), "BazMessage");
  }
}
