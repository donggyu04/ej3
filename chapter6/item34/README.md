# INT 상수 대신 열거 타입을 사용하라

## 정수 열거 패턴(int enum pattern)의 단점
```java
// int를 사용한 enum 패턴
public static final int FAIL = 1;
public static final int SUCCESS = 2;
 
// string을 사용한 enum 패턴
public static final String FEMAIL = "3";
public static final String MAIL = "4";
```

- 단점
1. Type Safe 하지 않다.
   ex)
   ```java
    public static final int APPLE_FUJI = 0;
    public static final int APPLE_PIPPIN = 1;
    public static final int APPLE_GRANNY_SMITH = 2;

    public static final int ORANGE_NAVEL = 0;
    public static final int ORANGE_TEMPLE = 1;
    public static final int ORANGE_BLOOD = 2;

    APPLE_FUJI == ORANGE_NAVEL;
   ```
2. 상수값이 변경되면 반드시 다시 컴파일 해야 한다.
3. 정수 상수는 문자열로 출력하기 까다롭다
4. 문자열 열거 패턴은 더 나쁘다.

## Java의 열거 Type
```java

@SuppressWarnings("serial") // No serialVersionUID needed due to
                            // special-casing of enum types.
public abstract class Enum<E extends Enum<E>>
        implements Comparable<E>, Serializable {
```

- 열거(enum) 타입의 특징
1. 열거 타입 자체는 클래스(class)이다.
2. 상수 하나당 자신의 인스턴스를 하나씩 만들어(Singleton) public static final 필드로 공개한다.
3. 열거 타입은 밖에서 접근할 수 있는 생성자를 제공하지 않으므로, 사실상 final이다.
(따라서 열거 타입 내 필드도 final이다.)

- 열거(enum) 타입의 장점
1. Type Safe 하다
   ex)
   ```java
    public enum Apple {FUJI, PIPPIN, GRANNY_SMITH}
    public enum Orange {NAVEL, TEMPLE, BLOOD}

    Apple.FUJI == ORANGE.NAVEL
    ```
2. 각자의 namespace가 있다.
   - 공개 되는 것이 오직 필드의 이름 뿐이라, 컴파일 시, 클라이언트의 코드에 정수 값이 각인되지 않기 때문이다.

## 데이터와 메서드를 갖는 열거 타입
열거 타입은 메서드와 필드를 추가 할 수 있다.
열거 타입 상수 각각을 특정 데이터와 연결지으려면 생성자에서 데이터를 받아 인스턴스 필드에 저장하면 된다.

```java
public enum Planet {
    MERCURY(3.302e+23, 2.439e6),
    VENUS  (4.869e+24, 6.052e6),
    EARTH  (5.975e+24, 6.378e6),
    MARS   (6.419e+23, 3.393e6),
    JUPITER(1.899e+27, 7.149e7),
    SATURN (5.685e+26, 6.027e7),
    URANUS (8.683e+25, 2.556e7),
    NEPTUNE(1.024e+26, 2.477e7);

    private final double mass;           // In kilograms
    private final double radius;         // In meters
    private final double surfaceGravity; // In m / s^2

    // Universal gravitational constant in m^3 / kg s^2
    private static final double G = 6.67300E-11;

    // Constructor
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
        surfaceGravity = G * mass / (radius * radius);
    }

    public double mass()           { return mass; }
    public double radius()         { return radius; }
    public double surfaceGravity() { return surfaceGravity; }

    public double surfaceWeight(double mass) {
        return mass * surfaceGravity;  // F = ma
    }
}
```

## 상수별 메서드 구현
사칙 연산을 Enum으로 구현해보기.
```java
public enum Operation {
    PLUS, MINUS, TIMES, DIVIDE
}
```

### Switch-case 구현
```java
public double apply(double x, double y) {
    switch(this) {
        case PLUS: return x + y;
        case MINUS: return x - y;
        case TIMES: return x * y;
        case DIVIDE: return x / y;
    }
    throw new AssertionError("Unknown Operator" + this);

}
```

### abstract method를 이용한 구현
```java
public enum Operation {
    PLUS{
        public double apply(double x, double y) {
            return x + y;
        }
    }, 
    MINUS{
        public double apply(double x, double y) {
            return x - y;
        }
    }, 
    TIMES{
        public double apply(double x, double y) {
            return x * y;
        }
    }, 
    DIVIDE{
        public double apply(double x, double y) {
            return x / y;
        }
    };
    
    public abstract double apply(double x, double y);    
}
```

### Funtional Interface를 이용한 구현
```java
public enum Operation {
    PLUS((x, y) -> x + y),
    MINUS((x, y) -> x - y),
    TIMES((x, y) -> x * y),
    DIVIDE((x, y) -> x / y);
    
    private BiFunction<Double, Double, Double> operationFuntion;   
}
```
 
## 전략적 열거 타입 패턴

상수별 메서드 구현에는 열거 타입 상수끼리 코드를 공유하기 어렵다는 단점이 있다.
아래 예시는 요일별로 일당을 계산해 주는 열거타입 메서드이다.
```java
enum PayrollDay {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
    
    private static final int MINS_PER_SHIFT = 8 * 60;
    
    int pay(int minutesWorked, int payRate) {
        int basePay = minutesWorked * payRate;
        int overtimePay;
        switch(this) {
            case SATURDAY:
            case SUNDAY:
                overtimePay = basePay / 2;
                break;
            default:
                overtimePay = minutesWorked <= MINS_PER_SHIFT ? 0 : 
                							(minutesWorked - MINS_PER_SHIFT) * payRate / 2;
        }
        return basePay + overtimePay;
    }
}
```
관리 관점에서는 위험한 코드이다.
공휴일과 같은 새로운 값을 열거타입에 추가하려면 ***그 값을 처리하는 case문을 잊지말고*** 추가해 줘야 한다. 상수별 메서드 구현으로 급여를 정확히 계산하는 방법은 두 가지다.

1. 잔업수당을 계산하는 코드를 모든 상수에 중복해서 넣는다.
2. 평일용/주말용으로 나눠 각각 도우미 메서드를 생성한다음 각 상수가 자신에게 필요한 메서드를 적절히 호출한다.

```java
enum PayrollDay {
    MONDAY(WEEKDAY),
    TUESDAY(WEEKDAY),
    WEDNESDAY(WEEKDAY),
    THURSDAY(WEEKDAY),
    FRIDAY(WEEKDAY),
    SATURDAY(WEEKEND),
    SUNDAY(WEEKEND);
    
    private final PayType payType;
    
    PayrollDay(PayType payType) {
        this.payType = payType;
    }
    
    int pay(int minutesWorked, int payRate) {
        return payType.pay(minutesWorked, payRate);
    }
    
    enum PayType {
        WEEKDAY {
            int overtimePay(int minutesWorked, int payRate) {
                return minutesWorked <= MINS_PER_SHIFT ? 0 : (minutesWorked - MINS_PER_SHIFT) * payRate / 2;
            }
        },
        
        WEEKEND {
            int overtimePay(int minutesWorked, int payRate) {
                return minutesWorked * payRate / 2
            }
        };
        
        abstract int overtimePay(int minutesWorked, int payRate);
        private static final int MINS_PER_SHIFT = 8 * 60;
        
        int pay(int minutesWorked, int payRate) {
            int basePay = minutesWorked & payRate;
            return basePay + overtimePay(minutesWorked, payRate);
        }
    }
}
```

## 요약
1. 필요한 원소를 컴파일타입에 알 수 있는 상수 집합이라면 항상 열거 타입을 사용하자
2. int 상수 대신 열거 타입으로 정의하라
3. 각 상수와 특정 데이터를 연결 짓거나, 상수마다 다른 action이 필요한 경우 열거 타입이 효과적이다.
4. switch문으로 분기하여 로직을 구성하기 보다는 전략 열거타입 패턴을 사용하여 로직을 구현하라.

## 출처
https://wedul.site/285
https://jaehun2841.github.io/2019/02/03/effective-java-item34/#%EC%84%9C%EB%A1%A0