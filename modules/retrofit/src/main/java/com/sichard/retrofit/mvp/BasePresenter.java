package com.sichard.retrofit.mvp;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <br>类表述：Presenter的基类，用来和MVPActivity配合使用，自动完成View的绑定和解绑。
 * 加入了动态代理，在代理调用方法时做空指针判定，好处是不用在每个View的接口中加非空判定、
 * <br>详细描述：改为注释掉的代码会出现空指针异常。
 * <br><b>Author Caoshichao </b></br>
 * <br><b>Date  2020/4/2</b></br>
 */
public abstract class BasePresenter<M extends IModel, V extends IView> {
    protected M mMode;
    protected V mProxyView;
    private WeakReference<V> mWeakReferenceView;

    @SuppressWarnings("unchecked")
    public void attachView(V view) {
        mWeakReferenceView = new WeakReference<>(view);
        mProxyView = (V) Proxy.newProxyInstance(view.getClass().getClassLoader(),
                view.getClass().getInterfaces(), new MvpViewHandler(mWeakReferenceView.get()));
//        mProxyView = view;

        if (mMode == null) {
            mMode = createMode();
        }
    }


    public void detachView() {
        if (mWeakReferenceView.get() != null) {
            mWeakReferenceView.clear();
            mWeakReferenceView = null;
        }
        mMode = null;
//        mProxyView = null;
    }

    protected abstract M createMode();


    private boolean isViewAttached() {
        return mWeakReferenceView != null && mWeakReferenceView.get() != null;
    }


    /**
     * 调用处理器，动态代理的调用最终转到该类invoke方法并转交给实际对象来处理业务。
     * 防止 页面关闭P异步操作调用V 方法 空指针问题
     */
    private class MvpViewHandler implements InvocationHandler {

        private IView realObject;

        MvpViewHandler(IView realObject) {
            this.realObject = realObject;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //如果V层没被销毁, 执行V层的方法.
            if (isViewAttached()) {
                return method.invoke(realObject, args);
            }
            //P层不需要关注V层的返回值
            return null;
        }
    }

}
