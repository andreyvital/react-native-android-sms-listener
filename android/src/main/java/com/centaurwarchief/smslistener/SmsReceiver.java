package com.centaurwarchief.smslistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class SmsReceiver extends BroadcastReceiver {
    private ReactApplicationContext mContext;

    private static final String EVENT = "com.centaurwarchief.smslistener:smsReceived";

    public SmsReceiver() {
        super();
    }

    public SmsReceiver(ReactApplicationContext context) {
        mContext = context;
    }

    private void receiveMessage(SmsMessage message) {
        if (mContext == null) {
            return;
        }

        if (! mContext.hasActiveCatalystInstance()) {
            return;
        }

        Log.d(
            SmsListenerPackage.TAG,
            String.format("%s: %s", message.getOriginatingAddress(), message.getMessageBody())
        );

        WritableNativeMap receivedMessage = new WritableNativeMap();

        receivedMessage.putString("originatingAddress", message.getOriginatingAddress());
        receivedMessage.putString("body", message.getMessageBody());

        mContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(EVENT, receivedMessage);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            for (SmsMessage message : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                receiveMessage(message);
            }

            return;
        }

        try {
            final Bundle bundle = intent.getExtras();

            if (bundle == null || ! bundle.containsKey("pdus")) {
                return;
            }

            final Object[] pdus = (Object[]) bundle.get("pdus");

            for (Object pdu : pdus) {
                receiveMessage(SmsMessage.createFromPdu((byte[]) pdu));
            }
        } catch (Exception e) {
            Log.e(SmsListenerPackage.TAG, e.getMessage());
        }
    }
}
