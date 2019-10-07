# wait와 notify 보다는 동시성 유틸리티를 애용하라

wait과 notify를 **올바르게** 사용하기는 아주 까다롭다. 고수준 동시성 유틸리티를 사용하길 권장한다.

## java.util.concurrent 유틸리티의 세 가지 범주
* ExecutorService
* 동시성 컬렉션
* 동기화 장치
ExecutorService는 item80에서 다뤘고, 나머지 두 가지 범주에 대해 알아본다.

## 동시성 컬렉션
높은 성능을 위해 각자 내부에서 동기화를 수행한다. 따라서 동시성 컬렉션에서 동시성을 무력화하는것은 불가능하며, 외부에서 락을 추가로 사용하면 속도가 느려진다.

즉, 여러 메서드를 원자적으로 묶어서 호출 할 수도 없으므로 여러 기본 동작을 하나로 묶는 '상태 의존적 수정' 메서드들이 추가되었다. ex) Map.putIfAbsent()

**ConcurrentMap으로 구현한 동시성 정규화 맵**
```java
private static final ConcurrentMap<String, String> map = new ConcurrentHashMap<>();

public static String intern(String s) {
    String previousValue = map.putIfAbsent(s, s);
    return previousValue == nulll ? s : previousValue;
}
```

**ConcurremntMap으로 구현한 동시성 정규화 맵 - ConcurrentHashMap은 검색 기능에 최적화 되어있다!**
```java
public static String intern(String s) {
   String result = map.get(s); 
   if (result == null) {
       result = map.putIfAbsent(s, s);
       if (result == null) {
           result = s;
       }
   }
   return result;
}
```

## 동기화 장치
스레드가 다른 스레드를 기다릴 수 있게 한다.
* CountDownLatch
* Semaphore
* CyclicBarrier
* Exchanger
* Phaser

### CountDownLatch
일회성 장벽이다. 인자로 int 값을 받아서 countDown 메서드가 몇 번 호출되어야 대기중인 스레드들을 깨우는지 정할 수 있다.

**동시 실행 시간을 재는 간단한 프레임워크**
```java
public static long time(Executor executor, int concurrency, Runnable action) throws InterruptedException {
    CountDownLatch ready = new CountDownLatch(concurrency);
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(concurrency);

    for (int i = 0; i < concurrency; i++) {
        executor.execute(() -> {
            // 현재 스레드 준비 완료를 알림
            ready.countDown();
            try {
                // 시작 대기!
                start.await();
                action.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                // 작업 마쳤음을 알림
                done.countDown();
            }
        });
    }

    ready.await();  // 모든 스레드 준비 완료를 대기
    long startNanos = System.nanoTime();
    start.countDown();  // 시작!
    done.await();   // 모든 스레드가 작업을 마치기를 대기
    return System.nanoTime() - startNanos;
}
```

이 예제를 아래와 같이 호출하면 데드락에 빠진다.
```java
time(Executors.newSingleThreadExecutor(), 3, () -> {
    // do something
});
```

파라미터로 넘겨준 동시성 수준보다 Executor가 갖는 스레드 풀이 적으므로 Executor는 첫 번째 스레드가 종료하길 기다리고, 첫 번째 스레드는 다른 스레드가 생성되어 준비되길 기다린다. 이런 상태를 **thread starvation dealock**이라고 한다.

## wait와 notify를 사용하는 표준 방식
최대한 사용하지 않는게 좋겠지만, 레거시 코드를 유지 보수할 경우를 위해 알려주겠다.

**wait 메서드를 사용하는 표준 방식**
```java
synchronized (obj) {
    while (<조건 충족되지 않았다>)
        obj.wait()
    
    // 조건 충족시의 동작
}
```
**wait 메서드를 사용할 때에는 반드시 while loop을 사용해라. 반복문 밖에서는 절대로 호출하지 말자.**
만약 조건이 충족되어 notify()가 호출 된 이후 wait() 메서드가 호출된다면 해당 스레드가 깨어남을 보장 할 수 없다.

