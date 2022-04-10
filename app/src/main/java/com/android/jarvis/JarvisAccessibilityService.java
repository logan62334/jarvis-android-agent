package com.android.jarvis;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class JarvisAccessibilityService extends AccessibilityService {
    private static final String TAG = "JarvisService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event != null && event.getPackageName() != null && getRootInActiveWindow() != null) {
            retry(event,"重新加载");
            Log.i(TAG, "onAccessibilityEvent: " + event.getPackageName());
            AccessibilityNodeInfo accessibilityNodeInfo = event.getSource();
            if (accessibilityNodeInfo != null) {
                String currentClassName = event.getClassName().toString();
                Log.i(TAG, "currentClassName: " + currentClassName);
                switch (currentClassName) {
                    case "com.yaya.zone.home.HomeActivity":
                        Log.i(TAG, "去结算");
                        findAndPerformAction("去结算", accessibilityNodeInfo);
                        break;
                    case "cn.me.android.cart.activity.WriteOrderActivity":
                        if (isExist("请选择送达时间", accessibilityNodeInfo)) {
                            Log.i(TAG, "请选择送达时间");
                            findAndPerformAction("请选择送达时间", accessibilityNodeInfo);
                        } else {
                            Log.i(TAG, "立即支付");
                            findAndPerformAction("立即支付", accessibilityNodeInfo);
                        }
                        break;
                    case "iy":
                        chooseDeliveryTime(accessibilityNodeInfo);
                        break;
                    case "lm4":
                        handlePopup("继续支付", accessibilityNodeInfo);
                        break;
                    case "zv0":
                        handlePopup("选择送达时间", accessibilityNodeInfo);
                        break;
                    case "dy":
//                        Log.i(TAG, "返回购物车");
//                        handlePopup("返回购物车", accessibilityNodeInfo);
                        Log.i(TAG, "重新加载");
                        handlePopup("重新加载", accessibilityNodeInfo);
                        break;
                    default:
                        Log.i(TAG, "默认");
                        if (isExist("去结算", accessibilityNodeInfo)) {
                            findAndPerformAction("去结算", accessibilityNodeInfo);
                        } else if (isExist("返回购物车", accessibilityNodeInfo)) {
                            findAndPerformAction("重新加载", accessibilityNodeInfo);
                        } else {
                            findAndPerformAction("立即支付", accessibilityNodeInfo);
                        }
                        break;
                }
            }
        }
    }

    private void retry(AccessibilityEvent event,String text){
        try {
            Log.i(TAG, "currentTextName: " + event.getText().toString());
            String currentTextName = event.getText().toString();
            if (("[" + text + "]").equals(currentTextName)) {
                Log.i(TAG, text);
                findAndPerformParentAction(text,event.getSource());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void chooseDeliveryTime(AccessibilityNodeInfo accessibilityNodeInfo) {
        List<AccessibilityNodeInfo> nodes = accessibilityNodeInfo.findAccessibilityNodeInfosByText("-");
        if (nodes != null && !nodes.isEmpty()) {
            for (int i = 0; i < nodes.size(); i++) {
                if (nodes.get(i).getParent().isEnabled()) {
                    nodes.get(i).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
    }

    private void handlePopup(String text, AccessibilityNodeInfo accessibilityNodeInfo) {
        findAndPerformAction(text, accessibilityNodeInfo);
    }

    private void findAndPerformAction(String text, AccessibilityNodeInfo accessibilityNodeInfo) {
        List<AccessibilityNodeInfo> nodes = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodes != null && !nodes.isEmpty()) {
            for (int i = 0; i < nodes.size(); i++) {
                nodes.get(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    private void findAndPerformParentAction(String text, AccessibilityNodeInfo accessibilityNodeInfo) {
        List<AccessibilityNodeInfo> nodes = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodes != null && !nodes.isEmpty()) {
            for (int i = 0; i < nodes.size(); i++) {
                if (nodes.get(i).getParent().isEnabled()) {
                    nodes.get(i).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
    }

    private boolean isExist(String text, AccessibilityNodeInfo accessibilityNodeInfo) {
        List<AccessibilityNodeInfo> nodes = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        return nodes != null && !nodes.isEmpty();
    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "服务被Interrupt");
    }

}

