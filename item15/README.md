#클래스와 멤버의 접근 권한을 최소화하라
컴포넌트와 잘 설계된 컴포터는의 가장 큰 차이는 클래스 내부 데이터와 내부 구현정보를 외부 컴포넌트로 부터 얼마나 잘 숨겼나 이다. 즉, 구현과 APi를 분리하여 API를 통해서만 다른 컴포넌트와 소통하는 것이다. 내부 구현정보를 숨기는 것을 정보은닉, 캡슐화 라고 한다.

## 캡슐화의 장점
* 개발속도을 높이고, 관리비용을 낮춘다.
* 재사용성을 높인다. 
* 프로그램 제작하는 난이도를 낮춰준다

## 정보은닉 구현하기
1. 모든 클래스와 멤버의 접근성을 가능한 좁힌다.
   * privvate: 맴버선언한 톱 클래스에서만 접근가능
   * package-private: 멤버가 소속된 패키지안의 모든 클래스에서 접근
   * protected: package-private의 접근범위를 포함하며, 하위클래스에서 접근가능
   * public: 모든 곳에서 접근가능
2. public 클래스의 인스턴스 필드는 되도록 public이 아니어야 한다.
   public 필드는 불변을 보장할 수 없기 때문이다. 또한 쓰레드에 안전하지 ㅇ낳다.
3. 클래스에서 public static final 배열 필드를 두거나 이 필드를 반환하는 접근자 메서드를 제공해서는 안된다.
```java
// 보안적 허점이 존재합니다.
public static final Thing[] VALUES= {...};

//해결책1
private static final Thing[] PRIVATE_VALUES= {...};
public static final List<Thing> VALUES = Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));

//해결책2
private static final Thing[] PRIVATE_VALUES= {...};
public static final Thing[] values() {
    return PRIVATE_VALUES.clone();
}
```

---- 
# 요약
1. 접근가능성을 최소화 하라. 꼭 필요한 것만 골라 public API를 설계하라
2. 상수용 public static final 필드 외에는 어떠한 public 필드를 가져서는 안된다.

### 참고자료
https://sjh836.tistory.com/170?category=679845