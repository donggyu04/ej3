# 스레드 안정성 수준을 문서화하라
멀티스레드 환경에서도 API를 안전하게 사용하게 하려면 클래스가 지원하는 스레드 안정성 수준을 정확히 명시해야 한다.  
ex) [ObjectMapper javadoc](https://fasterxml.github.io/jackson-databind/javadoc/2.7/com/fasterxml/jackson/databind/ObjectMapper.html)

## 스레드 안정성 수준
* 불변(immutable) : 이 클래스의 인스턴스는 상수와 같아서 외부 동기화도 필요없다.
* 무조건적 스레드 안전(unconditionally thread-safe): 이 클래스의 인스턴스는 수정될 수 있으나, 내부에서 충실히 동기화하여 별도의 외부 동기화 없이 동시에 사용해도 안전하다.
* 조건부 스레드 안전(conditionally thread-safe): 일부 메서드는 동시에 사용하려면 외부에서 동기화 해야한다.
* 스레드 안전하지 않음(not thread-safe): 동시에 사용하려면 각각의 메서드 호출을 클라이언트가 외부 동기화 메커니즘으로 감싸야 한다.
* 스레드 적대적(thread-hostile): 이 클래스는 모든 메서드 호출을 외부 동기화로 감싸더라도 멀티스레드 환경에서 안전하지 않다.  
  [item78. generateSerialNumber](https://github.com/donggyu04/ej3/tree/master/chapter11/item78)에서 내부 동기화를 수행하지 않으면 스레드 적대적이다.

위 기준들은 [자바 병렬 프로그래밍](http://www.yes24.com/Product/goods/3015162)에 소개된 `@Immutable`, `@ThreadSafe` 그리고 `@NotThreadSafe` 어노테이션과 대략 일치한다.

조건부 스레드 안전의 경우 어떤 순서로 호출할 때 동기화가 필요한지 어떤 락을 얻어야하는지 설명해주어야 한다.
[Collections.synchronizedMap](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html)


>synchronizedMap이 반환한 맵의 컬렉션 뷰를 순회하려면 반드시 그 맵을 락으로 사용해 수동으로 동기화하라.
>```java
>Map<K, V> m = Collections.synchronizedMap(new HashMap<>());
>Set<K> s = m.keySet();
>...
>synchronized(m) {
>    for (K key : s) 
>        key.f();
>}
>```
>이대로 따르지 않으면 동작을 예측할 수 없다.

## 무조건적 스레드 안전을 위해서는 비공개 락을 사용하자
클래스가 외부에서 사용할 수 있는 락을 제공하면?
* 클라이언트에서 직접 동기화 가능
* 고성능 동시성 제어 메커니즘과 혼용 불가능
* 장시간 락을 쥐고 놓지 않는 DoS 공격에 노출

공개된 락 대신 비공개 락을 사용해라 (synchronized 키워드도 공개 락과 마찬가지이다.)

**비공개 락 객체 관용구 - DoS를 방지**
```java
private final Object lock = new Object();   // 반드시 final로 선언! 실수로라도 락이 변경된다면 알 수 없는 결과를 초래할 것이다.

public void foo() {
    synchronized(lock) {
        ...
    }
}
```
외부에서 객체의 동기화에 관여할 수 없다. 락 객체를 동기화 객체 안으로 캡슐화한 것이다.
