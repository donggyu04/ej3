# @Override 애너테이션을 일관되게 사용하라

- 자바가 기본으로 제공하는 것 중 가장 중요한 애너테이션이다.
- 메서드 선언에만 달 수 있다.
- 많은 버그들을 예방해준다.

## 버그를 찾아보자
```java
package effectivejava.chapter6.item40;
import java.util.*;

// Can you spot the bug? (Page 188)
public class Bigram {
    private final char first;
    private final char second;

    public Bigram(char first, char second) {
        this.first  = first;
        this.second = second;
    }

    public boolean equals(Bigram b) {
        return b.first == first && b.second == second;
    }

    public int hashCode() {
        return 31 * first + second;
    }

    public static void main(String[] args) {
        Set<Bigram> s = new HashSet<>();
        for (int i = 0; i < 10; i++)
            for (char ch = 'a'; ch <= 'z'; ch++)
                s.add(new Bigram(ch, ch));
        System.out.println(s.size());
    }
}
```

```
260
```

Set은 중복을 허용하지 않고, equals() 메서드를 오버라이드 했기 때문에 정상 동작할 것으로 보이지만 결과는 26이 아닌 260이다.

자세히 살펴보면 equals() 메서드를 오버라이드한 것이 아닌 오버로딩 해버렸다. 의도치 않게 매개변수의 자료형을 다르게 작성한 것이다. `@Override` 애너테이션을 사용하면 이런 사태를 방지할 수 있다. 위 코드의 equals() 메서드에 `@Override` 애너테이션을 추가하면 컴파일 에러가 발생한다. 사소한 실수를 컴파일 타임에 알려주므로 헤매지 않고 곧바로 수정할 수있다.

## 결론
**상위 클래스의 메서드를 재정의하려는 모든 메서드에 `@Override` 애너테이션을 달자.** 예외는 상위 추상 메서드를 재정의 하는 경우 뿐이다. 이때는 당연히 오버라이드해야만 하므로 `@Override` 애너테이션이 필요 없지만, 일관성을 위해서는 추가해도 좋다.

인터페이스의 메서드를 오버라이드 할 때에도 `@Override` 애너테이션을 추가하면 실수로 메서드 시그니처를 다르게 입력하는 상황을 방지할 수 있다. 

