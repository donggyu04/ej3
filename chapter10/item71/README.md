# 71. 필요 없는 검사 예외 사용은 피하라.

검사 예외를 싫어하는 자바 프로그래머가 많지만 제대로 활용하면 aPI와 프로그램의 질을 높일 수 있다.

```java
catch (TheCheckedException e) {
    throw new AssertionError();
}

} catch(TheCheckedException e) {
    e.printStackTrace();
    System.exit(1);
}
```

검사 예외가 단 하나 뿐이라면 오직 그 예외 때문에 API 사용자는 try블록을 추가해야하고 스트림에서 직접 사용하지 못하게 된다. 그러니 이런상황이라면 검사 예외를 안 던지는 방법이 없는지 고민해볼 가치가 있다.

```java
try {
    obj.action(args);
} catch (TheCheckedException e) {

}

if(obj.actionPPermitted(args)) {
    obj.action(args);
}
```
=> 아래의 코드가 더 유연하다. 
   but, 여러스레드가 동시에 접근 할수 있거나 외부요인에의해 상태가 변할 수 있다면 이 리팩토링은 적절하지 않다.


요약
* 꼭 필요한 곳에만 사용한다면 검사 예외는 프로그램의 안정성을 높여준다. 복구가 가능하고 호출자가 그 처리를 해주길 바란다면, 우선 Optional을 반환해도 될지 고민하자.