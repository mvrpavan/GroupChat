package com.android.apps.groupchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import org.w3c.dom.Text;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends Activity {

    ListView listViewMessages;
    MessagesAdapter objMessagesAdapter;
    List<ParseObject> listMessages;
    ParseUser currentUser;
    String currentUsername;
    View viewLoadingPanel;
    Button btnSendMessageListView;
    EditText editTextSendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            Intent takeToLoginActivity = new Intent(HomepageActivity.this, LoginActivity.class);
            startActivity(takeToLoginActivity);
            finish();
            return;
        }
        else {
            ParseInstallation.getCurrentInstallation().put("user", ParseUser.getCurrentUser().getUsername());
            ParseInstallation.getCurrentInstallation().saveInBackground();

            currentUsername = currentUser.getUsername();
            listViewMessages = (ListView) findViewById(R.id.listViewMessages);
            viewLoadingPanel = findViewById(R.id.loadingPanelHomepage);
            editTextSendMessage = (EditText) findViewById(R.id.editTextSendMessage);
            btnSendMessageListView = (Button) findViewById(R.id.btnSendMessageHomepage);
            btnSendMessageListView.setEnabled(false);
            editTextSendMessage.clearFocus();

            CommonFunctions.disableEnableControls(false, (ViewGroup) findViewById(R.id.relativelayoutHomepage));
            viewLoadingPanel.setVisibility(View.VISIBLE);

            ParseDatastore.ReloadDatastore(HomepageActivity.this, listViewMessages);
            objMessagesAdapter = ParseDatastore.objMessagesAdapter;
            listMessages = ParseDatastore.listMessages;

            CommonFunctions.disableEnableControls(true, (ViewGroup) findViewById(R.id.relativelayoutHomepage));
            viewLoadingPanel.setVisibility(View.GONE);
        }

        listViewMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent takeToMessageDetails = new Intent(HomepageActivity.this, MessageDetailsActivity.class);
                takeToMessageDetails.putExtra("ObjectID", ParseDatastore.listMessages.get(position).getObjectId());
                takeToMessageDetails.putExtra("Message", ParseDatastore.listMessages.get(position).get("Message").toString());
                startActivityForResult(takeToMessageDetails, 0);
            }
        });

        editTextSendMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!editTextSendMessage.getText().toString().isEmpty())
                    btnSendMessageListView.setEnabled(true);
                else
                    btnSendMessageListView.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnSendMessageListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseObject objParseObject = ParseObject.create("GroupMessages");
                objParseObject.put("Username", ParseUser.getCurrentUser().getUsername());
                objParseObject.put("Message", editTextSendMessage.getText().toString());

                objParseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            editTextSendMessage.setText("");
                            editTextSendMessage.clearFocus();
                            KeyBoard.toggle(HomepageActivity.this);

                            ParseDatastore.ReloadDatastore(HomepageActivity.this, listViewMessages);
                            objMessagesAdapter = ParseDatastore.objMessagesAdapter;
                            listMessages = ParseDatastore.listMessages;

                            ParsePush push = new ParsePush();
                            ParseQuery objQuery = ParseInstallation.getQuery();
                            objQuery.whereNotEqualTo("installationId", ParseInstallation.getCurrentInstallation().getInstallationId());
                            push.setQuery(objQuery);
                            push.setMessage(ParseUser.getCurrentUser().getUsername() + ": " + editTextSendMessage.getText().toString());
                            push.sendInBackground(new SendCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) {
                                        Toast.makeText(HomepageActivity.this, "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            AlertDialogs.showErrorDialog(HomepageActivity.this, e);
                        }
                    }
                });
            }
        });

        KeyBoard.toggle(HomepageActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homepagemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        CommonFunctions.disableEnableControls(false, (ViewGroup) findViewById(R.id.relativelayoutHomepage));
        viewLoadingPanel.setVisibility(View.VISIBLE);

        int id = item.getItemId();

        switch (id) {
            case R.id.menuitemSendMessage:
                CommonFunctions.disableEnableControls(true, (ViewGroup) findViewById(R.id.relativelayoutHomepage));
                viewLoadingPanel.setVisibility(View.GONE);
                Intent takeToSendMessageActivity = new Intent(HomepageActivity.this, SendMessageActivity.class);
                startActivity(takeToSendMessageActivity);
                finish();
                break;
            case R.id.menuitemReload:
                ParseDatastore.ReloadDatastore(HomepageActivity.this, listViewMessages);
                objMessagesAdapter = ParseDatastore.objMessagesAdapter;
                listMessages = ParseDatastore.listMessages;

                CommonFunctions.disableEnableControls(true, (ViewGroup) findViewById(R.id.relativelayoutHomepage));
                viewLoadingPanel.setVisibility(View.GONE);

                Toast.makeText(HomepageActivity.this, "Messages Reloaded", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuitemLogout:
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(HomepageActivity.this, "Goodbye " + currentUsername, Toast.LENGTH_SHORT).show();
                            Intent takeToLoginActivity = new Intent(HomepageActivity.this, LoginActivity.class);
                            startActivity(takeToLoginActivity);
                            finish();
                        } else {
                            viewLoadingPanel.setVisibility(View.GONE);
                            CommonFunctions.disableEnableControls(true, (ViewGroup) findViewById(R.id.relativelayoutHomepage));
                            AlertDialogs.showErrorDialog(HomepageActivity.this, e);
                        }
                    }
                });
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

class MessagesAdapter extends ArrayAdapter<ParseObject> {

    Context objContext;
    List<ParseObject> ListMessages;
    public MessagesAdapter(Context context, List<ParseObject> Messages) {
        super(context, R.layout.activity_message_list_item, Messages);
        objContext = context;
        ListMessages = Messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageViewHolder objMessageViewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(objContext).inflate(R.layout.activity_message_list_item, null);
            objMessageViewHolder = new MessageViewHolder();
            objMessageViewHolder.Username = (TextView) convertView.findViewById(R.id.tvUsernameListView);
            objMessageViewHolder.Message = (TextView) convertView.findViewById(R.id.tvMessageListView);
            objMessageViewHolder.SentDate = (TextView) convertView.findViewById(R.id.tvSentDateListView);

            convertView.setTag(objMessageViewHolder);
        }
        else {
            objMessageViewHolder = (MessageViewHolder) convertView.getTag();
        }

        objMessageViewHolder.Username.setText(ListMessages.get(position).getString("Username"));
        objMessageViewHolder.Message.setText(ListMessages.get(position).getString("Message"));
        DateFormat df = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss");
        objMessageViewHolder.SentDate.setText(df.format(ListMessages.get(position).getCreatedAt()));

        return convertView;
    }

    public static class MessageViewHolder {
        TextView Username;
        TextView Message;
        TextView SentDate;
    }
}