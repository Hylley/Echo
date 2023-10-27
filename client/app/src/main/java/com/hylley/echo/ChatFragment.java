package com.hylley.echo;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;

public class ChatFragment extends Fragment
{
    RecyclerView recycler_view;
    EditText message_input;

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        recycler_view = view.findViewById(R.id.recycler_view);
        message_input = view.findViewById(R.id.message_input);

        recycler_view.post(() -> recycler_view.scrollTo(0, recycler_view.getBottom()));
        message_input.setOnTouchListener((_view, motion) ->
        {
            recycler_view.scrollTo(0, recycler_view.getBottom());
            return false;
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
}