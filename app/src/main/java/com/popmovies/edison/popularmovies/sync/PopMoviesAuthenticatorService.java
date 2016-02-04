package com.popmovies.edison.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Edison on 2/3/2016.
 * A bound Service that instantiates the authenticator
 * when started.
 */
public class PopMoviesAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private PopMoviesAuthenticator mAuthenticator;
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new PopMoviesAuthenticator(this);
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
