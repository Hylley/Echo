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

import com.hylley.echo.MainActivity;
import com.hylley.echo.R;

public class ChatFragment extends Fragment
{
    MainActivity main_activity;
    RecyclerView chat_recycler_view;
    EditText message_input;

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

        //region Chats and messages
        ChatAdapter chat_recycler_view_adapter = new ChatAdapter(chat_recycler_view);
        LinearLayoutManager chat_recycler_view_manager = new LinearLayoutManager(getContext());
        chat_recycler_view.setLayoutManager(chat_recycler_view_manager);
        chat_recycler_view.setAdapter(chat_recycler_view_adapter);
        chat_recycler_view_manager.setStackFromEnd(true);
        chat_recycler_view_manager.setReverseLayout(false);

        for(int i = 0; i < 50; i++)
        {
            chat_recycler_view_adapter.append("Hylley", "Eu sou lindo " + i);
        }
        //endregion
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
}