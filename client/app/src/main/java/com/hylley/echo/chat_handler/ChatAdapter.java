package com.hylley.echo.chat_handler;
import com.hylley.echo.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatAdapter extends RecyclerView.Adapter<MessageViewHolder>
{
    List<Message> chat;
    RecyclerView view;

    @SuppressWarnings("unused")
    public ChatAdapter(RecyclerView view, List<Message> chat)
    {
        this.view = view;
        this.chat = chat;
    }
    public ChatAdapter(RecyclerView view)
    {
        this.view = view;
        this.chat = new ArrayList<>();
    }

    @NonNull @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new MessageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message, parent, false), this);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position)
    {
        holder.load_values(chat.get(position).username, chat.get(position).message_body);
    }

    @Override
    public int getItemCount() {
        return chat.size();
    }

    public void append(Message message)
    {
        chat.add(message);
        this.notifyItemInserted(chat.size() - 1);
    }

    public void append(String username, String message_body)
    {
        chat.add(new Message(username, message_body));
        int index = chat.size() - 1;
        this.notifyItemInserted(index);

        Objects.requireNonNull(view.getLayoutManager()).scrollToPosition(index);
    }
}

class MessageViewHolder extends RecyclerView.ViewHolder
{
    TextView message_username_view;
    TextView message_body_view;

    ChatAdapter adapter;

    public MessageViewHolder(@NotNull View item_view, ChatAdapter adapter)
    {
        super(item_view);

        this.adapter = adapter;
        this.message_username_view = item_view.findViewById(R.id.message_username);
        this.message_body_view = item_view.findViewById(R.id.message_body);
    }

    public void load_values(String username, String message_body)
    {
        this.message_username_view.setText(username);
        this.message_body_view.setText(message_body);
    }
}