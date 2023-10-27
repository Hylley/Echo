package com.hylley.echo.network_handler;

import com.hylley.echo.MainActivity;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Client extends Thread implements Runnable
{
    //region Constants
    private static final int LISTEN_PORT = 6969;
    private static final int SEND_PORT = 6968;
    //endregion

    public static String id;
    private static InetAddress server_address;
    ClientListener listener;
    public ConcurrentLinkedQueue<HashMap<String, String>> packet_queue = new ConcurrentLinkedQueue<>();

    public static Socket socket;

    static MainActivity main_activity;

    public Client(String id, MainActivity main_activity)
    {
        Client.id = id;
        Client.main_activity = main_activity;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
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
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // append and flush in logical chunks
            writer.append(id).append("\n");
            writer.append("appending more before flushing").append("\n");
            writer.flush();
        }
        catch (IOException e) { throw new RuntimeException(e); }

        // Escuta por novas instruções do servidor
        listener = new ClientListener(); listener.start();

        // Envia os pacotes da fila
        do
        {
            for (HashMap<String, String> packet : packet_queue)
            {
                send(packet);
            }
        }
        while (!socket.isClosed());
    }

    public void send(Object packet)
    {
        try
        {
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
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