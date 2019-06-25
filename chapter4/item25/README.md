# 톱레벨 클래스는 한 파일에 하나만 담으라

소스 파일 하나에 톱레벨 클래스를 여러개 선언하여도, 자바 컴퍼일러는 문제삼지 않는다. 

하지만 문제가 되는 경우는 존재한다. 
예를들면 한 파일에 클래스 2개가 정의되어있는데, 
다른 파일에 똑같은 클래스명으로 클래스 2개가 정의되어 있는 경우이다. 
이럴 때는 컴파일이 실패하거나, 
컴파일 순서에 따라 어떻게 동작할 지 예측할 수 없게 된다.

```java
package com.github.sejoung.codetest.toplevel;

// 코드 25-1 두 클래스가 한 파일(Utensil.java)에 정의되었다. - 따라 하지 말 것! (150쪽)
class Utensil {
    static final String NAME = "pan";
}

class Dessert {
    static final String NAME = "cake";
}
```

```java
package com.github.sejoung.codetest.toplevel;

public class Main {
    public static void main(String[] args) {
        System.out.println(Utensil.NAME + Dessert.NAME);
    }
}
```

```java
package com.github.sejoung.codetest.toplevel;

// 코드 25-2 두 클래스가 한 파일(Dessert.java)에 정의되었다. - 따라 하지 말 것! (151쪽)
class Utensil {
    static final String NAME = "pot";
}

class Dessert {
    static final String NAME = "pie";
}
```

```java

javac Main.java Dessert.java


javac Main.java Utensil.java

```

해결책은 간단하나 하나의 파일에 하나의 클래스만 담으면 된다.

굳이 여러게의 클래스를 하나의 파일에 담고 싶으면 정적맴버 클래스를 사용하는 방법도 있다.

```java
package com.github.sejoung.codetest.toplevel;

// 코드 25-3 톱레벨 클래스들을 정적 멤버 클래스로 바꿔본 모습 (151-152쪽)
public class Test {
    public static void main(String[] args) {
        System.out.println(Utensil.NAME + Dessert.NAME);
    }

    private static class Utensil {
        static final String NAME = "pan";
    }

    private static class Dessert {
        static final String NAME = "cake";
    }
}
```