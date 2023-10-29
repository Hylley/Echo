package com.hylley.echo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class FormFragment extends Fragment
{
    MainActivity main_activity;

    EditText full_name_input;
    EditText user_name_input;

    public FormFragment(MainActivity main_activity)
    {
        super();
        this.main_activity = main_activity;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        full_name_input = view.findViewById(R.id.full_name);
        user_name_input = view.findViewById(R.id.user_name);

        full_name_input.addTextChangedListener(new TextWatcher()
        {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { main_activity.full_name_changed(String.valueOf(s)); }
        });

        user_name_input.addTextChangedListener(new TextWatcher()
        {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { main_activity.user_name_changed(String.valueOf(s)); }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_form, container, false);
    }
}