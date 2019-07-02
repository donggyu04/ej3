# 제네릭과 가변인수를 함께 쓸 때는 신중하라

### 가변인수 메서드 
- 가변인수 메서드를 호출하면 자동적으로 가변인수들을 담기위한 배열이 만들어진다
- 앞선 item들에서 배열(공변)과 제네릭(불공변)을 함께 사용함으로써 문제가 생기던 case들과 동일한 문제가 발생 가능
````
static void dangerous(List<String>... stringLists) {
    List<Integer> intList = List.of(42);
    Object[] objects = stringLists;
    object[0] = intList;                // StringList 배열에 IntegerList 할당 
    String s = stringLists[0].get(0);   // ClassCastException
}
````
- 직접적인 형변환이 없지만 ClassCastException이 발생한다 

## 왜 컴파일 타임에 막아주지 않는건가?
- 안전하지 않은 코드지만 사용하기에 좋기 때문
    - Java API에도 많이 포함되어있는데 이 메서드들은 안전한다
    ````
    Arrays.asList(T... a)
    Collections.addAll(Collection<? super T> c, T... elements)
    ````
- `@SafeVarargs`
    - 메서드에 붙는 어노테이션으로 제네릭 가변인수를 사용함으로써 필연적으로 발생하는 경고를 숨겨줌
    - 안전한 메서드인게 확실하지 않다면 붙이면 안됨 
    - 아래 두가지 조건을 만족하면 안전하다고 할 수 있다
        1. 전달받은 매개변수 배열에 아무것도 저장하지 않는다
        2. 전달받은 매개변수 배열을 노출하지 않는다
    - 재정의가 가능하면 안되기떄문에 static / final 메서드에만 붙일 수 있다.