# 비검사 경고를 제거하라

비검사 경고 (ex. warning: [unchecked] unchecked ...) 를 제거할수록 타입 안정성이 높아진다고 볼 수 있다. 
만약 타입 안정성이 확실한데 컴파일러의 경고를 없애고 싶다면 @SuppressWarnings("unchecked")를 사용하자. 
로그에 파묻혀서 필요한 로그를 발견하기 어렵게 하지않도록 말이다.

이때 @SuppressWarnings의 범위를 최대한 줄여서 달자. 
메소드레벨, 클래스레벨 보단 비검사 경고가 뜨는 지역변수 레벨에 다는 것이 가장 좋다. 
또한 이때 타입에 안전한 이유를 주석으로 추가해두는 것이 좋다.

```java
/**
 * Indicates that the named compiler warnings should be suppressed in the
 * annotated element (and in all program elements contained in the annotated
 * element).  Note that the set of warnings suppressed in a given element is
 * a superset of the warnings suppressed in all containing elements.  For
 * example, if you annotate a class to suppress one warning and annotate a
 * method to suppress another, both warnings will be suppressed in the method.
 *
 * <p>As a matter of style, programmers should always use this annotation
 * on the most deeply nested element where it is effective.  If you want to
 * suppress a warning in a particular method, you should annotate that
 * method rather than its class.
 *
 * @author Josh Bloch
 * @since 1.5
 * @jls 4.8 Raw Types
 * @jls 4.12.2 Variables of Reference Type
 * @jls 5.1.9 Unchecked Conversion
 * @jls 5.5.2 Checked Casts and Unchecked Casts
 * @jls 9.6.3.5 @SuppressWarnings
 */

@Target({TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE})
@Retention(RetentionPolicy.SOURCE)
public @interface SuppressWarnings {
    /**
     * The set of warnings that are to be suppressed by the compiler in the
     * annotated element.  Duplicate names are permitted.  The second and
     * successive occurrences of a name are ignored.  The presence of
     * unrecognized warning names is <i>not</i> an error: Compilers must
     * ignore any warning names they do not recognize.  They are, however,
     * free to emit a warning if an annotation contains an unrecognized
     * warning name.
     *
     * <p> The string {@code "unchecked"} is used to suppress
     * unchecked warnings. Compiler vendors should document the
     * additional warning names they support in conjunction with this
     * annotation type. They are encouraged to cooperate to ensure
     * that the same names work across multiple compilers.
     * @return the set of warnings to be suppressed
     */
    String[] value();
}
```