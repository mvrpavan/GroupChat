package com.android.apps.groupchat;

/**
 * Created by pavan on 11/1/2015.
 */
import android.app.Application;
import com.parse.Parse;

public class App extends Application {

    @Override public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "Mmkk5bhnsJq7EUIl1TY8zyCnIT4FBd1UPgf7MQI2", "BnUmx7zFNzlVuHh0hpt9c6pYqs9QWJboxUguhlXI");
    }
}
