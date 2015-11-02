package com.android.apps.groupchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.w3c.dom.Text;

import java.util.List;

public class MessageDetailsActivity extends Activity {

    TextView tvMessageDetails;
    Button btnOKMessageDetails;
    View viewLoadingPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        tvMessageDetails = (TextView) findViewById(R.id.tvMessageDetails);
        btnOKMessageDetails = (Button) findViewById(R.id.btnOKMessageDetails);
        viewLoadingPanel = findViewById(R.id.loadingPanelMessageDetails);

        String ObjectID = getIntent().getStringExtra("ObjectID");

        CommonFunctions.disableEnableControls(false, (ViewGroup) findViewById(R.id.relativelayoutMessageDetails));
        viewLoadingPanel.setVisibility(View.VISIBLE);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupMessages");
        query.getInBackground(ObjectID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                CommonFunctions.disableEnableControls(true, (ViewGroup) findViewById(R.id.relativelayoutMessageDetails));
                viewLoadingPanel.setVisibility(View.GONE);

                if (e == null) {
                    tvMessageDetails.setText(parseObject.getString("Message"));
                }
                else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MessageDetailsActivity.this);
                    alertDialogBuilder.setTitle("Error");
                    alertDialogBuilder.setMessage(e.getMessage().toUpperCase());
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });

                    alertDialogBuilder.create().show();

                    finish();
                }
            }
        });

        btnOKMessageDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
