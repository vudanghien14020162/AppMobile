package com.mobitv.ott.other;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class CallStateListener extends PhoneStateListener {
    private Context context;
    private int prev_state = -1;

    public CallStateListener(Context context) {
        this.context = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                prev_state = state;
                Intent intent = new Intent(Const.FLAG_INCOMING_CALL);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                prev_state = state;
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                if ((prev_state == TelephonyManager.CALL_STATE_OFFHOOK)
                        || (prev_state == TelephonyManager.CALL_STATE_RINGING)) {
                    Intent intent2 = new Intent(Const.FLAG_CALL_ENDED);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
                }
                prev_state = state;
                break;
        }
    }
}
