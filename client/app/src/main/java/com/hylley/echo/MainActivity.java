package com.hylley.echo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.text.Normalizer;

public class MainActivity extends AppCompatActivity
{
    //region App
    BottomNavigationView view;

    static FormFragment form_fragment = new FormFragment();
    static ChatFragment chat_fragment = new ChatFragment();

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

        getSupportFragmentManager().beginTransaction().replace(R.id.container, form_fragment).commit();
        view = findViewById(R.id.navbar);
        badge = view.getOrCreateBadge(R.id.chat);

        view.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            }
        });

        client.start();
    }

    public static void set_chat_icon_unread_badge(boolean visible)
    {
        badge.setVisible(visible);
    }

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