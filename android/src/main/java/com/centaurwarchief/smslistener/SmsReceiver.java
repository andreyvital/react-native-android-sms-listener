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

    private void receiveMessage(SmsMessage message, String body) {
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
        receivedMessage.putString("body", body.length() > 0 ? body : message.getMessageBody());
        receivedMessage.putDouble("timestamp", message.getTimestampMillis());

        mContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(EVENT, receivedMessage);
    }

    private void receiveMultipartMessage(SmsMessage[] messages) {
        SmsMessage sms = messages[0];
        String body;

        if (messages.length == 1 || sms.isReplace()) {
            body = sms.getDisplayMessageBody();
        } else {
            StringBuilder bodyText = new StringBuilder();

            for (SmsMessage message : messages) {
                bodyText.append(message.getMessageBody());
            }

            body = bodyText.toString();
        }

        receiveMessage(sms, body);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            receiveMultipartMessage(Telephony.Sms.Intents.getMessagesFromIntent(intent));

            return;
        }

        try {
            final Bundle bundle = intent.getExtras();

            if (bundle == null || ! bundle.containsKey("pdus")) {
                return;
            }

            final Object[] pdus = (Object[]) bundle.get("pdus");
            final SmsMessage[] messages = new SmsMessage[pdus.length];

            for (int i = 0; i < pdus.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }

            receiveMultipartMessage(messages);
        } catch (Exception e) {
            Log.e(SmsListenerPackage.TAG, e.getMessage());
        }
    }
}
