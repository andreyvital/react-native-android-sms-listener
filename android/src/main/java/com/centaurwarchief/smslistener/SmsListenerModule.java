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
    private Activity mActivity;

    public SmsListenerModule(ReactApplicationContext context, Activity activity) {
        super(context);

        mActivity = activity;
        mReceiver = new SmsReceiver(context);

        registerReceiverIfNecessary(mReceiver);
    }

    private void registerReceiverIfNecessary(BroadcastReceiver receiver) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mActivity.registerReceiver(
                receiver,
                new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
            );

            return;
        }

        mActivity.registerReceiver(
            receiver,
            new IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        );
    }

    private void unregisterReceiver(BroadcastReceiver receiver) {
        mActivity.unregisterReceiver(receiver);
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
        return "SmsListener";
    }
}
