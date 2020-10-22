package wee.digital.fpa.util

import android.os.Environment
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import okio.buffer
import okio.sink
import retrofit2.Response
import java.io.File
import java.io.IOException


interface SimpleObserver<T> : Observer<T> {

    override fun onSubscribe(d: Disposable) {
    }

    override fun onComplete() {
    }

    override fun onError(e: Throwable) {
        onComplete(null, e)
    }

    override fun onNext(t: T) {
        onComplete(t, null)
    }

    fun onComplete(t: T?, e: Throwable?)
}

fun Observable<Response<ResponseBody>>.writeFile(fileName: String): Observable<File> {
    return flatMap(object : Function<Response<ResponseBody>, ObservableSource<File>> {
        override fun apply(response: Response<ResponseBody>): ObservableSource<File> {
            return Observable.create(object : ObservableOnSubscribe<File> {
                override fun subscribe(emitter: ObservableEmitter<File>) {
                    val source = response.body()?.source()
                            ?: throw NullPointerException("download data is empty")
                    try {
                        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absoluteFile, fileName)
                        file.sink().buffer().apply {
                            writeAll(source)
                            close()
                        }
                        emitter.onNext(file)
                    } catch (e: IOException) {
                        emitter.onError(e)
                    } finally {
                        emitter.onComplete()
                    }
                }
            })
        }
    })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}