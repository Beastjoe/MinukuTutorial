package edu.umich.si.inteco.tutorial2;

import android.app.Application;
import android.content.Context;

import com.bugfender.sdk.Bugfender;
import com.firebase.client.Config;
import com.firebase.client.Firebase;

import edu.umich.si.inteco.minuku.config.Constants;
import edu.umich.si.inteco.minuku.config.UserPreferences;

/**
 * Created by mwnewman on 6/2/17.
 */

public class MinukuTutorialApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize preferences
        Context context = getApplicationContext();
        UserPreferences.getInstance().Initialize(context);
        Config mConfig = Firebase.getDefaultConfig();
        mConfig.setPersistenceEnabled(true);
        Firebase.setDefaultConfig(mConfig);
        Firebase.setAndroidContext(context);

        //Third party logging and log monitoring mechanism.
        //https://bugfender.com/
        Bugfender.init(this, "N7pdXEGbmKhK9k8YtpFPyXORtsAwgZa5", false);
        Bugfender.setForceEnabled(true);
    }
}
