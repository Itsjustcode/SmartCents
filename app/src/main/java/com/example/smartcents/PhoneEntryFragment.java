package com.example.smartcents;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

//fragment for entering a phone number
public class PhoneEntryFragment extends Fragment {

    private EditText edtPhoneNumber; //input field for the phone number
    private Button btnContinue; //button to continue to the next screen

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate and return the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtPhoneNumber = view.findViewById(R.id.edt_phone_number); //find the phone number input field
        btnContinue = view.findViewById(R.id.btn_continue); //find the continue button

        //add a text watcher to format the phone number as the user types
        edtPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        //set what happens when the continue button is clicked
        btnContinue.setOnClickListener(v -> {
            //navigate to the phone verification screen
            Navigation.findNavController(v).navigate(R.id.action_phoneEntryFragment_to_phoneVerificationFragment);
        });
    }

    //class to handle phone number formatting ****WORK IN PROGRESS*****
    private class PhoneNumberFormattingTextWatcher implements TextWatcher {
        private boolean isFormatting; //tracks if formatting is in progress
        private int lastStart; //tracks the cursor position
        //stores the text before changes
        private String lastBefore;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (!isFormatting) { //only update if not already formatting
                lastStart = start; //save the starting cursor position
                lastBefore = s.toString(); //save the text before changes
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!isFormatting) { //prevent recursive calls
                isFormatting = true; //set formatting flag
                String formatted = formatPhoneNumber(s.toString()); //format the phone number
                edtPhoneNumber.setText(formatted); //set the formatted number
                edtPhoneNumber.setSelection(Math.min(formatted.length(), lastStart + count - before)); //adjust cursor position
                isFormatting = false; //reset formatting flag
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            //not used here
        }

        //method to format the phone number as (XXX) XXX-XXXX..............NOT WORKING
        private String formatPhoneNumber(String phone) {
            phone = phone.replaceAll("[^\\d]", ""); // Remove all non-digit characters
            if (phone.length() > 3 && phone.length() <= 6) { // Format as (XXX) XXX
                return "(" + phone.substring(0, 3) + ") " + phone.substring(3);
            } else if (phone.length() > 6) { // Format as (XXX) XXX-XXXX
                return "(" + phone.substring(0, 3) + ") " + phone.substring(3, 6) + "-" + phone.substring(6);
            }
            return phone; // Return as-is for shorter lengths
        }
    }
}
