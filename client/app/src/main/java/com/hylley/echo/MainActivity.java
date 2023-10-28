package com.hylley.echo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hylley.echo.chat_handler.ChatFragment;
import com.hylley.echo.network_handler.Client;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity
{
    //region Android app stuff
    BottomNavigationView view;
    FormFragment form_fragment = new FormFragment();
    ChatFragment chat_fragment = new ChatFragment(this);
    int active_fragment;
    static BadgeDrawable badge;
    //endregion

    //region Network stuff
    Client client = new Client("girlhood04", this);
    public static final boolean debug = true;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region Fragments and tabs layout setup
        getSupportFragmentManager().beginTransaction().replace(R.id.container, form_fragment).commit();
        view = findViewById(R.id.navbar);
        badge = view.getOrCreateBadge(R.id.chat); badge.setVisible(false);

        view.setOnItemSelectedListener(item ->
        {
            int switch_fragment = item.getItemId();

            if(switch_fragment == R.id.form)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, form_fragment).commit();
                active_fragment = switch_fragment;
                return true;
            }

            if(switch_fragment == R.id.chat)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, chat_fragment).commit();
                active_fragment = switch_fragment;
                return true;
            }

            return false;
        });
        //endregion

        client.start();
    }

    public void append_network_global_message(String message)
    {
        if(!Client.connected) return;

        HashMap<String, String> packet = new HashMap<>();
        packet.put("request_type", "GLOBAL_TEXT_MESSAGE");
        packet.put("name", Client.id);
        packet.put("text", message);
        client.packet_queue.add(packet);
    }

    public void append_local_global_message(String username, String message_body)
    {
        chat_fragment.add_message(username, message_body);
        if(debug) System.out.println("[" + username + "] " + message_body);
    }

    public void restart_client()
    {
        client.end_process(); client = null;
        client = new Client("girlhood04", this);
        client.start();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(!isFinishing() || client == null) return;

        client.end_process();
        if(debug) System.out.println("Process ended successfully");
    }

    @SuppressWarnings("unused")
    public static void set_chat_icon_unread_badge(boolean visible) { badge.setVisible(visible); }

    @SuppressWarnings("unused")
    public static void set_chat_icon_unread_badge(boolean visible, int pops)
    {
        badge.setNumber(pops);
        badge.setVisible(visible);
    }

    public void fragment_chat_is_ready() { if(debug) System.out.println("Chat is ready"); }
}