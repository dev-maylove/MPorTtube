package com.mporttube.ui.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext

private const val YOUTUBE_HOME = "https://www.youtube.com/"

/**
 * Home intentionally uses the official YouTube web experience instead of
 * reverse-engineering private YouTube APIs. Navigation, Settings and Library
 * remain part of MPorTtube.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeHomeScreen(
    onOpenLibrary: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    var webView by remember { mutableStateOf<WebView?>(null) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var webViewKey by remember { mutableStateOf(0) }

    BackHandler(enabled = webView?.canGoBack() == true) {
        webView?.goBack()
    }

    DisposableEffect(webView) {
        onDispose {
            webView?.apply {
                stopLoading()
                loadUrl("about:blank")
                onPause()
                destroy()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("YouTube") },
                actions = {
                    IconButton(onClick = {
                        errorMessage = null
                        loading = true
                        if (webView == null) webViewKey++ else webView?.loadUrl(YOUTUBE_HOME)
                    }) { Text("↻") }
                    IconButton(onClick = onOpenSettings) { Text("⚙") }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { webView?.loadUrl(YOUTUBE_HOME) },
                    icon = { Text("▶") },
                    label = { Text("YouTube") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOpenLibrary,
                    icon = { Text("▣") },
                    label = { Text("Library") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOpenSettings,
                    icon = { Text("⚙") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            key(webViewKey) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { viewContext ->
                    WebView(viewContext).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.userAgentString = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120 Mobile Safari/537.36"
                        settings.domStorageEnabled = true
                        settings.loadsImagesAutomatically = true
                        settings.mediaPlaybackRequiresUserGesture = true
                        settings.builtInZoomControls = false
                        settings.displayZoomControls = false
                        settings.setSupportZoom(false)
                        webChromeClient = WebChromeClient()
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView,
                                request: WebResourceRequest
                            ): Boolean {
                                val uri = request.url ?: return true
                                val host = uri.host.orEmpty()
                                return if (
                                    host.endsWith("youtube.com") ||
                                    host.endsWith("youtu.be") ||
                                    host.endsWith("google.com")
                                ) {
                                    false
                                } else {
                                    runCatching {
                                        context.startActivity(
                                            Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()))
                                        )
                                    }
                                    true
                                }
                            }

                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                loading = true
                                errorMessage = null
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                loading = false
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                if (request?.isForMainFrame == true) {
                                    loading = false
                                    errorMessage = "Tidak dapat memuat YouTube. Periksa koneksi internet lalu coba lagi."
                                }
                            }

                            override fun onRenderProcessGone(
                                view: WebView?,
                                detail: RenderProcessGoneDetail?
                            ): Boolean {
                                // Returning true prevents the host app from force closing when
                                // Android's WebView renderer is killed by the system.
                                loading = false
                                errorMessage = "Renderer YouTube dihentikan sistem. Halaman akan dibuat ulang."
                                webView = null
                                webViewKey++
                                return true
                            }
                        }
                        loadUrl(YOUTUBE_HOME)
                        webView = this
                    }
                },
                update = { current -> webView = current }
            )
            }

            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            errorMessage?.let { message ->
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = message, color = MaterialTheme.colorScheme.onSurface)
                    Button(onClick = {
                        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_HOME))) }
                    }) { Text("Buka YouTube di Browser / Aplikasi") }
                }
            }
        }
    }
}
