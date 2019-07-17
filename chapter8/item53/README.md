## 가변인수는 신중히 사용하라

### 가변인수에 최소 갯수를 정해주고 싶은경우
````
static int min(int... args) {
    if(args.length == 0) {
        throw new IllegalArgumentException("인수가 1개 이상 필요합니다.");
    }
    
    int min = args[0];
    for ...
}
````
- 인수가 없이 min()으로 호출하면 RTE 발생
- 코드도 깔끔하지 못하다 

#### 개선한 코드
````
static int min(int firstArg, int... remainArgs) {
    int min = firstArg;
    for ...
}
````
- 위의 방법을 활용하면 가변인수 사용으로 인한 성능 저하도 예방 할 수 있다 

### 가변인수로 인한 성능 저하를 예방하는 법
- 가변인수는 메서드 호출시마다 배열을 만들어서 파라미터를 할당해주기때문에 일반적인 메서드 호출보다는 비용이 비싸다
````
public void foo() {}
public void foo(int a1) {}
public void foo(int a1, int a2) {}
public void foo(int a1, int a2, int a3) {}
public void foo(int a1, int a2, int a3, int... rest) {}
````
- 파라미터를 4개이상 사용하는 경우가 적다고 가정했을때 `public void foo(int... args) {}`가 아니라  
   위의 코드처럼 성능 개선을 할 수 있다

>  `EnumSet.of` 가 좋은 예   
>   https://docs.oracle.com/javase/8/docs/api/index.html?java/util/EnumSet.html

