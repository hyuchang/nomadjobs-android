package com.valuebiz.nomadjobs.activities

import android.annotation.TargetApi
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.annotation.RequiresApi
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.valuebiz.nomadjobs.R
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class WebViewActivity : BaseActivity() {

    companion object {
        @JvmField val REQUEST_SELECT_FILE: Int = 100
        @JvmField val FILECHOOSER_RESULT_CODE: Int = 1
    }

    val WEB_SCHEME: String = "servicemarketnative"
    val webInterface: HucloudJavascriptInterface = HucloudJavascriptInterface()
    var webView: WebView? = null
    var dialog:Dialog? = null
    val retrofit = Retrofit.Builder()
            .baseUrl("https://olive.hutt.co.kr/")
            .build()
    private var mBackPressTime: Long = 0
    private var mCloseToast: Toast? = null

    var uploadMessage: ValueCallback<Array<Uri>>? = null
    var mUploadMessage: ValueCallback<Uri>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out)

        init()
    }

    fun init(){
        setContentView(R.layout.activity_web_view)
        webView = findViewById<WebView?>(R.id.webView)
        var splashImg:LinearLayout = findViewById(R.id.splash_img)
        webViewInit()
        Handler().postDelayed(Runnable {
            splashImg.visibility = View.GONE
        }, 1500)
    }

    fun webViewInit(){
        val settings = webView!!.settings
        settings.javaScriptEnabled = true
        settings.userAgentString = settings.userAgentString + "*AndroidServicemarket"
        settings.javaScriptCanOpenWindowsAutomatically = true // new window
        settings.cacheMode=WebSettings.LOAD_DEFAULT
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            var cookieManager:CookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setAcceptThirdPartyCookies(webView,true)
        }
        settings.setSupportMultipleWindows(true)
        // localStorage 사용설정
        settings.domStorageEnabled = true
        settings.allowFileAccess = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        webView!!.isHorizontalScrollBarEnabled = false
        webView!!.webChromeClient = WebChromeClient()
        webView!!.persistentDrawingCache = ViewGroup.PERSISTENT_ANIMATION_CACHE
        webView!!.webViewClient = WebViewClient()
        webView!!.loadUrl("https://www.buyservice.co.kr")
    }

    override fun onBackPressed() {
        if ( webView != null ) {
            if ( webView!!.canGoBack()) {
                webView!!.goBack()
            } else {
                if (System.currentTimeMillis() > mBackPressTime + 2000) {
                    this.mBackPressTime = System.currentTimeMillis()
                    this.mCloseToast = Toast.makeText(this, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT)
                    this.mCloseToast!!.show()
                    return
                }
                if (System.currentTimeMillis() <= mBackPressTime + 2000) {
                    finish()
                    if (this.mCloseToast!! != null)
                        this.mCloseToast!!.cancel()
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    inner class HucloudJavascriptInterface {

        fun <T> callback(fn: (Throwable?, Response<T>?) -> Unit): Callback<T> {
            return object : Callback<T> {
                override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) = fn(null, response)
                override fun onFailure(call: Call<T>, t: Throwable) = fn(t, null)
            }
        }

        fun process(url: Uri) {
            var event: String = url.toString().split("//")[1].split("?")[0]
            var queryValue: String = ""
            // 한개만임 우선
            if ( url.query != null )
                queryValue = url.query.split("=")[1]
            when (event) {
                "googleLogin" -> requestGoogleLogin()
            }
        }
    }

    inner class WebViewClient : android.webkit.WebViewClient() {

        var firstLoad:Boolean = false
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            if (request!!.url.scheme.startsWith(WEB_SCHEME)) {
                webInterface.process(request.url)
                return true
            } else if(request!!.url.scheme.startsWith("kakaolink")
                    ||request!!.url.scheme.startsWith("market")){
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(request.url.toString())))
                return true
            } else if(request!!.url.scheme.startsWith("intent")){
                try{
                    val intent:Intent = Intent.parseUri(request.url.toString(), 0)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }catch (e:Exception){
                    e.printStackTrace()
                } finally {
                    return true
                }

            }
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            if ( !firstLoad ) {
                webView!!.loadUrl("javascript:localStorage.setItem('pushToken', '" + FirebaseInstanceId.getInstance().token + "')")
                firstLoad = true
            }
        }

        private fun telAction(action: String) {
            val i = Intent(Intent.ACTION_CALL)
            i.data = Uri.parse(action)
            startActivity(i)
        }
    }

    inner class WebChromeClient : android.webkit.WebChromeClient() {

        override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
            return super.onJsConfirm(view, url, message, result)
        }

        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            return super.onJsAlert(view, url, message, result)
        }

        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            println("Web Console : " + consoleMessage!!.message())
            return super.onConsoleMessage(consoleMessage)
        }

        override fun onCloseWindow(window: WebView?) {
            super.onCloseWindow(window)
            if ( dialog != null ) {
                dialog!!.dismiss()
                dialog = null
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
            if (uploadMessage != null) {
                uploadMessage?.onReceiveValue(null)
                uploadMessage = null
            }
            uploadMessage = filePathCallback
            imageChooser()
            return true
        }

        fun imageChooser() {
            var contentSelectionIntent:Intent = Intent(Intent.ACTION_GET_CONTENT)
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
            contentSelectionIntent.type = "image/*"

            startActivityForResult(contentSelectionIntent, FILECHOOSER_RESULT_CODE)
        }

        override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
            var newWebView:WebView = WebView(applicationContext)
            newWebView.settings.javaScriptEnabled = true
            newWebView.webChromeClient = WebChromeClient()
            newWebView.webViewClient = WebViewClient()
            dialog = Dialog(this@WebViewActivity, R.style.Theme_AppCompat_NoActionBar)
            dialog!!.setContentView(newWebView)
            dialog!!.show()
            (resultMsg!!.obj as WebView.WebViewTransport).webView = newWebView
            resultMsg.sendToTarget()
            return true
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if ( resultCode != RESULT_OK ) return
        if ( requestCode == RC_SIGN_IN) {
            var task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            task.addOnSuccessListener(OnSuccessListener { result ->
                var jobj = JSONObject()
                jobj.put("accessToken", "ServiceMarketAndroidAccesstoken!!!!@##sdldd123kr52k3j4kj12h4k3k23l1kwj")
                jobj.put("refreshToken", "")
                jobj.put("expire", result.expirationTimeSecs)
                jobj.put("thirdId", result.id)
                jobj.put("email", result.email)
                jobj.put("joinType", 3)
                jobj.put("username", result.displayName)
                jobj.put("profileUrl", result.photoUrl.toString())
                var objToString:String = jobj.toString()
                webView!!.loadUrl("javascript:${'$'}eventBus.Instance.${'$'}emit('google_auth_success', $objToString)")
                mGoogleSignInClient.signOut()
            })
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == FILECHOOSER_RESULT_CODE) {
                if (uploadMessage == null) {
                    return
                }

                uploadMessage?.onReceiveValue(arrayOf(data!!.data))
                uploadMessage = null
            }
        }
    }
}
