## 상속보다는 컴포지션을 사용하라 

#### 상위 클래스와 하위 클래스 모두 같은 프로그래머가 통제한다면 괜찮지만, 그렇지 않은 상속은 많은 문제를 야기할 수 있다

#### HashSet의 예 
- HashSet의 성능을 높이려면 생성된 이후 추가된 원소의 수를 알아야 한다고 한다.
```java
    public class InstrumentedHashSet<E> extends HashSet<E> {
        private int addCount = 0;
      
        ...
      
        @Override
        public boolean add(E e) {
            addCount++;
            return super.add(e);
        }
        
        @Override
        public boolean addAll(Collection<? extends E> c) {
            addCount += c.size();
            return super.addAll(c);
        }
    }
```
- 위의 예에서 볼 수 있는 자신이 관리하지 않는 상위 클래스에 대한 상속이 좋지 않은 이유
    1. HashSet의 addAll은 add 함수를 사용해서 구현되어있기 때문에 addCount가 틀리게 된다 
    2. addAll에서 addCount를 + 하지 않도록 수정하더라도 언제 HashSet의 addAll 메서드가 바뀔지 알 수 없다
    3. Collection c 를 loop 돌며 super.add를 호출한다고 하더라도 성능/번거로움/버그 가능성 등의 이슈가 남아있다 
    4. 상위클래스에서 add 함수와 같은 addElement 함수가 추가된다고 하면 addCount를 보장 할 수 없다. 

#### 그러니깐 Composition을 사용하자
- 위와 같은 문제를 피하고 싶다면 기존(상위) 클래스를 인스턴스로 갖도록 구성하자 
    - 상속은 순수한 is-a 관계에서만 사용하자 
    - 상속은 캡슐화를 해친다 
```java
public class SetWrapper<E> implements Set<E> {
    private final Set<E> set; //멤버 변수로 포함
    public SetWrapper(Set<E> s) {
        this.set = s;
    }
    
    //Override methods - 주입 받은 set을 통해서 한번씩 다시 호출
    public void clear() {
        set.clear();
    }
    
    public boolean add(E e) {
        set.add(e);
    }
    
    ...
}

    public class InstrumentedHashSet<E> extends SetWrapper<E> {
        private int addCount = 0;
      
        ...
      
        @Override
        public boolean add(E e) {
            addCount++;
            return super.add(e);
        }
        
        @Override
        public boolean addAll(Collection<? extends E> c) {
            addCount += c.size();
            return super.addAll(c);
        }
    }
```

- InstrumentedHashSet은 SetWrapper를 상속받는 다는것을 제외하면 동일하지만, 
   super 메서드들의 동작이 달라지게 된다 

- 위와 같은 형태를 Wrapper 클래스라 하고, 데코레이터 패턴을 사용한 예이다.
    - 큰 의미로는 delegate 패턴에도 포함된다.
    
- Wrapper 클래스는 단점이 거의 없지만 callback 과는 어울리지 않는다
    - SELF Problem 이라고 하는데, 내부 클래스의 this를 어딘가로 전달하고 callback이 호출된다면 어떻게 동작할지 예상할 수 없게된다.
    - https://stackoverflow.com/questions/28254116/wrapper-classes-are-not-suited-for-callback-frameworks
