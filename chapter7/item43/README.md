# 람다보다는 메소드 참조를 사용하라

함수 객체를 람다보다도 더 간결하는 만드는 방법이 바로 메서드 참조

### 메소드 참조

우리는 구현해야 하는 메소드의 본문을 람다 표현식으로 (익명클래스로 생성되도록 하는) 대체할 수 있다는 것을 알았습니다.
하지만, 이미 우리가 구현하고자 하는 람다식 자체가 구현되어있는 경우가 있습니다.
이럴때 사용하는 메서드 참조용 특수 문법이 있는데, 이 방식을 메소드 참조라고 표현합니다. 

```java
String [] strings = new String [] {
    "6", "5", "4", "3", "2", "1"
};

List<String> list = Arrays.asList(strings);

for(String s : strings) {
    System.out.println(s);
}
````

리스트를 순회하며 요소를 출력하는 단순한 예제 코드 입니다.

하지만 반복적인 for문의 사용과 iterator는 굉장히 지치게 하죠.

```java
    /**
     * Performs the given action for each element of the {@code Iterable}
     * until all elements have been processed or the action throws an
     * exception.  Unless otherwise specified by the implementing class,
     * actions are performed in the order of iteration (if an iteration order
     * is specified).  Exceptions thrown by the action are relayed to the
     * caller.
     *
     * @implSpec
     * <p>The default implementation behaves as if:
     * <pre>{@code
     *     for (T t : this)
     *         action.accept(t);
     * }</pre>
     *
     * @param action The action to be performed for each element
     * @throws NullPointerException if the specified action is null
     * @since 1.8
     */
    default void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        for (T t : this) {
            action.accept(t);
        }
    }
```

그래서 자바8의 ArrayList 클래스에는 각 요소에 함수를 적용하는 forEach 메소드를 지원합니다.

코드를 조금 수정해 볼까요 ? 

```java
String [] strings = new String [] {
    "6", "5", "4", "3", "2", "1"
};

List<String> list = Arrays.asList(strings);

list.forEach(x -> System.out.println(x));
```

이런식으로 간결해 지는것을 볼 수 있습니다.
하지만, 결과적으로 따지고 본다면, forEach메소드는 매개변수로 Consumer interface를 전달받죠. 

물론 우리는 람다식을 이용하여, Consumer가 가지고있는 accept 메소드 구현체를 직접 전달함으로써 해결하고 있지만
지금 우리가 전달함 람다식의 내용은 list의 요소를 입력받아, 단순히 println메소드에 전달해주는 역할만 하고 있게 되는것이지요.

즉, Consumer가 구현해야 되는 accept메소드가 실행될때 println메소드를 한번더 실행해주는 형태라고 보시면 될 것 같습니다.
그렇다면 결국 메소드의 call stack이 1depth 깊어진 결과라고 보여지는데요,
그냥 System클래스가 가진 println메소드를 forEach에게 전달할 순 없을까요?

```java
String [] strings = new String [] {
    "6", "5", "4", "3", "2", "1"
};

List<String> list = Arrays.asList(strings);

list.forEach(System.out::println);
```

이렇게 표현해 주는 것만으로, forEach에게 println을 전달해 주게 되는것이지요.
이러한 예에서 볼 수 있듯이 :: (double colon) 연산자는 메소드 이름과 클래스를 분리하거나, 
메소드 이름과 객체의 이름을 분리합니다.
이는 다음과 같이 세 가지 형태로 사용할 수 있습니다.

1. 클래스::인스턴스메소드 (public)
``` String::compareToIgnoreCase는 (x, y) -> x.compareToIgnoreCase(y) ```

2. 클래스::정적메소드 (static)
``` Object::isNull은 x -> Object.isNull(x) ```

3. 객체::인스턴스메소드 (new)
``` System.out::println은 x -> System.out.println(x) ```

### 람다보다는 메소드 참조

```java
// 람다식
map.merge(key, 1, (count, incr) -> count + incr);

// 메서드 참조식
map.merge(key, 1, Integer::sum);
```

메서드 참조를 사용하는 편이 보통은 더 짧고 간결하므로, 
람다로 구현했을 때 너무 길거나 복잡하다면 메서드 참조가 좋은 대안이다.

때로는 람다가 메서드 참조보다 간결할 때도 있는데 
주로 메서드와 람다가 같은 클래스에 있는 경우이다.

```java
service.execute(GoshThisClassNameIsHumongous::action);

service.execute(() -> action());
```

| 메서드 참조 유형 |	예	 | 같은 기능을 하는 람다 |
|---|---|---|
| 정적 | Integer::parseInt	|  str -> Integer.parseInt(str) | 
| 한정적(인스턴스) | 	Instant.now()::isAfter	Instant then = Instant.now(); t -> then.isAfter(t)
| 비한정적(인스턴스) | 	String::toLowerCase	 | str -> str.toLowerCase() | 
| 클래스  | 생성자	TreeMap<K,V>::new	 | () -> new TreeMap<K,V>() | 
| 배열 생성자 | 	int[]::new | 	len -> new Int[len] | 

메서드 참조는 람다의 간단 명료한 대안이 될수 있다.
메서드 참조쪽이 명확하다면 메서드 참조를 쓰고, 그렇지 않다면 람다를 사용하자.
