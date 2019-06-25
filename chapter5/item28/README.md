## 배열보다는 리스트를 사용하라.

### 배열 vs 제네릭 타입

#### 배열(공변) vs 제네릭 타입(불공변)
 - Sub가 Super의 하위 타입이라면, 배열 Sub[]는 배열 Super[]의 하위 타입이 된다.
 - 서로 다른 타입 Type1과 Type2가 있을 때, List<Type1>은 List<Type2>의 하위 타입도 아니고 상위 타입도 아니다.

```java
Object[] ObjectArray = new Long[1];
ObjectArray[0] = "타입이 달라 넣을 수 없다."; //ArrayStoreException을 던진다. 런타임시에 알 수 있다.
``` 
```java
List<Object> ol = new ArrayList<Long>();
ol.add("타입이 달라 넣을 수 없다."); //컴파일시에 알 수 있다.
```
> 공변(covariant) - 자기자신과 자기자신을 상속받은 타입을 허용합니다.<br>
> 불공(Invariant) - 자기자신만을 허용합니다.

#### 배열(실체화) vs 제네릭 타입(소거) 
 - 배열은 런타임에도 자신이 담기로 한 원소의 타입을 인지하고 확인한다.
 - 제네릭 타입은 컴파일 시에 타입을 검사하며 런타임에는 타입 정보를 소거한다.
 
 
#### 배열과 제네릭은 섞어쓰기란 어렵다.
```java
public class Chooser {
    private final Object[] choiceArray;

    public Chooser(Collection choices) {
        choiceArray = choices.toArray(choices);
    }

    public Object choose() { //메소드를 사용할때마다 타입 캐스팅을 해야한다.
        Random rnd = ThreadLocalRandom.current();
        return choiceArray[rnd.nextInt(choiceArray.length)];
    }
```

```java
public class Chooser<T> {
    private final T[] choiceArray;

    public Chooser(Collection<T> choices) {
        choiceArray = (T[]) choices.toArray(choices); // Unchecked Cast 경고 발생
    }

    public T choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceArray[rnd.nextInt(choiceArray.length)];
    }
```

```java
public class Chooser<T> {
    private final List<T> choiceList;

    public Chooser(Collection<T> choices) {
        choiceList = new ArrayList<>(choices);
    }

    public T choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceList.get(rnd.nextInt(choiceList.size()));
    }
```
배열과 제네릭은 섞어쓰기는 어렵다. 섞어쓰다가 컴파일 오류나 경고를 만나면, 가장 먼저 배열을 리스트로 대체하는 방법을 적용해보자.