## equals는 일반 규약을 지켜 재정의하라.
 - Object에서 final이 아닌 메서드(equals, hashCode, toString, clone, finalize)는 모두 재정의(overriding)을 염두에 두고 설계된 것이라 재정의 시 지켜야하는 일반 규약이 명확히 정의되어 있다.

### equals를 overriding하지 말아야 할 상황.
 - 각 인스턴스가 본질적으로 고유하다.
    - 값을 표현하는 클래스(model)가 아니라 동작하는 개체(로직이 있는)를 표현하는 클래스
 - 상위 클래스에서 재정의한 equals가 하위 클래스에도 그대로 쓰이는 경우
 - 클래스가 private이거나 package-private이고 equals를 호출할 일이 없는 경우.
 
### equals를 overriding를 고려할 2가지 조건.
 1. Object identity(객체가 물리적으로 같은가)가 아닌 logical equality인지 확인해야 할 때 
 2. 상위 클래스의 equals가 logical equality을 비교하도록 override하지 않았을 떄.
 
### equals의 overriding 규약 조건.
pre) 참조 값은 null이 아니어야 한다. 
 - 반사성(reflexivity): x.equals(x)는 true
 - 대칭성(symmetry): x.equals(y)가 true면 y.equals(x)도 true
 - 추이성(transitivity): x.equals(y)가 true, y.equals(z)가 true면 x.equals(z)도 true
 - 일관성(consistency): x.eqauls(y)를 반복해서 호출하면 항상 true를 반환하거나 항상 false를 반환
 - null이 아님: x.equals(null)은 false
 
### equals의 override시 주의 사항
 - equals를 overriding할 땐 반드시 hashcode도 재정의하자.
 - 너무 복잡하게 비교를 하려고하지 말자.
 - Object 외의 타입을 매개변수로 받는 equals는 선언하지 말자.
 
### equals를 overriding하는 best practice
 1. `==` 연산자를 사용해 입력이 자기 자신의 참조인지 확인한다.
 2. `instanceof` 연산자로 입력이 올바른 타입인지 확인한다.
 3. 입력을 올바른 타입으로 형변환한다.
 4. 입력 객체와 자기 자신의 대응되는 '핵심'(같은지 판단이 될 수 있는) 필드들이 모두 일치하는지 하나씩 검사한다.
    - 비교에 cost가 싼 필드부터 검사하도록 한다.
    - float은 `Float.compare(float, float)`, double은 `Double.compare(double, double)`로 비교하자.
    
```java
public class People {
    private String name;
    private int age;

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof People)) {
            return false;
        }

        People people = (People) o;
        return (people.age == age) &&
                (people.name != null && people.name.equals(name));
    }
}
```