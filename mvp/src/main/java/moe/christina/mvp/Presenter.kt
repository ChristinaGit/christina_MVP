package moe.christina.mvp

import android.support.annotation.CallSuper
import io.reactivex.disposables.Disposable
import moe.christina.common.core.RxSchedulers

interface Presenter<in TScreen : Screen> {
    fun bindScreen(newScreen: TScreen)

    fun unbindScreen()
}

abstract class BasePresenter<TScreen : Screen> : Presenter<TScreen> {
    protected var screen: TScreen? = null
        get
        private set

    final override fun bindScreen(newScreen: TScreen) {
        if (screen !== null) {
            throw IllegalStateException("Presenter is already bound.")
        }

        screen = newScreen

        onBindScreenInternal(newScreen)
    }

    final override fun unbindScreen() {
        val oldScreen = screen ?: throw IllegalStateException("Presenter is already unbound.")

        screen = null

        onUnbindScreenInternal(oldScreen)
    }

    @CallSuper
    protected open fun onScreenDestroy(screen: TScreen) {
    }

    @CallSuper
    protected open fun onScreenCreate(screen: TScreen) {
    }

    @CallSuper
    protected open fun onScreenAppear(screen: TScreen) {
    }

    @CallSuper
    protected open fun onScreenDisappear(screen: TScreen) {
    }

    @CallSuper
    protected open fun onBindScreen(screen: TScreen) {
    }

    @CallSuper
    protected open fun onUnbindScreen(screen: TScreen) {
    }

    private var screenStateChangedObserver: Disposable? = null

    private fun onBindScreenInternal(screen: TScreen) {
        screenStateChangedObserver = screen.onScreenStateChanged
                .subscribeOn(RxSchedulers.main)
                .subscribe(this::screenStateChangedHandler)

        onBindScreen(screen)
    }

    private fun onUnbindScreenInternal(screen: TScreen) {
        screenStateChangedObserver?.dispose()
        screenStateChangedObserver = null

        onUnbindScreen(screen)
    }

    private fun screenStateChangedHandler(event: ScreenLifecycleEvent) {
        val screen = screen ?: throw IllegalStateException("Screen is null")
        when (event) {
            ScreenLifecycleEvent.CREATE -> onScreenCreate(screen)
            ScreenLifecycleEvent.APPEAR -> onScreenAppear(screen)
            ScreenLifecycleEvent.DISAPPEAR -> onScreenDisappear(screen)
            ScreenLifecycleEvent.DESTROY -> onScreenDestroy(screen)
        }
    }
}