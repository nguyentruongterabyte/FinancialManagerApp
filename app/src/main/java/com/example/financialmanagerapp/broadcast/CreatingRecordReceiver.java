package com.example.financialmanagerapp.broadcast;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.activity.CreatingRecordActivity;

public class CreatingRecordReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.example.financialManagerApp.CREATING_RECORD".equals(intent.getAction())) {
            Intent creatingRecordActivity = new Intent(context, CreatingRecordActivity.class);

            // Add flags to start a new task and clear top
            creatingRecordActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Start the activity
            context.startActivity(creatingRecordActivity);

            // Apply animation
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }

        }
    }
}
