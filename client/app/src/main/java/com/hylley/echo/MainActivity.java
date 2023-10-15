package com.hylley.echo;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;


public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // try { ListenNetwork.listen(); } catch (Exception e) { throw new RuntimeException(e); }
    }
}

class ListenNetwork
{
    public static boolean keep_listening = true;
    static int LISTEN_PORT = 6969;

    public static InetAddress server_address;

    public static void listen() throws Exception
    {
        try(DatagramSocket socket = new DatagramSocket(LISTEN_PORT))
        {
            byte[] data_buffer = new byte[1024];

            DatagramPacket received_packet = new DatagramPacket(data_buffer, data_buffer.length);
            socket.receive(received_packet);

            String message = new String(received_packet.getData(), received_packet.getOffset(), received_packet.getLength());
            server_address = received_packet.getAddress();

            System.out.println(server_address.getHostAddress());
        }
    }
}