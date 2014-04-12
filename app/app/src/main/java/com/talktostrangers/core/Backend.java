package com.talktostrangers.core;


import android.location.Location;
import android.util.Log;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.EventCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Future;

/**
 * Created by simon on 12.04.14.
 */
public class Backend implements ConnectCallback {
    private static Backend ourInstance = new Backend();
    public static Backend getInstance() {
        return ourInstance;
    }
    private Backend() {}

    private static final String TAG = "Backend";
    public static final String URL = "http://10.0.5.10:3001";
    public static final String AREA_CHAT = "area";

    private SocketIOClient mClient;
    private ChatListener mChatListener;

    private Future<SocketIOClient> cc;
    public void connect() {
        cc = SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), URL, this);
    }

    public boolean isConnected() {
        if (mClient != null) {
            return mClient.isConnected();
        }
        return false;
    }

    public void disconnect() {
        if (mClient != null) {
            mClient.disconnect();
            mClient = null;
        }
    }

    public void updateLocation(Location loc) {

        double arr[] = {loc.getLatitude(), loc.getLongitude()};
        try {
            mClient.emit("update_location", new JSONArray(arr));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void subscribeToChat(String type) {
        // TODO
    }

    @Override
    public void onConnectCompleted(Exception ex, SocketIOClient client) {
        if (ex != null) {
            ex.printStackTrace();
            return;
        }
        mClient = client;
        Log.d(TAG, "connected");

        try {
            double arr[] = {22, 23};
            mClient.emit("update_location", new JSONArray(arr));
        } catch (JSONException e) {}

        EventCallback callback =new EventCallback() {
            @Override
            public void onEvent(JSONArray jsonArray, Acknowledge acknowledge) {
                if (mChatListener == null) {
                    Log.d(TAG, "No chat listener set");
                    return;
                }

                Message messages[] = new Message[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Message m = new Message();
                        m.timestamp = obj.getLong("timestamp");
                        m.text = obj.getString("text");
                        m.authorID = obj.getString("authorID");
                        m.authorName = obj.getString("authorName");

                        messages[i] = m;
                    } catch (JSONException e) {
                        Log.e(TAG, "Json parsing error", e);
                    }
                }
                mChatListener.onReceivedMessages(messages);
            }
        };

        client.on("get_msg_buffer", callback);
        client.on("get_msg", callback);
    }

    public void setChatListener(ChatListener _listener) {
        mChatListener = _listener;

        for (int i = 0; i < 10; i++) {
            Message m = new Message();
            m.text = "Hello "+i;
            mChatListener.onReceivedMessages(new Message[]{m});
        }
    }

    public interface ChatListener {
        public void onReceivedMessages(Message[] messages);
    }
}
