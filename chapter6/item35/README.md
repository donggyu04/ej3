# Ordinal 메서드 대신 인스턴스 필드를 사용하라
-  대부분의 열거 타입 상수는 자연스럽게 하나의 정숫값에 대응된다.
모든 열거 타입은 해당 상수가 그 열거 타입에서 몇 번째 위치인지를 반환하는 ordinal method를 제공한다.

## Ordinal 잘못 사용한 예
```java
public enum Ensemble {
    SOLO, DUET, TRIO, QUARTET, QUINTET,
    SEXTET, SEPET, OCTET, NONET, DECTET;
    
    public int numberOfMusicians() { return ordinal() + 1; }
}
```

## 인스턴스 필드활용하여 Ordinal 구현하기
```java
public enum Ensemble {
    SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5),
    SEXTET(6), SEPET(7), OCTET(8), DOUBLE_QUARTET(8),
    NONET(9), DECTET(10), TRIPLE_QUARTET(12);
    
    private final int numberOfMusicians;
    Ensemble(int numberOfMusicians) {
        this.numberOfMusicians = numberOfMusicians;
    }
    public int numberOfMusicians() { return this.numberOfMusicians; }
}
```

- 대부분의 프로그래머는 이 값이 필요는 없다.
이 값은 EnumSet이나 EnumMap 같은 열거 타입 기반의 범용 자료구조에 사용 될 목적으로 만들어졌다.


## 요약

***사용하지 않는게 좋다..***

## 출처
https://jaehun2841.github.io/2019/02/04/effective-java-item35/#ordinal%EC%9D%84-%EC%9E%98%EB%AA%BB-%EC%82%AC%EC%9A%A9%ED%95%9C-%EC%98%88
