package com.emre.parselivequeryjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView messages;
    private EditText message;
    private ImageView send;
    public ProgressDialog progressDialog;


    private ParseLiveQueryClient parseLiveQueryClient;
    private SubscriptionHandling<ParseObject> subscriptionHandling;
    private static final String TAG = "MainActivity";
    private MessagesAdapter messagesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        messages = findViewById(R.id.messages);
        message = findViewById(R.id.message);
        send = findViewById(R.id.send);
        getMessages();
        setupLiveQuery();

        send.setOnClickListener(view -> {
            sendMessage();
        });
    }

    private void sendMessage(){
        progressDialog.show();
        ParseObject object = new ParseObject("Message");
        object.put("message",message.getText().toString());
        object.saveInBackground(e -> {
            progressDialog.hide();
            if (e!=null)
                Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void getMessages() {
        ParseQuery<ParseObject> query = new ParseQuery<>("Message");
        query.findInBackground((objects, e) -> {
            if (e==null){
                initMessages(objects);
            } else {
                Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initLiveQuery() {
        if (subscriptionHandling != null) {
            subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, (query, object) -> {
                MainActivity.this.runOnUiThread(() -> {
                    messagesAdapter.addItem(object);
                });
            });
            subscriptionHandling.handleEvent(SubscriptionHandling.Event.DELETE, (query, object) -> {
                MainActivity.this.runOnUiThread(() -> {
                    messagesAdapter.removeItem(object);
                });
            });
            subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, (query, object) -> {
                MainActivity.this.runOnUiThread(() -> {
                    messagesAdapter.updateItem(object);
                });
            });
        }
    }


    private void setupLiveQuery() {
        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Message");
        subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
        subscriptionHandling.handleSubscribe(query -> {
            initLiveQuery();
        });
    }

    private void initMessages(List<ParseObject> messages) {
        messagesAdapter = new MessagesAdapter(this,messages);
        this.messages.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        this.messages.setAdapter(messagesAdapter);
    }
}

