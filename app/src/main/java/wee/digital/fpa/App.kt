package wee.digital.fpa

import android.app.Application

lateinit var app: App private set

class App : Application() {

    /**
     * [Application] override
     */
    override fun onCreate() {
        super.onCreate()
        app = this
        app.onModulesInject()
    }


}

