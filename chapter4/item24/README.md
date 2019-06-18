# 멤버 클래스는 되도록 static으로 만들라

## 중첩 클래스 (Nested Class)
다른 클래스 안에 정의된 클래스. 자신을 감싼 바깥 클래스에서만 쓰여야한다. 그 외의 경우엔 별도의 클래스로 분리하는게 좋다.

**중첩 클래스의 종류**
* 정적 멤버 클래스
* 멤버 클래스
* 익명 클래스
* 지역 클래스

## 정적 멤버 클래스
* 바깥 클래스의 private 멤버에 접근할 수있다는 점을 빼고는 일반 클래스와 동일하다.
* 흔히 바깥 클래스와 함께 쓰일 때만 유용한 public 도우미 클래스로 쓰인다.
* **바깥 인스턴스와 무관하게 중첩 클래스 인스턴스가 존재할 수 있다면 정적 멤버 클래스로 만들어야 한다.**

### 정적 멤버 클래스 예시
[Effecitve Java Builder Pattern](https://github.com/jbloch/effective-java-3e-source-code/blob/master/src/effectivejava/chapter2/item2/builder/NutritionFacts.java)
```java
new NutritionFacts.Builder();
```

## 비정적 멤버 클래스
비정적 멤버 클래스는 바깥 클래스의 인스턴스와 암묵적으로 연결된다.  
정규화된 this(***클래스명.this***)를 사용해 바깥 인스턴스의 참조를 가져올 수 있다.

```java
@Getter
public class Outer {
    private String name = "outer";

    public class Inner {
        public String getOuterName() {
            return Outer.this.name;     // 클래스명.this
        }
    }
}
```

보통은 바깥 클래스 인스턴스를 초기화할때 내부에서 내부 클래스 인스턴스를 생성하는것이 보통이다.  
`바깥 클래스 인스턴스.new 내부 클래스()`를 호출해서 직접 생성할 수 도 있다.

```java
@Test
public void testGetOuterName() {
    Outer outer = new Outer();
    Outer.Inner inner = outer.new Inner();  // 바깥 클래스 인스턴스.new 내부 클래스()

    assertEquals(outer.getName(), inner.getOuterName());
}
```

* 비정적 멤버 클래스의 인스턴스와 바깥 인스턴스 사이의 관계는 멤버 클래스가 인스턴스화될 때 확립되며, 변경될 수 없다.
* **두 인스턴스간의 관계 정보는 비정적 멤버 클래스의 인스턴스 안에 만들어져 메모리 공간을 차지하며, 생성 시간이 더 걸린다.**
* **가비지 컬렉션이 바깥 클래스의 인스턴스를 수거하지 못해서 메모리 누수가 생길 수 있다.**
* 비정적 멤버 클래스는 어댑터를 정의할 때 자주 쓰인다.

### 어댑터
어떤 클래스의 인스턴스를 감싸 마치 다른 클래스의 인스턴스처럼 보이게하는 뷰로 사용하는것

```java
public class MySet<E> extends AbstractSet<E> {
    ...

    @Override public Iterator<E> iterator() {
        return new MyIterator();
    }

    private class MyIterator implements Iterator<E> {
        ...
    }
}
```

## 익명 클래스
* 이름이 없다.
* 바깥 클래스의 멤버가 아니다.
* 쓰이는 시점에 선언과 동시에 인스턴스가 만들어진다.
* 코드의 어디서든 만들 수 있다.
* 비정적 문맥에서만 바깥 클래스의 인스턴스를 참조할 수 있다.

```java
interface Animal {
    void sayHello();
}

@Test
public void testAnonymousClass() {
    Animal dog = new Animal() {
        @Override
        public void sayHello() {
            System.out.println("멍멍");
        }
    };

    Animal cat = () -> System.out.println("야옹");

    Arrays.asList(dog, cat).forEach(Animal::sayHello);
}
```

### 익명 클래스의 사용 제약
* 선언한 지점에서만 인스턴스화 가능하다.
* instanceof 검사나 클래스의 이름이 필요한 작업은 수행할 수 없다.
* 여러 인터페이스를 구현할 수 없다.
* 인터페이스를 구현하면서 다른 클래스를 상속할 수 없다.
* 짧지 않으면 가독성이 떨어진다.

람다를 지원하기 전에는 작은 함수 객체를 즉석에서 생성하기 위해 자주 사용됐지만 그 역할은 이제 람다로 넘어갔다.

## 지역 클래스
네 가지 중첩 클래스 중 가장 드물게 사용된다. 지역변수가 선언될 수 있는 곳에 선언 가능하고, 유효 범위도 지역변수와 같다.

* 멤버 클래스처럼 이름이 있고 반복해서 사용할 수 있다.
* 익명 클래스처럼 비정적 문맥에서 사용될 경우 바깥 인스턴스를 참조할 수 있다.
* 정적 멤버는 가질 수 없으며, 가독성을 위해 짧게 작성해야한다.

```java
public class LocalClassTest {
    private final String hello = "Hello";
    
    @Test
    public void testLocalClass() {
        class MyLocalClass {
            private void sayHello() {
                System.out.println(hello);
            }
        }
        
        new MyLocalClass().sayHello();
    }
}
```

## 결론
* 중첩 클래스에는 네 가지가 있으며, 필요에 맞게 사용해라. 
* 비정적 멤버 클래스는 단점이 많다. 정적 멤버 클래스를 추천한다.