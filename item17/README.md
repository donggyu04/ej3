## 변경 가능성을 최소화 하라
#### 불변 클래스는 설계, 구현, 사용하기 쉬우며 오류가 생길 여지도 적고 안전하다.
#### 클래스를 불변으로 만드려면 다섯가지 규칙을 따르면 된다.
1. 객체의 상태를 변경하는 메서드를 제공하지 않는다.

    - 멤버의 상태를 바꾸는 setter를 제공하거나, final이 아닌 public 멤버를 포함하지 않는다 
    
2. 클래스를 확장할 수 없도록 한다.

    - 확장이 불가능 하도록 클래스를 final로 선언한다
    
    - private 생성자 만들 남겨두고 public 정적 팩토리를 제공한다
    
    ``` 
        public class Complex {
        private final double re;
        private final double im;
        
        private Complex(double re, double im) {
            this.re = re;
            this.im = im;
        }
        
        public static Complex valueOf(double re, double im) {
            return new Complex(re, im);
        }
      }
    ```
        - public 생성자가 없음으로 상속이 불가능 하고, 
          멤버 변수가 final 임으로 변경이 불가능하다
3. 모든 필드를 final로 선언한다.

    - 필드를 final로 선언함으로써 값의 재할당을 막을 수 있다
    
4. 모든 필드를 private로 선언한다.

    - 필드를 private으로 선언 함으로써 불변해야 하는 값에 직접 접근을 막을 수 있다.
   
    ```
        public final class ImmutableField {
            public String mutable;
            public final String immutableButNotGood;
            private String mutableButCanbeImutate;
            private final String immutable;
            
            public ImmutableField(String immutableButNotGood, String immutable) {
                this.immutableButNotGood = immutableButNotGood;
                this.immutable = immutable;
            }
      }
    ```
5. 자신 외에는 내부의 가변 컴포넌트에 접근할 수 없도록 한다.

    - 불변 객체 내부에 가변 객체를 참조하는 필드가 있다면 절대 클라이언트에 해당 필드를 제공하면 안된다 
    
    - 가변 필드를 제공해야 한다면 방어적 복사(readObject)를 통해 전달한다 
    
    ```
        public final class Complex {
            private double re;
            private double im;
            
            public Complex(double re, double im) {
                this.re = re;
                this.im = im;
            }
            
            public Complex plus(Complex c) {
                return new Complex(re + c.re, im + c.im);
            }
      }
    ```
#### 불변 객체의 장점
1. Thread Safe 

    - Thread Safe 한 클래스를 만드는 가장 쉬운 방법은 객체를 불변으로 만드는것 
    
2. 인스턴스를 중복 생성할 필요가 없다

    - Thread Safe 함으로 공유가 자유로워 재활용이 권장된다
    
    ```
      // Complex 클래스가 제공하는 재활용 가능한 상수들
      public static final Complex ZERO = new Complex(0, 0);
      public static final Complex ONE = new Complex(1,0);
    ```
    
    - 위의 이유로 불변 객체는 new를 사용한 생성보다는 "정적 팩토리"를 통해서 제공하는 방법도 좋다 
        - Boxing 된 primative 타입 클래스들과 BigInteger가 여기에 속함
        
3. 방어적 복사(clone)가 필요없다

    - 방어적 복사를 해봐야 원본과 같은 불변 객체가 생성됨으로 의미가 없다
    
    - 불변 클래스는 clone 메서드를 제공하지 않는 것을 권장
        - String 클래스의 clone을 사용하지 말자
        
4. 실패 원자성을 내포하고있다

    - 불변 객체끼리는 내부의 데이터를 공유할 수 있다
    ```
      //BigInter 클래스의 negate 메서드
      public BigInteger negate() {
          return new BigInteger(this.mag, -this.signum);
      }
    ```
        - 부호만 반대고 기존의 mag(값 배열)은 그대로 공유
        
        - BigInteger의 메서드 중에 내부의 값을 변경하는 메서드가 없기 때문에(불변) 가능한 방식
        
        - BigInteger의 연산 메서드(add, multiply) 등은 모두 새로운 BigInter를 생성해서 반환
        
5. 실패 원자성을 내포하고있다

    - 메서드에서 예외가 발생한 경우, 메서드 호출전과 동일한 상태여야 한다
        
### 정리
1. getter가 있다고 해서 무조건 setter를 만들지는 말자 

    - 클래스는 불변인게 좋다
    
2.  불변 클래스는 장점이 많으며 단점은 특정 상황에서의 잠재적인 성능저하 뿐이다

    - 조금 다른 비슷한 인스턴스를 계속 생성하는 경우
    
3.  불변으로 만들수 없는 클래스라도 변경할 수 있는 부분은 최소한으로 줄이자

    - 변경 가능한 부분을 제외하고는 모두 final로 선언하자
    
    - 특별한 이유가 없다면 모든 필드는 private final 이어야 한다
    
