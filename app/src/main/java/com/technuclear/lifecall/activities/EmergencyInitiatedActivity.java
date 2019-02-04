package com.technuclear.lifecall.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.technuclear.lifecall.R;
import com.technuclear.lifecall.others.GPSTracker;

import static com.technuclear.lifecall.activities.MainActivity.EMERGENCY_INITIATED_KEY;
import static com.technuclear.lifecall.activities.MainActivity.emerPrefEditor;
import static com.technuclear.lifecall.activities.MainActivity.friendsPhoneNumbers;

public class EmergencyInitiatedActivity extends AppCompatActivity  {

    Button callForHelp;
    TextView view1;
    TextView view3;
    TextView timer;
    TextView view2;

    final Context context = this;

    boolean emergencyInitiated = false;
    Handler handler;

    private GPSTracker gpsTracker;
    double latitude;
    double longitude;

    SmsManager smsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_initiated);

        view1 = (TextView) findViewById(R.id.emergency_initiated_text);
        view3 = (TextView) findViewById(R.id.emergency_initiated_emergency);
        timer = (TextView) findViewById(R.id.emergency_initiated_time_left);
        view2 = (TextView) findViewById(R.id.emergency_initiated_back_text);

        handler = new Handler();
        gpsTracker = new GPSTracker(this);

        smsManager = SmsManager.getDefault();

        callForHelp = (Button) findViewById(R.id.emergency_initiated_call_for_help);
        callForHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emergencyInitiated)
                    return;
                runTimer();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if (emergencyInitiated) {
                handler.removeCallbacksAndMessages(null);
                view1.setText("Press button below to initiate emergency");
                view3.setText("");
                timer.setText("");
                view2.setText("");
                emergencyInitiated = false;
                Toast toast = Toast.makeText(this, "Request withdrawn", Toast.LENGTH_SHORT);
                toast.show();

                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void runTimer() {
        view1.setText("The request will be initiated in");
        view3.setText("");
        timer.setText("");
        view2.setText("Press back button to cancel");

        final int[] seconds = {15};

        emergencyInitiated = true;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (seconds[0] == 0) {
                    view1.setText("Press button below to initiate emergency");
                    view3.setText("");
                    timer.setText("");
                    view2.setText("");

                    findLocation();
                    sendSMS();
                    sendSMSToFiends();
                    showDialog();

                    emerPrefEditor.putBoolean(EMERGENCY_INITIATED_KEY, true).commit();

                    emergencyInitiated = false;

                    return;
                }

                String time = String.format(":%02d", seconds[0]);
                timer.setText(time);
                seconds[0]--;
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.notified_dialog_box);
        dialog.setTitle("Emergency Initiating");

        // set the custom dialog components - text, image and button
        //ImageView image = (ImageView) dialog.findViewById(R.id.image);
        //image.setImageResource(R.drawable.ic_launcher);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialog_window_OK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void findLocation() {
        // check if GPS enabled
        //if(gpsTracker.canGetLocation()){
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();


        //} else{
        // can't get location
        // GPS or Network is not enabled
        // Ask currentUser to enable GPS/network in settings
        //gpsTracker.showSettingsAlert();
        //}
    }

    private void sendSMS() {
            smsManager.sendTextMessage(MainActivity.AMBULANCE_PHONE_NUMBER, null, "A person just suffered "
                    + getIntent().getStringExtra(SelectEmergencyActivity.EMERGENCY_TYPE) + ". " + "Click the link below to mark their co-ordinates on map."
                    + "\nhttp://lifecall.com/trackpatient," + latitude + "," + longitude + "", null, null);
    }

    private void sendSMSToFiends() {
        for (int i = 0; i< friendsPhoneNumbers.size(); i++) {
            smsManager.sendTextMessage(friendsPhoneNumbers.get(i), null, "Your friend just suffered "
                    + getIntent().getStringExtra(SelectEmergencyActivity.EMERGENCY_TYPE) + ". " + "Click the link below to mark their co-ordinates on map."
                    + "\nhttp://lifecall.com/trackpatient," + latitude + "," + longitude + "", null, null);
        }
    }
}
