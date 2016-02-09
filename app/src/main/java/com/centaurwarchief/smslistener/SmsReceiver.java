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
    private static final String EVENT = "com.centaurwarchief.smslistener:smsReceived";
    private ReactApplicationContext mContext;

    public SmsReceiver(ReactApplicationContext context) {
        this.mContext = context;
    }

    private void receiveMessage(SmsMessage message) {
        if (! this.mContext.hasActiveCatalystInstance()) {
            return;
        }

        WritableNativeMap receivedMessage = new WritableNativeMap();

        receivedMessage.putString("originatingAddress", message.getOriginatingAddress());
        receivedMessage.putString("body", message.getMessageBody());

        this.mContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(EVENT, receivedMessage);
    }

    private void logReceivedMessage(SmsMessage message) {
        Log.d(
                SmsListener.TAG,
                String.format("%s: %s", message.getOriginatingAddress(), message.getMessageBody())
        );
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

            if (messages.length == 0) {
                return;
            }

            logReceivedMessage(messages[0]);
            receiveMessage(messages[0]);

            return;
        }

        try {
            final Bundle bundle = intent.getExtras();

            if (bundle == null) {
                return;
            }

            if (! bundle.containsKey("pdus")) {
                return;
            }

            final Object[] pdus = (Object[]) bundle.get("pdus");

            for (Object pdu : pdus) {
                SmsMessage message;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    message = SmsMessage.createFromPdu((byte[]) pdu, bundle.getString("format"));
                } else {
                    message = SmsMessage.createFromPdu((byte[]) pdu);
                }

                logReceivedMessage(message);
                receiveMessage(message);
            }
        } catch (Exception e) {
        }
    }
}
