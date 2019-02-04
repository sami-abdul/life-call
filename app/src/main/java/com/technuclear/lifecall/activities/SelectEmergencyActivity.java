package com.technuclear.lifecall.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.technuclear.lifecall.R;

public class SelectEmergencyActivity extends AppCompatActivity {

    public static final String EMERGENCY_TYPE = "emergencyType";
    public static final String HAS_NUMBER = "hasNumber";
    public static final String PHONE_NUMBER = "phoneNumber";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    SharedPreferences phonePref;
    SharedPreferences.Editor phoneEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_emergency);

        sharedPreferences = getSharedPreferences(HAS_NUMBER, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        phonePref = getSharedPreferences(PHONE_NUMBER, Context.MODE_PRIVATE);
        phoneEditor = phonePref.edit();

        if (!sharedPreferences.getBoolean(HAS_NUMBER, false))
            showInputDialog();

        MainActivity.AMBULANCE_PHONE_NUMBER = phonePref.getString(PHONE_NUMBER, null);
    }

    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.prompts, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setTitle("Enter Phone Number")
                .setMessage("Provide phone number of ambulance depot for the purpose of demonstration")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.AMBULANCE_PHONE_NUMBER = editText.getText().toString();
                        editor.putBoolean(HAS_NUMBER, true).commit();
                        phoneEditor.putString(PHONE_NUMBER, MainActivity.AMBULANCE_PHONE_NUMBER);
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void onAccident(View view) {
        Intent intent = new Intent(this, EmergencyInitiatedActivity.class);
        intent.putExtra(EMERGENCY_TYPE, "accident");
        startActivity(intent);
    }

    public void onHeartAttack(View view) {
        Intent intent = new Intent(this, EmergencyInitiatedActivity.class);
        intent.putExtra(EMERGENCY_TYPE, "heartAttack");
        startActivity(intent);
    }

    public void onInjury(View view) {
        Intent intent = new Intent(this, EmergencyInitiatedActivity.class);
        intent.putExtra(EMERGENCY_TYPE, "injury");
        startActivity(intent);
    }

    public void onPregnancy(View view) {
        Intent intent = new Intent(this, EmergencyInitiatedActivity.class);
        intent.putExtra(EMERGENCY_TYPE, "pregnancy");
        startActivity(intent);
    }
}
