# 불필요한 객체 생성을 피하라

객체 생성의 비용은 비교적 적은편이지만, 같은 기능을 하는 객체를 매번 생성하는것 보다는 객체 하나를 재사용하는것이 당연히 효율적이다.  
재사용은 빠르고 세련되다. 특히 불변 객체는 언제든 재사용할 수 있다.
> 아주 무거운 객체가 아니라면 객체 풀을 만들지는 말자. DB connection 같은 경우는 생성 비용이 매우 크기 때문에 풀을 사용하는것이 적합하지만, 일반적인 경우 자체적인 객체 풀은 코드를 헷갈리에 만들고 메모리 사용량을 늘리고 성능을 떨어트린다.

### 객체 재사용
객체의 재사용 측면에서 아래 코드는 최악의 코드이다.
```java
String s = new String("platform");
```

위 코드가 실행될 때마다 String 인스턴스가 새로 만들어진다. 위에서 생성하는 String 객체는 인자로 전달하는 `"platform"`와 기능적으로 완전히 동일하다.

```java
String s = "platform";
```

이 코드는 매번 새로운 인스턴스를 생성하는 대신에 하나의 String 인스턴스를 사용한다. 게다가 같은 가상 머신 안에서 위와 동일한 문자열 리터럴을 사용하는 모든 코드가 같은 객체를 재사용함이 보장된다. ([JLS, 3.10.5](https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.5))

> new String() 생성자로 생성한 문자열도 동일한 문자 리터럴을 사용할 수 있도록 intern() 메서드를 사용할 수 있다. 하지만 생성자를 이용해서 문자열 인스턴스를 생성하는 일은 없어야겠다.
> ```java
> @Test
>    public void testStringEquals() {
>        String platform = "platform";
>
>        assertNotSame(platform, new String("platform"));
>        assertSame(platform, new String("platform").intern());
>    }
> ```

### 정적 팩터리 메서드
생성자 대신 정적 팩터리 메서드를 제공하는 클래스에서는 정적 팩터리 메서드를 이용해서 불필요한 객체 생성을 피할 수 있다. Boolean 값을 생성하는 아래 코드가 그 예시이다.
```java
public final class Boolean implements java.io.Serializable, Comparable<Boolean> {
    ...
    @Deprecated(since="9")
    public Boolean(String s) {
        this(parseBoolean(s));
    }

    public static Boolean valueOf(String s) {
        return parseBoolean(s) ? TRUE : FALSE;
    }
    ...
}
```
Boolean.valueOf() 메서드는 불필요한 객체 생성을 하지 않고 재사용한다. Boolean 클래스의 문자열을 매개변수로 받는 생성자는 JDK 9 부터 deprecated 되었다.

### 캐싱
생성 비용이 아주 비싼 객체도 있다. 이런 비싼 객체가 반복해서 필요하다면 캐싱하여 재사용하는것이 좋다.
```java
boolean isRomanNumeral(String s) {
    return s.matches("^(?=[MDCLXVI])M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$")
}
```
**String.matches는 정규 표현식을 이용해서 문자열 형태를 확인하는 가장 쉬운 방법이지만, 성능이 중요한 상황에서 반복적으로 사용하기엔 적합하지 않다.** 이 메서드에서 내부적으로 사용하는 Pattern 인스턴스는 생성 비용이 큰 데다가, 한 번 쓰고 버려져서 바로 가비지 컬렉션 대상이 된다. Pattern을 인스턴스화하여 캐싱해두고 위 메서드 호출시에 사용하는것이 더 좋은 성능을 보인다.

```java
@Test
public void testRomanNumeralsWithoutCaching() {
    Function<String, Boolean> isRomanNumerals = s -> s.matches("^(?=[MDCLXVI])M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
    long startTime = System.nanoTime();
    assertTrue(isRomanNumerals.apply("LXXXVIII"));
    long endTime = System.nanoTime();

    System.out.println(endTime - startTime);    // 0.17ms
}

@Test
public void testRomanNumeralsWithCaching() {
    final Pattern ROMAN = Pattern.compile("^(?=[MDCLXVI])M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
    Function<String, Boolean> isRomanNumerals = s -> ROMAN.matcher(s).matches();
    
    long startTime = System.nanoTime();
    assertTrue(isRomanNumerals.apply("LXXXVIII"));
    long endTime = System.nanoTime();

    System.out.println(endTime - startTime);    // 0.07ms
}
```

### 오토박싱
**오토박싱은 기본 타입과 그에 대응하는 박싱된 기본 타입의 구분을 흐려주지만, 완전히 없애주는 것은 아니다.** 비슷하다고해서 박싱된 기본 타입과 기본 타입을 섞어서 사용하면 성능 저하를 불러올 수 있다.
```java
@Test
public void testSumOfInteger() {
    long sum = 0L;      // primitive long type
    
    long startTime = System.nanoTime();
    for (int i = 0; i < Integer.MAX_VALUE; i++) {
        sum += i;
    }
    long endTime = System.nanoTime();

    System.out.println(endTime - startTime);    // 777.89 ms
}

@Test
public void testSumOfIntegerWithAutoBoxing() {
    Long sum = 0L;      // wrapped primitive long type

    long startTime = System.nanoTime();
    for (int i = 0; i < Integer.MAX_VALUE; i++) {
        sum += i;
    }
    long endTime = System.nanoTime();

    System.out.println(endTime - startTime);    // 6671.6 ms
}
```
**박싱된 기본 타입보다는 기본 타입을 사용하고, 의도치 않은 오토박싱이 없도록 주의해야한다.** 

### 개인적인 의견
* 객체 재사용 측면에서 정적 팩터리 메서드의 이점을 잘 알고 사용해야겠습니다. 이전까지는 `of()` 메서드들이 간결한 표현을 위함이라고 생각했는데 경우에 따라서 객체 재사용을 적용할 수 있습니다.

## Reference
https://blog.javarouka.me/2018/11/25/%EB%B6%88%ED%95%84%EC%9A%94%ED%95%9C-%EA%B0%9D%EC%B2%B4%EC%83%9D%EC%84%B1-%ED%9A%8C%ED%94%BC/