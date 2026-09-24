package com.vaibhav.affordability;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.graphics.PixelFormat;
import android.view.*;
import android.widget.*;
import android.content.*;
import java.util.*;
import java.util.regex.*;

public class CheckoutAccessibilityService extends AccessibilityService {
    private WindowManager wm;
    private View overlay;
    private long lastShown=0;

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {
        String pkg = event.getPackageName()==null ? "" : event.getPackageName().toString();
        if (!pkg.contains("swiggy") && !pkg.contains("blinkit")) return;
        AccessibilityNodeInfo root=getRootInActiveWindow();
        if(root==null) return;
        String text=collect(root);
        Double amount=extract(text);
        if(amount!=null && amount>0 && System.currentTimeMillis()-lastShown>3000) {
            show(amount, pkg.contains("swiggy") ? "Swiggy" : "Blinkit");
        }
    }
    String collect(AccessibilityNodeInfo n) {
        StringBuilder s=new StringBuilder();
        if(n.getText()!=null) s.append(n.getText()).append(" ");
        for(int i=0;i<n.getChildCount();i++) { AccessibilityNodeInfo c=n.getChild(i); if(c!=null){s.append(collect(c)); c.recycle();}}
        return s.toString();
    }
    Double extract(String t) {
        Pattern p=Pattern.compile("(?i)(total|pay|amount|grand total)[^₹0-9]{0,30}₹?\\s*([0-9,]+(?:\\.\\d{1,2})?)");
        Matcher m=p.matcher(t);
        if(m.find()) try{return Double.parseDouble(m.group(2).replace(",",""));}catch(Exception e){}
        return null;
    }
    void show(double amount,String app) {
        lastShown=System.currentTimeMillis();
        wm=(WindowManager)getSystemService(WINDOW_SERVICE);
        LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(35,25,35,25);
        TextView t=new TextView(this); t.setText("CAN YOU AFFORD IT?\n"+app+" ₹"+String.format("%.0f",amount)+"\n\nSet your financial values in the Affordability app."); t.setTextSize(18); box.addView(t);
        Button close=new Button(this); close.setText("Continue"); close.setOnClickListener(v->remove()); box.addView(close);
        overlay=box;
        WindowManager.LayoutParams lp=new WindowManager.LayoutParams(
            -2,-2, WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        wm.addView(overlay,lp);
    }
    void remove(){ if(overlay!=null){wm.removeView(overlay);overlay=null;} }
    @Override public void onInterrupt(){}
}
