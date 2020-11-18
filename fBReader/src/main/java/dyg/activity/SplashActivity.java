package dyg.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dyg.android.reader.R;

import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.zlibrary.core.options.Config;

public class SplashActivity extends Activity {

    private static final String first_open = "first_open";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_splash);
        // 第一次打开，直接显示prewindow
        // 第一次打开直接到本地书库
        boolean first = Config.Instance().getSpecialBooleanValue(first_open, true);
        if (first) {
            FbDefaultActivity.startActivity(this);
            finish();
            Config.Instance().setSpecialBooleanValue(first_open, false);
        } else {
            loadAd();

        }
    }
    private void loadAd() {
        startActivity(new Intent(SplashActivity.this, FBReader.class));
        finish();
    }
}
