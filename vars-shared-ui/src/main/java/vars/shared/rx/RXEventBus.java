package vars.shared.rx;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * An event bus based on <a href="https://github.com/ReactiveX/RxJava">RXJava</a>.
 *
 * @author Brian Schlining
 * @since 2015-07-17T16:37:00
 */
public class RXEventBus {

    /*
     If multiple threads are going to emit events to this
     then it must be made thread-safe like this instead
     */
    private final Subject<Object, Object> subject = new SerializedSubject<>(PublishSubject.create());

    public void send(Object o) {
        subject.onNext(o);
    }

    public Observable<Object> toObserverable() {
        return subject;
    }

    public boolean hasObservers() {
        return subject.hasObservers();
    }
}
