package com.project.jsh.androidprj;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        handler.postDelayed(new splashHandler(),1500); // 1.5초 로딩 후 종료
    }

    private class splashHandler implements  Runnable{

        @Override
        public void run() {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            SplashActivity.this.finish();
        }
    }
}
