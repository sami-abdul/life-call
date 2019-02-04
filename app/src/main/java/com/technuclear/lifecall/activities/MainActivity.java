package com.technuclear.lifecall.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.technuclear.lifecall.R;
import com.technuclear.lifecall.entities.User;
import com.technuclear.lifecall.fragments.HomeFragment;
import com.technuclear.lifecall.fragments.MoviesFragment;
import com.technuclear.lifecall.fragments.NotificationsFragment;
import com.technuclear.lifecall.fragments.PhotosFragment;
import com.technuclear.lifecall.others.CircleTransform;
import com.technuclear.lifecall.tables.FriendsTable;
import com.technuclear.lifecall.tables.PhoneNumbersTable;
import com.technuclear.lifecall.tables.UserTable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.technuclear.lifecall.activities.LoginActivity.currentUserID;
import static com.technuclear.lifecall.activities.LoginActivity.mClient;
import static com.technuclear.lifecall.activities.LoginActivity.mTable;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtBloodGroup;
    private Toolbar toolbar;

    // urls to load navigation header background image
    // and profile image
    //private static final String urlNavHeaderBg = "http://wallpapercave.com/wp/KuUolRB.png";
    private static final String urlNavHeaderBg = "http://api.androidhive.info/images/nav-menu-header-bg.jpg";
    private static final String urlProfileImg = "https://cdn2.iconfinder.com/data/icons/ios-7-icons/50/user_male2-512.png";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_TRACK_PATIENT = "trackPatient";
    private static final String TAG_EMERGENCY_CONTACTS = "emergencyContacts";
    private static final String TAG_PERSONAL_DETAILS = "personalDetails";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    private SharedPreferences.Editor editor;

    boolean permissionGranted;
    static final Integer REQUEST_CODE = 0x1;

    public static String URL = "url";

    UserTable currentItem;

    User user;

    public static String AMBULANCE_PHONE_NUMBER;

    public static final String FRIEND_IN_TROUBLE_KEY = "friendInTroubleKey";
    public static SharedPreferences friendPref;
    public static SharedPreferences.Editor friendPredEditor;

    public static final String EMERGENCY_INITIATED_KEY = "emergencyInitiatedKey";
    public static SharedPreferences emerPref;
    public static SharedPreferences.Editor emerPrefEditor;

    public static ArrayList<String> phoneNumbers;
    public static ArrayList<String> friendsPhoneNumbers;

    public static final String CURRENT_USER_ID = "currentUserId";
    public static final String CURRENT_USER_NAME = "currentUserName";
    public static final String CURRENT_USER_AGE = "currentUserAge";
    public static final String CURRENT_USER_WEIGHT = "currentUserWeight";
    public static final String CURRENT_USER_BLOOD = "currentUserBlood";
    public static final String CURRENT_USER_HAS_DISEASE = "currentUserHasDisease";
    public static final String CURRENT_USER_DISEASE = "currentUserDisease";

    public static SharedPreferences currentUserIdPref;
    public static SharedPreferences currentUserNamePref;
    public static SharedPreferences currentUserAgePref;
    public static SharedPreferences currentUserWeightPref;
    public static SharedPreferences currentUserBloodPref;
    public static SharedPreferences currentUserHasDiseasePref;
    public static SharedPreferences currentUserDiseasePref;

    public static SharedPreferences.Editor currentUserIdPrefEditor;
    public static SharedPreferences.Editor currentUserNamePrefEditor;
    public static SharedPreferences.Editor currentUserAgePrefEditor;
    public static SharedPreferences.Editor currentUserWeightPrefEditor;
    public static SharedPreferences.Editor currentUserBloodPrefEditor;
    public static SharedPreferences.Editor currentUserHasDiseasePrefEditor;
    public static SharedPreferences.Editor currentUserDiseasePrefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentUserIdPref = getSharedPreferences(CURRENT_USER_ID, Context.MODE_PRIVATE);
        currentUserNamePref = getSharedPreferences(CURRENT_USER_NAME, Context.MODE_PRIVATE);
        currentUserAgePref = getSharedPreferences(CURRENT_USER_AGE, Context.MODE_PRIVATE);
        currentUserWeightPref = getSharedPreferences(CURRENT_USER_WEIGHT, Context.MODE_PRIVATE);
        currentUserBloodPref = getSharedPreferences(CURRENT_USER_BLOOD, Context.MODE_PRIVATE);
        currentUserHasDiseasePref = getSharedPreferences(CURRENT_USER_HAS_DISEASE, Context.MODE_PRIVATE);
        currentUserDiseasePref = getSharedPreferences(CURRENT_USER_DISEASE, Context.MODE_PRIVATE);

        currentUserIdPrefEditor = currentUserIdPref.edit();
        currentUserNamePrefEditor = currentUserNamePref.edit();
        currentUserAgePrefEditor = currentUserAgePref.edit();
        currentUserWeightPrefEditor = currentUserWeightPref.edit();
        currentUserHasDiseasePrefEditor = currentUserHasDiseasePref.edit();
        currentUserDiseasePrefEditor = currentUserDiseasePref.edit();
        currentUserBloodPrefEditor = currentUserBloodPref.edit();

        currentItem = new UserTable();

        createUserObject();

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.nav_header_friends_list_name);
        txtBloodGroup = (TextView) navHeader.findViewById(R.id.nav_header_website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // initializing navigation menu
        setUpNavigationView();

        //fetchDriverPhoneNumbers();
        fetchFriendsPhoneNumbers();

        friendPref = getSharedPreferences(FRIEND_IN_TROUBLE_KEY, Context.MODE_PRIVATE);
        friendPredEditor = friendPref.edit();

        emerPref = getSharedPreferences(EMERGENCY_INITIATED_KEY, Context.MODE_PRIVATE);
        emerPrefEditor = emerPref.edit();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

        askForPermissions();

        Uri uri = getIntent().getData();
        if (uri != null) {
            friendPredEditor.putBoolean(FRIEND_IN_TROUBLE_KEY, true).commit();

            String url = uri.getSchemeSpecificPart();
            Intent intent = new Intent(this, TrackPatientActivity.class);
            intent.setData(uri);
            intent.putExtra(URL, url);
            startActivity(intent);
        }
    }

    List<FriendsTable> friendsList;
    private void fetchFriendsPhoneNumbers() {
        final MobileServiceTable<FriendsTable> friendsTable = mClient.getTable(FriendsTable.class);
        friendsPhoneNumbers = new ArrayList<>();
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    friendsList = friendsTable.where().field("deleted").eq(false).execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (friendsList != null)
                            for (FriendsTable item : friendsList) {
                                friendsPhoneNumbers.add(item.getPhoneNumber());
                            }
                    }
                });
                return null;
            }
        }.execute();
    }

    List<PhoneNumbersTable> driverList;
    private void fetchDriverPhoneNumbers() {
        final MobileServiceTable<PhoneNumbersTable> phoneNumbersTable = mClient.getTable(PhoneNumbersTable.class);

        phoneNumbers = new ArrayList<>();
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    driverList = phoneNumbersTable.where().field("deleted").eq(false).execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            int i=0;
                            for (PhoneNumbersTable item : driverList) {
                                phoneNumbers.add(item.getPhoneNumber());
                                i++;
                            }
                    }
                });
                return null;
            }
        }.execute();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void createUserObject() {
        final Thread t = new Thread() {
            @Override
            public void run() {
                if (isNetworkAvailable()) {
                    currentItem = getItemReferenceFromTable();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (currentItem != null) {
                                currentUserIdPrefEditor.putString(CURRENT_USER_ID, currentItem.getId()).commit();
                                currentUserNamePrefEditor.putString(CURRENT_USER_NAME, currentItem.getName()).commit();
                                currentUserAgePrefEditor.putInt(CURRENT_USER_AGE, currentItem.getAge()).commit();
                                currentUserWeightPrefEditor.putInt(CURRENT_USER_WEIGHT, currentItem.getWeight()).commit();
                                currentUserBloodPrefEditor.putString(CURRENT_USER_BLOOD, currentItem.getBloodGroup()).commit();
                                currentUserHasDiseasePrefEditor.putBoolean(CURRENT_USER_HAS_DISEASE, currentItem.getHasDisease()).commit();
                                currentUserDiseasePrefEditor.putString(CURRENT_USER_DISEASE, currentItem.getDisease()).commit();
                                user = new User(currentItem.getId(), currentItem.getName(), currentItem.getAge(), currentItem.getWeight(), currentItem.getHasDisease(), currentItem.getDisease(), currentItem.getBloodGroup());
                            }
                            loadNavHeader();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            user = new User(currentUserIdPref.getString(CURRENT_USER_ID, null), currentUserNamePref.getString(CURRENT_USER_NAME, null), currentUserAgePref.getInt(CURRENT_USER_AGE, 0), currentUserWeightPref.getInt(CURRENT_USER_WEIGHT, 0),
                                    currentUserHasDiseasePref.getBoolean(CURRENT_USER_HAS_DISEASE, false), currentUserDiseasePref.getString(CURRENT_USER_DISEASE, null), currentUserBloodPref.getString(CURRENT_USER_BLOOD, null));;
                        }
                    });
                }
            }
        };
        t.start();
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
        if (!temp.isEmpty())
            return temp.get(0);
            // Reference not found, returns null
        else
            return null;
    }

    private void askForPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS};
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
        }
    }

    private boolean checkForSMSPermission() {
        String permission = "android.permission.SEND_SMS";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkForGPSPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED)
            permissionGranted = true;
        else
            Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        if (isNetworkAvailable()) {
            txtName.setText(user.getName());
            txtBloodGroup.setText(user.getBloodGroup());
        } else {
            txtName.setText(currentUserNamePref.getString(CURRENT_USER_NAME, null));
            txtBloodGroup.setText(currentUserBloodPref.getString(CURRENT_USER_BLOOD, null));
        }

        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // photos
                PhotosFragment photosFragment = new PhotosFragment();
                return photosFragment;
            case 2:
                // movies fragment
                MoviesFragment moviesFragment = new MoviesFragment();
                return moviesFragment;
            case 3:
                // notifications fragment
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;


            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_friends:
                        startActivity(new Intent(MainActivity.this , FriendsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_track_patient:
                        if (emerPref.getBoolean(EMERGENCY_INITIATED_KEY, false)) {
                            startActivity(new Intent(MainActivity.this, TrackPatientActivity.class));
                            drawer.closeDrawers();
                            return true;
                        }
                        else {
                            Toast.makeText(MainActivity.this, "No Friend in trouble", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    case R.id.nav_personal_details:
                        Intent intent = new Intent(MainActivity.this , UserTableActivity.class);
                        intent.putExtra("edit", true);
                        startActivity(intent);
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void getHelp(View view) {
        if (!checkForGPSPermission() || !checkForSMSPermission()) {
            Toast.makeText(this, "Permissions denied. Please restart the app", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this , SelectEmergencyActivity.class);
        startActivity(intent);
    }
}
