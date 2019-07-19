## null이 아닌, 빈 컬렉션이나 배열을 반환하라

### Collection의 size가 0이라면 null?
````
private final List<Cheese> cheeseInStock = ...;

public List<Cheese> getCheeses() {
    return cheeseInStock.isEmpty() ? null : new ArrayList<>(cheeseInStock);
}
````
- cheeseInStock이 empty 상태라고 해서 특별히 취급할 이유가 없다

### null return의 단점
1. 호출하는곳에서 null check가 필요하다 
2. null check를 안함으로써 발생하는 문제는 RTE기 때문에 문제가 발생할때까지 파악 할 수 없다

### null을 return 했던 이유는?
- 빈 컬렉션을 리턴시 매번 할당한다면 성능상의 이슈가 생길수도 있다고 하지만, 불변 컬렉션을 반환하면 해결!
