# 문자열 연결은 느리니 주의하라

문자열 연결 연산자(+)는 여러 문자열을 하나로 합쳐주는 편리한 수단이다.
그런데 한 줄짜리 출력값 `return prefix + str + suffix; 정도?` 
혹은 작고 크기가 고정된 객체의 문자열 표현을 만들 때라면 괜찮다.

하지만 문자열 여러개를 사용하기 시작하면 성능 저하를 피할 수 없다.

#### 문자열 연결 연산자로 문자열 n개를 연결하는 시간은 n^2에 비례한다.

문자열은 불변이기 때문에 두 문자열을 연결하는 경우에는 양쪽의 내용을 복사하여 연결한 다음 새로운 String 객체를 만들어야 하기 때문이다.

### 코드 63-1 문자열 연결을 잘못 사용한 예 - 느리다!
```java
public String statement() {
    String result = "";
    for (int i = 0; i < numItems(); i++) {
        result += lineForItem(i); //문자열 연결
    }
    return result;
}
```

### 코드 63-2 StringBuilder를 사용하면 문자열 연결 성능이 크게 개선
```java
public String statement2() {
    StringBuilder sb = new StringBuilder(numItems() * LINE_WIDTH);
    for (int i = 0; i < numItems(); i++) {
       sb.append(lineForItem(i));
    }
    return sb.toString();
}
```

### String, StringBuffer, StringBuilder의 비교
- String과 StringBuffer는 Java 1.0의 등장과 함께 같이 등장하였다.
- StringBuilder는 조금 뒤인 Java 1.5부터 등장하였다.
- String의 concat 연산은 + 기호를 사용하여 concatination을 수행한다.
- StringBuffer와 StringBuilder는 append 메서드를 통해 concatination을 수행한다.
- 정확히 말하면 StringBuffer와 StringBuilder는 `AbstractStringBuilder`를 상속하고 있으며, 결국은 같은 `append` 메서드를 사용한다.
- StringBuffer와 StringBuilder 차이점은 **thread-safe** 에 있다.
    - StringBuffer의 append 메서드에는 `syncronized` 예약어가 붙어있어 **thread-safe**하다
    - StringBuilder의 append 메서드는 **thread-safe** 하지 않다.
따라서 multi-thread 환경에서 문자열 결합을 할 때는 StringBuffer를 사용하는 것이 안전하다.
    - 단일 thread라면 StringBuilder를 사용하는 것이 StringBuffer보다 성능이 더 좋다.
(아무래도 동기화 체크를 안해도 되니 말이다.)

### String + String 연산이 느린 이유
String 클래스의 `immutable` 특성 때문이다. 즉 String 의 value 값은 한 번 생성되면 변경될 수 없습니다.

아래는 Java 8 API 문서의 String Class 설명의 일부분입니다.
>The String class represents character strings. All string literals in Java programs, such as “abc”, are implemented as instances of this class. Strings are constant; their values cannot be changed after they are created. String buffers support mutable strings. Because String objects are immutable they can be shared.

요약해보면, String 클래스는 문자열을 나타냅니다. 자바에서 “abc” 와 같은 모든 리터럴 문자열은 String의 인스턴스로 생성됩니다. 이는 상수이고 이 값은 한 번 생성되면 변경될 수 없습니다. thread safe 하기 때문에 여러 스레들이 공유하여 사용할 수 있습니다.

```java
String a = "aa";
String b = new String("bb");

a = a + b;
```
위의 두 줄의 코드가 실행되면 “aa”, “bb” 문자열에 대해 메모리가 할당되고 a, b 변수가 그 값을 각각 참조하게 됩니다.

a = a + b; 이 실행된 후에는 어떻게 될까요?

앞서 살펴본 바에 의하면 String 클래스는 immutable 하다. 값이 불변한다고 했습니다. a가 참조하고 있는 공간에 “aa” 대신에 “aabb” 라는 값으로 바꿔주는 것이 아니라 “aabb”에 대해 새로운 String 인스턴스를 생성하여 a가 참조하도록 합니다. 이전에 참조하던 “aa”는 쓰레기가 되고 나중에 가비지 컬렉션에 의해 처리됩니다. 바로 이런 이유 때문에 더 많은 시간과 메모리가 소요되는 것입니다. 연산을 많이 하면 할수록 이런 성능 차이는 더욱 심해집니다.

정말 그런지 실험을 해보도록 할까요?
```java
String a = "aa";
String b = new String("bb");

System.out.println("a address : " + a.hashCode());
System.out.println("b address : " + b.hashCode());

a = a + b;

System.out.println("a address : " + a.hashCode());
System.out.println("b address : " + b.hashCode());
```

결과는 아래와 같습니다.

```java
a address : 3104
b address : 3136
a address : 2986080
b address : 3136
```

### StringBuilder의 append()가 빠른이유

아래 코드는 `AbstractStringBuilder`의 `append()` 메서드이다.
```java
/**
    * Appends the specified string to this character sequence.
    * <p>
    * The characters of the {@code String} argument are appended, in
    * order, increasing the length of this sequence by the length of the
    * argument. If {@code str} is {@code null}, then the four
    * characters {@code "null"} are appended.
    * <p>
    * Let <i>n</i> be the length of this character sequence just prior to
    * execution of the {@code append} method. Then the character at
    * index <i>k</i> in the new character sequence is equal to the character
    * at index <i>k</i> in the old character sequence, if <i>k</i> is less
    * than <i>n</i>; otherwise, it is equal to the character at index
    * <i>k-n</i> in the argument {@code str}.
    *
    * @param   str   a string.
    * @return  a reference to this object.
*/
public AbstractStringBuilder append(String str) {
    if (str == null)
        return appendNull();
    int len = str.length();
    ensureCapacityInternal(count + len);
    str.getChars(0, len, value, count);
    count += len;
    return this;
}
```
- 새로운 String의 길이 만큼 AbstractStringBuilder의 내의 byte[]의 사이즈를 늘리고 복사한다.
- 그 다음 String에 대한 byte[]를 AbstractStringBuilder의 내의 byte[]에 추가한다.
- String + String 연산과의 차이점은 불필요한 String 객체가 발생하지 않는다는 점이다.

### String Concatination의 발전
- Java String 연산에 대한 성능최적화를 다방면으로 생각하고 있고, 
Java 9 부터 String의 내부 배열을 `char[] -> byte[]`로 변경하여 성능을 더 향상 시켰다.
- Java 1.5 버전부터 String + String연산에 대해 Compile Time에 StringBuilder를 사용하도록 코드를 변경한다.
하지만 JDK가 항상 자동으로 바꿔준다는 보장이 없으니 String + String 보다는 StringBuilder를 사용해야 한다.
