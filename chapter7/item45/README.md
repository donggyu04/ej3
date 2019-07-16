# 스트림은 주의해서 사용하라

스트림 API는 다량의 데이터 처리 작업(순차적이든 병렬적이든)을 돕고자 자바8에 추가되었다.

이 API가 제공하는 추상 개념 중 핵심은 두 가지다.

- 스트림(Stream)은 데이터 원소의 유한 혹은 무한 시퀀스(sequence)를 의미
- 스트림 파이프라인(Stream Pipeline)은 이 원소들로 수행하는 연산단계를 표현하는 개념

스트림의 원소들은 어디서부터든 올 수 있다.

- 컬렉션 (Collection)
- 배열 (Array)
- 파일 (File)
- 정규표현식 (Regex Pattern Matcher)
- 난수 생성기 (Random Generator)
- 다른 스트림 (Other Stream)

### 일반 loop를 이용한 코드

```java
public static void main(String[] args) {
    File dectionary = new File(args[0]);
    int minGroupSize = Integer.parseInt(args[1]);

    Map<String, Set<String>> groups = new HashMap<>();
    try (Scanner s = new Scanner(dectionary)) {
        while(s.hasNext()) {
            String word = s.next();
            groups.computeIfAbsent(alphabetize(word), 
                                   (unused) -> new TreeSet<>()).add(word);
        }
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }
    
    for(Set<String> group : groups.values()) {
        if(group.size() >= minGroupSize) {
            System.out.println(group.size() + ": " + group);
        }
    }
}

private static String alphabetize(String s) {
    char[] a = s.toCharArray();
    Arrays.sort(a);
    return new String(a);
}
```

자바 8에서 추가된  ```computeIfAbsent``` 메서드를 사용했다.
맵 안에 키가 있는지 찾은 다음, 있으면 단순히 그 키에 매핑된 값을 반환한다.


```java
public static void main(String[] args) throws IOException {
    File dectionary = new File(args[0]);
    int minGroupSize = Integer.parseInt(args[1]);

    try(Stream<String> words = Files.lines(dectionary.toPath())) {
        words.collect(
            groupingBy(word -> word.chars().sorted()
                       .collect(StringBuilder::new,
                                (sb, c) -> sb.append((char) c),
                                StringBuilder::append).toString()))
            .values().stream()
            .filter(group -> group.size() >= minGroupSize)
            .map(group -> group.size() + ": " + group)
            .forEach(System.out::println);
    }
}
```

### Stream을 과도하게 쓴 코드

```java
public static void main(String[] args) throws IOException {
    File dectionary = new File(args[0]);
    int minGroupSize = Integer.parseInt(args[1]);

    try(Stream<String> words = Files.lines(dectionary.toPath())) {
        words.collect(
            groupingBy(word -> word.chars().sorted()
                       .collect(StringBuilder::new,
                                (sb, c) -> sb.append((char) c),
                                StringBuilder::append).toString()))
            .values().stream()
            .filter(group -> group.size() >= minGroupSize)
            .map(group -> group.size() + ": " + group)
            .forEach(System.out::println);
    }
}
```

### Stream을 적절히 활용한 코드

```java
public static void main(String[] args) throws IOException {
    File dectionary = new File(args[0]);
    int minGroupSize = Integer.parseInt(args[1]);

    try(Stream<String> words = Files.lines(dectionary.toPath())) {
        words.collect(groupingBy(word -> alphabetize(word)))
            .values().stream()
            .filter(group -> group.size() >= minGroupSize)
            .forEach(group -> System.out.println(group.size() + ": " + group));
    }
}
```

스트림을 적절하게 사용하면 명료해진다.

### 스트림이 적합하지 않은 경우

- 데이터가 파이프라인의 여러 단계(stage)를 통과할 때 이 데이터의 각 단계에서의 값들에 동시에 접근하기 어려운 경우
- 스트림 파이프라인은 한 값을 다른 값에 매핑하고 나면 원래의 값을 잃는 구조이기 때문