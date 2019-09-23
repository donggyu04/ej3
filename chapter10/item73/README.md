# 73. 추상화 수준에 맞는 예외를 던지라 

### 수행하려는 일과 관련없어 보이는 예외가 튀어나오면 안된다.
사용자 목록에 정상적인 사용자를 추가했는데 IllegalArgumentException이 발생한다고 생각해보자.

알고보니 사용자를 추가하는 addUser 함수 내부에서 나이를 체크하는 로직이 있었고, 
새로 추가된 사용자의 나이가 조건에 부합하지 않았다면 IllegalArgumentException은 매우 적절한 예외이다.

하지만 적절한것은 하위 수준에서만이고 addUser에서는 해당 예외를 자신의 추상화 수준에 맞게 변경해서 던져야 하는데
이걸 예외 번역(Exception Translation)이라고 한다

````
try {
    //하위 수준 예외를 상위 수준 예외로 번역 
    ... 
} catch (IllegalArgumentException e) {
    // 하위 수준의 예외가 디버깅에 도움이 된다면 chaining 되도록 포함시켜주자
    throw new InvalidAgeException(e);
    
}
````

* 예외를 무조건 전파하는것 보다는 예외 번역이 나은 방법이지만,      
   해결 가능한 예외라면 번역해서 전파하지 말고 처리하는게 더 좋다
