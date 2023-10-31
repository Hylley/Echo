package com.hylley.echo.network_handler;

import android.widget.Toast;

import com.hylley.echo.MainActivity;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Client extends Thread implements Runnable
{
    static MainActivity main_activity;

    //region Constants
    private static final int LISTEN_PORT = 6969;
    private static final int SEND_PORT = 6968;
    private static final int PING_ATTENDANCE_PERIOD = 20;
    //endregion

    //region Statics
    public static String id;
    public static String full_name;
    public static String user_name;
    //endregion

    //region Network
    public static Socket socket;
    private static ObjectOutputStream output;
    private static InetAddress server_address;
    ClientListener listener;
    public static boolean connected = false;
    public ConcurrentLinkedQueue<HashMap<String, String>> packet_queue = new ConcurrentLinkedQueue<>(); // Thread-safe API; eu fiz o meu dever de casa ;)
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    //endregion

    public Client(String id, MainActivity main_activity)
    {
        Client.id = id;
        Client.main_activity = main_activity;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void run()
    {
        connected = false;
        // Escuta pacotes de descoberta através de UDP
        if(MainActivity.debug) System.out.println("Searching for broadcast");
        main_activity.runOnUiThread(() -> Toast.makeText(main_activity.getApplicationContext(), "Buscando servidor...", Toast.LENGTH_SHORT).show());

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
            output = new ObjectOutputStream(socket.getOutputStream());

            output.writeObject(id);
            executor.scheduleAtFixedRate(this::ping_attendance, Client.PING_ATTENDANCE_PERIOD, Client.PING_ATTENDANCE_PERIOD, TimeUnit.SECONDS);
        }
        catch (IOException e) { throw new RuntimeException(e); }
        if(MainActivity.debug) System.out.println("Connected");
        main_activity.runOnUiThread(() -> Toast.makeText(main_activity.getApplicationContext(), "Conexão estabelecida", Toast.LENGTH_SHORT).show());

        connected = true;

        // Escuta por novas instruções do servidor
        listener = new ClientListener(main_activity); listener.start();

        // Envia os pacotes da fila
        do
        {
            Iterator<HashMap<String, String>> iterator = packet_queue.iterator();
            while (iterator.hasNext())
            {
                HashMap<String, String> packet = iterator.next();
                send(packet);
                iterator.remove();
            }
        }
        while (!socket.isClosed());
    }

    public void ping_attendance()
    {
        if(Client.full_name.isBlank() || Client.full_name.isEmpty()) return;

        HashMap<String, String> packet = new HashMap<>();
        packet.put("request_type", "PING_ATTENDANCE");
        packet.put("id", Client.id);
        packet.put("full_name", Client.full_name);

        this.send(packet);
    }

    public void send(Object packet)
    {
        try
        {
            output.writeObject(packet);
        }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    public void end_process()
    {
        HashMap<String, String> packet = new HashMap<>();
        packet.put("request_type", "CLIENT_DISCONNECT");
        packet.put("id", Client.id);
        send(packet);
        connected = false;

        try
        {
            socket.close();
        }
        catch (IOException e) { throw new RuntimeException(e); }
        if(MainActivity.debug) System.out.println("End client process");
    }
}