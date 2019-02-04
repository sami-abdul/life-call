package com.technuclear.lifecall.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.technuclear.lifecall.R;
import com.technuclear.lifecall.activities.FriendsTableActivity;
import com.technuclear.lifecall.activities.MainActivity;
import com.technuclear.lifecall.activities.UserTableActivity;
import com.technuclear.lifecall.adapters.FriendsTableAdapter;
import com.technuclear.lifecall.tables.FriendsTable;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;
import static com.technuclear.lifecall.activities.LoginActivity.mClient;


public class PhotosFragment extends Fragment {

    private static final int RESULT_PICK_CONTACT = 85500;

    public String id;
    public String phone;
    public String name;

    ListView listView;

    Intent intent;

    public static final String CONTACT_INFO_NAME = "name";
    public static final String CONTACT_INFO_ID = "id";
    public static final String CONTACT_INFO_PHONE_NUMBER = "phoneNumber";
    public static final String CONTACT_INFO_EDIT = "infoEdit";

    public static MobileServiceTable<FriendsTable> mTable;
    private FriendsTableAdapter friendsTableAdapter;


    public PhotosFragment() {
        mTable = mClient.getTable(FriendsTable.class);

        friendsTableAdapter = new FriendsTableAdapter(getContext(), R.layout.friends_list);
        refreshItemsFromTable();

        listView = (ListView) getView().findViewById(R.id.listView);
        listView.setAdapter(friendsTableAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView name = (TextView) getView().findViewById(R.id.friends_list_name);
                TextView phone = (TextView) getView().findViewById(R.id.friends_list_phone);
                intent.putExtra(CONTACT_INFO_NAME, name.getText().toString());
                intent.putExtra(CONTACT_INFO_PHONE_NUMBER, phone.getText().toString());
                intent.putExtra(CONTACT_INFO_EDIT, true);
                startActivity(intent);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photos, container, false);
    }

    public void goToAttract(View v)
    {

    }


    /**
     * Refresh the list with the items in the Table
     */
    private void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // adapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<FriendsTable> results = refreshItemsFromMobileServiceTable();

                    //Offline Sync
                    //final List<UserTable> results = refreshItemsFromMobileServiceTableSyncTable();

                    getActivity().runOnUiThread(new Runnable() {
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

    /**
     * Refresh the list with the items in the Mobile Service Table
     */

    private MobileServiceList<FriendsTable> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException, MobileServiceException {
        return mTable.execute().get();
    }

    /**
     * Creates a dialog and shows it
     *
     * @param exception The exception to show in the dialog
     * @param title     The dialog title
     */
    private void createAndShowDialogFromTask(final Exception exception, String title) {
        getActivity().runOnUiThread(new Runnable() {
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            getContactData(data);
        }
    }

    public void getContactData(Intent data){
        ContentResolver cr = getActivity().getContentResolver();

        Uri contactData = data.getData();
        Log.v("Contact", contactData.toString());
        Cursor c = getActivity().managedQuery(contactData,null,null,null,null);

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
            intent = new Intent(getContext(), FriendsTableActivity.class);
            startActivity(intent);
        }
    }
}