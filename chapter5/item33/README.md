# 타입 안전 이종 컨테이너를 고려하라

## 타입 안전 이종 컨테이너 패턴 (type safe heterogeneous container pattern)
- 서로다른 요소들을 하나의 컨테이너에 저장하면서 타입 안전도 보장하기 위한 패턴 
    - 일반적인 Map 과 비교해봤을때 모든 key 가 다른 타입일 수 있고, 모든 value 도 다른 타입일 수 있다.
````java
public class Favorites {
    private Map<Class<?>, Object> favorites = new HashMap<>();
    
    public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(Objects.requireNonNull(type), type.cast(instance));
    }

    public <T> T getFavorite(Class<T> type) {
        return type.cast(favorites.get(type));
    }
}

// main
public static void main(String[] args) {
    Favorites container = new Favorites();
    
    container.putFavorite(String.class, "JAVA");
    container.putFavorite(Integer.class, 4);
    container.putFavorite(Class.class, Favorites.class);
    
    String favoriteString = container.getFavorite(String.class);
    int favoriteInteger = container.getFavorite(Integer.class);
    Class<?> favoriteClass = container.getFavorite(Class.class);

    }
}
````
- 위 코드에서 문제점을 찾아보자
    1. raw type을 사용한다면 문제가 생긴다
        ```` 
            container.putFavorite((Class)Integer.class, "에베베");
     
            HashSet<Integer> set = new HashSet<>();
            ((HashSet)set).add("에베베");
        ````
    2. 실체화 불가 타입에는 사용할 수 없다 
        - String, String[] 등은 저장할 수 있지만, List<String>은 저장 할 수 없다.
        - List\<String>.class는 문법 오류가 나고, List.class는 List<Integer>.class 등과 구분할 수가 없다 
            - Spring에는 ParameterizedTypeReference라는 클래스가 있는데 완벽하진 않지만 이 문제를 해결하기 위한 시도로 만들어졌다
            - http://gafter.blogspot.com/2006/12/super-type-tokens.html
    
