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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

    EditText editTextUsername, editTextPassword;
    Button btnLogin, btnRegister;
    View viewLoadingPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GetControls();
        SetControlDefaults();
        AddActionListeners();
    }

    private void GetControls() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        viewLoadingPanel = findViewById(R.id.loadingPanel);
    }

    private void SetControlDefaults() {
        btnLogin.setEnabled(false);
    }

    private void AddActionListeners() {
        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!editTextUsername.getText().toString().isEmpty() && !editTextPassword.getText().toString().isEmpty()) {
                    btnLogin.setEnabled(true);
                }
                else {
                    btnLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!editTextUsername.getText().toString().isEmpty() && !editTextPassword.getText().toString().isEmpty()) {
                    btnLogin.setEnabled(true);
                }
                else {
                    btnLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonFunctions.disableEnableControls(false, (ViewGroup) findViewById(R.id.relativelayoutLogin));
                viewLoadingPanel.setVisibility(View.VISIBLE);
                //Get Username & Password
                final String Username = editTextUsername.getText().toString();
                String Password = editTextPassword.getText().toString();

                ParseUser.logInInBackground(Username, Password, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null) {
                            //Login Successful
                            Toast.makeText(LoginActivity.this, "Welcome back, " + parseUser.getUsername(), Toast.LENGTH_SHORT).show();
                            Intent homePageIntent = new Intent(LoginActivity.this, HomepageActivity.class);
                            startActivity(homePageIntent);
                            finish();
                        } else {
                            viewLoadingPanel.setVisibility(View.GONE);
                            CommonFunctions.disableEnableControls(true, (ViewGroup) findViewById(R.id.relativelayoutLogin));
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
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

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent RegisterIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(RegisterIntent);
            }
        });
    }
}
