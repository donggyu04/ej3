#Clone 재정의는 주의해서 진행해라 

Clonable은 복제해도 되는 믹스인 인퍼페이스이다.
```java
public interface Clonabe { }
```
그러면 clone에 메소드는 어디에 있을까? 
clone 메소드가 선언된 곳은  **Object**이고 **protected**메소드로 선언되는데 이를 통해 많은 문제가 발생한다.
```java
@HotSpotIntrinsicCandidate
protected native Object clone() throws CloneNotSupportedException;
```

그러면 Clonalbe은 무슨 역할을 할까? Object의 clone 메소드의 동작 방식을 결정한다. Cloneable을 구현한 클랫의 인스턴스에서 clone을 호출하면 그 객체의 필드ㄹ들을 하나하나 복사한 객체를 반환하며, 그렇지 않은 객체의 클래스 인스턴스에서 CloneNotSupported Exception을 던진다
```java
public class PhoneNumber {
    public String number;

    public PhoneNumber(String number) {
        this.number = number;
    }

    @Override
    public PhoneNumber clone() {
        return (PhoneNumber) super.clone();
    }
}

=> 컴파일 에러 발생

public class PhoneNumber implements Cloneable {
    public String number;

    public PhoneNumber(String number) {
        this.number = number;
    }

    @Override
    public PhoneNumber clone() {
        try {
            //super.clone 호출 시 객체필드들을 하나하나 복사한 객체들을 반환 (shallow copy)
            return (PhoneNumber) super.clone();
        } catch (final CloneNotSupportedException e) {
            //cloneable을 구현하지 않는 객체에 대해 해당 exception이 발생한다.
            // Cloneable 인터페이스를 구현한 이상 이 코드는 절대 실행되지 않는다.
            throw new AssertionError();
        }
    }
}
=> 정상적 빌드
```
Clonable 경우에는 상위 클래스에 정의된 protected 메소드의 동작방식을 변경한 것을 확인 할 수 있다.


실무에서는 Cloneable을 구현한 클래스는 **clone 메소드를 public**으로 제공하며, 사용자는 당연히 **복제가 제대로** (아래의 규약대로) 이뤄지리라 기대한다. 

clone 메소드의 일반적인 명세 규칙
* x.clone() != x
* x.clone.getClass() -- x.getClass()
* x.clone.equals(x)

하지만 위의 규칙들은 권장사항이지 필수사항이 아니다. 또한 이를강제 할수 없기 때문에 구현에 주의하여야한다.

----
## clone 명세 허술하여 발생 할수 있는 문제
clone 구현 시 발생 할 수 있는 문제점
```java
public class Person implements Cloneable {
    public String name;

    public Person(String name) {
        this.name = name;
    }

    @Override
    public Person clone() {
        try {
            return new Person(this.name);
        } catch (final CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
```
```java
public class Student extends Person {
    public String class;

    public Student(String name, String class) {
        super(name);
        this.class = class;
    }

    @Override
    public Student clone() {
        try {
            return (Student) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
```

Shallow copy 문제
```java
public class Stack {
    private Object[] elements;
    private int size = 0 ;
    private static final int DEFAULT_CAPACITY = 16;

    public Stack() {
        this.elements = new Object[DEFAULT_CAPACITY];
    }
    
    ....
    @Override
    public Stack clone() {
        try {
            Stack result = (Stack) super.clone();
            result.elements = elements.clone();
            return result;
        } catch (final CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    
}
```
해당 문제는 hash Table 혹은 연결리스트에서도 발생할 수있느니 구현시 유념하면서 코딩하여야 한다.

## Clone 재정의시 참고사항

1. Cloneable 구현하는 클래스는 clone 재정의를 한다. 이때 접근제어자를 public으로, thorws 절도 없애야 한다. 
2. 상속용 클래스는 **implements clonable**을 구현하면 안된다. 이는 하위 클래스에서 구현하여야 한다.

※ 상위 클래스에서 정의해야할 경우 아래와 같이 clone을 퇴하시키는 방법도 있다.
```java
@Override
public Type clone() throws CloneNotSupportedException {
    // 자식 클래스에서는 super.clone()을 통해 clone 메서드를 재정의 하므로 이를 못하게 막을수 있다.
    throw new CloneNotSupportedException();
}
```
## 복사 생성자와 복사 팩토리를 사용해라
복사 생성자와 복사 팩토리에서는 자기 타입을 인수로 받아 복사하는 방식을 취한다.
```java
public Type(final Type type) { ... }
public static Type newInstance(final Type type) { ... }
```
---
#요약
1. 상속용 클래스에 Cloneable을 구현하지 말아라
2. cloneable을 구현해야할 경우 명세에 맞게 구현해라
    - shallow Copy에 유념하라
    - 접근제어자를 public으로 바꾸고 반환형을 변경하여라
3. 복제기능은 생성자와 팩터리를 이용하는게 최고이다.
   예외로 배열만은 clone메소드 방식이 가장 깔끔하다.
   


### 참고자료
https://perfectacle.github.io/2018/12/16/effective-java-ch03-item13-clone-method/
https://sjh836.tistory.com/169