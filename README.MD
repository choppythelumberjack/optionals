# Optionals

I wrote this library upon realizing that while Java Optionals are frequently touted as the 
replacement for nullable parameters, the reality is they are quite difficult to compose
given that Java last the equivalent of Scala's for-comprehensions or Kotlin's
specialized notations. For example, composing five nullable elements in Java
looks something like this:
```java
String foo = getFoo();
String bar = getBar();
String baz = getBaz();
String blin = getBlin();
String blaz = getblaz();

Optional<MyClass> output =
    Optional.ofNullable(foo).flatMap(fooVal -> 
      Optional.ofNullable(bar),flatMap(barVal ->
        Optional.ofNullable(baz).flatMap(bazVal ->
          Optional.ofNullable(blin).flatMap(blinVal ->
            Optional.ofNullable(baz).map(blazVal ->
              // Then fro here we need to use these somehow...
              new MyClass(fooVal, barVal, bazVal, blinVal, blazVal)
            )
          }
        )
      )
    )
```

Another issue to note that if the arguments passed into the `Optional.ofNullable` method
actually throws a NullPointerException, this will not be caught. It can only be caught if
the actual value of the argument is a lambda. While this may not be desirable in call cases,
I believe that in many cases it is.

I envision a simpler API for composiong optional values that looks like something like this.
```java
Optionals1<MyClass> output =
    Optionals
      .of(() -> foo)
      .and(() -> bar)
      .and(() -> baz)
      .and(() -> blin)
      .and(() -> blaz)
      .then((foo, bar, baz, blin, blaz) -> new MyClass(fooVal, barVal, bazVal, blinVal, blazVal))
```

This essentially combines the notion of first-fail optionality (i.e. the first null value
causes all the ones after that to be null) with a simple notion of tuples.

That is to say, if the following occurs:
```java
Optionals1<MyClass> output =
    Optionals
      .of(() -> foo)
      .and(() -> bar)
      .and(() -> null) // or throws a NullPointerException
      .and(() -> blin)
      .and(() -> blaz)
      .then((foo, bar, baz, blin, blaz) -> new MyClass(fooVal, barVal, bazVal, blinVal, blazVal))

output.isDefined() // true
output.value1() // "foo"
output.value2() // "bar"
output.value3() // null
output.value4() // null
output.value5() // null
```

Optionally, you can also add a message in order to more easily locate the issue.
```java
Optionals1<MyClass> output =
    Optionals
      .of(() -> foo, "Foo is null")
      .and(() -> bar, "Bar is null")
      .and(() -> null, "Baz is null"))
      .and(() -> blin, "Blin is null"))
      .and(() -> blaz, "Blaz is null"))
      .then((foo, bar, baz, blin, blaz) -> new MyClass(fooVal, barVal, bazVal, blinVal, blazVal))

output.message() // "Baz is null"
```


Right now there are just a couple of methods that can be used

## and(() -> nextValue, <message>)
Add another value to the composition, and an optional message to add if the value is null.
```
Optionals1<String> a = Optionals.of(() -> "foo")
Optionals2<String, String> b = a.and(() -> "bar")
```

## andThen((lastElementValue) -> nextValue, <message>)
Add another value to the composition, using the last value that we had.
This is very useful since optional-object compositions are typically
created using a cascade where each element depends on the previous one.
```
Optionals2<S, S>       a = Optionals.of(() -> "foo", () -> "bar")
Optionals3<S, S, S>    b = a.and((bar) -> bar + "baz")
Optionals4<S, S, S, S> c = b.and((barbaz) -> barbaz + blin)
```

## andThenOf((value1, value2... valueN) -> nextValue, <message>)
This adds another value to the composition, similarly to `andThen`, however
any/all of the currently elements in the composition can be used to create
the next element.
```
Optionals2<S, S>    a = Optionals.of(() -> "foo", () -> "bar")
Optionals3<S, S, S> b = a.and((foo, bar) -> foo + bar)
```

## andThenOf((value1, value2... valueN) -> newSingleValue, <message>)
This allows mapping any/all elements of the current composition
into a single new composition.
```
Optionals2<S, S> a = Optionals.of(() -> "foo", () -> "bar")
Optionals1<S>    b = a.and((foo, bar) -> foo + bar)
```





