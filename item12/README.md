## toString을 항상 재정의하라.
[toString()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html#toString())의 규약을 보면 아래와 같은 명세가 있다.
 - 간결하면서 사람이 읽기 쉬운 형태의 유익한 정보를 반환한다.
 - 모든 하위 클래스에서 이 메서드를 재정의하라.
즉, toString을 잘 구현한 클래스는 디버깅하기도 쉽고, 테스트시에 failure 메시지도 읽기 좋고, 사용하기에도 훨씬 즐겁다.(읽기 쉽고 유익한 정보를 반환하기에)
toString()을 재정의시에 포맷(주석)을 명시하는 것이 좋습니다.

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

    @Override
    public int hashCode() {
        int result = Integer.hashCode(age);
        result = 31 * result + name.hashCode();
        return result;
    }

    /**
     * People 객체에 대한 field들을 리턴합니다.
     * 형식은 아래와 같습니다.
     * "이름-나이"
     */
    @Override
    public String toString() {
        return String.format("%s-%d", name, age);
    }
}
```