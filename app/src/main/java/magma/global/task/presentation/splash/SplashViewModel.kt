package magma.global.task.presentation.splash

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import magma.global.task.data.repository.DataRepository
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SplashViewModel  @Inject constructor(
    private val dataRepository: DataRepository,
    override val coroutineContext: CoroutineContext,
) : ViewModel(), CoroutineScope {

}