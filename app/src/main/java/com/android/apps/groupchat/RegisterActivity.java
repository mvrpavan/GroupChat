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
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends Activity {

    EditText editTextUsername, editTextPassword, editTextEmailAddress;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        SetControls();
        SetControlDefaults();
        AddActionListeners();
    }

    private void SetControls() {
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextEmailAddress = (EditText) findViewById(R.id.editTextEMail);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
    }

    private void SetControlDefaults() {
        btnSignUp.setEnabled(false);
    }

    private void AddActionListeners() {
        editTextUsername.addTextChangedListener(AllEditTextWatcher);
        editTextPassword.addTextChangedListener(AllEditTextWatcher);
        editTextEmailAddress.addTextChangedListener(AllEditTextWatcher);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create new User
                ParseUser parseUser = new ParseUser();
                parseUser.setUsername(editTextUsername.getText().toString());
                parseUser.setPassword(editTextPassword.getText().toString());
                parseUser.setEmail(editTextEmailAddress.getText().toString());

                parseUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(RegisterActivity.this, "User " + editTextUsername.getText().toString() + " added successfully", Toast.LENGTH_LONG).show();
                            Intent takeToLoginActivity = new Intent(RegisterActivity.this, LoginActivity.class);
                            takeToLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(takeToLoginActivity);
                        }
                        else {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
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

    private TextWatcher AllEditTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if  (!editTextUsername.getText().toString().isEmpty()
                    && !editTextPassword.getText().toString().isEmpty()
                    && !editTextEmailAddress.getText().toString().isEmpty()) {
                btnSignUp.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
