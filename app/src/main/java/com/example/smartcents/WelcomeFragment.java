package com.example.smartcents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

//fragment for the welcome screen
public class WelcomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        //set up navigation to account creation when the button is clicked
        view.findViewById(R.id.getStartedButton).setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_welcomeFragment_to_accountCreationFragment)
        );

        return view;
    }
}
