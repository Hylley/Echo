package com.hylley.echo;

import android.util.Log;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.net.UnknownHostException;

class Client extends Thread implements Runnable
{
    static final int LISTEN_PORT = 6969;
    static final int SEND_PORT = 6968;

    static boolean keep_listening = true;
    public static InetAddress server_address;

    @Override
    public void run()
    {
        try {
            Log.d("STATE", "Hearing: " + InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        try (DatagramSocket socket = new DatagramSocket(LISTEN_PORT))
        {
            byte[] data_buffer = new byte[1024];

            while (keep_listening)
            {
                DatagramPacket received_packet = new DatagramPacket(data_buffer, data_buffer.length);
                socket.receive(received_packet);

                String message = new String(received_packet.getData(), received_packet.getOffset(), received_packet.getLength());
                server_address = received_packet.getAddress();

                System.out.println(server_address.getHostAddress());
                System.out.println(message);

                if(!message.equals("ATTENDANCE_COUNT") || server_address == null) continue;

                pingServer();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void pingServer() throws IOException
    {
        if(server_address == null) return;

        DatagramSocket socket = new DatagramSocket();
        String message = "girlhood2nt93";

        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), server_address, SEND_PORT);
        socket.send(packet);
        socket.close();
    }
}