# 78. 공유 중인 가변 데이터는 동기화해 사용하라.

- 동기화의 기능

  1. 일관성이 깨진 상태를 볼 수 없게 함.
  2. 모든 이전 수정의 최종 결과를 볼 수 있도록 보장 함.

> Java 언어 명세상 long과 double외의 변수를 읽고 쓰는 동작은 atomic 이다.

- atomic 동작? 그럼 성능 향상을 위해 atomic 동작에는 동기화를 말아야 할까?
  1. 아주 위험한 발상
  2. 자바 언어는 쓰레드가 필드를 읽을 때 **항상 수정이 완전히 반영된** 값을 얻는 것은 보장 하지만, 한 쓰레드가 저장한 값이 다른 쓰레드에게 **보이는가**는 보장하지 못함.
  3. **동기화는 배타적 실행뿐 아니라 쓰레드 사이의 안정적인 통신에 꼭 필요하다.**

> Thread.stop()은 사용하지 말 것 (deprecated, 안전성 보장 못함) 

- 그럼 다른 쓰레드를 멈추는 올바른 방법은?
    1. 쓰레드는 자신의 boolean 필드를 폴링하면서 그 값에 따라 멈춤.
    2. 다른 쓰레드에서 위 쓰레드를 멈추고자 할 때 해당 값을 변경 시킴.

```java
public class StopThread {
    private static boolean stopRequested;

    public static void main(String[] args) throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            while (!stopRequested) {
                i++;
            }
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(1);
        stopRequested = true;
    }
}
```
- 위 쓰레드는 1초 후에 멈출 것인가?

    1. 멈 추지 않고 실행 됨
    2. 원인은? 동기화!
    3. 해결법은?

```java
public class StopThread {
    private static boolean stopRequested;

    private static synchronized void requestStop() {
        stopRequested = true;
    }

    private static synchronized boolean stopRequested() {
        return stopRequested;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            while (!stopRequested()) {
                i++;
            }
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(1);
        requestStop();
    }
}
```
> 읽기 쓰기 모두에 동기화 하지 않으면 동작을 보장하지 않는다.

- 위 코드는 배타적 수행과 쓰레드간 통신중 통신 목적으로만 사용됐다.

    1. volatile을 사용하면 동기화 부분을 제거 할 수 있다.
    2. volatile 한정자는 배타적 수행과 관계 없이 항상 가장 최근에 기록된 값을 가져온다.

```java
public class StopThread {
    private static volatile boolean stopRequested;

    public static void main(String[] args) throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            while (!stopRequested) {
                i++;
            }
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(1);
        stopRequested = true;
    }
}
```

- volatile은 주의해서 사용해야 한다.

```java
private static volatile int nextSerialNumber = 0;

public static int generateSerialNumber() {
    return nextSerialNumber++;
}
```

  1. 위 코드의 문제점은 `++` 연산.
  2. `++` 연산은 코드상으로는 하나지만 실제로는 `nextSerialNumber` 필드에 2번 접근한다 (먼저 값을 읽고, 그런 다음 새로운 값을 저장)
  3. 만약 이 2번의 접근 사이에 다른 쓰레드가 들어와 값을 읽어 가면 변경 전의 데이터를 읽어가게 된다. (safety failure)

- 위의 문제 해결법
  1. `generateSerialNumber` 메서드에 `synchronized` 사용
  2. `java.util.concurrent.atomic` 패키지 사용

```java
private static final AtomicLong nextSerialNum = new AtomicLong();

public static long generateSerialNumber() {
    return nextSerialNum.getAndIncrement();
}
```

- 정리
  1. item78의 문제를 해결하는 가장 좋은 방법은 애초에 가변데이터를 공유하지 않는 것
  2. 여러 쓰레드가 가변 데이터를 공유한다면 그 데이터를 읽고 쓰는 동작은 반드시 동기화 작업을 해야한다.