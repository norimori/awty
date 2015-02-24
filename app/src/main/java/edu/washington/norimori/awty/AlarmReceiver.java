package edu.washington.norimori.awty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by midori on 2/23/15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        String phnNum = intent.getStringExtra("phnNum");
        Toast.makeText(context, phnNum + ": " + message, Toast.LENGTH_SHORT).show();
        Log.d("yay", phnNum + ": " + message);
    }
}
