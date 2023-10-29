package com.hylley.echo.network_handler;

import com.hylley.echo.MainActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.util.HashMap;

public class ClientListener extends Thread implements Runnable
{
    ObjectInputStream input;
    MainActivity main_activity;

    public ClientListener(MainActivity main_activity) { this.main_activity = main_activity; }

    @Override @SuppressWarnings("ConstantConditions")
    public void run()
    {
        do
        {
            try
            {
                input = new ObjectInputStream(Client.socket.getInputStream());
                @SuppressWarnings("unchecked") HashMap<String, String> packet = (HashMap<String, String>) input.readObject();
                if(MainActivity.debug) System.out.println("New package received: " + packet);

                switch (packet.get("request_type"))
                {
                    case "SERVER_DISCONNECT":
                        Client.main_activity.restart_client();
                        return;

                    case "GLOBAL_TEXT_MESSAGE":
                        String origin_username = packet.get("username");
                        main_activity.runOnUiThread(() -> Client.main_activity.append_local_global_message(origin_username, packet.get("text")));
                        break;

                    default: System.out.println("Err: Invalid request type;"); break;
                }
            }
            catch(SocketException e)
            {
                if(!Client.socket.isClosed()) throw new RuntimeException(e);
                if(MainActivity.debug) System.out.println("Socket closed");
            }
            catch (IOException | ClassNotFoundException e) { throw new RuntimeException(e); }
        }
        while (!Client.socket.isClosed());
    }
}
