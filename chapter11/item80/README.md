# 80. 쓰레드보다는 실행자, 테스크, 스트림을 애용하라.

- `java.util.concurrent` 패키지의 등장 및 주요 기능
    1. 특정 테스트가 완료되기를 기다림
    2. 테스크 중 아무거나 하나(invokeAny), 모든 테스트(invokeAll)의 완료를 기다림
    3. 완료된 테스크의 결과를 차례로 받기
    4. 테스크를 특정 시간, 주기로 실행
    5. etc...

```java

ExecutorService exec = Executors.newSingleThreadExecutor();

// run task
exec.execute(runnable);

// grace full termination
exec.shutdown();

```

 - 상황에 따라 적절한 ThreadPool 구현체를 사용하면 됨.