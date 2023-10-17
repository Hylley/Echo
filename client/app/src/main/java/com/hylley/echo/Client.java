package com.hylley.echo;

import android.widget.EditText;
import java.io.ObjectOutputStream;
import java.io.IOException;
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

    private InetAddress server_address;
    private boolean keep_listening = true;

    //region Inputs
    EditText name_input;
    EditText id_input;
    //endregion

    public Client(EditText name_input, EditText id_input)
    {
        this.name_input = name_input;
        this.id_input = id_input;
    }

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
                if(server_address == null) continue;

                switch (message)
                {
                    case "ATTENDANCE_COUNT":
                        ping_attendance();
                        break;
                    case "GLOBAL_TEXT_MESSAGE":
                    case "GLOBAL_FILE_MESSAGE":
                        // Não implementado ainda
                        break;
                }
            }
            while (keep_listening);

        }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    private void ping_attendance() throws IOException
    {
        if(server_address == null) return;

        Socket socket = new Socket(server_address, SEND_PORT);
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream()); output.flush();

        Map<String, String> body = new HashMap<>();
        body.put("request_type", "ATTENDANCE_COUNT"             );
        body.put("user_id"     , id_input.getText().toString()  );
        body.put("user_name"   , name_input.getText().toString());

        output.writeObject(body);
        output.close(); socket.close();
    }

    public void end_process() { keep_listening = false; }
}