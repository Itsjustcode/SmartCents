package com.example.smartcents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

// Fragment for the welcome screen
public class WelcomeFragment extends Fragment {

    private EditText usernameEditText;
    private EditText passwordEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        // Initialize the UI elements
        usernameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);

        // Set up navigation to account creation when the Sign Up button is clicked
        view.findViewById(R.id.signUpButton).setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_welcomeFragment_to_accountCreationFragment)
        );

        // Set up navigation to home screen when the Login button is clicked
        view.findViewById(R.id.loginButton).setOnClickListener(v -> {
            // Get the username and password from the input fields
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Validate if the username or password is empty
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter both username and password", Toast.LENGTH_SHORT).show();
            } else if (username.equals("admin") && password.equals("1234")) {
                // Successful login
                Toast.makeText(getActivity(), "Login successful!", Toast.LENGTH_SHORT).show();

                // Navigate to the HomeFragment
                Navigation.findNavController(view).navigate(R.id.action_welcomeFragment_to_homeFragment);
            } else {
                // Invalid credentials
                Toast.makeText(getActivity(), "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
