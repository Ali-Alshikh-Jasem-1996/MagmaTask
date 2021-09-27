package magma.global.task.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import magma.global.task.presentation.splash.SplashActivity
import magma.global.task.presentation.map.MapsActivity

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): SplashActivity

    @ContributesAndroidInjector
    abstract fun contributeMapsActivity(): MapsActivity

}