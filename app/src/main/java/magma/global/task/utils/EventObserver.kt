package magma.global.task.utils

import androidx.lifecycle.Observer


class EventObserver<T>(var content: EventUnhandledContent<T>?) : Observer<Event<T>> {

    interface EventUnhandledContent<T> {
        fun onEventUnhandledContent(t: T)
    }

    override fun onChanged(event: Event<T>?) {
        if (event != null) {
            val result = event.getContentIfNotHandled()
            if (result != null && content != null) {
                content!!.onEventUnhandledContent(result)
            }
        }
    }
}