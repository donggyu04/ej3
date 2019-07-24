## 적시에 방어적 복사본을 만들라

### 자바는 안전하다?
C,C++ 같이 안전하지 않은 언어에서 흔히 보는 버퍼 오버런, 배열 오버런, 와일드 포인터 같은 메모리 충돌 오류에서 안전하다. 하지만 자바라 해도 다른 클래스로 부터의 침범을 아무런 노력없이 다 막을수 있는건 아니다.

=> 클라이언트가 불변식을 꺠드리려 혈안이 되어있다고 가정하고 방어적 프로그래밍을 해야한다.


### 불변식을 지키지 못하는 클래스

```java
class Period {
    private final Date start;
    private final Date end;

    public Period(Date start, Date end) {
        if(start.compareTo(end) > 0) {
            throw new IllegalArgumentException(start + " after " + end);
        }
        this.start = start;
        this.end = end;
    }
    public Date start() { return start; }
    public Date end() { return end; }
 
}
```


```java
Date start = new Date();
Date end = new Date();
Period period = new Period(start, end);
end.setYear(78); // p의 내부를 수정했다!
```
※ Java8 이상일 경우 Date 대신 불변인 Instant를 사용하면된다( 혹은 LocalDateTime 이나 ZonedDateTime을 사용해도 된다.)

생성자에서 받은 가변 매개변수 각각을 방어적으로 복사해야된다.
```java
public Period(Date start, Date end) {
    this.start = new Date(start.getTime());
    this.end = new Date(end.getTime());

    //방어적 복사본을 만들고, 이복사본으로 유효성 검사를 하였다.
    if(start.compareTo(end) > 0) {
        throw new IllegalArgumentException(start + " after " + end);
    }
}
```
=> 이는 멀티스레드 환경에서 원본객체를 수정하면 생길수 있는 문제를 방어하기 위해서..

매개변수가 제3자에 의해 확장 될수 있는 타입이라면 방어적 복사본을 만들때 clone을 사용해서는 안된다.

```java
Date start = new Date();
Date end = new Date();
Period period = new Period(start, end);

period.end().setMonth(3);
```

```java
public Date start() { 
    return new Date(start.getTime());
}
public Date end() { 
    return new Date(end.getTime());
}
```

### 요약

1. 클아이언트로 반환하는 구성요소가 가변이라면 방어적 복사를 해야한다.
2. 복사비용이 너무 크거나 잘못 수정할 일이 없음을 신뢰한다면 방어적 복사 대신 해당 구성요소를 수정 했을때의 책임이 클라이언트에 있다고 명시하자!