package com.hylley.echo.chat_handler;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.hylley.echo.MainActivity;
import com.hylley.echo.R;

public class ChatFragment extends Fragment
{
    MainActivity main_activity;
    EditText message_input;
    FrameLayout send_button;

    RecyclerView chat_recycler_view;
    ChatAdapter chat_recycler_view_adapter;
    LinearLayoutManager chat_recycler_view_manager;

    public ChatFragment(MainActivity main_activity)
    {
        super();
        this.main_activity = main_activity;
    }
    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        chat_recycler_view = view.findViewById(R.id.recycler_view);
        message_input = view.findViewById(R.id.message_input);
        send_button = view.findViewById(R.id.send_button);

        //region Chats, messages and related stuff
        chat_recycler_view_adapter = new ChatAdapter(chat_recycler_view);
        chat_recycler_view_manager = new LinearLayoutManager(getContext());
        chat_recycler_view.setLayoutManager(chat_recycler_view_manager);
        chat_recycler_view.setAdapter(chat_recycler_view_adapter);
        chat_recycler_view_manager.setStackFromEnd(true);
        chat_recycler_view_manager.setReverseLayout(false);

        message_input.setOnFocusChangeListener((v, focused) ->
        {
            if(!focused) return;
            chat_recycler_view_adapter.scroll_bottom();
        });

        send_button.setOnClickListener((_view) ->
        {
            String text = String.valueOf(message_input.getText());
            if (text.isEmpty() || text.isBlank()) return;

            main_activity.append_network_global_message(text);
            message_input.setText("");
        });
        //endregion

        main_activity.fragment_chat_is_ready();
    }

    public void add_message(String username, String message_body)
    {
        chat_recycler_view_adapter.append(username, message_body);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
}