package com.android.sichard.common.rx;

import android.util.Log;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.SchedulerSupport;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

/**
 * <p>
 * Created by zhoudawei on 2016/11/10.
 */
public class Rx2Util {

    public static void init() {
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e("Rx2Util", "errorHandler accept", throwable);
            }
        });
    }

    public static final Consumer<Throwable> EMPTY_THROWABLE_CONSUMER = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            if (throwable == null) {
                throwable = new NullPointerException("onError called with null. Null values are generally not allowed in 2.x operators and sources.");
            }
            Log.e("Rx2Util", "emptyThrowable accept", throwable);
        }

        @Override
        public String toString() {
            return "ThrowableConsumer";
        }
    };

    public static Consumer<Throwable> emptyThrowable() {
        return EMPTY_THROWABLE_CONSUMER;
    }

    @CheckReturnValue
    @NonNull
    public static <T> Flowable<T> getFlowableOnIo(final Source<T> source) {
        return getFlowable(source)
                .subscribeOn(Schedulers.io());
    }

    @CheckReturnValue
    @NonNull
    public static <T> Flowable<T> getFlowableOnMain(final Source<T> source) {
        return getFlowable(source)
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @CheckReturnValue
    @NonNull
    @SchedulerSupport(SchedulerSupport.NONE)
    public static <T> Flowable<T> getFlowable(final Source<T> source) {
        return getFlowable(source, BackpressureStrategy.BUFFER);
    }

    @CheckReturnValue
    @NonNull
    @SchedulerSupport(SchedulerSupport.NONE)
    public static <T> Flowable<T> getFlowable(final Source<T> source, BackpressureStrategy mode) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> emitter) {
                try {
                    if (!emitter.isCancelled()) {
                        emitter.onNext(source.call());
                    }
                    if (!emitter.isCancelled()) {
                        emitter.onComplete();
                    }
                } catch (Exception e) {
                    emitter.tryOnError(Exceptions.propagate(e));
                }
            }
        }, mode);
    }

    @CheckReturnValue
    @NonNull
    public static <T> Observable<T> getObservableOnIo(final Source<T> source) {
        return getObservable(source)
                .subscribeOn(Schedulers.io());
    }

    @CheckReturnValue
    @NonNull
    public static <T> Observable<T> getObservableOnMain(final Source<T> source) {
        return getObservable(source)
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @CheckReturnValue
    @NonNull
    @SchedulerSupport(SchedulerSupport.NONE)
    public static <T> Observable<T> getObservable(final Source<T> source) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                try {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(source.call());
                    }
                    if (!emitter.isDisposed()) {
                        emitter.onComplete();
                    }
                } catch (Exception e) {
                    emitter.tryOnError(Exceptions.propagate(e));
                }
            }
        });
    }
}
