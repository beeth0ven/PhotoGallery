package cn.beeth0ven.photogallery.RxExtension;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by Air on 2017/2/13.
 */

public class ComputedVariable<T> {

    public interface Getter<T> {

        T getValue() ;
    }

    public interface Setter<T> {

        void setValue(T newValue);
    }

    private Getter<T> _getter;
    private Setter <T> _setter;
    private BehaviorSubject<T> _subject;

    public ComputedVariable(Getter<T> getter, Setter <T> setter) {
        _getter = getter;
        _setter = setter;
        _subject = BehaviorSubject.createDefault(getter.getValue());
    }

    public T getValue() {
        return _getter.getValue();
    }

    public void setValue(T newValue) {
        _setter.setValue(newValue);
        _subject.onNext(newValue);
    }

    public Observable<T> asObservable() {
        return _subject;
    }
}
