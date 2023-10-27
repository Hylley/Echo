package com.hylley.echo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hylley.echo.chat_handler.ChatAdapter;
import com.hylley.echo.chat_handler.ChatFragment;
import com.hylley.echo.chat_handler.Message;
import com.hylley.echo.network_handler.Client;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
{
    //region App
    BottomNavigationView view;

    FormFragment form_fragment = new FormFragment();
    ChatFragment chat_fragment = new ChatFragment(this);

    static BadgeDrawable badge;
    //enregion

    //region Network
    Client client = new Client("girlhood04", this);
    public static final boolean debug = true;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region Fragments and tabs
        getSupportFragmentManager().beginTransaction().replace(R.id.container, form_fragment).commit();
        view = findViewById(R.id.navbar);
        badge = view.getOrCreateBadge(R.id.chat); badge.setVisible(false);

        view.setOnItemSelectedListener(item ->
        {
            int selected_id = item.getItemId();

            if(selected_id == R.id.form)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, form_fragment).commit();
                return true;
            }

            if(selected_id == R.id.chat)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, chat_fragment).commit();
                return true;
            }

            return false;
        });
        //endregion

        client.start();
    }

    public void send_global_message(Message message)
    {
        System.out.println(message);
    }

    @SuppressWarnings("unused")
    public static void set_chat_icon_unread_badge(boolean visible)
    {
        badge.setVisible(visible);
    }

    @SuppressWarnings("unused")
    public static void set_chat_icon_unread_badge(boolean visible, int pops)
    {
        badge.setNumber(pops);
        badge.setVisible(visible);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(!isFinishing() || client == null) return;

        client.end_process();
    }
}