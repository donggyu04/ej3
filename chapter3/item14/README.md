# Comparable을 구현할지 고려하라
Comparable인 터페이스의 compareTo에 대해서 알아보자. compareTo는 동치성 비교에 더해 순서까지 비교할수 있으며 제너릭한다. Comparable을 구현했다는 것은 **자연적인 순서**가 있음을 뜻한다.

## CompareTo의 규약
1. 객체보다 작으면 => 음의 정수 
   같으면 0
   크면 양의정수
2. 비교할수 없는 타입객체시 ClassCastException을 던진다
3. 대칭성을 보장하여야한다.
   **sgn(x.compareTo(y)) == -sgn(y.compareTo(x))**
4. 추이성을 보장하여야한다.
   **x.compareTo(y) > 0** 이고, **y.compareTo(z) > 0**일 때 **x.compareTo(z) > 0**
5. 명시성을 보장하여야한다.(필수는 아니지만 권고사항 하지만 지키는게 좋다)
   **(x.compareTo(y) == 0) == (x.equals(y))**


명시성 보장이 되지 않았을때 헷갈릴 수 있는 문제
```java
@Test
void test() {
    final var one1 = new BigDecimal("1.0");
    final var one2 = new BigDecimal("1.00");

    // equals와 compareTo의 결과가 다르다.
    assertNotEquals(one2, one1);
    assertEquals(0, one1.compareTo(one2));

    final HashSet<BigDecimal> hashSet = new HashSet<>();
    hashSet.add(one1);
    hashSet.add(one2);

    final TreeSet<BigDecimal> treeSet = new TreeSet<>();
    treeSet.add(one1);
    treeSet.add(one2);

    assertEquals(2, hashSet.size());
    assertEquals(1, treeSet.size());
}
HashSet은 equals
TreeSet은 compareTo를 통해 비교
```
## CompareTo 작성요령
기본작성요령은 equlas와 비슷하다.
1. 제네릭이므로 인자타입을 확인하거나 형변환이 필요없다.
2. 동치인지가 아니라 순서를 비교한다.
3. 참조필드 비교 시 compareTo를 재귀적으로 호출한다.
4. Comparable을 구현하지 않은 필드나 표준이 아닌 순서 비교시 비교자(Comparator)를 대신 사용한다.
    1.  Comparable은 기본 정렬기준을 제시 sort() 활용할수 있도록
    2.  Compartor는 기본 정렬기준외에 다른 정렬기준으로 정렬 시 사용
5.  기본 정수타입 비교시 관계 연산자(>, <) 보다는 정적메소드 compare를 사용하라
6.  핵심적인 필드부터 비교후, 결과가 도출된다면 바로 return하라.

## Comparator 편의성 메소드
약간 성능저하가 발생하지만 편의성을 제공하는 메소드들이 있다.
```java
public class ComparableTest implements Comparable<Type> {
    private int number;
    private int number2;
    private int number3;

    @Override
    public int compareTo(final Type t) {
        // 자바의 타입 추론의 한계 때문에 처음에는 타입을 명시해줘야한다.
        // 이렇게 해주는 것만으로 Comparator를 구현한 것이고 이 구현체를 통해 순서를 비교할 수 있다.
        return Comparator.comparingInt((Type t2) -> t2.number)
                         .thenComparingInt(t2 -> t2.number2)
                         .thenComparingInt(t2 -> t2.number3)
                         .compare(this, t);
    }
}
```


---
# 요약
1. 순서를 고려하는 클래스라면 Comparable 인터페이스를 구현하라
2. compareTo 필드 비교시 >,< 보다는 compareTo를 사용하라
3. Compare 메소드 구현시 Comparator가 편의성을 제공하는 메소드를 사용하라


### 부록
[compareToBuilder를 쓰자](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/builder/CompareToBuilder.html)
[Comparing](https://www.baeldung.com/java-8-comparator-comparing)

### 참고자료
https://perfectacle.github.io/2018/12/16/effective-java-ch03-item13-clone-method/
https://sjh836.tistory.com/169