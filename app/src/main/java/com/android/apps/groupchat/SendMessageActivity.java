package com.android.apps.groupchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class SendMessageActivity extends Activity {

    EditText editTextMessage;
    Button btnSendMessage, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        //Initialize
        editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        btnSendMessage = (Button) findViewById(R.id.btnSendMessage);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnSendMessage.setEnabled(false);

        //ActionListeners
        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!editTextMessage.getText().toString().isEmpty())
                    btnSendMessage.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseObject objParseObject = ParseObject.create("GroupMessages");
                objParseObject.put("Username", ParseUser.getCurrentUser().getUsername());
                objParseObject.put("Message", editTextMessage.getText().toString());

                objParseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(SendMessageActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            Intent takeToHomepageActivity = new Intent(SendMessageActivity.this, HomepageActivity.class);
                            startActivity(takeToHomepageActivity);
                            finish();
                        }
                        else {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SendMessageActivity.this);
                            alertDialogBuilder.setTitle("Error");
                            alertDialogBuilder.setMessage(e.getMessage().toUpperCase());
                            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });

                            alertDialogBuilder.create().show();
                        }
                    }
                });
            }
        });
    }
}
