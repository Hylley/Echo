package com.hylley.echo;

import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


class Client extends Thread implements Runnable
{
    private static final int LISTEN_PORT = 6969;
    private static final int SEND_PORT = 6968;
    public static String id;
    private static InetAddress server_address;

    private boolean keep_listening = true;

    public Client(String id) { Client.id = id; }

    @Override
    public void run()
    {
        // Escuta pacotes de descoberta através de UDP
        try( DatagramSocket broadcast = new DatagramSocket(LISTEN_PORT) )
        {
            byte[] data_buffer = new byte[1024];

            do
            {
                DatagramPacket received_packet = new DatagramPacket(data_buffer, data_buffer.length);
                broadcast.receive(received_packet);

                String message = new String(received_packet.getData(), received_packet.getOffset(), received_packet.getLength());


                if(!message.equals("DISCOVERY")) continue;
                Client.server_address = received_packet.getAddress();
                if(Client.server_address == null) continue;


                Socket socket = new Socket(Client.server_address, SEND_PORT);
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream()); output.flush();

                Map<String, String> body = new HashMap<>();
                body.put("request_type", "DISCOVERY_C");

                output.writeObject(body);
                output.close(); socket.close();

                break;
            }
            while (keep_listening);
        }
        catch (IOException e) { throw new RuntimeException(e); }

        if(MainActivity.debug) System.out.println("Client connected to: " + Client.server_address);

        // Escuta pacotes do servidor
        try (Socket socket = new Socket(Client.server_address, LISTEN_PORT))
        {
            do
            {
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message = input.readLine();
                System.out.println("Server: " + message);
            }
            while (keep_listening);
        }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    private static void ping_attendance() throws IOException
    {
        if(Client.server_address == null)
        {
            System.out.println("Err.: Invalid server address null");
            return;
        };

        Socket socket = new Socket(Client.server_address, SEND_PORT);
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream()); output.flush();

        Map<String, String> body = new HashMap<>();
        body.put("request_type", "ATTENDANCE_COUNT");
        body.put("echo_id"     , Client.id         );

        output.writeObject(body);
        output.close(); socket.close();

        if(MainActivity.debug) System.out.println("Attendance response to: " + Client.server_address + " at " + SEND_PORT);
    }

    public void end_process()
    {
        keep_listening = false;
        if(MainActivity.debug) System.out.println("End client process");
    }
}