package knowre.android.rxjavamission

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import knowre.android.rxjavamission.databinding.ActivityMainBinding
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import rx.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel>()

    companion object {
        const val INPUT_DELAY = 300L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeQueryEditText()
        initializeSubscribe()
    }

    private fun initializeSubscribe() = with(viewModel) {
        query.subscribe {
            Timber.d("[Query] : $it")
        }
    }

    private fun initializeQueryEditText() = with(binding) {
        val queryObservable = Observable.unsafeCreate<String> { emitter ->
            etQuery.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    emitter.onNext(s.toString())
                }

                override fun afterTextChanged(s: Editable?) = Unit
            })
        }

        queryObservable.debounce(INPUT_DELAY, TimeUnit.MILLISECONDS)
            .filter { query -> query.isNotEmpty() }
            .subscribe { query ->
                viewModel.handleEvent(MainViewModel.Event.OnQueryTextChanged(query))
            }
    }

}