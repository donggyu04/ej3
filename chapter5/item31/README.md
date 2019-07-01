# 한정적 와일드카드를 사용해 API 유연성을 높이라

### 계속.. 공변과 불공변 
 - 이번 챕터에서 계속나오는 공변/불공변에 관한 이야기를 계속해보자 
 ````java
public class Stack<E> {
    public Stack();
    
    public E pop();
    
    public boolean isEmpty();
    
    public void push(E e);
        
    public void pushAll(Iterable<E> src) {
        for(E e: src) {
            push(e);
        }
    }
}

public static void main() {
    Stack<Number> stack = new Stack<Number>();
    Iterable<Integer> integers = ...;
    stack.pushAll(integers);
}
````
 - 얼핏 보기엔 정상 동작 할 것도 같은 위의 코드는 사실 에러가 난다 
     - Iterable\<Integer> src와 Stack\<Number>는 서로 관련이 없는 전혀 다른 타입이기 때문 
     - Generic은 불공변!

그렇다면 위와 같은 코드를 정상적으로 동작시키기 위해선 어떻게 해야 할까?

## 한정적 와일드 카드 
- 위와 같은 문제를 해결하기 위한 방법
- `pushAll` 함수의 매개변수를 E의 Iterable(`Iterable<E>`)이 아닌 E의 하위 타입의 Iterable(`Iterable<? extends E>`)로 변경해주면 된다 
```
public void pushAll(Iterable<? extends E> src) {
    for (E e : src) 
        push(e);
}
```
pushAll을 위와 같이 수정하면 정상적으로 동작한다, 

반대 경우인 popAll도 추가로 작성해보자


````
...
public void popAll(Collection<E> destination) {
    while(!isEmpty())
        destination.add(pop());
}

public static void main() {
    Stack<Number> stack = new Stack<>();
    Collection<Object> destination = ...;
    stack.popAll(destination);
}
````
- 위의 코드도 특별히 문제가 없어보이지만 pushAll과 같은 문제를 가지고 있다
     - Collection\<Object> destination와 Stack\<Number>는 관련이 없는 다른 타입(불공변)

--- 
pushAll의 경험을 살려 위의 문제를 해결해보자면

popAll의 매개변수가 E의 Collection(`Collection<E>`)가 아니라 E의 상위 타입의 Collection(`Collection<? super E>`)이 되어야 한다.
 - pushAll은 `원소의 생산자(producer)` 와일드 카드가 적용된 경우
 - popAll은 `원소의 소비자(Consumer)`에 와일드 카드가 적용된 경우
 - 추가적으로 매개변수가 소비자와 생산자 역할을 모두 한다면 와일드카드를 사용하지 말아야 한다 
 
## PECS (Producer-Extends,Consumer-Super)
- 매개변수 타입에 유연성을 주기 위한 원칙
    - = 와일드카드 타입을 사용하기 위한 원칙
- 매개변수 타입 T가 `생산자라면 <? extends T>`, `소비자라면 <? super T>`

## 복잡한 예제와 함께 마무리
````
public static <E extends Comparable<E>> E max(List<E> list);                    // 와일드 카드 미적용
public static <E extends Comparable<? super E>> E max(List<? extends E> list);  // 와일드 카드 적용
````

- 입력 파라미터 `List<? extends E> list` 
    - max 함수의 입력 파라미터인 list는 producer이기 때문에 extends로 유연성을 높였다
    - producer인지 consumer인지 헷갈린다면 데이터의 흐름을 생각하면 좋을듯 하다 
        - max를 계산하기위한 데이터를 제공 -> producer   
           가지고있는 데이터를 사용해서 max를 반환 -> consumer
           
- 타입 파라미터 `<E extends Comparable<? super E>> E`
    - 와일드카드를 미적용한것과 비교해보면 Comparable 부분만 변경된것을 볼 수 있는데    
       Comparable/Comparator는 언제나 Consumer 이기때문에 super를 통해서 유연성을 높였다 
       

## 진짜 마무리.. E 와 ? 어떤걸 써야할까
````
// 어떤게 나은 방식일까?
public static <E> void swap(List<E> list, int i, int j);
public static void swap(List<?> list, int i, int j);
````
- 타입 매개변수를 신경 쓸 필요없이 index 원소만 교환해주기 때문에 ?를 사용한 방식이 더 낫다
- 메서드 선언에 타입 매개변수가 한번만 나온다면 와일드카드로 대체하는것이 바람직하다

문제점
- List<?> 에 넣을수 있는 원소는 null 밖에 없다..  
    - List\<E>에는 E만 넣을 수 있다 
    - ? == null 이라고 볼수있는건가..?

해결책
- 제네릭 메서드로 helper 함수를 만들어서 타입을 알아낸다
```
// 기존 코드 -> 컴파일 불가
public static void swap(List<?> list, int i, int j) {
    list.set(i, list.set(j, list.get(i));
}


// helper 함수 사용
public static void swap(List<?> list, int i, int j) {
    swapHelper(list, i, j);
}
private static <E> void swapHelper(List<E> list, int i, int j) {
    list.set(i, list.set(j, list.get(i));
}
```
- 코드가 복잡해지지만 클라이언트는 복잡한 swapHelper의 존재를 모른체 와일드카드를 통해 더 쉽게 사용 할 수 있다

## 결론
- 구현이 복잡해지더라도 널리 쓰일 라이브러리를 작성한다면 `와일드카드`를 사용해서 `유연성`을 늘려주자
- `PECS` 