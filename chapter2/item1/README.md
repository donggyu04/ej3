# 1. 생성자 대신 정적 팩터리 메서드를 고려하자.

일반적인 public 생성자 대신 **정적 팩토리 메서드**를 사용할 때의 장. 단점을 비교해 보자.

```java
// 정적 팩토리 메서드 예
PageRequest.of(pageNumber, pageSize);
BigInteger.valueOf(Integer.MAX_VALUE);
Array.newInstance();
```

- 장점

   1. 이름을 가질 수 있다.
  2. 호출될 때마다 인스턴스를 새로 생성하지 않아도 된다.
  3. 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.
  4. 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.
  5. 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다. (Service Provider 프레임워크의 근간이 된다) 

- 단점

  1. 상속을 하려면 public이나 protected 생성자가 필요하니 정적 팰터리 메서드만 제공하면 하위 클래스를 만들 수 없다.
  2. 정적 팩터리 메서드는 프로그래머가 찾기 어렵다.

> 정적 팩터리 메서드와 public 생성자는 각자의 쓰임새가 있으니 상대적인 장단점을 이해하고 사용하는 것이 좋다. 그렇다고 하더라도 정적 팩터리를 사용하는게 유리한 경우가 더 많으므로 무작정 public 생성자를 재공하던 습관이 있다면 고치자.

> 같은 주제 블로그: https://johngrib.github.io/wiki/static-factory-method-pattern/ https://jaehun2841.github.io/2019/01/06/effective-java-item1/