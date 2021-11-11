package knowre.android.rxjavamission

import androidx.lifecycle.ViewModel
import rx.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit


internal class MainViewModel : ViewModel() {

    private val _query = BehaviorSubject.create<String>()
    val query get() = _query.asObservable()

    fun handleEvent(event: Event) = when (event) {
        is Event.OnQueryTextChanged -> {
            _query.onNext(event.value)
        }
    }

    sealed class Event {
        class OnQueryTextChanged(val value: String) : Event()
    }
}