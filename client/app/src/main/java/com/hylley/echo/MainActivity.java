package com.hylley.echo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity
{
    Client client = new Client("girlhood04");

    public static final boolean debug = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(debug) System.out.println("Server started");
        client.start();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if(!isFinishing() || client == null) return;

        client.end_process();
    }
}