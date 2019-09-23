# 74. 메서드가 던지는 모든 예외를 문서화하라 

`throws` Exception/Throwable은 최악이다
- 어떤 예외가 발생할지도 알 수 없고, 의도하지 않은 다른 예외까지 삼켜질 수가 있다

모든 Checked Exception은 `@throws` 태그를 사용하여 문서화 하고,
Unchecked Exception도 문서화해서 프로그래머가 실수할 만한 내용을 명시해주는게 좋다
- 그렇다고 Unchecked Exception을 `throws` 목록에 넣지는 말자