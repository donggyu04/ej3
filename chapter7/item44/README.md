# 표준 함수형 인터페이스를 사용하라

람다를 지원하면서 코드 모범사례도 크게 바뀌었다.
상위클래스의 기본 클래스를 재정의 하여 원하는 동작을 하게 만드는 템플릿 메서드 패턴의 매력이 크게 줄어
템플릿 메소드 패턴의 매력이 감소하는 대신 함수 객체를 받아 제공하는 것이 떠오르고 있다.

다만...
필요한 용도에 맞는게 있다면 직접 구현하지 말고, 
표준 함수형 인터페이스(java.util.function)를 사용하길 바랍니다.
그럼 함수형 인터페이스란 무엇인지 알아보겠습니다.

### 함수형 인터페이스(Functional Interface)

이번 시간에는 java8에서 기본적으로 제공해주는 함수형 인터페이스(Functional Interface)들을 알아보겠습니다.

| 인터페이스명 | 메소드명  | 설명  | 
|---|---|---|
| Runnable | void run() | 실행할 수 있는 인터페이스 | 
| Supplier | T get() | 제공할 수 있는 인터페이스 | 
| Consumer | void accept(T t) | 소비할 수 있는 인터페이스 | 
| Function<T, R> | R apply (T t) | 입력을 받아서 출력할 수 있는 인터페이스 | 
| Predicate | Boolean test(T t | )입력을 받아 참, 거짓을 판단할 수 있는 인터페이스 | 
| UnaryOperator | T apply(T t) | 단항 연산할 수 있는 인터페이스 | 

단항은 Unary, 이항 Binary 이러한 식으로
(기본타입별 LongToIntFunction, LongBinaryOperator, BooleanSupplier 등)
위 표를 보면 43가지는 충분히 유츄가 가능합니다.

표준 함수형 인터페이스 대부분은 기본타입만 지원합니다. 
(LongToIntFunction.applyAsInt는 long 인수를 받고 int를 반환)


1) Runnable

기존부터 존재하던 인터페이스로 스레드를 생성할때 주로사용하였으며 가장 기본적인 함수형 인터페이스다. 
void 타입의 인자없는 메서드를 갖고있다.

```java
Runnable r = () -> System.out.println("hello functional");
r.run();
```

2) Supplier<T>

인자는 받지않으며 리턴타입만 존재하는 메서드를 갖고있다. 
순수함수에서 결과를 바꾸는건 오직 인풋(input) 뿐이다. 
그런데 input이 없다는건 내부에서 랜덤함수같은것을 쓰는게 아닌이상 항상 같은 것을 리턴하는 메서드라는걸 알 수 있습니다.

```java
Supplier<String> s = () -> "hello supplier";
String result = s.get();
```

3) Consumer<T>

리턴을 하지않고(void), 인자를 받는 메서드를 갖고있다. 
인자를 받아 소모한다는 뜻으로 인터페이스 명칭을 이해하면 될듯 합니다.

```java
Consumer<String> c = str -> System.out.println(str);
c.accept("hello consumer");
```

4) Function<T, R>

인터페이스 명칭에서부터 알 수 있듯이 전형적인 함수를 지원한다고 보면 된다. 하나의 인자와 리턴타입을 가지며 그걸 제네릭으로 지정해줄수있다. 그래서 타입파라미터(Type Parameter)가 2개다.

```java
Function<String, Integer> f = str -> Integer.parseInt(str);
Integer result = f.apply("1");
```

5) Predicate<T>

하나의 인자와 리턴타입을 가진다. Function과 비슷해보이지만 리턴타입을 지정하는 타입파라미터가 안보인다. 반환타입은 boolean 타입으로 고정되어있다. Function<T, Boolean>형태라고 보면된다.

```java
Predicate<String> p = str -> str.isEmpty();
boolean result = p.test("hello");
```

6) UnaryOperator<T>

하나의 인자와 리턴타입을 가진다. 
그런데 제네릭의 타입 파라미터가 1개인걸 보면 감이 오는가? 
인자와 리턴타입의 타입이 같아야한다.

```java
UnaryOperator<String> u = str -> str + " operator";
String result = u.apply("hello unary");
```

7) BinaryOperator<T>

동일한 타입의 인자 2개와 인자와 같은 타입의 리턴타입을 가진다.

```java
BinaryOperator<String> b = (str1, str2) -> str1 + " " + str2;
String result = b.apply("hello", "binary");
```

8) BiPredicate<T, U>

서로 다른 타입의 2개의 인자를 받아 boolean 타입으로 반환한다.

```java
BiPredicate<String, Integer> bp = (str, num) -> str.equals(Integer.toString(num));
boolean result = bp.test("1", 1);
```

9) BiConsumer<T, U>

서로 다른 타입의 2개의 인자를 받아 소모(void)한다.

```java
BiConsumer<String, Integer> bc = (str, num) -> System.out.println(str + " :: " + num);
bc.accept("숫자", 5);
```

10) BiFunction<T, U, R>

서로 다른 타입의 2개의 인자를 받아 또 다른 타입으로 반환한다.

```java
BiFunction<Integer, String, String> bf = (num, str) -> String.valueOf(num) + str;
String result = bf.apply(5, "678");
```

11) Comparator<T>

자바의 전통적인 인터페이스 중 하나이다. 객체간 우선순위를 비교할때 사용하는 인터페이스인데 전통적으로 1회성 구현을 많이 하는 인터페이스이다. 람다의 등장으로 Comparator의 구현이 매우 간결해져 Comparable 인터페이스의 실효성이 많이 떨어진듯 하다.

```java
Comparator<String> c = (str1, str2) -> str1.compareTo(str2);
int result = c.compare("aaa", "bbb");
```




참고 : 
https://sjh836.tistory.com/173 [빨간색코딩]
https://multifrontgarden.tistory.com/125 [우리집앞마당]


