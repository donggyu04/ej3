# 익명 클래스 보다는 람다를 사용하라

JDK 1.1부터 함수 객체를 만드는 주요 수단은 익명 클래스가 되었다. 하지만 람다가 나오면서 낡은 기술이 되어버렸다.

```java
Collections.sort(words, new Comparator<String>() {
    public int compare(String s1, String s2) {
        return Integer.compare(s1.length(), s2.length());
    }
});
```
과거 객체 지향 패턴에는 이런 방식이 적절했다. 하지만, 익명 클래스를 사용하는 방식은 코드가 너무 길기 때문에 자바는 함수형 프로그래밍에 적합하지 않았다.

## FunctionalInterface
예전에는 자바에서 함수 타입을 표현할 때 **추상 메서드를 하나만 담은 인터페이스**를 사용했다. 자바 8 부터는 그 특별한 의미를 인정받아서 인터페이스의 인스턴스를 람다식을 사용해 만들 수 있게 되었다. 이 인터페이스를 FunctionalInterface라고 명명한다. 

익명 클래스보다 훨씬 간결하고 동작이 명확하게 드러난다.

```java
Collections.sort(words, 
    (s1, s2) -> Integer.compare(s1.length(), s2.length()));
```

여기서 람다, 매개변수(s1, s2), 반환값의 타입은 코드에 언급되지 않는다. 이것은 컴파일러가 타입 추론을 해준 것이다. 상황에 따라 컴파일러가 타입을 추론을 할 수 없는 경우도 있는데 이때는 프로그래머가 직접 명시해야한다.

**타입을 명시해야 코드가 더 명확할 때만 제외하고는, 람다의 모든 매개변수 타입은 생략하자.**

> 타입 추론에 관해 한마디 덧붙일 게 있다. ...

위 코드를 Comparator 클래스의 메서드를 활용하면 더 간결하게 만들 수 있다.
```java
Collections.sort(words, comparingInt(String::length));
```

자바 8 에서 List 인터페이스에 추가된 sort 메서드를 이용하면 더 좋다.
```java
words.sort(comparingInt(String::length));
```

## 열거 타입 람다
item32의 [Opertaion 예제](https://github.com/jbloch/effective-java-3e-source-code/blob/master/src/effectivejava/chapter6/item34/Operation.java) 도 람다를 적용하면 보다 간결하고 깔끔하게 구현할 수 있다.

```java
public enum Operation {
    PLUS  ("+", (x, y) -> x + y),
    MINUS ("-", (x, y) -> x - y),
    TIMES ("*", (x, y) -> x * y),
    DIVIDE("/", (x, y) -> x / y);

    private final String symbol;
    private final DoubleBinaryOperator op;  // Operation을 인스턴스 필드로 저장

    Operation(String symbol, DoubleBinaryOperator op) {
        this.symbol = symbol;
        this.op = op;
    }
    
    public double apply(double x, double y) {
        return op.applyAsDouble(x, y);
    }
}
```

람다 기반의 위 예제를 보면 모든곳에 람다를 적용하고 싶어질지도 모르겠다. 하지만 람다가 언제나 좋은것은 아니다.

**메서드나 클래스와는 달리, 람다는 이름이 없고 문서화도 못 한다. 따라서 코드 자체로 동작이 명확히 설명되지 않거나 코드 줄 수가 많아지면 람다를 쓰지 말아야한다.**

위 예제에서 열거 타입 생성자 안의 람다는 열거 타입의 인스턴스 멤버에 접근할 수 없다. 따라서 람다식이 길어지거나, 인스턴스 필드 혹은 메서드를 사용해야하는 경우라면 상수별 클래스 몸체를 사용해야한다.

## 추가 내용

### Comparator는 왜 Functional Interface인가?
```java
@FunctionalInterface
public interface Comparator<T> {
    int compare(T o1, T o2);
    boolean equals(Object obj);
    ...
}
```
참고: [Stackoverflow Question](https://stackoverflow.com/questions/34842208/why-functionalinterface-annotation-is-added-to-comparator-interface-in-java-8)

### `@FunctionalInterface` Annotation Target
```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FunctionalInterface {}
```
**`@FunctionalInterface` 애너테이션은 오직 추상 메서드가 하나뿐인 인터페이스에만 달 수 있다.** 애너테이션 명세에 타깃이 **ElementType.TYPE**으로 명시되어있지만 일반적인 애너테이션과는 다른 취급을 받는다. Class, Abstract class, Enum 혹은 추상 메서드가 하나 이상인 인터페이스에 `@FunctionalInterface` 애너테이션을 달 경우 컴파일 타임에 오류를 잡아낼 수 있다.

#### Java Annotation Target Scope
enum | 적용 대상
----|----
ANNOTATION_TYPE  | Annotation type declaration
CONSTRUCTOR | Constructor declaration
FIELD | Field declaration (includes enum constants)
LOCAL_VARIABLE | Local variable declaration
METHOD | Method declaration
PACKAGE | Package declaration
PARAMETER | Parameter declaration
**TYPE** | **Class, interface (including annotation type), or enum declaration**

출처: [ElementType Java Doc](https://docs.oracle.com/javase/7/docs/api/java/lang/annotation/ElementType.html)
