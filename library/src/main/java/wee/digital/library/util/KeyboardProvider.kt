package wee.digital.library.util

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.PopupWindow
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import wee.digital.library.R

class KeyboardProvider constructor(private val activity: FragmentActivity) : PopupWindow(activity),
        ViewTreeObserver.OnGlobalLayoutListener {

    companion object

    interface Listener {
        fun onKeyboardShow(height: Int, orientation: Int)

        fun onKeyboardHide(orientation: Int)
    }

    private var listener: Listener? = null

    /** The cached landscape height of the keyboard  */
    private var landscapeHeight: Int = 0

    /** The cached portrait height of the keyboard  */
    private var portraitHeight: Int = 0

    private val popupView: View?

    private val parentView: View

    /**
     * Get the screen orientation
     *
     * @return the screen orientation
     */
    private val screenOrientation: Int
        get() = activity.resources.configuration.orientation

    init {

        val inflater = activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.popupView = inflater.inflate(R.layout.view_popup, null, false)
        contentView = popupView

        softInputMode =
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        inputMethodMode = INPUT_METHOD_NEEDED

        parentView = activity.findViewById(android.R.id.content)

        width = 0
        height = WindowManager.LayoutParams.MATCH_PARENT

        popupView!!.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    fun observe(listener: Listener) {
        this.listener = listener
        activity.lifecycle.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onStart() {
                Handler(Looper.getMainLooper()).postDelayed({
                    start()
                }, 1000)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onPause() {
                close()
            }
        })
    }

    /**
     * Start the KeyboardProvider, this must be called after the onResume of the Activity.
     * PopupWindows are not allowed to be registered before the onResume has finished
     * of the Activity.
     */
    fun start() {
        if (!isShowing && parentView.windowToken != null) {
            setBackgroundDrawable(ColorDrawable(0))
            showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0)
        }
    }

    fun close() {
        popupView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
        this.listener = null
        dismiss()
    }

    /**
     * [ViewTreeObserver.OnGlobalLayoutListener] implement
     * Popup window itself is as big as the window of the Activity.
     * The keyboard can then be calculated by extracting the popup view bottom
     * from the activity window height.
     */
    override fun onGlobalLayout() {
        val screenSize = Point()
        activity.windowManager.defaultDisplay.getSize(screenSize)

        val rect = Rect()
        popupView!!.getWindowVisibleDisplayFrame(rect)

        val orientation = screenOrientation
        val keyboardHeight = screenSize.y - rect.bottom

        when {
            keyboardHeight == 0 -> {
                listener?.onKeyboardHide(orientation)
            }
            orientation == Configuration.ORIENTATION_PORTRAIT -> {
                //if (portraitHeight == keyboardHeight) return
                portraitHeight = keyboardHeight
                listener?.onKeyboardShow(keyboardHeight, orientation)
            }
            else -> {
                //if (landscapeHeight == keyboardHeight) return
                landscapeHeight = keyboardHeight
                listener?.onKeyboardShow(keyboardHeight, orientation)
            }
        }
    }

}