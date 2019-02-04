package com.technuclear.lifecall.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.technuclear.lifecall.R;

import dmax.dialog.SpotsDialog;

public class LoggingInActivity extends AppCompatActivity {

    private AlertDialog progress;
    Intent intent;

    public static final String IS_NETWORK_AVAILABLE = "isNetworkAvailable";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging_in);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }

        intent = new Intent(this, MainActivity.class);

        loadProgressBar();
    }

    private void loadProgressBar() {
        progress = new SpotsDialog(this, R.style.Custom);

        if (getIntent().getBooleanExtra(IS_NETWORK_AVAILABLE, false)) {
            progress.setMessage("Signing In");
            progress.setCancelable(false);
            progress.show();

            final Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10000);
                        progress.dismiss();
                        LoggingInActivity.this.finish();
                        startActivity(intent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
        } else
            startActivity(intent);
    }
}