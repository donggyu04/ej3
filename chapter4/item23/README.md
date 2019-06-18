# 태그 달린 클래스보다는 클래스 계층구조를 활용하라
태그 달린 클래스를 써야 하는 상황은 거의 없다. 새로운 클래스를 작성하는 데 태그 필드가 등장한다면 태그를 없애고 계층구조로 대체하는 방법을 생각해보자. 
### 태그 달린 클래스 - 클래스 계층구조보다 훨씬 나쁘다!
```java
class Figure {
    enum Shape { RECTANGLE, CIRCLE };

    // Tag field - the shape of this figure
    final Shape shape;

    // These fields are used only if shape is RECTANGLE
    double length;
    double width;

    // This field is used only if shape is CIRCLE
    double radius;

    // Constructor for circle
    Figure(double radius) {
        shape = Shape.CIRCLE;
        this.radius = radius;
    }

    // Constructor for rectangle
    Figure(double length, double width) {
        shape = Shape.RECTANGLE;
        this.length = length;
        this.width = width;
    }

    double area() {
        switch(shape) {
            case RECTANGLE:
                return length * width;
            case CIRCLE:
                return Math.PI * (radius * radius);
            default:
                throw new AssertionError(shape);
        }
    }
}
```

### 태그 달린 클래스의 단점
* Enum 선언, 태그 필드, 태그에 다른 분기를 위한 switch 문 등 **불필요한 코드가 많다**.
* 여러 구현이 한 클래스에 혼합되어 있어서 **가독성이 나쁘다**.
* 다른 태그를 위한 코드 때문에 **불필요한 메모리 사용이 많다**.
* 필드를 final로 지정하려면 사용하지 않는 필드까지 생성자에서 초기화해야한다.
* 새로운 타입을 추가하기 복잡하다.
* 인스턴스의 타입만으로는 현재 나타내는 의미를 알 수 없다.

#### => 태그 달린 클래스는 장황하고, 오류를 내기 쉽고, 비효율적이다. 태그 달린 클래스는 클래스 계층구조를 어설프게 흉내낸 아류일 뿐이다.
**클래스 계층구조로 변환**
```java
abstract class Figure {
    abstract double area();
}

class Circle extends Figure {
    final double radius;

    Circle(double radius) { this.radius = radius; }

    @Override double area() { return Math.PI * (radius * radius); }
}

class Rectangle extends Figure {
    final double length;
    final double width;

    Rectangle(double length, double width) {
        this.length = length;
        this.width  = width;
    }
    @Override double area() { return length * width; }
}
```

### 클래스 계층구조로 변경시 장점
* 쓸데없는 코드가 사라졌다.
* 모든 필드가 final이다.
* 클래스의 생성자가 모든 필드를 초기화하고 추상 메서드를 모두 구현했는지 컴파일러가 확인해준다.
* 상속을 통한 자연스러운 확장이 가능하다.
* 유연성과 컴파일 타임 타입 검사 능력을 높여준다.

**확장하여 정사각형을 구현한 예시**
```java
class Square extends Rectangle {
    Square(double side) {
        super(side, side);
    }
}
