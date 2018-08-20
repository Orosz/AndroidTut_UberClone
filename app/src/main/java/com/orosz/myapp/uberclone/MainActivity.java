package com.orosz.myapp.uberclone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Switch riderOrDriverButton;
    Button getStartedButton;

    static String userOption;

    private FirebaseAuth mAuth;

    public void getStarted(View view) {

        String riderOrDriver = "rider";

        //Check user mode
        if (riderOrDriverButton.isChecked()) {

            riderOrDriver = "driver";

        }

        userOption = riderOrDriver.toString();
        Toast.makeText(getApplicationContext(), mAuth.getCurrentUser().getUid(), Toast.LENGTH_LONG).show();

        //redirect user depending on userOption

        if (userOption.equals("rider")) {

            Intent intent = new Intent(getApplicationContext(), YourLocation.class);
            intent.putExtra("UserUID", mAuth.getCurrentUser().getUid());
            intent.putExtra("UserOption", userOption);
            startActivity(intent);

        } else {

            Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
            intent.putExtra("UserUID", mAuth.getCurrentUser().getUid());
            intent.putExtra("UserOption", userOption);
            startActivity(intent);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        getStartedButton = (Button) findViewById(R.id.getStartedButton);
        riderOrDriverButton = (Switch) findViewById(R.id.switchUserMode);

        //Anonymous Auth
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            Toast.makeText(getApplicationContext(), "User is a " + userOption,
//                                    Toast.LENGTH_LONG).show();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });




    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }



}
