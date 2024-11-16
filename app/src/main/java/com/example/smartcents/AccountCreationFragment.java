package com.example.smartcents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class AccountCreationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //create the layout for this screen.
        return inflater.inflate(R.layout.fragment_account_creation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set up the button to navigate to the Phone Entry screen.
        view.findViewById(R.id.btn_continue_with_phone).setOnClickListener(v -> {
            //navigate to the PhoneEntryFragment.
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_accountCreationFragment_to_phoneEntryFragment);
        });
    }
}
