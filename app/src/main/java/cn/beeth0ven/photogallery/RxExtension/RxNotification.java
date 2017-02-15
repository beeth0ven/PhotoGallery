package cn.beeth0ven.photogallery.RxExtension;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by Air on 2017/2/15.
 */

public class RxNotification {

    public static final PublishSubject<MyVoid> showNotification = PublishSubject.create();

}