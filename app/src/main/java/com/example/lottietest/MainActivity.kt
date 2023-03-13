package com.example.lottietest

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.annotation.RawRes
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private var toggleBase64 = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        webView.apply {
            webViewClient = client
            webChromeClient = chromeClient
            settings.javaScriptEnabled = true
            addJavascriptInterface(JavaScriptInterface(), "Bridge")
            //loadDataWithBaseURL(" https://appassets.androidplatform.net", lottieAnimation, "text/html", "utf-8", null)


        }

    }

    fun loadCss(v: View) {

        toggleBase64 = false
        webView.loadData(css, "text/html", "UTF-8")
    }

    fun loadBase64(v: View) {
        toggleBase64 = true
        webView.loadData(base64, "text/html", "UTF-8")
    }

    inner class JavaScriptInterface {
        @JavascriptInterface
        fun onFrameRendered(frameCount: Int) {
        }

        @JavascriptInterface
        fun onProgressCompleted() {
        }

        @JavascriptInterface
        fun onProgressChange(value: Float) {
        }
    }

    private val client = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            Log.d(TAG, "onPageFinished")

            val frameScript =
                """
                     javascript:(function(){
                            document.addEventListener('onNewFrame', function(e){
                                Bridge.onFrameRendered(e.value);
                            },false);
                      })()
                """

            val s1 = Single.just(resources.getRawTextFile(R.raw.poppinsbase64))
            val s2 = Single.just(resources.getRawTextFile(R.raw.data08))
            Single.zip(s1, s2) { t1, t2 -> Pair(t1, t2) }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({

                    val js = if (!toggleBase64) {
                        "javascript:loadJson('${it.second}')"
                    } else "javascript:loadBase64Font('${it.first}','${it.second}')"
                    webView.loadUrl(js)


                }, {

                })
        }
    }


    fun Resources.getRawTextFile(@RawRes id: Int) =
        openRawResource(id).bufferedReader().use { it.readText() }

    private val chromeClient = object : WebChromeClient() {
        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            Log.d(TAG, "onConsoleMessage: ${consoleMessage?.message()}")
            return super.onConsoleMessage(consoleMessage)
        }
    }


    companion object {

        const val css =
            """
                <!DOCTYPE html>
                <html>
                
                <head>
                    <script src="https://cdnjs.cloudflare.com/ajax/libs/bodymovin/5.7.5/lottie.min.js"></script>
                    <script src="https://cdnjs.com/libraries/bodymovin" type="text/javascript"></script>
                
                </head>
                
                <div id="sampleText">Sample Text</div>
                <div id="animation-container"></div>               
                
                <style>
                
                    
                    @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@800&display=swap');
                    
                    body {
                        margin: 0%;
                    }
     
                </style>
                
                <body>
                
                    <script>               
                                                    
                        
                        let animation
                
                        function loadJson(animationData, base64) {
                                    
                                console.log('Poppins', document.fonts.check("14px Poppins-Bold")); 
                                
                                let ele = document.getElementById('sampleText');
                                ele.style.fontFamily = 'Poppins';
                                
                                let ele2 = document.getElementById('animation-container');
                                ele2.style.fontFamily = 'Poppins';
                                
                                animation = bodymovin.loadAnimation({
                                    container: document.getElementById('animation-container'),
                                    renderer: 'svg', // Required
                                    loop: true,
                                    autoplay: true,
                                    animationData: JSON.parse(animationData),
                                    name: "Test Lottie",
                                })
                    
                                //animation.pause();
                                animation.renderer.elements[0].canResizeFont(true);
                                console.log('canResize', animation.renderer.elements[0].textProperty.canResize);
                                //console.log(animation);
                    
                                animation.addEventListener('enterFrame', function () {                             
                                    if(frameCount < 180){
                                        frameCount++
                                        const frameEvent = new Event('onNewFrame');
                                        frameEvent.value = frameCount;
                                        document.dispatchEvent(frameEvent);
                                    }
                                    
                                })
                                
                                 changeText();
                                
                                 //animation.goToAndStop(0, true);        
                                                      
                
                        }
                
                        var progress = 0;
                        frameCount = 0;
                        
                        function renderNextFrame() {
                            progress = progress + 0.7056; // 6 sec duration
                            animation.goToAndStop(progress, true)                
                        }
                
                        function changeText() {
                            animation.renderer.elements[0].updateDocumentData({ f: 'Poppins-Bold', t: 'Domestic LPG price hiked by Rs 50 per 14.2-kg cylinder, first hike since July 2022' }, 0);
                        }
                                
                    </script>
                
                </body>
                
            </html>
            """

        const val base64 =
            """
                <!DOCTYPE html>
                <html>
                
                <head>
                    <script src="https://cdnjs.cloudflare.com/ajax/libs/bodymovin/5.7.5/lottie.min.js"></script>
                    <script src="https://cdnjs.com/libraries/bodymovin" type="text/javascript"></script>
                
                </head>
                
                <div id="sampleText">Sample Text</div>
                <div id="animation-container"></div>               
                
                <style>
                                                      
                    body {
                        margin: 0%;
                    }
     
                </style>
                
                <body>
                
                    <script>               
                
                
                        function loadBase64Font(base64font, jsonData) {
                            
                            console.log('jsonData' , typeof(jsonData));
                            
                            let url = "url(data:font/truetype;charset=utf-8;base64," + base64font + ") format('truetype')";
                            let font = new FontFace('Poppins-Bold', url);
                            font.load().then(function (loadedFace) {
                            
                               
                                document.fonts.add(loadedFace);
                                                                
                                console.log('fontface', document.fonts.check("14px Poppins")); 
                                console.log('fontface', document.fonts.check("14px Poppins-Bold")); 
                                console.log('fontface', loadedFace.status);                             
                                
                                loadJson(jsonData);
                               
                            }).catch(function (error) {
                                console.log(error);
                            });
                        }
                        
                        let animation
                
                        function loadJson(animationData) {
                            
                                console.log('Poppins', document.fonts.check("14px Poppins-Bold")); 
                                
                                let ele = document.getElementById('sampleText');
                                ele.style.fontFamily = 'Poppins-Bold';
                                ele.innerHTML = 'Sample Text with base64'
                                
                                let ele2 = document.getElementById('animation-container');
                                ele2.style.fontFamily = 'Poppins-Bold';
                                
                                animation = bodymovin.loadAnimation({
                                    container: document.getElementById('animation-container'),
                                    renderer: 'svg', // Required
                                    loop: true,
                                    autoplay: true,
                                    animationData: JSON.parse(animationData),
                                    name: "Test Lottie",
                                })
                    
                                //animation.pause();
                                animation.renderer.elements[0].canResizeFont(true);
                                console.log('canResize', animation.renderer.elements[0].textProperty.canResize);
                                //console.log(animation);
                    
                                animation.addEventListener('enterFrame', function () {                             
                                    if(frameCount < 180){
                                        frameCount++
                                        const frameEvent = new Event('onNewFrame');
                                        frameEvent.value = frameCount;
                                        document.dispatchEvent(frameEvent);
                                    }
                                    
                                })
                                
                                 changeText();
                                
                                 //animation.goToAndStop(0, true);                                                             
              
                        }
                
                        var progress = 0;
                        frameCount = 0;
                        
                        function renderNextFrame() {
                            progress = progress + 0.7056; // 6 sec duration
                            animation.goToAndStop(progress, true)                
                        }
                
                        function changeText() {
                            animation.renderer.elements[0].updateDocumentData({ f: 'Poppins-Bold', t: 'Domestic LPG price hiked by Rs 50 per 14.2-kg cylinder, first hike since July 2022' }, 0);
                        }
                                
                    </script>
                
                </body>
                
            </html>
            """
        private const val TAG = "MainActivity"
    }
}