# 박싱된 기본 타입 보다는 기본 타입을 사용하라

## 기본 타입과 박싱된 기본 타입의 차이
1. 기본 타입은 값만 가지고 있으나, 박싱된 기본 타입은 식별성(identity)을 추가로 갖는다.
2. 기본 타입의 값은 언제나 유효하나, 박싱된 기본 타입은 유효하지 않은 값, 즉 null을 가질 수 있다.
3. 기본 타입이 박싱된 기본 타입보다 시간과 메모리 사용면에서 더 효율적이다.

## 예시
**Compartor 문제를 찾아보자**
```java
Comparator<Integer> naturalOrder = (i, j) -> (i < j) ? -1 : (i = j ? 0 : 1);
```

**수정한 Comparator**
```java
Comparator<Integer naturalOrder = (iBoxed, jBoxed) -> {
    int i = iBoxed, j = jBoxed; // 오토박싱
    return i < j ? -1 : (i = j ? 0 : 1);
}
```

**결과를 맞춰보자**
```java
Integer i;

public void test() {
    if (i == 42) {
        System.out.println("Hello");
    }
}
```

**매우 느림**
```java
Long sum = 0L;
for (long i = 0; i < Integer.MAX_VALUE; i++) {
    sum += i;
}
System.out.println(sum);
```

## 그러면 박싱된 기본 타입은 언제쓰라구요?
박싱된 기본 타입을 어쩔 수 없이 써야하는 경우는 타입 매개변수를 사용하는 경우이다. 자바 언어는 타입 매개변수로 기본 타입을 쓸 수 없다. 예를 들어, `Collection<int>` 혹은 `int.class` 는 사용할 수 없다.

## 개인적 의견
기본 타입과 박싱된 기본 타입을 분류하는 정책을 사용하는것은 결국 Generic 때문입니다. JDK 1.5에서 Type Erasure를 도입하고, 박싱된 기본 타입을 추가한 당시의 결정은 분명히 합리적이었지만 지금은 매우 큰 아쉬움으로 남습니다. (아직까지 이 문제에 대한 의견은 분분합니다.)

기본 타입이 성능과 메모리 측면에서 좋긴 하지만, 이는 컴파일러 단에서 해결할 수 있는 문제입니다. 모든 기본 타입을 박싱된 기본 타입으로 사용하고, 컴파일시에 적절하게 지금의 기본 타입처럼 변환해준다면 훨씬 간단한 타입 체계를 갖게되고, 간편하게 타입 매개변수를 사용할 수 있습니다. 

지금의 우리는 기본 타입과 박싱된 기본 타입에 대해서 모두 알고있기 때문에 자연스럽게 받아들여지지만, 동일한 기본 자료형을 표현하기 위한 키워드가 한 쌍씩 존재하는 셈이니 다소 이상한 형태입니다.

### Scala
Scala에서는 기본 타입과 박싱된 기본 타입에 대한 분류가 없습니다. 수 타입(numeric type)들은 스칼라 컴파일러에 의해 값 타입의 인스턴스를 바이트코드화할 때 자유롭게 자바의 기본 타입으로 변환됩니다.

하지만, Scala 역시 Java와 동일하게 Type Erasure를 갖기때문에 런타임시에 generic 정보는 모두 지워집니다. Scala에서는 TypeTag를 제공해서 런타임에 지워진 타입 정보를 얻어올 수 있습니다.

```scala
import scala.reflect.runtime.universe._
scala> typeTag[List[Int]].tpe
res9: reflect.runtime.universe.Type = scala.List[Int]
```

## Reference
https://stackoverflow.com/questions/27647407/why-do-we-use-autoboxing-and-unboxing-in-java

https://stackoverflow.com/questions/2504959/why-can-java-collections-not-directly-store-primitives-types

https://blog.knoldus.com/type-erasure-in-scala/