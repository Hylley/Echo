package com.hylley.echo;
//region Imports
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.net.Socket;
//endregion

class Client extends Thread implements Runnable
{
    private static final int LISTEN_PORT = 6969;
    private static final int SEND_PORT = 6968;

    private InetAddress server_address;
    private boolean keep_listening = true;

    @Override
    public void run()
    {
        try( DatagramSocket socket = new DatagramSocket(LISTEN_PORT) )
        {
            byte[] data_buffer = new byte[1024];

            do
            {
                DatagramPacket received_packet = new DatagramPacket(data_buffer, data_buffer.length);
                socket.receive(received_packet);

                String message = new String(received_packet.getData(), received_packet.getOffset(), received_packet.getLength());
                server_address = received_packet.getAddress();

                if(!message.equals("ATTENDANCE_COUNT") || server_address == null) continue;

                ping_server();
            }
            while (keep_listening);

        }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    private void ping_server() throws IOException
    {
        if(server_address == null) return;

        Socket socket = new Socket(server_address, SEND_PORT);
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream()); output.flush();
        output.writeObject("girlhood2nt93");
        output.close(); socket.close();
    }

    public void end_process()
    {
        keep_listening = false;
    }
}