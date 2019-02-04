package com.technuclear.lifecall.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;
import com.technuclear.lifecall.R;
import com.technuclear.lifecall.tables.FriendsTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.technuclear.lifecall.activities.LoginActivity.mClient;


public class FriendsTableActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;

    private EditText name;
    private EditText age;
    private RadioGroup hasDisease;
    private EditText disease;
    private EditText weight;
    private Spinner bloodGroup;
    private EditText phoneNumber;

    TextView view1;
    TextView view2;
    Button confButton;
    Button delButton;

    private GoogleApiClient client;

    Intent intent;
    Intent previousIntent;

    String currentFriendID;
    String currentFriendName;
    String currentFriendPhoneNumber;
    FriendsTable currentItem;

    private boolean isNewContact;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_table);

        intent = new Intent(this, FriendsActivity.class);

        progressBar = (ProgressBar) findViewById(R.id.activity_frienfs_progress);
        progressBar.setVisibility(View.VISIBLE);

        name = (EditText) findViewById(R.id.editText5);
        name.setVisibility(View.INVISIBLE);
        phoneNumber = (EditText) findViewById(R.id.editText6);
        phoneNumber.setVisibility(View.INVISIBLE);
        age = (EditText) findViewById(R.id.editText7);
        age.setVisibility(View.INVISIBLE);
        weight = (EditText) findViewById(R.id.editText8);
        weight.setVisibility(View.INVISIBLE);
        bloodGroup = (Spinner) findViewById(R.id.spinner2);
        bloodGroup.setVisibility(View.INVISIBLE);
        hasDisease = (RadioGroup) findViewById(R.id.radio_group2);
        hasDisease.setVisibility(View.INVISIBLE);
        disease = (EditText) findViewById(R.id.editText9);
        disease.setVisibility(View.INVISIBLE);
        view1 = (TextView) findViewById(R.id.textView8);
        view1.setVisibility(View.INVISIBLE);
        view2 = (TextView) findViewById(R.id.textView9);
        view2.setVisibility(View.INVISIBLE);
        confButton = (Button) findViewById(R.id.button10);
        confButton.setVisibility(View.INVISIBLE);
        delButton = (Button) findViewById(R.id.activity_friends_delete_btn);
        delButton.setVisibility(View.INVISIBLE);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        previousIntent = getIntent();
        currentFriendID = previousIntent.getStringExtra(FriendsActivity.CONTACT_INFO_ID);
        currentFriendName = previousIntent.getStringExtra(FriendsActivity.CONTACT_INFO_NAME);
        currentFriendPhoneNumber = previousIntent.getStringExtra(FriendsActivity.CONTACT_INFO_PHONE_NUMBER);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                currentItem = getItemReferenceFromTable();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createTable();
                    }
                });
                return null;
            }
        };
        runAsyncTask(task);
    }

    /**
     * Finishes the activity and logs the currentUser out on the press of back button
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {

        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Initializes the table
     */
    private void createTable() {
        // Offline Sync
        //mTable = mClient.getSyncTable("UserTable", UserTable.class);

        //Init local storage
        try {
            initLocalStore().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (MobileServiceLocalStoreException e) {
            e.printStackTrace();
        }

        name.setVisibility(View.VISIBLE);
        phoneNumber.setVisibility(View.VISIBLE);
        age.setVisibility(View.VISIBLE);
        weight.setVisibility(View.VISIBLE);
        bloodGroup.setVisibility(View.VISIBLE);
        hasDisease.setVisibility(View.VISIBLE);
        view1.setVisibility(View.VISIBLE);
        view2.setVisibility(View.VISIBLE);
        confButton.setVisibility(View.VISIBLE);

        if (previousIntent.getBooleanExtra(FriendsActivity.CONTACT_INFO_EDIT, false))
            findViewById(R.id.activity_friends_delete_btn).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.activity_friends_delete_btn).setVisibility(View.INVISIBLE);

        progressBar.setVisibility(View.INVISIBLE);

        if (previousIntent.getBooleanExtra(FriendsActivity.CONTACT_INFO_EDIT, false)) {
            name.setText(currentItem.getName());
            phoneNumber.setText(currentItem.getPhoneNumber());
            age.setText(String.valueOf(currentItem.getAge()));
            weight.setText(String.valueOf(currentItem.getWeight()));

            if (currentItem.getBloodGroup().equals("A+"))
                bloodGroup.setSelection(0);

            else if (currentItem.getBloodGroup().equals("A-"))
                bloodGroup.setSelection(1);

            else if (currentItem.getBloodGroup().equals("B+"))
                bloodGroup.setSelection(2);

            else if (currentItem.getBloodGroup().equals("B-"))
                bloodGroup.setSelection(3);

            else if (currentItem.getBloodGroup().equals("O+"))
                bloodGroup.setSelection(4);

            else if (currentItem.getBloodGroup().equals("O-"))
                bloodGroup.setSelection(5);

            else if (currentItem.getBloodGroup().equals("AB+"))
                bloodGroup.setSelection(6);

            else if (currentItem.getBloodGroup().equals("AB-"))
                bloodGroup.setSelection(7);

            if (currentItem.getHasDisease()) {
                hasDisease.check(R.id.radioButton3);
                disease.setVisibility(View.VISIBLE);
                disease.setText(currentItem.getDisease());
            } else {
                hasDisease.check(R.id.radioButton4);
            }
        }

        else{
            name.setText(currentFriendName);
            phoneNumber.setText(currentFriendPhoneNumber);
        }

        hasDisease.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                if (checkedId == R.id.radioButton3)
                    disease.setVisibility(View.VISIBLE);
                else
                    disease.setVisibility(View.INVISIBLE);
            }
        });
    }
/*
    private FriendsTable getItemFromTable() {

        FriendsActivity.mTable.lookUp()
    }*/

    /**
     * Gets the reference of previous entry by the currentUser
     */
    private FriendsTable getItemReferenceFromTable() {
        List<FriendsTable> temp = null;
        try {
            temp = FriendsActivity.mTable.where().field("phoneNumber").eq().val(currentFriendPhoneNumber).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        // Reference found
        if (!temp.isEmpty())
            return temp.get(0);
        else
            return null;
    }

    public void onDelete(View view) {
        FriendsActivity.mTable.delete(currentItem);
        finish();
    }

    public void onConfirm(View view) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Previous entry in the table found
                if (currentItem != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RadioButton but = (RadioButton) findViewById(hasDisease.getCheckedRadioButtonId());
                            if (name.getText().toString().equals("") || age.getText().toString().equals("") || weight.getText().toString().equals("")
                                    || phoneNumber.getText().toString().equals("")
                                    || hasDisease.getCheckedRadioButtonId() == -1) {
                                Toast toast = Toast.makeText(FriendsTableActivity.this, "Fill all the fields", Toast.LENGTH_SHORT);
                                toast.show();
                                return;
                            }
                            currentItem.setName(name.getText().toString());
                            currentItem.setAge(Integer.parseInt(age.getText().toString()));
                            currentItem.setWeight(Integer.parseInt(weight.getText().toString()));
                            currentItem.setBloodGroup(bloodGroup.getSelectedItem().toString());
                            currentItem.setFriendID(currentFriendID);
                            currentItem.setPhoneNumber(phoneNumber.getText().toString());

                            if (but.getText().equals("Yes")) {
                                currentItem.setHasDisease(true);
                                currentItem.setDisease(disease.getText().toString());
                            }
                            else if (but.getText().equals("No")) {
                                currentItem.setHasDisease(false);
                                currentItem.setDisease("");
                            }
                        }
                    });
                    isNewContact = false;
                    updateItem(currentItem);
                }
                // No previous entry in the table found
                else {
                    currentItem = new FriendsTable();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (name.getText().toString().equals("") || age.getText().toString().equals("") || weight.getText().toString().equals("")
                                    || phoneNumber.getText().toString().equals("")
                                    || hasDisease.getCheckedRadioButtonId() == -1) {
                                Toast toast = Toast.makeText(FriendsTableActivity.this, "Fill all the fields", Toast.LENGTH_SHORT);
                                toast.show();
                                return;
                            }
                            currentItem.setName(name.getText().toString());
                            currentItem.setAge(Integer.parseInt(age.getText().toString()));
                            currentItem.setWeight(Integer.parseInt(weight.getText().toString()));
                            currentItem.setBloodGroup(bloodGroup.getSelectedItem().toString());
                            currentItem.setFriendID(currentFriendID);
                            currentItem.setPhoneNumber(phoneNumber.getText().toString());
                            RadioButton but = (RadioButton) findViewById(hasDisease.getCheckedRadioButtonId());
                            if (but.getText().equals("Yes")) {
                                currentItem.setHasDisease(true);
                                currentItem.setDisease(disease.getText().toString());
                            }
                            else {
                                currentItem.setHasDisease(false);
                                currentItem.setDisease("");
                            }
                        }
                    });
                    isNewContact = true;
                    addItem(currentItem);
                }
                return null;
            }
        };
        runAsyncTask(task);
        finish();
    }

    /**
     * Updates the item
     */
    public void updateItem(FriendsTable item) {
        if (mClient == null)
            return;
        else if (name.getText().toString().equals("") || age.getText().toString().equals("") || weight.getText().toString().equals("")
                || phoneNumber.getText().toString().equals("")) {
            return;
        }

        try {
            updateItemInTable(item);
        } catch (final Exception e) {
            createAndShowDialogFromTask(e, "Error");
        }
    }

    /**
     * Updates the item in the mobile service table
     */
    public void updateItemInTable(FriendsTable item) throws ExecutionException, InterruptedException {
        FriendsActivity.mTable.update(item).get();
    }

    /**
     * Adds the item
     */
    public void addItem(FriendsTable item) {
        if (mClient == null) {
            return;
        } else if (name.getText().toString().equals("") || age.getText().toString().equals("") || weight.getText().toString().equals("")
                || phoneNumber.getText().toString().equals("")) {
            Toast toast = Toast.makeText(FriendsTableActivity.this, "Fill all the fields", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        try {
            addItemInTable(item);
        } catch (final Exception e) {
            createAndShowDialogFromTask(e, "Error");
        }
    }

    /**
     * Adds the item to the mobile service table
     */
    public void addItemInTable(FriendsTable item) throws ExecutionException, InterruptedException {
        FriendsActivity.mTable.insert(item).get();
    }

    /**
     * Initialize local storage
     *
     * @return
     * @throws MobileServiceLocalStoreException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private AsyncTask<Void, Void, Void> initLocalStore() throws MobileServiceLocalStoreException, ExecutionException, InterruptedException {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    MobileServiceSyncContext syncContext = mClient.getSyncContext();

                    if (syncContext.isInitialized())
                        return null;

                    SQLiteLocalStore localStore = new SQLiteLocalStore(mClient.getContext(), "OfflineStore", null, 1);

                    Map<String, ColumnDataType> tableDefinition = new HashMap<String, ColumnDataType>();
                    tableDefinition.put("name", ColumnDataType.String);
                    tableDefinition.put("age", ColumnDataType.String);
                    tableDefinition.put("weight", ColumnDataType.String);
                    tableDefinition.put("bloodGroup", ColumnDataType.String);
                    tableDefinition.put("hasDisease", ColumnDataType.Boolean);
                    tableDefinition.put("disease", ColumnDataType.String);
                    tableDefinition.put("phoneNumber", ColumnDataType.String);

                    localStore.defineTable("FriendsTable", tableDefinition);

                    SimpleSyncHandler handler = new SimpleSyncHandler();

                    syncContext.initialize(localStore, handler).get();

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };
        return runAsyncTask(task);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param exception The exception to show in the dialog
     * @param title     The dialog title
     */
    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }

    /**
     * Creates a dialog and shows it
     *
     * @param exception The exception to show in the dialog
     * @param title     The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if (exception.getCause() != null) {
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message The dialog message
     * @param title   The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    /**
     * Run an ASync task on the corresponding executor
     *
     * @param task
     * @return
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("UserTable Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class ProgressFilter implements ServiceFilter {

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                        }
                    });

                    resultFuture.set(response);
                }
            });

            return resultFuture;
        }
    }
}

