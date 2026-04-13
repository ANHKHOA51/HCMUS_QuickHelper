package com.example.hcmus_quickhelper.features.booking.ui

import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.hcmus_quickhelper.R

class BookingFragment : Fragment(R.layout.fragment_booking) {

    private lateinit var webView: WebView
    private lateinit var edtSearch: EditText
    private lateinit var tvLocationName: TextView
    private lateinit var tvLocationAddress: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView = view.findViewById(R.id.webViewMap)
        edtSearch = view.findViewById(R.id.edtSearch)
        tvLocationName = view.findViewById(R.id.tvLocationName)
        tvLocationAddress = view.findViewById(R.id.tvLocationAddress)


        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        webView.addJavascriptInterface(WebAppInterface(), "AndroidInterface")

        webView.loadUrl("file:///android_asset/map.html")

       // search
        edtSearch.setOnEditorActionListener { _, _, _ ->
            val query = edtSearch.text.toString()

            if (query.isNotEmpty()) {
                // Gọi sang HTML/JS
                webView.loadUrl("javascript:searchLocation('$query')")
            }
            true
        }
    }

    inner class WebAppInterface {
        @JavascriptInterface
        fun updateLocationInfo(name: String, displayName: String) {
            // dùng runOnUiThread để cập nhật UI trên Main Thread do chạy dưới background Thread của WebView
            activity?.runOnUiThread {
                tvLocationName.text = name
                tvLocationAddress.text = displayName
            }
        }
    }
}