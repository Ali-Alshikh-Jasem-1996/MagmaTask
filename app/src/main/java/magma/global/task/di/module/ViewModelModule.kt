package magma.global.task.di.module

import androidx.lifecycle.ViewModel
import magma.global.task.utils.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import magma.global.task.presentation.splash.SplashViewModel
import magma.global.task.presentation.map.MapsViewModel

// Because of @Binds, ViewModelModule needs to be an abstract class

@Module
abstract class ViewModelModule {

// Use @Binds to tell Dagger which implementation it needs to use when providing an interface.
    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindMainViewModel(viewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MapsViewModel::class)
    abstract fun bindMapsViewModel(viewModel: MapsViewModel): ViewModel
    
    

}