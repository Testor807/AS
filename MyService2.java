package com.example.as;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;

public class MyService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        // 处理可访问性事件
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String packageName = accessibilityEvent.getPackageName().toString();
            if (packageName.equals("com.google.android.youtube")) {
                performSearch("Appium");
            }
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        setServiceInfo(info);

        // 启动YouTube应用程序
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
        if (launchIntent != null) {
            startActivity(launchIntent);
        }
    }

    private void performSearch(String query) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            // 查找搜索框并输入关键字
            List<AccessibilityNodeInfo> MenuButtons = rootNode.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/menu_item_view");
            if (!MenuButtons.isEmpty()) {
                System.out.println("Node exist");
                MenuButtons.get(1).performAction(AccessibilityNodeInfo.ACTION_CLICK);

                List<AccessibilityNodeInfo> searchBox = rootNode.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/search_edit_text");
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, query);
                searchBox.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

                
            }else{
                System.out.println("Node not exist");
            }
        } else {
            Log.e("AccessibilityService", "Root node is null");
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {

        int action = event.getAction();
        int keyCode = event.getKeyCode();
        // the service listens for both pressing and releasing the key
        // so the below code executes twice, i.e. you would encounter two Toasts
        // in order to avoid this, we wrap the code inside an if statement
        // which executes only when the key is released
        if (action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                Log.d("Check", "KeyUp");
                Toast.makeText(this, "KeyUp", Toast.LENGTH_SHORT).show();
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                Log.d("Check", "KeyDown");
                Toast.makeText(this, "KeyDown", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onKeyEvent(event);
    }
}
