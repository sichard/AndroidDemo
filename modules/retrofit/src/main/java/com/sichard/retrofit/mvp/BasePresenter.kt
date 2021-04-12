package com.sichard.retrofit.mvp

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * <br></br>类表述：Presenter的基类，用来和MVPActivity配合使用，自动完成View的绑定和解绑。
 * 加入了动态代理，在代理调用方法时做空指针判定，好处是不用在每个View的接口中加非空判定、
 * <br></br>详细描述：改为注释掉的代码会出现空指针异常。
 * <br></br>**Author Caoshichao **
 * <br></br>**Date  2020/4/2**
 */
abstract class BasePresenter<M : IModel, V : IView> {
    protected var mMode: M? = null
    protected lateinit var mProxyView: V
    private var mWeakReferenceView: WeakReference<V>? = null
    protected var mMainScope = MainScope()

    @Suppress("UNCHECKED_CAST")
    open fun attachView(view: V) {
        mWeakReferenceView = WeakReference(view)
        mProxyView = Proxy.newProxyInstance(view.javaClass.classLoader,
                view.javaClass.interfaces, MvpViewHandler()) as V
        if (mMode == null) {
            mMode = createMode()
        }
    }

    fun detachView() {
        mWeakReferenceView?.get()?.run {
            mWeakReferenceView!!.clear()
            mWeakReferenceView == null
        }
        mMainScope.cancel()
        mMode = null
    }

    protected abstract fun createMode(): M

    /**
     * 调用处理器，动态代理的调用最终转到该类invoke方法并转交给实际对象来处理业务。
     * 防止 页面关闭P异步操作调用V 方法 空指针问题
     */
    private inner class MvpViewHandler : InvocationHandler {
        @Throws(Throwable::class)
        //注意下面返回值"Any?"中的?不可省略，因为invoke方法可能返回null
        override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
            //如果V层没被销毁, 执行V层的方法.
            return mWeakReferenceView?.get()?.let {
                //emptyArray()是为了防止方法输入参数为0时报错
                method.invoke(it, *(args ?: emptyArray()))
            }
        }
    }
}