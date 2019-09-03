# 69. 예외는 진짜 예외 상황에만 사용하라.

```java
try {
  int i = 0;
  while(true) {
    range[i++].climb();
  }
} catch (ArrayIndexOutOfBooundsException e) { }
```

=> 예외는 오직 예외상황에서만 써야한다. 절대적으로 일상적인 제어 흐름용으로 쓰여서는 안된다.

```java
for(Iterator<Foo> i = collection.iterator(); i.hasNext();) {
  Foo foo = i.next();
}

try {
  Iterator<Foo> i = collection.iterator();
  while(true) {
    Foo foo = i.next();
  }
} catch( NoSuchElementException e) {
}
```

잘 설계된 API라면 클라이언트가 정상적인 제어흐름에서 예외를 사용할 일이 없게 해야된다.
=> 위의 경우처럼 '상태 의존적' 메소드를 제공하는 클래스는 '상태 검사' 메서드도 함께 제공해야 한다.

Iterator 인터페이스의 next와 hasNext가 각각 상태 의존적 메소드와 상태 검사 메소드에 해당된다.


요약
1. 예외는 예외상황에 쓸의도로 설계 되었다.
2. 정상적인 제어흐름에서 사용해서는 안되며, API 에서 이를 강요하게 해서도 안된다.