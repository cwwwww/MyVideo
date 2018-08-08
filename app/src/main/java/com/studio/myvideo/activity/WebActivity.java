package com.studio.myvideo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import com.studio.myvideo.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2018/6/27.
 */

public class WebActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        WebView webView = findViewById(R.id.webView);
        String url = this.getIntent().getStringExtra("url");
        webView.getSettings().setJavaScriptEnabled(true);
        if (url != null && url.length() > 0 && url.startsWith("http")) {
            webView.loadUrl(url);
        } else {
            Toast.makeText(WebActivity.this, "网址错误", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
