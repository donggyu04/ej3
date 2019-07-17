## 스트림 병렬화는 주의해서 적용하라.
```java
// 아이템45의 스트림을 사용해 20개의 메르센 소수를 생성하는 프로그램.
   public static void main(String[] args) {
       primes().map(p -> TWO.pow(p.intValueExact()).subtract(ONE))
           .filter(mersenne -> mersenne.isProbablePrime(50))
           .limit(20)
           .forEach(System.out::println);
}
   static Stream<BigInteger> primes() {
       return Stream.iterate(TWO, BigInteger::nextProbablePrime);
}
```
데이터 소스가 `Stream.iterate`거나 중간 연산으로 `limit`를 쓰면 파이프라인 병렬화로는 성능 개선을 기대할 수 없다.
 - 무차별적으로 스트림 파이프라인을 병렬화하지 마라.
 - 스트림 병렬화는 분할 매커니즘에 기반한 [Fork/join pool](https://blog.naver.com/tmondev/220945933678)을 사용한다.
 - 스트림의 소스가 ArrayList, HashMap, HashSet, ConcurrentHashMap의 인스턴스거나 배열, int 범위, long 범위 일때 병렬화의 효과가 가장 좋다.
 
### 병렬화의 performance는 어떻게 얻을까??
 - ArrayList, HashMap, HashSet, ConcurrentHashMap의 인스턴스거나 배열, int 범위, long 범위는 데이터를 원하는 크기로 정확하고 손쉽게 나눌 수 있어서
   다수의 스레드에 분배하기에 좋다는 특징이 있다.
    - 스레드 분배 작업은 spliterator가 담당하며, spliterator 객체는 Stream이나 Iterable의 spliterator 메서드로 얻어올 수 있다.
 - 참조 지역성(locality of reference)이 병렬 스트림에 가장 적합하다.  Primitive arrays가 가장 최적화. 
   > 참조 지역성: 이웃한 원소의 참조들이 메모리에 연속해서 저장되어 있다는 뜻.
 - 축소(reduce, min, max, count, sum)이나 short circuit 연산(anyMatch, allMatch, noneMatch)가 병렬화에 적합하다.
   > 축소(Reduction): 파이프라인에서 만들어진 모든 원소를 하나로 합치는 작업.<br>
 - 가변 축소를 수행하는 Stream의 collect 메서드는 컬렉션을 합치는 부담이 크기때문에 병렬화에 적합하지 않다.
 - spliterator 메서드를 재정의하라.
 
### 스트림을 잘못 병렬화하면 성능이 나빠질 뿐만 아니라 결과 자체가 잘못되거나 예상 못한 동작이 발생할 수 있다.
 - 병렬 스트림으로 프로그램의 정확성이 손상될 수 있다.
    - 병렬화한 파이프라인이 사용하는 mappers, filters 혹은 프로그래머가 제공한 다른 함수 객체가 명세대로 동작하지 않을 때 벌어질 수 있다.
       - 함수 객체에 대한 규약
          1. 2x(3x4) = (2x3)x4와 같은 순서에 상관없이 결과가 같은 결합법칙을 만족해야한다.
          2. non-interfering: 파이프라인이 수행하는 동안 데이터가 변경되지 않아야 한다.
          3. 상태를 갖지 않아야한다. input에 의해서만 return value가 결정되어야한다.
          
### 스트림 병렬화는 오직 성능 최적화 수단임을 기억해야한다.
 - 다른 최적화와 마찬가지로 변경 전후로 반드시 성능을 테스트해야한다.
 - 운영 시스템과 흡사한 환경에서 테스트해야한다.
 - 아마도 스트림 안의 원소수 X 수행되는 코스 줄 수의 값이 수십만은 되야 성능이 나아진다.
 
```java
   static long pi(long n) {
       return LongStream.rangeClosed(2, n)
           .mapToObj(BigInteger::valueOf)
           .filter(i -> i.isProbablePrime(50))
           .count();
}
//31 seconds
``` 
```java
   static long pi(long n) {
       return LongStream.rangeClosed(2, n)
}
.parallel()
.mapToObj(BigInteger::valueOf)
.filter(i -> i.isProbablePrime(50))
.count();

//9.2 seconds
```
왜 성능이 좋아질까?? 그저... 아마도...
1. LongStream
2. Reduction
3. Limit를 안함


### 결론: 스트림 병렬화 하지마라.
[https://stackoverflow.com/questions/20851214/fork-join-for-javaee-app](https://stackoverflow.com/questions/20851214/fork-join-for-javaee-app)
[https://stackoverflow.com/questions/22845699/is-it-discouraged-using-java-8-parallel-streams-inside-a-java-ee-container](https://stackoverflow.com/questions/22845699/is-it-discouraged-using-java-8-parallel-streams-inside-a-java-ee-container)