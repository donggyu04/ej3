# 79. 과도한 동기화는 피하라.

- 동기화 메서드나 동기화 블록 안에서는 제어를 절대로 클라이언트에 양도하면 안 된다.
    1. 동기화 블록 안에서는 재 정의 가능한 함수를 호출하면 안됨
    2. 클라이언트에서 넘어온 객체의 메서드를 호출해서는 안됨
    3. 위와 같은 메서드는 외계인과 같아서 통제가 불가능

- 구체적인 예
```java
    private final List<SetObserver<E>> observers = new ArrayList<>();

    public void addObserver(SetObserver<E> observer) {
        synchronized(observers) {
            observers.add(observer);
        }
    }

    public boolean removeObserver(SetObserver<E> observer) {
        synchronized(observers) {
            return observers.remove(observer);
        }
    }

    private void notifyElementAdded(E element) {
        synchronized(observers) {
            for (SetObserver<E> observer : observers) {
                observer.added(this, element);
            }
        }
    }

    @Override
    public boolean add(E element) {
        boolean added = super.add(element);
        if (added) {
            notifyElementAdded(element);
        }
        return added;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean result = false;
        for (E element : c) {
            result |= add(element);  // Calls notifyElementAdded
        }
        return result;
    }
```

```java
@FunctionalInterface
public interface SetObserver<E> {
    // Invoked when an element is added to the observable set
    void added(ObservableSet<E> set, E element);
}
```

```java
// set에 데이터가 추가되면 해당 데이터를 출력

public class Test1 {
    public static void main(String[] args) {
        ObservableSet<Integer> set = new ObservableSet<>(new HashSet<>());

        set.addObserver((s, e) -> System.out.println(e));

        for (int i = 0; i < 100; i++) {
            set.add(i);
        }
    }
}
```

- 몇 가지 실험

    1. 정수 값을 출력하다가 23번째일때 구독 취소.
        - 23출력 직후 `ConcurrentModificationException 발생`
        - 순회 도중에 제거를 하려고 했기 때문

    ```java
    public class Test2 {
        public static void main(String[] args) {
            ObservableSet<Integer> set = new ObservableSet<>(new HashSet<>());

            set.addObserver(new SetObserver<>() {
                public void added(ObservableSet<Integer> s, Integer e) {
                    System.out.println(e);
                    if (e == 23)
                        s.removeObserver(this);
                }
            });

            for (int i = 0; i < 100; i++)
                set.add(i);
        }
    }
    ```

    2. 구독 취소를 또 다른 쓰레드에게 양도
        - Dead lock 발생
        - `s.removeObserver()` 에서 lock을 얻으려는데 메인 쓰레드에서 이미 lock을 가져감
        - 메인 쓰레드는 `s.removeObserver()` 가 끝나기만 기다림

    ```java
    public class Test3 {
    public static void main(String[] args) {
        ObservableSet<Integer> set = new ObservableSet<>(new HashSet<>());

        set.addObserver(new SetObserver<>() {
            public void added(ObservableSet<Integer> s, Integer e) {
                System.out.println(e);
                if (e == 23) {
                    ExecutorService exec = Executors.newSingleThreadExecutor();
                    try {
                        exec.submit(() -> s.removeObserver(this)).get();
                    } catch (ExecutionException | InterruptedException ex) {
                        throw new AssertionError(ex);
                    } finally {
                        exec.shutdown();
                    }
                }
            }
        });

        for (int i = 0; i < 100; i++)
            set.add(i);
    }
    ```

- 해결법
    1. 외계인 메서드 호출 부분을 동기화 블럭 밖으로 뺴면 된다.
    ```java
    // Alien method moved outside of synchronized block - open calls

    private void notifyElementAdded(E element) {
        List<SetObserver<E>> snapshot = null;

        synchronized(observers) {
            snapshot = new ArrayList<>(observers);
        }

        for (SetObserver<E> observer : snapshot) {
            observer.added(this, element);
        }
    }
    ```

    2. `CopyOnWriteArrayList` 사용
        - ArrayList 구현 class
        - 항상 깨끗한 복사본을 만들어 수행하도록 구현 됨 (순회 시 lock 필요 없음)
        - 수정할 일은 드물고 수회만 빈번히 일어나는 관찰자 리스트 용도로 최적

    ```java
    private final List<SetObserver<E>> observers = new CopyOnWriteArrayList<>();

    public void addObserver(SetObserver<E> observer) {
        observers.add(observer);
    }

    public boolean removeObserver(SetObserver<E> observer) {
        return observers.remove(observer);
    }

    private void notifyElementAdded(E element) {
        for (SetObserver<E> observer : observers) {
            observer.added(this, element);
        }
    }
    ```

- 정리
    1. 기본 규칙은 동기화 영역에서는 가능한 일을 적게하는 것.
    2. 교착상태와 데이터 훼손을 피하려면 동기화 블록안에서는 외계인 메서드 호출 금지