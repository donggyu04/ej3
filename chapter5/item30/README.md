## 이왕이면 제네릭 메서드로 만들라.

### static utility 메서드는 보통 제네릭의 좋은 후보군이다.
  - `java.util.Collections`의 알고리즘 메서드는 모두 제네릭이다.
  - (타입 매개 변수들을 선언하는) 타입 매개변수 목록은 메서드의 제한자와 반환 타입 사이에 온다.
     아래 예를 통해 확인해보자.
```java
// Uses raw types
public static Set union(Set s1, Set s2) {
   Set result = new HashSet(s1);
   result.addAll(s2);
   return result;
}

// 제네릭 메서드
public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
   Set<E> result = new HashSet<E>(s1); 
   result.addAll(s2);
   return result;
}
```
 - 컴파일 시에 warning 메시지가 없으며 type safe하고 쓰기도 쉽다.
 
### 제네릭 싱글톤 팩토리 패턴
 - 불변 객체를 여러 타입으로 활용할 수 있게하기 위한 패턴.
 - 제네릭은 런타임시에 타입 정보가 소거되므로 하나의 객체를 어떤 타입으로든 매개변수화 할 수 있다.
   요청한 타입 매개 변수에 맞게 매번 그 객체의 타입을 바꿔주는 정적 팩토리를 만든다.
 - `java.util.Collections.emptySet()`, `java.util.Collections.reverseOrder()`가 그 예이다.
```java
public class GenericSingletonFactory {
    //제 네릭 싱글턴 팩터리 패턴
    private static UnaryOperator<Object> IDENTITY_FN = (t) -> t;

    @SuppressWarnings("unchecked")
    public static <T> UnaryOperator<T> identityFunction() {
        return (UnaryOperator<T>) IDENTITY_FN;
    }

    public static void main(String[] args) {
        String[] strings = { "삼베", "대마", "나일론" };
        UnaryOperator<String> sameString = identityFunction();
        for (String s : strings)
            System.out.println(sameString.apply(s));

        Number[] numbers = { 1, 2.0, 3L };
        UnaryOperator<Number> sameNumber = identityFunction();
        for (Number n : numbers)
            System.out.println(sameNumber.apply(n));
    }
}
```

### 재귀적 타입 한정(Recursive Type Bound)
 - 자기 자신이 들어간 표현식을 사용하여 타입 매개변수의 허용 범위를 한정할 수 있다.
 - 주로 타입의 자연적 순서를 정하는 `Comparable` 인터페이스와 함께 쓰인다.
```java
public interface Comparable<T> {
    int compareTo(T o);
}

public class RecursiveTypeBound {
    //재귀적 타입 한정 사용
    // <E extends Comparable<E>>는 모든 타입 E는 자신과 비교할 수 있다.라는 뜻
    public static <E extends Comparable<E>> E max(Collection<E> c) {
        if (c.isEmpty())
            throw new IllegalArgumentException("컬렉션이 비어 있습니다.");

        E result = null;
        for (E e : c)
            if (result == null || e.compareTo(result) > 0)
                result = Objects.requireNonNull(e);

        return result;
    }

    public static void main(String[] args) {
        List<String> argList = Arrays.asList(args);
        System.out.println(max(argList));
    }
}
```