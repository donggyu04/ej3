## 다중정의는 신중히 사용하라.

### 다중정의와 재정의
```java
public class CollectionClassifier {
    public static String classify(Set<?> s) {
        return "집합";
    }

    public static String classify(List<?> lst) {
        return "리스트";
    }

    public static String classify(Collection<?> c) {
        return "그 외";
    }

    public static void main(String[] args) {
        Collection<?>[] collections = {
                new HashSet<String>(),
                new ArrayList<BigInteger>(),
                new HashMap<String, String>().values()
        };

        for (Collection<?> c : collections)
            System.out.println(classify(c));
    }
}
```


=> 어느 메소드를 호출할지가 컴파일 타임에 정해지기 때문에..

```java
class Wine {
    String name() { return "포도주"; }
}

class SparklingWine extends Wine {
    @Override
    String name() {
        return "발포성 포도주";
    }
}

class Champagne extends SparklingWine {
    @Override
    String name() {
        return "샴페인";
    }
}

public class Overriding {
    public static void main(String[] args) {
        List<Wine> wineList = List.of(
                new Wine(), new SparklingWine(), new Champagne());

        for (Wine wine : wineList)
            System.out.println(wine.name());
    }
}
```

=> 재정의한 메소드는 동적으로 선택되고, 다중정의한 메소드는 정적으로 선택된다.

### 다중정의사용시 유의점

1. 안전하고 보수적으로 가려면 매개변수 수가 같은 다중정의는 만들지 말자
2. 다중정의하는대신 메소드 이름을 다르게 지어주자
※ readBoolean, readnt ...

1번과 비슷하게 Auto boxing으로 생기는 문제..
```java

public class SetList {
    public static void main(String[] args) {
        Set<Integer> set = new TreeSet<>();
        List<Integer> list = new ArrayList<>();

        for (int i = -3; i < 3; i++) {
            set.add(i);
            list.add(i);
        }
        for (int i = 0; i < 3; i++) {
            set.remove(i);
            list.remove(i);
        }
        System.out.println(set + " " + list);
    }

```


=> 결과는..? [-3,-2,-1] , [-2,0,2]

set.remove는 remove(Object)
list.remove(i) 의경우 remove(int index) 와 remove(Object)가 있어 remove(int) 형태를 취하여 해당오류가 발생

```java
for (int i = 0; i < 3; i++) {
    set.remove(i);
    list.remove((Integer)i);
}
```

### 람다에서의 다중정의 문제
```java
public class Lamda {
    public static void main(String[] args) {
        //1번 Thread
        new Thread(System.out::println).start();

        //2번 Excutor Service
        ExecutorService es = Executors.newCachedThreadPool();

        es.submit(System.out::println);

    }
}
```

=>  <T>submit(java.util.concurrent.Callable<T>) in java.util.concurrent.ExecutorService

2번의 경우에 참조메소드(println)메소드와 호출메소드(submit)이 양쪽 다 다중정의 되어 해당 문제가 발생하였다.

=> 따라서 메소드를 다중정의 할 때 , 서로 다른 함수형 인터페이스라도 같은 위치의 인수로 받아서는 안된다.

### 같은 객체를 입력받는 다중 정의 메소드는 동일 동작하게 하라
```java
public boolean contentEquals(StringBuffer sb) {
    return contentEquals((CharSequence)sb);
}
```

### 요약
1. 다중정의 허용한다고 해서 꼭 활용할 필요는 없다.
2. 매개변수 수가 같을때는 다중정의를 피하라.
3. 만약 매개변수가 같은 다중정의를 피할수 없는 경우, 같은객체를 입력받는 다중정의 메소드는 동일 동작하게 하라.