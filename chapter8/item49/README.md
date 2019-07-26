## 매개변수가 유효한지 검사하라

### 매개변수 검사를 제대로 수행 하지 않을 시 생길 수 있는 문제
1. 메소드 수행 중간에 모호한 예외를 던질수 있다
2. 메소드가 잘 수행되었지만 잘못된 결과를 반환한다
3. 메소드는 문제없이 수행되었지만, 객체를 이상한 상태로 만들어 놓아 미래의 알수 없는 시점에
   메소드와 관련없는 오류를 낼수 있다.

=> 다시 말해, 메소드 검사에 실패하면 실패 원자성을 어기는 결과를 낳을 수 있다.
이를 해결하기 위해 **IllegalArgumentException, IndexOutOfBoundsException, NullPointerExcetio**를 활용하여 즉각 매개변수 검사 오류를 반환 할수 있다.


### 매개변수 검사시 JAVA의 기능을 활용하라

JAVA 7의 추가된 java.util.Objects.requireNonNull을 활용하면 null 검사를 수동으로 하지 않아도 된다.
```
this.strategy = Objects.requireNonNull(strategy);
```

JAVA9 의 추가된 Obejcts 범위 검사기능을 활용할수도 있다.
**checkFromIndexSize, checkFromToIndex, checkIndex**

단언문(assert)를 이요해 매개변수 유효성을 검증 할수 있다.

```java
private static void sort(long a[], int offset, int length) {
    assert a != null;
    assert offset >=0 && offset <= a.length;
    assert length >=0 && length <= a.length - offset;
}

```


### 요약
메소드나 생성자 작성시 매개변수들에 어떤제약이 있을지 생각하고 명시적으로 검사를 해야한다.
