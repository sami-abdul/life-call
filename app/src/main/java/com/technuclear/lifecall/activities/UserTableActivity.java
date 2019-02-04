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
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
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
import com.technuclear.lifecall.adapters.UserTableAdapter;
import com.technuclear.lifecall.tables.UserTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.technuclear.lifecall.activities.LoginActivity.HAS_DATA_KEY;
import static com.technuclear.lifecall.activities.LoginActivity.currentUserID;
import static com.technuclear.lifecall.activities.LoginActivity.dataEditor;
import static com.technuclear.lifecall.activities.LoginActivity.mClient;
import static com.technuclear.lifecall.activities.LoginActivity.mTable;

public class UserTableActivity extends AppCompatActivity {

    //Offline Sync
    /**
     * Mobile Service Table used to access and Sync data
     */
    //private MobileServiceSyncTable<UserTable> mTable;

    /**
     * Adapter to sync the items driverList with the view
     */
    private UserTableAdapter mAdapter;

    /**
     * EditText containing the "New To Do" text
     */
    private EditText name;
    private EditText age;
    private EditText weight;
    private Spinner bloodGroup;
    private RadioGroup hasDisease;
    private EditText disease;
    TextView view1;
    TextView view2;
    Button butt;

    /**
     * Progress spinner to use for table operations
     */
    private ProgressBar mProgressBar;

    public static String signInService = "message";
    public UserTable currentItem;
    private String currentItemID;
    private boolean isNewUser;

    Intent intent;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * Initializes the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_table);

        intent = new Intent(this, MainActivity.class);

        name = (EditText) findViewById(R.id.editText1);
        name.setVisibility(View.INVISIBLE);
        age = (EditText) findViewById(R.id.editText2);
        age.setVisibility(View.INVISIBLE);
        weight = (EditText) findViewById(R.id.editText3);
        weight.setVisibility(View.INVISIBLE);
        bloodGroup = (Spinner) findViewById(R.id.spinner1);
        bloodGroup.setVisibility(View.INVISIBLE);
        hasDisease = (RadioGroup) findViewById(R.id.radio_group1);
        hasDisease.setVisibility(View.INVISIBLE);
        disease = (EditText) findViewById(R.id.editText4);
        disease.setVisibility(View.INVISIBLE);
        view1 = (TextView) findViewById(R.id.textView7);
        view1.setVisibility(View.INVISIBLE);
        view2 = (TextView) findViewById(R.id.textView3);
        view2.setVisibility(View.INVISIBLE);
        butt = (Button) findViewById(R.id.button4);
        butt.setVisibility(View.INVISIBLE);

        hasDisease.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radioButton1)
                    disease.setVisibility(View.VISIBLE);
                else
                    disease.setVisibility(View.INVISIBLE);
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.activity_user_table_progress);
        mProgressBar.setVisibility(View.VISIBLE);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                currentItem = getItemReferenceFromTable();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initializeFields();
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
    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getIntent().getBooleanExtra("edit", false)) {
                finish();
            }
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);

        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Initializes the table
     */
    private void initializeFields() {
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
        age.setVisibility(View.VISIBLE);
        weight.setVisibility(View.VISIBLE);
        bloodGroup.setVisibility(View.VISIBLE);
        hasDisease.setVisibility(View.VISIBLE);
        view1.setVisibility(View.VISIBLE);
        view2.setVisibility(View.VISIBLE);
        butt.setVisibility(View.VISIBLE);

        mProgressBar.setVisibility(View.INVISIBLE);

        if (currentItem != null) {
            name.setText(currentItem.getName());
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
                hasDisease.check(R.id.radioButton1);
                disease.setVisibility(View.VISIBLE);
                disease.setText(currentItem.getDisease());
            } else {
                hasDisease.check(R.id.radioButton2);
            }
        }

        mAdapter = new UserTableAdapter(this, R.layout.row_list_to_do);
        //ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
        //listViewToDo.setAdapter(mAdapter);

        refreshItemsFromTable();
    }

    private UserTable getItemReferenceFromTable() {
        List<UserTable> temp = null;
        try {
            temp = mTable.where().field("userID").eq().val(currentUserID).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        // Reference found
        if (temp != null)
            return temp.get(0);
            // Reference not found, returns null
        else
            return null;
    }

    /**
     * Confirms values added by currentUser and adds/updates in the table
     */
    public void onConfirm(View view) {
        dataEditor.putBoolean(HAS_DATA_KEY, true).commit();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Previous entry in the table found
                if (currentItem != null) {
                    final UserTable finalItem = currentItem;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RadioButton but = (RadioButton) findViewById(hasDisease.getCheckedRadioButtonId());
                            if (name.getText().toString().equals("") || age.getText().toString().equals("") || weight.getText().toString().equals("")
                                    || hasDisease.getCheckedRadioButtonId() == -1) {
                                Toast toast = Toast.makeText(UserTableActivity.this, "Fill all the fields", Toast.LENGTH_SHORT);
                                toast.show();
                                return;
                            }
                            finalItem.setName(name.getText().toString());
                            finalItem.setAge(Integer.parseInt(age.getText().toString()));
                            finalItem.setWeight(Integer.parseInt(weight.getText().toString()));
                            finalItem.setBloodGroup(bloodGroup.getSelectedItem().toString());
                            finalItem.setUserId(currentUserID);

                            if (but.getText().equals("Yes")) {
                                finalItem.setHasDisease(true);
                                finalItem.setDisease(disease.getText().toString());
                            }
                            else if (but.getText().equals("No")) {
                                finalItem.setHasDisease(false);
                                finalItem.setDisease("");
                            }
                        }
                    });

                    isNewUser = false;
                    updateItem(finalItem);
                }
                // No previous entry in the table found
                else {
                    currentItem = new UserTable();
                    final UserTable finalItem1 = currentItem;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (name.getText().toString().equals("")
                                    || age.getText().toString().equals("")
                                    || weight.getText().toString().equals("")
                                    || hasDisease.getCheckedRadioButtonId() == -1) {
                                Toast toast = Toast.makeText(UserTableActivity.this, "Fill all the fields", Toast.LENGTH_SHORT);
                                toast.show();
                                return;
                            }
                            finalItem1.setName(name.getText().toString());
                            finalItem1.setAge(Integer.parseInt(age.getText().toString()));
                            finalItem1.setWeight(Integer.parseInt(weight.getText().toString()));
                            finalItem1.setBloodGroup(bloodGroup.getSelectedItem().toString());
                            finalItem1.setUserId(currentUserID);
                            RadioButton but = (RadioButton) findViewById(hasDisease.getCheckedRadioButtonId());
                            if (but.getText().equals("Yes")) {
                                finalItem1.setHasDisease(true);
                                finalItem1.setDisease(disease.getText().toString());
                            }
                            else {
                                finalItem1.setHasDisease(false);
                                finalItem1.setDisease("");
                            }
                        }
                    });
                    isNewUser = true;
                    addItem(finalItem1);
                }
                startActivity(intent);
                return null;
            }
        };
        runAsyncTask(task);
    }

    /**
     * Updates the item
     */
    public void updateItem(UserTable item) {
        if (mClient == null)
            return;
        else if (name.getText().toString().equals("") || age.getText().toString().equals("") || weight.getText().toString().equals("")) {
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
    public void updateItemInTable(UserTable item) throws ExecutionException, InterruptedException {
        mTable.update(item).get();
    }

    /**
     * Adds the item
     */
    public void addItem(UserTable item) {
        if (mClient == null) {
            return;
        } else if (name.getText().toString().equals("") ||
                age.getText().toString().equals("") ||
                weight.getText().toString().equals("")) {
            Toast toast = Toast.makeText(UserTableActivity.this, "Fill all the fields", Toast.LENGTH_SHORT);
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
    public void addItemInTable(UserTable item) throws ExecutionException, InterruptedException {
        mTable.insert(item).get();
    }

    /**
     * Refresh the driverList with the items in the Table
     */
    private void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // adapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<UserTable> results = refreshItemsFromMobileServiceTable();

                    //Offline Sync
                    //final List<UserTable> results = refreshItemsFromMobileServiceTableSyncTable();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();

                            for (UserTable item : results) {
                                mAdapter.add(item);
                            }
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);
    }

    /**
     * Refresh the driverList with the items in the Mobile Service Table
     */

    private List<UserTable> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException, MobileServiceException {
        return mTable.execute().get();
    }

    //Offline Sync
    /**
     * Refresh the driverList with the items in the Mobile Service Sync Table
     */
    /*private List<UserTable> refreshItemsFromMobileServiceTableSyncTable() throws ExecutionException, InterruptedException {
        //sync the data
        sync().get();
        Query query = QueryOperations.field("complete").
                eq(val(false));
        return mTable.read(query).get();
    }*/

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

                    localStore.defineTable("UserTable", tableDefinition);

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

    //Offline Sync
    /**
     * Sync the current context and the Mobile Service Sync Table
     * @return
     */
    /*
    private AsyncTask<Void, Void, Void> sync() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    MobileServiceSyncContext syncContext = mClient.getSyncContext();
                    syncContext.push().get();
                    mTable.pull(null).get();
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };
        return runAsyncTask(task);
    }
    */

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