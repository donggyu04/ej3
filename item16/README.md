## Public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라
Public 클래스에서 public 필드를 사용하는 단점은 크게 3가지가 있다
#### 1. API를 수정하지 않고는 내부 표현을 바꿀 수 없다
- public 필드명을 변경해야 하는 경우
```java
    class PublicFieldClass {
      // myString 으로 수정하기 위해선 사용한 곳을 모두 찾아 수정해줘야함
      public String mySting; 
    } 
  
    class PrivateFieldClass {
      // mySting이 직접적으로 사용되는곳은 getter 뿐
      private String mySting;
      
      public String getMyString() {
        return this.mySting;
    }
  }
```
#### 2. 불변식을 보장할 수 없다.
- public 필드는 일반적으로 변경이 자유롭다.
```
    class PublicFieldClass {  
      public String myString;
    }
  
    
    PublicFieldClass publicFieldClass = new PublicFieldClass();
    publicFieldClass.myString = "asd";

```
- final 필드라면 불변은 보장하지만 여전히 1,3 번 문제가 남아있다.
```java
    class PublicFinalClass {
      public final String myString;
      
      public PublicFinalClass(String myString) {
        this.myString = myString;
      }
    }

    PublicFieldClass publicFieldClass = new PublicFieldClass("asd");
    //publicFieldClass.myString = "asd"; -> Error
```
#### 3. 외부에서 접근할때 부수작업을 수행 할 수 없다
- Boolean 타입 필드의 값이 NULL일때 false를 리턴하고 싶은 경우
- ```java

    class FalseIfNull {
      public Boolean isTruePublic;  
      private Boolean isTruePrivate; 
    
      public boolean getIsTruePrivate() {
          return this.isTruePrivate == null ? false : this.isTruePrivate;   
      }
    }
   ```
    
따라서 public 클래스의 필드는 private으로 바꾸고 getter를 추가하는것이 바람직 하다.

