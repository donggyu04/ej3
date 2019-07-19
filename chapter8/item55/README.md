## 옵셔널 반환은 신중히 하라

### Optional의 필요성
optional 이전에는 return에 적합한 값을 찾지못하면 null을 리턴하거나 예외를 던졌다
- null return은 Item 54에서 이유로 추천되지 않음
- 예외는 정말 예외 상황에서만 사용해야하고, stack trace를 캡쳐하는 비용도 크다 
> optional을 리턴하는 메서드에서는 절대 null을 리턴하지 말자    
> null도 포함가능한 optional을 만들고 싶으면 `Optional.ofNullAble(value)` 사용

### Optional 활용법 
1. optional은 API의 사용자에게 `반환값이 없을 수도 있다`라는걸 명확하게 알려줄 수 있다
2. API의 사용자가 값이 없는 경우에 대한 처리를 간편하게 할 수있다
    ````
    //기본값 설정 (eager)
    String ret = max(inputs).orElse("returnValue"); 

    //기본값 설정 (lazy)
    String ret = max(inputs).orElseGet(() -> "returnValue"); 

    //예외 던지기
    String ret = max(inputs).orElseThrow(SomeException::new); 
    
    ````
    - `isPresent`를 사용해서 값이 있는지 알 수 있지만..   
       대부분 `isPresent`를 쓰지 않고도 다른 메서드를 통해서 같은 효과를 얻을 수 있다
       ```
       Optional<ProcessHandler> parentProcess = ph.parent();
       String parentPid = parentProcess.isPresent() ? parentProcess.get().pid() : "N/A";
       
       String parentPid = ph.parent()
                            .map(process -> String.valueOf(process.pid())
                            .orElse("N/A");
       ```

### Collection, Stream, 배열 등은 Optional로 감싸면 안된다 
위와 같은 컨테이너 타입을 옵셔널로 감싸면 좋지 않다
- Item 54의 내용처럼 빈 컨테이너를 반환하는걸 추천
- API 사용자가 처리해야하는 코드 증가
- Optional 객체로 감싸는 만드는 비용도 존재
> primitive 타입의 랩퍼 클래스를 Optional에 넣으면 랩핑이 중첩됨으로 비효율적이다   
> primitive 타입을 위한 별도의 optional 클래스가 존재 (`OptionalInt`, `OptionalLong`, `OptionalDouble`)