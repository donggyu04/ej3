# 프로그램의 동작을 스레드 스케줄러에 기대지 말라!

여러 스레드가 실행 중이면 운영체제의 스레드 스케줄러가 어떤 스레드를 얼마나 오래 실행할지 정한다. 정상적인 운영체제라면 이 작업을 공정하게 수행(라운드로빈?) 하지만 구체적인 스케줄링 정책은 운영체제마다 다를 수 있다. 따라서 잘 작성된 프로그램이라면 이 정책에 좌지우지돼서는 안된다. **정확성이나 성능이 스레드 스케줄러에 따라 달라지는 프로그램이라면 다른 플랫폼에 이식하기 어렵다.**

견고하고 이식성 좋은 프로그램을 작성하는 가장 좋은 방법은 **실행 가능한 스레드의 평균적인 수를 프로세서 수보다 지나치게 많아지지 않도록 하는것이다.** 그래야 스레드 스케줄러가 고민할 거리가 줄어든다. 실행 준비가 된 스레드들은 맡은 작업을 완료할 때까지 계속 실행되도록 만들자. 

실행 가능한 스레드 수를 적게 유지하는 주요기법은 각 스레드가 무언가 유용한 작업을 완료한 후에는 다음 일거리가 생길 때까지 대기하도록 하는 것이다. **스레드는 당장 처리해야할 작업이 없다면 실행돼서는 안된다.**  예) 실행자 프레임워크를 예로 들면, 스레드 풀 크기를 적절히 설정하고 작업은 짧게 유지하면된다. 단, 너무 짧으면 작업을 분배하는 부담이 오히려 성능을 떨어뜨릴 수 도 있다. 

스레드는 바쁜 대기(busy waiting)상태가 되면 안된다. busy waiting상태는 스레드 스케줄러의 변덕에 취약할 뿐 아니라, 프로세서에 큰 부담을 주어 다른 유용한 작업이 실행될 기회를 박탈한다. 

> 바쁜대기(busy waiting) - 어떤 특장 공유자원에 대하여 두개 이상의 프로세스나 스레드가 그 이용권한을 획득하고자 하는 동기화 상황에서 그 권한 획득을 위한 과정에서 일어나는 현상. 대부분의 경우에 스핀락과 이것을 동일하게 생각하지만 엄밀히 말하자면 스핀락이 바쁜 대기 개념을 이용한 것이다. 



```java
//끔찍한 CountDownLatch 구현 - 바쁜 대기 버전! 
public class SlowCountDownLatch{
  private int count;
  
  public SlowCountDownLatch(int count){
    if(count <0){
      throw new IllegalArgumentException(count + " < 0");
    }
    this.count = count;
  }
  
  public void await(){
    while(true){
      synchronized(this){
        if (count == 0)
          return;
      }
    }
  }
  
  public synchronized void countDown(){
    if(count != 0){
      count--;
    }
  }
}
```

내 컴퓨터에서 래치를 기다리는 스레드를 1,000개를 만들어 자바의 CountDownLatch와 비교해 보니 약 10배가 느렸다. 하나 이상의 스레드가 필요도 없이 실행 가능한 상태인 시스템은 흔하게 볼 수 있다. 이런 시스템은 성능과 이식성이 떨어질 수 있다.



특정 스레드가 다른 스레드들과 비교해 CPU시간을 충분히 얻지 못해서 간신히 돌아가는 프로그램을 보더라도 Thread.yield (양도하다, 우선순위를 내어주다) 를 써서 문제를 고쳐보려는 유횩을 떨쳐내자. 테스트할 수단도 없다. 차라리 애플리케이션 구조를 바꿔 동시에 실행 가능한 스레드 수가 적어지도록 조치해주자. 스레드 몇 개의 우선순위를 조율해서 애플리케이션의 반응 속도를 높이는 것도 타당할 수 있겠으나, 절대 합리적이지 않다. 



## 핵심정리

프로그램의 동작을 스레드 스케줄러에 기대지 말자. 견고성과 이식성을 모두 해치는 행위다. 같은 이유로, Thread.yield와 스레드 우선순위에 의존해서도 안된다. 이 기능들은 스레드 스케줄러에 제공하는 힌트일 뿐이다. 스레드 우선순위는 이미 잘 동작하는 프로그램의 서비스 품질을 높이기 위해 드물게 쓰일 수 있지만, 간신히 동작하는 프로그램을 ‘고치는 용도’로 사용해서는 절대 안된다.

