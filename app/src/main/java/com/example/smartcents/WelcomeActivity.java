//package where our Java class is stored
package com.example.smartcents;

//importing the necessary classes for our app
import android.content.Intent; //used to move from one screen to another
import android.os.Bundle; //used to save and restore activity state
import android.view.View; //allows us to set up button actions
import android.widget.Button; //the button we use to start account creation
import androidx.appcompat.app.AppCompatActivity; //base class for activities that use the modern look

//this class represents the welcome screen activity
public class WelcomeActivity extends AppCompatActivity {

    //onCreate method is the first method called when this screen opens
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //calling the parent class to set up the activity

        setContentView(R.layout.activity_welcome); //setting the layout of this activity to the welcome screen XML

        //finding the button by its ID so we can set up what it does when clicked
        Button getStartedButton = findViewById(R.id.getStartedButton);

        //setting up a click listener on the Get Started button
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //this code runs when the button is clicked
                //creating an Intent to move from this screen to the MainActivity screen
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent); //starting the MainActivity
                finish(); //closing the welcome screen so it doesn’t remain in the background
            }
        });
    }
}