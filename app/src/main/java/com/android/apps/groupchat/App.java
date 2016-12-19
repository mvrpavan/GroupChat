package com.android.apps.groupchat;

/**
 * Created by pavan on 11/1/2015.
 */
import android.app.Application;
import com.parse.Parse;
//import com.parse.ParseConfig;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

public class App extends Application {

    @Override public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        //Parse.initialize(this, "Mmkk5bhnsJq7EUIl1TY8zyCnIT4FBd1UPgf7MQI2", "BnUmx7zFNzlVuHh0hpt9c6pYqs9QWJboxUguhlXI");
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("Mmkk5bhnsJq7EUIl1TY8zyCnIT4FBd1UPgf7MQI2")
                .clientKey(null)
                .server("https://test-nodejs-app.herokuapp.com/parse/").build());
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground("GroupChatMessagesChannel");
    }
}
