package com.centaurwarchief.smslistener;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.Telephony;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

public class SmsListenerModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
    private BroadcastReceiver mReceiver;
    private boolean isReceiverRegistered = false;

    public SmsListenerModule(ReactApplicationContext context) {
        super(context);

        mReceiver = new SmsReceiver(context);
        getReactApplicationContext().addLifecycleEventListener(this);
        registerReceiverIfNecessary(mReceiver);
    }

    private void registerReceiverIfNecessary(BroadcastReceiver receiver) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && getCurrentActivity() != null) {
            getCurrentActivity().registerReceiver(
                receiver,
                new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
            );
            isReceiverRegistered = true;
            return;
        }

        if (getCurrentActivity() != null) {
            getCurrentActivity().registerReceiver(
                    receiver,
                    new IntentFilter("android.provider.Telephony.SMS_RECEIVED")
            );
            isReceiverRegistered = true;
        }
    }

    private void unregisterReceiver(BroadcastReceiver receiver) {
        if (isReceiverRegistered && getCurrentActivity() != null) {
            getCurrentActivity().unregisterReceiver(receiver);
            isReceiverRegistered = false;
        }
    }

    @Override
    public void onHostResume() {
        registerReceiverIfNecessary(mReceiver);
    }

    @Override
    public void onHostPause() {
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onHostDestroy() {
        unregisterReceiver(mReceiver);
    }

    @Override
    public String getName() {
        return "SmsListenerPackage";
    }
}
