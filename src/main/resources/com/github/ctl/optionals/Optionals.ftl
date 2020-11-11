package com.github.ctl.optionals;

import java.util.Optional;

public class Optionals {

  <#macro Ts num><#list 1..num as t>T${t}<#if t != num>, </#if></#list></#macro>
  <#macro values num><#list 1..num as t>value${t}<#if t != num>, </#if></#list></#macro>
  <#macro Multi num><#list 1..num as t><#nested t><#if t != num>, </#if></#list></#macro>

  @FunctionalInterface
  public interface NextElement<T> {
    T make();
  }
  // A nexter for interfaces that are not indexed does not have an index to easily be able to tell that this particular
  // usage should not be incremented
  @FunctionalInterface
  public interface FromLastValue<T, R> {
    R makeNext(T value);
  }

  // Indexed versions of nexter for the interfaces which use that
<#list 1..22 as i>
  @FunctionalInterface
  public interface FromAllValues${i}<<@Ts num=i />, R> {
    R makeNext(<@Multi i; t>T${t} value${t}</@Multi>);
  }
</#list>

<#list 1..22 as o>
  public static class Optionals${o}<<@Ts num=o />> {
    protected boolean defined;
    protected String message;
    <#list 1..o as t>
    protected T${t} value${t};
    </#list>

    <#list 1..o as t>
    public T${t} value${t}() { return value${t}; }
    </#list>

    <#list 1..o as t>
    public Optional<T${t}> value${t}Opt() { return Optional.ofNullable(value${t}); }
    </#list>

    public String message() {
      return message;
    }

    public boolean isEmpty() {
      return !defined;
    }
    public boolean isDefined() {
      return defined;
    }

    private Optionals${o}(<#list 1..o as t>T${t} value${t}<#if t != o>, </#if></#list>, boolean defined, String message) {
      <#list 1..o as t>
      this.value${t} = value${t};
      </#list>
      this.defined = defined;
      this.message = message;
    }

    <#if o == 1>
    // Can only do 'or' for Optional1 because others optionals have different arity
    // then the 'or' clause. I.e. 'or' for option2 would have to be a different option2
    // which does not seem to make sense.
    public Optionals1<T1> or(NextElement<T1> newValue) {
      Optionals1<T1> newOpt = Optionals.of(newValue);
      if (isEmpty() && newOpt.isDefined()) return Optionals.of(newValue);
      else return this;
    }

    public Optionals1<T1> orElse(Optionals1<T1> newOpt) {
      // If the new value is null, the message of this one must remain the output (since the first null value presides)
      // therefore we do not switch to the new one if we are empty and they are empty as well
      if (isEmpty() && newOpt.isDefined()) return newOpt;
      else return this;
    }

    public Optionals1<T1> or(NextElement<T1> newValue, String message) {
      Optionals1<T1> newOpt = Optionals.of(newValue);
      // If the new value is null, the message of this one must remain the output (since the first null value presides)
      // therefore we do not switch to the new one if we are empty and they are empty as well
      if (isEmpty() && newOpt.isDefined()) return Optionals.of(newValue, message);
      else return this;
    }
    </#if>

    <#if o <= 21>
    public <R> Optionals${o+1}<<@Ts num=o />, R> and(NextElement<R> next) { return and(next, null); }
    public <R> Optionals${o+1}<<@Ts num=o />, R> and(NextElement<R> next, String message) {
      boolean defined = this.defined;
      String newMessage = this.message;
      R newValue = null;
      // If we are previously defined, try to get the next value
      if (defined) try {  newValue = next.make(); } catch (NullPointerException ignored) { }
      // If we have previously been defined and the new value is null set the newly undefined settings
      if (newValue == null && defined) {
        newMessage = message;
        defined = false;
      }
      return new Optionals.Optionals${o+1}<>(<@values num=o />, newValue, defined, newMessage);
    }

    public <R> Optionals${o+1}<<@Ts num=o />, R> andOf(FromLastValue<T${o}, R> next) { return andOf(next, null); }
    public <R> Optionals${o+1}<<@Ts num=o />, R> andOf(FromLastValue<T${o}, R> next, String message) {
      boolean defined = this.defined;
      String newMessage = this.message;
      R newValue = null;
      // If we are previously defined, try to get the next value
      if (defined) try { newValue = next.makeNext(value${o}); } catch (NullPointerException ignored) { }
      // If we have previously been defined and the new value is null set the newly undefined settings
      if (newValue == null && defined) {
        newMessage = message;
        defined = false;
      }
      return new Optionals.Optionals${o+1}<>(<@values num=o />, newValue, defined, newMessage);
    }

    public <R> Optionals${o+1}<<@Ts num=o />, R> andThenOf(FromAllValues${o}<<@Ts num=o />, R> next) { return andThenOf(next, null); }
    public <R> Optionals${o+1}<<@Ts num=o />, R> andThenOf(FromAllValues${o}<<@Ts num=o />, R> next, String message) {
      boolean defined = this.defined;
      String newMessage = this.message;
      R newValue = null;
      // If we are previously defined, try to get the next value
      if (defined) try { newValue = next.makeNext(<@values num=o />); } catch (NullPointerException ignored) { }
      // If we have previously been defined and the new value is null set the newly undefined settings
      if (newValue == null && defined) {
        newMessage = message;
        defined = false;
      }
      return new Optionals.Optionals${o+1}<>(<@values num=o />, newValue, defined, newMessage);
    }

    public <R> Optionals1<R> then(FromAllValues${o}<<@Ts num=o />, R> next) { return then(next, null); }
    public <R> Optionals1<R> then(FromAllValues${o}<<@Ts num=o />, R> next, String message) {
      boolean defined = this.defined;
      String newMessage = this.message;
      R newValue = null;
      // If we are previously defined, try to get the next value
      if (defined) try { newValue = next.makeNext(<@values num=o />); } catch (NullPointerException ignored) { }
      // If we have previously been defined and the new value is null set the newly undefined settings
      if (newValue == null && defined) {
        newMessage = message;
        defined = false;
      }
      return new Optionals.Optionals1<>(newValue, defined, newMessage);
    }
    </#if>
  }
</#list>

  // of-constructors
<#list 1..22 as o>
  public static <<@Ts num=o />> Optionals${o}<<@Ts num=o />> of(<@Multi num=o ; t>NextElement<T${t}> get${t}</@Multi>) { return of(<@Multi num=o ; t>get${t}, (String) null</@Multi>); }
  public static <<@Ts num=o />> Optionals${o}<<@Ts num=o />> of(<@Multi num=o ; t>NextElement<T${t}> get${t}, String message${t}</@Multi>) {
    String newMessage = null;
    boolean defined = true;
    <#list 1..o as t>
    T${t} value${t} = null;
    </#list>
    <#list 1..o as t>
    // If we are previously defined, try to get the next value
    if (defined) try {  value${t} = get${t}.make(); } catch (NullPointerException ignored) { }
    // If we have previously been defined and the new value is null set the newly undefined settings
    if (value${t} == null && defined) {
      defined = false;
      newMessage = message${t};
    }
    </#list>
    return new Optionals${o}<>(<@values num=o />, defined, newMessage);
  }
</#list>
}