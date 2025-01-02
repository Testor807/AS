package com.example.as;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    protected TextView text;
    protected Button btn;
    protected Button btn_launch;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.txt);
        btn = (Button) findViewById(R.id.btn);
        btn_launch = (Button) findViewById(R.id.btn_launch);
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);

        if(!checkAccessibilityPermission()){
            text.setText("The Accessibility Service has been closed");
            btn.setEnabled(true);
            btn_launch.setEnabled(false);
            //Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
        }else{
            text.setText("The Accessibility Service has been started");
            btn.setEnabled(false);
            btn_launch.setEnabled(true);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // request permission via start activity for result
                startActivity(intent);
            }
        });

        btn_launch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
                    uiDevice.executeShellCommand("am start -n com.google.android.youtube/com.google.android.apps.youtube.app.WatchWhileActivity");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        accessibilityManager.addAccessibilityStateChangeListener(new AccessibilityManager.AccessibilityStateChangeListener() {
            @Override
            public void onAccessibilityStateChanged(boolean enabled) {
                if (enabled) {
                    // Accessibility Service已启动
                    text.setText("This Accessibility start!");
                    btn.setEnabled(false);
                    btn_launch.setEnabled(true);
                } else {
                    text.setText("This Accessibility stop!");
                    btn.setEnabled(true);
                    btn_launch.setEnabled(true);
                }
            }
        });
    }

    // method to check is the user has permitted the accessibility permission
    // if not then prompt user to the system's Settings activity
    public boolean checkAccessibilityPermission () {
        int accessEnabled = 0;
        try {
            accessEnabled = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (accessEnabled == 0) {
            return false;
        } else {
            return true;
        }
    }
}
