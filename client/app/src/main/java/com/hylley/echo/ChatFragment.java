package com.hylley.echo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import org.jetbrains.annotations.NotNull;

public class ChatFragment extends Fragment
{
    ScrollView scroll_view;

    @Override
    public void onViewCreated(@NonNull View view, @NotNull Bundle savedInstanceState)
    {
        scroll_view = getView().findViewById(R.id.chat_scroll_view);
        scroll_view.post(() -> scroll_view.scrollTo(0, scroll_view.getBottom()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
}