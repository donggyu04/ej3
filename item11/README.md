## equals를 재정의하려든 hashCode도 재정의하라.
[hashCode()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html#hashCode())에 대한 명세를 확인해보면 다음과 같은 3가지 규약이 있다.
 - equals 비교에 사용되는 정보가 변경되지 않았다면, 애플리케이션이 실행되는 동안 그 객체의 hashCode는 몇 번을 호출해도 일관되게 항상 같은 값을 반환해야 한다.
 - euqals가 두 객체를 같다고 판단했다면, 두 객체의 hashCode는 똑같은 값을 반환해야 한다.
 - euqals가 두 객체를 다르게 판단했더라도, 두 객체의 hashCode가 서로 다른 값을 반환할 필요는 없다. 단, 다른 객체에 대해서는 다른 값을 반환해야 해시테이블의 성능이 좋아진다.
위 3가지 규약에서 2번째 규약을 보면 `두 객체의 hashCode는 똑같은 값을 반환해야 한다.`라고 명세되어 있다. 이를 어길시 `HashMap`, `HashSet`과 같은 collection 객체에서 이슈가 발생할 여지가 상당히 높다. 아래는 `HashMap`의 `get()`에 대한 코드이다.
```java
    public V get(Object key) {
        Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }
    
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```

### hashCode를 재정의하는 간단한 방법
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
        // 기본 타입 필드라면, Type.hashCode(field)를 수행.
        // field는 equals에 사용된 핵심 필드를 말한다.
        int result = Integer.hashCode(age);
        result = 31 * result + name.hashCode();
        return result;
    }
}
```