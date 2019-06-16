# 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라

### 대부분의 클래스는 하나 이상의 자원에 의존한다

```java
public class SpellChecker {
    private static final Lexicon dictionary = ...;

    private SpellChecker() {}   // Prevents intanciation

    public static boolean isValid(String word) { ... }
    public static List<String> suggestions(String typo) { ... }
}
```
* dictionary가 유일하므로 유연하지 못하다. 
* static 메서드로 구성된 유틸 클래스에 static final로 선언된 dictionary.

### 다양한 사전을 사용할 수 있는 유연한 구조 적용
```java
public class SpellChecker {
    private static Lexicon dictionary = ...;

    private SpellChecker() {}   // Prevents intanciation

    public static boolean isValid(String word) { ... }
    public static List<String> suggestions(String typo) { ... }

    public static void setDictionary(Lexicon dictionary) {
        SpellChecker.dictionary = dictionary;
    }
}
```

* 이해하기 어렵고 오류가 발생하기 쉽다.
* 멀티 스레드 환경에서 사용할 수 없다. 
* **사용하는 자원에 따라서 동작이 달라지는 클래스는 static 유틸 클래스나 싱글턴 방식이 적합하지 않다.**

### 인스턴스를 생성할 때 생성자에 필요한 자원을 넘겨주는 패턴

```java
public class SpellChecker {
    private final Lexicon dictionary;

    public SpellChecker(Lexicon dictionary) {
        this.dictionary = dictionary;
    }

    public boolean isValid(String word) { ... }
    public List<String> suggestions(String typo) { ... }
}
```

* 의존 객체 주입 패턴은 아주 단순하지만 유연성과 테스트 용이성을 개선한다. 
* 의존성이 너무 많아질 경우 코드가 복잡해진다.  
 => 이 때는 Dagger, Guice, Spring 같은 의존 객체 주입 프레임워크를 사용하면 복잡함을 해소할 수 있다.

### 팩토리 메서드 패턴
객체 주입 패턴의 변형으로 팩토리 메서드 패턴이 있습니다. 자바 8에서 추가된 Supplier<T> 메서드가 펙토리 메서드 패턴의 예시입니다.

```java
package java.util.function;

@FunctionalInterface
public interface Supplier<T> {
    T get();
}
```

**Consumer와 Supplier를 활용한 GenericBuilder**
```java
public class GenericBuilder<T> {
    private final Supplier<T> supplier;
    private List<Consumer<T>> setters = new ArrayList<>();

    private GenericBuilder(Supplier<T> supplier) {
        this.supplier = supplier;
    }
    
    public static <T> GenericBuilder<T> of(Supplier<T> instance) {
        return new GenericBuilder<>(instance);
    }
    
    public <U> GenericBuilder<T> with(BiConsumer<T, U> consumer, U value) {
        setters.add(setter -> consumer.accept(setter, value));
        return this;
    }
    
    public T build() {
        T instance = supplier.get();
        setters.forEach(s -> s.accept(instance));
        return instance;
    }
}
```

## Reference
https://01010011.blog/2016/12/29/java8-consumer-supplier-%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-builder-pattern-%EA%B5%AC%ED%98%84/