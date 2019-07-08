# 38. 확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라.

1. 대부분에 상황에서 열거 타입을 확장하는 것은 좋은 않은 생각이다.
    - 확장한 타입의 원소는 기반 타입의 원소로 취급하지만 그 반대는 아니다.
    - 기반 타입과 확장된 타입들의 원소 모두를 순회할 방법도 마땅치 않다.
    - 확장성을 높이려면 고려 할 요소가 늘어나 설계와 구현이 복잡해진다.

2. 하지만, 확장할 수 있는 열거 타입이 어울리는 쓰임이 있는데 바로 연산코드(Operation code)이다.
    - API가 제공하는 연상 외에 사용자 확장 연산을 추가할 수 있도록 열어줘야 할 때가 있다.
    - 열거 타입이 임의의 **인터페이스**를 구현할 수 있다는 사실을 이용.

```java
public interface Operation {
    double apply(double x, double y);
}
```
```java
public enum BasicOperation implements Operation {
    PLUS("+") {
        public double apply(double x, double y) { return x + y; }
    },
    MINUS("-") {
        public double apply(double x, double y) { return x - y; }
    },
    TIMES("*") {
        public double apply(double x, double y) { return x * y; }
    },
    DIVIDE("/") {
        public double apply(double x, double y) { return x / y; }
    };

    private final String symbol;

    BasicOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override public String toString() {
        return symbol;
    }
}
```
```java
public enum ExtendedOperation implements Operation {
    EXP("^") {
        public double apply(double x, double y) {
            return Math.pow(x, y);
        }
    },
    REMAINDER("%") {
        public double apply(double x, double y) {
            return x % y;
        }
    };

    private final String symbol;

    ExtendedOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override public String toString() {
        return symbol;
    }
}
```

3. 위와 같이 `Operation interface`를 확장한 후 해당 `interface`를 연산의 타입으로 사용하면 `Operation interface`를 구현한 또 다른 열거 타입을 정의해 기본 타입인 `BasicOperation`을 대체할 수 있다.

4. 인터페이스를 이용해 확장 가능한 열거 타입을 흉내 내는 방식에도 열거 타입끼리 구현을 상속할 수 없다는 문제가 있다.
    1. 아무 상태에도 의존하지 않는 경우 디폴트 구현을 이용해 인터페이스에 추가하는 방법이 있다.
    2. `BasicOperation`과 `ExtendedOperation`에는 symbol을 저장하고 찾는 로직이 각각 들어가야 되지만 공유하는 기능이 많다면 정적 도우미 메소드나, 도우미 클래스로 분리하는 방식으로 코드 중복을 줄일 수 있을 것이다.



### 핵심정리
> 열거 타입 자체는 확장할 수 없지만, 인터페이스와 그 인터페이스를 구현한 기본 열거 타입을 함께 사용해 같은 효과를 낼 수 있다. 이렇게 하면 클라이언트는 이 인터페이스를 구현해 자신만의 열거 타입(혹은 다른 타입)을 만들 수 있다. 그리고 API가 (기본 열거 타입을 직접 명시하지 않고) 인터페이스 기반으로 작성되었다면 기본 열거 타입의 인스턴스가 쓰이는 모든 곳을 새로 확장한 열거 타입의 인스턴스로 대체해 사용할 수 있다.