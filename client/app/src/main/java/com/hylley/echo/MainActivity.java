package com.hylley.echo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity
{
    Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.client = new Client(
            findViewById(R.id.name_input),
            findViewById(R.id.id_input)
        );

        client.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(!isFinishing() || client == null) return;

        client.end_process();
    }
}