# 인스턴스화를 막으려면 private 생성자를 사용하라

가끔은 static 메서드와 static 필드들만을 갖는 클래스를 만들때가 있습니다. 이런 방식으로 구현된 클래스는 객체지향의 관점에서 프로그래밍되지 않았을 가능성이 높지만 적절하게 사용할 경우 아주 유용합니다.

### 이런 경우 고려해 볼 만 하다
1. Primitive type 혹은 Array 관련 메서드들을 모아놓은 경우
    ```java
    // java.util.Arrays
    // ex) List<String> stooges = Arrays.asList("Larry", "Moe", "Curly");
    public static <T> List<T> asList(T... a) {
        return new ArrayList<>(a);
    }
    ```
2. 특정 인터페이스를 구현하는 객체를 생성해주는 static 메서드 혹은 팩토리 메서드를 모아놓는 경우
    ```java
    // java.util.Collections
    // ex) List<String> empty = Collections.emptyList();
    public static final List EMPTY_LIST = new EmptyList<>();
    public static final <T> List<T> emptyList() {
        return (List<T>) EMPTY_LIST;
    }
    ```
3. Final 클래스와 관련한 메서드를 모아놓을 경우.  
    => final 클래스는 상속할 수 없으므로 메서드를 추가할 수 없다.

### 인스턴스화를 막는 방법
추상 클래스로 만드는 것으로는 클래스의 인스턴스화를 막을 수 없습니다. 하위 클래스를 만들어서 인스턴스화 가능하기 때문입니다.  
인스터스화를 막기위해서는 private 생성자를 만들어야합니다. 생성자를 명시하지 않으면 컴파일러가 자동으로 기본 생성자를 만들기 때문에 인스턴스화 가능합니다.

### 인스턴스를 만들 수 없는 유틸리티 클래스
```java
public class MyUtil {
    // Prevents instanciation
    private MyUtil() {
        throw new AssertionError();
    }
    ...
}
```
* 반드시 AssertionErorr를 던질 필요는 없지만, 클래스 안에서 실수로 생성자 호출을 하는것을 방지합니다.
* Private 생성자는 직관적이지 않으므로 적절한 주석을 달아주길 추천합니다.
* 상속을 불가능하게 하는 효과가 있습니다. 하위 클래스의 생성자에서 상위 클래스의 생성자를 호출할 수 없게 됩니다.


### 개인적인 의견
* 클래스의 이름이나 용도를 보았을 때 정황상 인스턴스화를 막기 위해서 private 생성자를 사용했음을 파악할 수 있을 것이라고 생각합니다. 굳이 주석까지는 필요 없을 것이라 생각합니다.

