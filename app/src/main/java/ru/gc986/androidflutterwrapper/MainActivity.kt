package ru.gc986.androidflutterwrapper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import io.flutter.embedding.android.FlutterFragment
import io.flutter.plugin.common.MethodChannel
import kotlinx.android.synthetic.main.activity_main.*
import ru.gc986.androidflutterwrapper.ApplicationWrapper.Companion.ENGINE_ID

class MainActivity : AppCompatActivity() {

    private var flutterFragment: FlutterFragment? = null
    val TAG_FLUTTER_FRAGMENT = "TAG_FLUTTER_FRAGMENT"
    private val FLUTTER_CHANNEL_NAME = "channel"
    val flutterChannel = MethodChannel(ApplicationWrapper.flutterEngine.dartExecutor.binaryMessenger, FLUTTER_CHANNEL_NAME)

    val FLUTTER_ROUTE_SCREEN_TEXT = "text"
    val FLUTTER_ROUTE_SCREEN_BROWSER = "browser"
    val FLUTTER_ROUTE_SCREEN_MENU = "menu"
    val FLUTTER_ROUTE_SCREEN_CHILD = "child"

    val PUSH_ROUTE_FROM_NATIVE = "pushRouteFromNative"
    val ANDROID_BACK_PRESSED = "androidBackPressed"

    private fun log(message: String){
        Log.i("MyApp", message)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btMenu.setOnClickListener {
            openFlutterFragmentName(FLUTTER_ROUTE_SCREEN_MENU)
        }

        btText.setOnClickListener {
            openFlutterFragmentName(FLUTTER_ROUTE_SCREEN_TEXT)
        }

        btBrowser.setOnClickListener {
            openFlutterFragmentName(FLUTTER_ROUTE_SCREEN_BROWSER)
        }

        btChild.setOnClickListener {
            openFlutterFragmentName(FLUTTER_ROUTE_SCREEN_CHILD)
        }

        flutterChannel.setMethodCallHandler { call, result ->
            log("call.method :: ${call.method}")
            if (call.method == "pop") fragmentFinish()
        }
    }

    private fun openFlutterFragmentName(routeName: String){
        flutterFragment = FlutterFragment.withCachedEngine(ENGINE_ID).build()
        flutterFragment?.let {
            replaceFragment(it, TAG_FLUTTER_FRAGMENT, routeName)
        }
    }

    private fun replaceFragment(flutterFragment: FlutterFragment, tag: String, routeName: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.clRoot,
                flutterFragment,
                tag)
            .addToBackStack(null)
            .commit()

        flutterChannel.invokeMethod(PUSH_ROUTE_FROM_NATIVE, routeName)
    }

    private fun fragmentFinish(){
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun onBackPressed() {
        flutterChannel.invokeMethod(ANDROID_BACK_PRESSED,"")
    }

}