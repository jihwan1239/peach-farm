package com.igane.peach;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 이가네 복숭아 안드로이드 앱.
 * GitHub Pages에 올린 게임 주소를 그대로 열어 주는 껍데기 앱이라,
 * 웹에서 수정한 내용이 앱에도 바로 반영돼요.
 */
public class MainActivity extends Activity {
    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#FFE3D6"));
        window.setNavigationBarColor(Color.parseColor("#FFE3D6"));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        web = new WebView(this);
        web.setBackgroundColor(Color.parseColor("#FFE3D6"));
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setUserAgentString(s.getUserAgentString() + " IganePeachApp");

        final String site = getString(R.string.site_url);
        final String host = Uri.parse(site).getHost();

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (host != null && host.equalsIgnoreCase(uri.getHost())) {
                    return false; // 게임 안 페이지는 앱 안에서
                }
                try { // 구매 링크 같은 바깥 주소는 브라우저로
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (Exception ignored) { }
                return true;
            }
        });

        setContentView(web);
        if (savedInstanceState != null) {
            web.restoreState(savedInstanceState);
        } else {
            web.loadUrl(site);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    protected void onPause() {
        web.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        web.onResume();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        // 창이 열려 있으면 먼저 닫고, 아니면 앱 종료
        web.evaluateJavascript(
            "(function(){var m=document.getElementById('modal');if(m&&!m.hidden){m.hidden=true;return 1}return 0})()",
            value -> {
                if ("1".equals(value)) return;
                if (web.canGoBack()) web.goBack();
                else finish();
            });
    }
}
