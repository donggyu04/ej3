# 인터페이스는 타입을 정의하는 용도로만 사용하라

## 상수 인터페이스 안티 패턴
메서드 없이 static final 상수 필드들로 가득찬 인터페이스는 인터페이스를 잘못 사용한 대표적인 예다.
```java
public interface PhysicalConstants {
    // 아보가드로 수 (1/몰)
    static final double AVOGADROS_NUMBER = 6.022_140_857e23;
    // 볼츠만 상수 (J/K)
    static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;
    // 전자 질량 (kg)
    static final double ELECTRON_MASS = 9.109_383_56e-31;
}
```
* 일반적인 경우의 상수는 해당 상수를 사용하는 클래스 내부에 위치해서 내부 구현을 외부로부터 감춘다.  
  하지만 상수 인터페이스는 반대로 내부 구현을 노출한다.
* 외부로 노출된 상수는 사용자들에게 혼란을 준다.
* 사용자 코드에서 상수를 사용할 경우, 해당 상수에 종속되어 상수를 변경할 수 없게된다. (하위 호환성)

## 상수를 공개할 목적이라면 사용할 만한 방법들
### 클래스나 인터페이스 자체에 추가하기  
특정 클래스나 인터페이스와 상수가 밀접한 연관이 있다면 사용할만하다.
ex) `Integer.MIN_VALUE, Integer.MAX_VALUE`

### 열거타입으로 나타내기
열거 타입이 어울리지 않는다면 사용하지 말것

### 상수 유틸리티 클래스
이전에 소개했던 인스턴스를 생성할 수 없는 유틸리티 클래스에 상수를 추가한다.
```java
public class PhysicalConstants {
    private PhysicalConstants() { } // 인스턴스화 방지

    // 아보가드로 수 (1/몰)
    static final double AVOGADROS_NUMBER = 6.022_140_857e23;
    // 볼츠만 상수 (J/K)
    static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;
    // 전자 질량 (kg)
    static final double ELECTRON_MASS = 9.109_383_56e-31;
}
```

## 결론
인터페이스는 타입을 정의하는 용도로만 사용해야 한다. 상수 공개용 수단으로 사용하지 말자.