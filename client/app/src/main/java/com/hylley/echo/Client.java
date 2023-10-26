package com.hylley.echo;

import android.renderscript.ScriptGroup;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


class Client extends Thread implements Runnable
{
    //region Constants
    private static final int LISTEN_PORT = 6969;
    private static final int SEND_PORT = 6968;
    //endregion

    public static String id;
    private static InetAddress server_address;

    private Socket socket;
    private InputStream input;
    private OutputStream output;

    public Client(String id) { Client.id = id; }

    @Override
    public void run()
    {
        // Escuta pacotes de descoberta através de UDP
        if(MainActivity.debug) System.out.println("Searching for broadcast");
        try( DatagramSocket broadcast = new DatagramSocket(LISTEN_PORT) )
        {
            byte[] data_buffer = new byte[1024];

            do
            {
                DatagramPacket received_packet = new DatagramPacket(data_buffer, data_buffer.length);
                broadcast.receive(received_packet);

                ObjectInputStream input_stream = new ObjectInputStream(
                                                    new BufferedInputStream( // FUCK YOU, JAVA JUST CREATED THE CLASS NESTING.
                                                            new ByteArrayInputStream(data_buffer))); // Meu programador C interno grita em agonia.

                String message = (String)input_stream.readObject();
                if(MainActivity.debug) System.out.println("Received package: " + message);
                if(message.equals("DISCOVERY")) Client.server_address = received_packet.getAddress();
            }
            while (server_address == null);
        }
        catch (IOException | ClassNotFoundException e) { throw new RuntimeException(e); }

        if(MainActivity.debug) System.out.println("Find server at: " + Client.server_address);

        // Informa ao servidor o ID do cliente para se conectar
        try
        {
            if(MainActivity.debug) System.out.println("Connecting...");
            socket = new Socket(Client.server_address, SEND_PORT);
            BufferedWriter writter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // append and flush in logical chunks
            writter.append(id).append("\n");
            writter.append("appending more before flushing").append("\n");
            writter.flush();
        }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    public void send(Object packet)
    {
        try(ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream()))
        {
            output.writeObject(packet);
        }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    public void end_process()
    {
        try
        {
            socket.close();
        }
        catch (IOException e) { throw new RuntimeException(e); }
        if(MainActivity.debug) System.out.println("End client process");
    }
}