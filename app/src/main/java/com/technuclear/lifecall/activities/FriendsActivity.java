package com.technuclear.lifecall.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.technuclear.lifecall.R;
import com.technuclear.lifecall.adapters.FriendsTableAdapter;
import com.technuclear.lifecall.tables.FriendsTable;

import java.util.List;
import java.util.concurrent.ExecutionException;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

import static com.technuclear.lifecall.activities.LoginActivity.mClient;

public class FriendsActivity extends AppCompatActivity  {

    private static final int RESULT_PICK_CONTACT = 85500;

    public String id;
    public String phone;
    public String name;
    private static final String SHOWCASE_ID = "1";
  Button pick;

    ListView listView;

    Intent intent;

    public static final String CONTACT_INFO_NAME = "name";
    public static final String CONTACT_INFO_ID = "id";
    public static final String CONTACT_INFO_PHONE_NUMBER = "phoneNumber";
    public static final String CONTACT_INFO_EDIT = "infoEdit";

    public static MobileServiceTable<FriendsTable> mTable;
    private FriendsTableAdapter friendsTableAdapter;

    TextView nameView;
    TextView phoneView;

    FriendsTable selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = new Intent(this, FriendsTableActivity.class);

        mTable = mClient.getTable(FriendsTable.class);

        selection = new FriendsTable();
        friendsTableAdapter = new FriendsTableAdapter(this, R.layout.friends_list);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(friendsTableAdapter);
        pick = (Button) findViewById(R.id.button10);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selection = friendsTableAdapter.getItem(i);
                intent.putExtra(CONTACT_INFO_NAME, selection.getName());
                intent.putExtra(CONTACT_INFO_PHONE_NUMBER, selection.getPhoneNumber());
                intent.putExtra(CONTACT_INFO_EDIT, true);
                startActivity(intent);
            }
        });
        ShowcaseFriend();

        refreshItemsFromTable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshItemsFromTable();
    }

    private void editItemInTable() {
        intent.putExtra(CONTACT_INFO_NAME, selection.getName());
        intent.putExtra(CONTACT_INFO_PHONE_NUMBER, selection.getPhoneNumber());
        intent.putExtra(CONTACT_INFO_EDIT, true);
        startActivity(intent);
    }

    private void deleteItemFromTable() {
        nameView = (TextView) findViewById(R.id.friends_list_name);
        phoneView = (TextView) findViewById(R.id.friends_list_phone);

        final String phone = phoneView.getText().toString();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    List<FriendsTable> item = mTable.where().field("phoneNumber").eq().val(phone).execute().get();
                    System.out.println(phone);
                    mTable.delete(item.get(0));
                    refreshItemsFromTable();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_friends);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialog_window_OK);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void ShowcaseFriend()
    {
        new MaterialShowcaseView.Builder(this)
                .setTarget(pick)
                .setDismissText("Okay")
                .setContentText("Click here to pick an emergency contact . These contacts will be notified in case of emergency ")
                .setDelay(500) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
                .show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshItemsFromTable() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<FriendsTable> results = refreshItemsFromMobileServiceTable();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            friendsTableAdapter.clear();

                            for (FriendsTable item : results) {
                                friendsTableAdapter.add(item);
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

    private MobileServiceList<FriendsTable> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException, MobileServiceException {
        return mTable.execute().get();
    }

    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }

    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if (exception.getCause() != null) {
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    public void pickContact(View v) {
        Intent i = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            getContactData(data);
        }
    }

    public void getContactData(Intent data){
        ContentResolver cr = getContentResolver();

        Uri contactData = data.getData();
        Log.v("Contact", contactData.toString());
        Cursor c = managedQuery(contactData,null,null,null,null);

        if(c.moveToFirst()){
            id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            Log.v("Contact", "ID: " + id.toString());
            name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Log.v("Contact", "Name: " + name.toString());

            if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);

                while(pCur.moveToNext()){
                    phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Log.v("getting phone number", "Phone Number: " + phone);
                }
            }
            intent.putExtra(CONTACT_INFO_ID, id);
            intent.putExtra(CONTACT_INFO_NAME, name);
            intent.putExtra(CONTACT_INFO_PHONE_NUMBER, phone);
            intent.putExtra(CONTACT_INFO_EDIT, false);
            startActivity(intent);
            refreshItemsFromTable();
        }
    }
}