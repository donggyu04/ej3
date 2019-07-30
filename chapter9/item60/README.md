# 정확한 답이 필요하다면 float와 double은 피하라

float와 double 타입은 이진 부동소수점 연산에 쓰이며, 넓은 범위의 수를 빠르게 정밀한 **근사치**로 계산하도록 설계되었다. 따라서 정확한 결과가 필요할 때는 사용하면 안된다. 

## 예시
```java
System.out.println(1.03 - 0.42);
System.out.println(1.00 - 9 * 0.10);
```

```
0.6100000000000001
0.09999999999999998
```

[더 많은 예시](https://github.com/jbloch/effective-java-3e-source-code/tree/master/src/effectivejava/chapter9/item60)

## 결론
금융 계산에는 BigDecimal, int 혹은 long을 사용해야 한다.

코딩 시의 불편함이나 성능 저하를 신경쓰지 않는다면 BigDecimal을 사용하라.

성능이 중요하고 소숫점을 직접 다룰 수 있다면, int나 long을 사용하라. (각각의 숫자 범위에 맞추어 써야하는것은 당연)