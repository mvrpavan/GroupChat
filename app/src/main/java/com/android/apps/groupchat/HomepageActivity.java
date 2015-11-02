package com.android.apps.groupchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends Activity {

    ListView listViewMessages;
    MessagesAdapter objMessagesAdapter;
    List<ParseObject> listMessages;
    ParseUser currentUser;
    String currentUsername;
    View viewLoadingPanel;

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
            currentUsername = currentUser.getUsername();
            listViewMessages = (ListView) findViewById(R.id.listViewMessages);
            viewLoadingPanel = findViewById(R.id.loadingPanelHomepage);

            CommonFunctions.disableEnableControls(false, (ViewGroup) findViewById(R.id.relativelayoutHomepage));
            viewLoadingPanel.setVisibility(View.VISIBLE);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupMessages");
            query.orderByDescending("createdAt");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> MessageParseObjects, ParseException e) {
                    CommonFunctions.disableEnableControls(true, (ViewGroup) findViewById(R.id.relativelayoutHomepage));
                    viewLoadingPanel.setVisibility(View.GONE);
                    if (e == null) {
                        listMessages = MessageParseObjects;

                        objMessagesAdapter = new MessagesAdapter(listViewMessages.getContext(), listMessages);
                        listViewMessages.setAdapter(objMessagesAdapter);
                        objMessagesAdapter.notifyDataSetChanged();
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomepageActivity.this);
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

        listViewMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent takeToMessageDetails = new Intent(HomepageActivity.this, MessageDetailsActivity.class);
                takeToMessageDetails.putExtra("ObjectID", listMessages.get(position).getObjectId());
                startActivityForResult(takeToMessageDetails, 0);
            }
        });
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
                Intent reloadActivity = new Intent(HomepageActivity.this, HomepageActivity.class);
                startActivity(reloadActivity);
                finish();
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
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomepageActivity.this);
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

            convertView.setTag(objMessageViewHolder);
        }
        else {
            objMessageViewHolder = (MessageViewHolder) convertView.getTag();
        }

        objMessageViewHolder.Username.setText(ListMessages.get(position).getString("Username"));
        objMessageViewHolder.Message.setText(ListMessages.get(position).getString("Message"));

        return convertView;
    }

    public static class MessageViewHolder {
        TextView Username;
        TextView Message;
    }
}