## 스트림에서는 부작용 없는 함수를 사용하라.
### 딜레마
 - pure function : function의 결과는 **오직** input에만 의존해야한다.
    - `f(x) = x+1;` return value is only depends on x
 - 컴퓨터 과학에서 method는 side effect(input에만 의존한다)가 있을 수도 없을 수도 있다.
 - ```java
    Random randomizer = new Random();
    IntFunction<Integer> nonPureMethod = (x -> x + randomizer.nextInt());
    ```
   - 만약 순수 함수라면 몇 백번을 호출해도 같은 결과를 가져와야 하지만 위 코드는 아니다.
 - 람다 표현식은 수학적 개념에서 부터 왔지만 구현은 프로그래밍 언어로 되어있다. 어떻게 해야할까??
 
### 스트림에서는 부작용 없는 함수를 사용하라.
 - 스트림 패러다임의 핵심은 계산을 인련의 변환으로 재구성하는 부분이다. 이때 각 변환 단계는 가능한 한 이전 단계의 결과를 
    받아 처리하는 순수 함수여야 한다.
 - 순수 함수형 표현식에 의해서 Parallelism 이득이 많다.
 - 오작동을 막을 수 있다.
```java
   // 스트림 패더라임을 이해하지 못한 채 API만 사용했다 - 따라하지 말 것!
   Map<String, Long> freq = new HashMap<>();
   try (Stream<String> words = new Scanner(file).tokens()) {
       words.forEach(word -> {
           freq.merge(word.toLowerCase(), 1L, Long::sum);
}); }
```
 - 만약 병렬 스트림이라면? 운이 좋으면 정상 동작하고 아니면 오작동한다.
 - forEach 연산은 스트림이 수행한 계산 결과를 보고할 때만 사용하고, 계산하는 데는 쓰지 말자.
```java
   try (Stream<String> words = new Scanner(file).tokens()) {
       freq = words
           .collect(groupingBy(String::toLowerCase, counting()));
    }
```
 - 위 코드가 더 명확하고 읽기 쉽다.
 
> 스트림을 올바로 사용하기 위해 다음 링크를 보고 collectors를 제대로 활용하자. 
> https://www.baeldung.com/java-8-collectors