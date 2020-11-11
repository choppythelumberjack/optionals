package com.github.ctl.optionals;

import org.junit.Test;
import static com.github.ctl.optionals.Optionals.*;
import static org.junit.Assert.*;

public class TestOr {
  @Test
  public void or_first() {
    Optionals1<String> a = Optionals.of(() -> "foo");
    Optionals1<String> b = a.or(() -> "bar");
    assertEquals(a.value1(), "foo");
    assertEquals(b.value1(), "foo");
    assertEquals(a.isDefined(), true);
    assertEquals(a.message(), null);
    assertEquals(b.isDefined(), true);
    assertEquals(b.message(), null);
  }
  @Test
  public void or_second() {
    Optionals1<String> a = Optionals.of(() -> null);
    Optionals1<String> b = a.or(() -> "bar");
    assertEquals(a.value1(), null);
    assertEquals(b.value1(), "bar");
    assertEquals(a.isDefined(), false);
    assertEquals(a.message(), null);
    assertEquals(b.isDefined(), true);
    assertEquals(b.message(), null);
  }
  @Test
  public void or_second_message() {
    Optionals1<String> a = Optionals.of(() -> null, "FooMessage");
    Optionals1<String> b = a.or(() -> "bar", "BarMessage");
    assertEquals(a.value1(), null);
    assertEquals(b.value1(), "bar");
    assertEquals(a.isDefined(), false);
    assertEquals(a.message(), "FooMessage");
    assertEquals(b.isDefined(), true);
    assertEquals(b.message(), null);
  }

  @Test
  public void orElse_first() {
    Optionals1<String> a = Optionals.of(() -> "foo");
    Optionals1<String> b = a.orElse(Optionals.of(() -> "bar"));
    assertEquals(a.value1(), "foo");
    assertEquals(b.value1(), "foo");
    assertEquals(a.isDefined(), true);
    assertEquals(a.message(), null);
    assertEquals(b.isDefined(), true);
    assertEquals(b.message(), null);
  }
  @Test
  public void orElse_second() {
    Optionals1<String> a = Optionals.of(() -> null);
    Optionals1<String> b = a.orElse(Optionals.of(() -> "bar"));
    assertEquals(a.value1(), null);
    assertEquals(b.value1(), "bar");
    assertEquals(a.isDefined(), false);
    assertEquals(a.message(), null);
    assertEquals(b.isDefined(), true);
    assertEquals(b.message(), null);
  }
  @Test
  public void orElse_second_message() {
    Optionals1<String> a = Optionals.of(() -> null, "FooMessage");
    Optionals1<String> b = a.orElse(Optionals.of(() -> "bar", "BarMessage"));
    assertEquals(a.value1(), null);
    assertEquals(b.value1(), "bar");
    assertEquals(a.isDefined(), false);
    assertEquals(a.message(), "FooMessage");
    assertEquals(b.isDefined(), true);
    assertEquals(b.message(), null);
  }
}
