package com.hylley.echo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity
{
    Client client = new Client(); // Não acho que haja como ser mais específico.

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(!isFinishing() || client == null) return;

        client.end_process();
    }
}