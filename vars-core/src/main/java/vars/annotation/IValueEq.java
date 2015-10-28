package vars.annotation;

/**
 * @author Brian Schlining
 * @since 2015-10-20T15:08:00
 */
public interface IValueEq<T> {

    boolean equal(T a, T b);
}
