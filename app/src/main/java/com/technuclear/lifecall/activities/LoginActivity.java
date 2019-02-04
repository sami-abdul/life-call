package com.technuclear.lifecall.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;
import com.technuclear.lifecall.R;
import com.technuclear.lifecall.tables.UserTable;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoginActivity extends AppCompatActivity {

    public static final String CURRENT_USER_ID_KEY = "currentUserKey";
    public static final String HAS_DATA_KEY = "hasDataKey";
    public static final String IS_SIGNED_IN_KEY = "hasDataKey";

    public static boolean hasData;
    public static boolean isSignedIn;

    public static SharedPreferences userIdPref;
    public static SharedPreferences.Editor userIdPrefEditor;

    public static SharedPreferences dataPref;
    public static SharedPreferences.Editor dataEditor;

    public static SharedPreferences signedInPref;
    public static SharedPreferences.Editor signedInPrefEditor;

    public static MobileServiceClient mClient;
    public static MobileServiceTable<UserTable> mTable;

    public static String currentUserID;

    private boolean isNetworkAvailable = false;

    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";

    ProgressBar mProgressBar;

    public boolean bAuthenticating = false;
    public final Object mAuthenticationLock = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //mProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);

        // Initialize the progress bar
        //mProgressBar.setVisibility(ProgressBar.GONE);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }

        isNetworkAvailable = isNetworkAvailable();

        dataPref = getSharedPreferences(HAS_DATA_KEY, Context.MODE_PRIVATE);
        dataEditor = dataPref.edit();
        hasData = dataPref.getBoolean(HAS_DATA_KEY, false);

        userIdPref = getSharedPreferences(CURRENT_USER_ID_KEY, Context.MODE_PRIVATE);
        userIdPrefEditor = userIdPref.edit();
        currentUserID = userIdPref.getString(CURRENT_USER_ID_KEY, null);

        signedInPref = getSharedPreferences(IS_SIGNED_IN_KEY, Context.MODE_PRIVATE);
        signedInPrefEditor = signedInPref.edit();
        isSignedIn = signedInPref.getBoolean(IS_SIGNED_IN_KEY, false);

        createClient();
    }

    private void createClient() {
        try {
            mClient = new MobileServiceClient(
                    "https://lifecall.azurewebsites.net", this)
                    .withFilter(new ProgressFilter())
                    .withFilter(new RefreshTokenCacheFilter());

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });

            if (isSignedIn)
                authenticate(false);

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e) {
            createAndShowDialog(e, "Error");
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void authenticate(boolean bRefreshCache) {

        bAuthenticating = true;

        if (bRefreshCache || !loadUserTokenCache(mClient)) {
            // New login using the provider and update the token cache.
            mClient.login(MobileServiceAuthenticationProvider.Facebook, new UserAuthenticationCallback() {
                        @Override
                        public void onCompleted(MobileServiceUser user,
                                                Exception exception, ServiceFilterResponse response) {

                            synchronized (mAuthenticationLock) {
                                if (exception == null) {
                                    cacheUserToken(mClient.getCurrentUser());

                                    signedInPrefEditor.putBoolean(IS_SIGNED_IN_KEY, true).commit();

                                    currentUserID = user.getUserId();
                                    userIdPrefEditor.putString(CURRENT_USER_ID_KEY, currentUserID).commit();

                                    Toast.makeText(LoginActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
                                    createTable();

                                    if (hasData)
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    else
                                        startActivity(new Intent(LoginActivity.this, UserTableActivity.class));
                                } else {
                                    createAndShowDialog(exception.getMessage(), "Login Error");
                                }
                                bAuthenticating = false;
                                mAuthenticationLock.notifyAll();
                            }
                        }
                    });
        } else {
            // Other threads may be blocked waiting to be notified when
            // authentication is complete.
            synchronized (mAuthenticationLock) {
                bAuthenticating = false;
                mAuthenticationLock.notifyAll();
            }

            createTable();
            if (hasData)
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            else
                startActivity(new Intent(LoginActivity.this, UserTableActivity.class));
        }
    }

    private void cacheUserToken(MobileServiceUser user) {
        SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USERIDPREF, user.getUserId());
        editor.putString(TOKENPREF, user.getAuthenticationToken());
        editor.commit();
    }

    private boolean loadUserTokenCache(MobileServiceClient client) {
        SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        String userId = prefs.getString(USERIDPREF, null);
        if (userId == null)
            return false;
        String token = prefs.getString(TOKENPREF, null);
        if (token == null)
            return false;

        MobileServiceUser user = new MobileServiceUser(userId);
        user.setAuthenticationToken(token);
        client.setCurrentUser(user);

        return true;
    }

    public boolean detectAndWaitForAuthentication() {
        boolean detected = false;
        synchronized (mAuthenticationLock) {
            do {
                if (bAuthenticating == true)
                    detected = true;
                try {
                    mAuthenticationLock.wait(1000);
                } catch (InterruptedException e) {
                }
            }
            while (bAuthenticating == true);
        }
        if (bAuthenticating == true)
            return true;

        return detected;
    }

    private void waitAndUpdateRequestToken(ServiceFilterRequest request) {
        MobileServiceUser user = null;
        if (detectAndWaitForAuthentication()) {
            user = mClient.getCurrentUser();
            if (user != null) {
                request.removeHeader("X-ZUMO-AUTH");
                request.addHeader("X-ZUMO-AUTH", user.getAuthenticationToken());
            }
        }
    }

    private void createTable() {
        mTable = mClient.getTable(UserTable.class);
    }

    public void onFacebook(View view) {
        authenticate(false);
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

    private class RefreshTokenCacheFilter implements ServiceFilter {
        AtomicBoolean mAtomicAuthenticatingFlag = new AtomicBoolean();

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(
                final ServiceFilterRequest request,
                final NextServiceFilterCallback nextServiceFilterCallback
        )
        {
            // In this example, if authentication is already in progress we block the request
            // until authentication is complete to avoid unnecessary authentications as
            // a result of HTTP status code 401.
            // If authentication was detected, add the token to the request.
            waitAndUpdateRequestToken(request);

            // Send the request down the filter chain
            // retrying up to 5 times on 401 response codes.
            ListenableFuture<ServiceFilterResponse> future = null;
            ServiceFilterResponse response = null;
            int responseCode = 401;
            for (int i = 0; (i < 5 ) && (responseCode == 401); i++)
            {
                future = nextServiceFilterCallback.onNext(request);
                try {
                    response = future.get();
                    responseCode = response.getStatus().code;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    if (e.getCause().getClass() == MobileServiceException.class)
                    {
                        MobileServiceException mEx = (MobileServiceException) e.getCause();
                        responseCode = mEx.getResponse().getStatus().code;
                        if (responseCode == 401)
                        {
                            // Two simultaneous requests from independent threads could get HTTP status 401.
                            // Protecting against that right here so multiple authentication requests are
                            // not setup to run on the UI thread.
                            // We only want to authenticate once. Requests should just wait and retry
                            // with the new token.
                            if (mAtomicAuthenticatingFlag.compareAndSet(false, true))
                            {
                                // Authenticate on UI thread
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Force a token refresh during authentication.
                                        authenticate(true);
                                    }
                                });
                            }

                            // Wait for authentication to complete then update the token in the request.
                            waitAndUpdateRequestToken(request);
                            mAtomicAuthenticatingFlag.set(false);
                        }
                    }
                }
            }
            return future;
        }
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
