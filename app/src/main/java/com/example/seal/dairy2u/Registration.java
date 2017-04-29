package com.example.seal.dairy2u;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Registration extends AppCompatActivity{

    private EditText name_field;
    private EditText email_field;
    private EditText password_field;

    private FirebaseUser user;
    private DatabaseReference mDB;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mDB = FirebaseDatabase.getInstance().getReference();

        //Custom typefaces
        Typeface mainfont = Typeface.createFromAsset(getAssets(), "fonts/Neon.ttf");

        //Views
        name_field = (EditText) findViewById(R.id.input_name);
        name_field.setTypeface(mainfont);
        email_field = (EditText) findViewById(R.id.input_email);
        email_field.setTypeface(mainfont);
        password_field = (EditText) findViewById(R.id.input_password);
        password_field.setTypeface(mainfont);

        //Button
        Button b_reg = (Button) findViewById(R.id.b_reg);
        b_reg.setTypeface(mainfont);

        b_reg.setOnClickListener(new ButtonHandler());

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user != null) {
                    // User is signed in
                    Toast.makeText(Registration.this, R.string.signed_in, Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(registration.this, .class));
                }
            }
        };
        // [END auth_state_listener]
    }


    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]


    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    // [END on_stop_remove_listener]


    class ButtonHandler implements OnClickListener {
        public void onClick(View view) {

            final String email = email_field.getText().toString();
            final String pw = password_field.getText().toString();

            createAccount(email, pw);
        }
    }


    private void createAccount(String email, String password) {
        final String newemail = email;

        //--- Validate form
        if (!validateForm()) {
            return;
        }

        //-- Create user with email and password into Firebase
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //--- if registration fails
                if (!task.isSuccessful()) {
                    Toast.makeText(Registration.this, R.string.reg_failed, Toast.LENGTH_SHORT).show();

                } else {
                    //-- If registration successful, get uid and name
                    uid = task.getResult().getUser().getUid();
                    final String name = name_field.getText().toString();

                    //--- Create new user into Database and go to events page
                    writeNewUser(name, newemail);
                    //startActivity(new Intent(registration.this, .class));
                }
            }
        });
    }


    //--- FORM VALIDATION FOR ALL FIELDS
    private boolean validateForm() {
        boolean valid = true;

        String name = name_field.getText().toString();
        String email = email_field.getText().toString();
        String password = password_field.getText().toString();

        if (TextUtils.isEmpty(name)) {
            name_field.setError("Required.");
            valid = false;
        } else {
            name_field.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            email_field.setError("Required.");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_field.setError("Invalid email.");
            valid = false;
        } else {
            email_field.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            password_field.setError("Required.");
            valid = false;
        } else if (password.length() < 6) {
            password_field.setError("Needs to be longer.");
            valid = false;
        } else if (TextUtils.isDigitsOnly(password)){
            password_field.setError("Needs a mix of abc & 123");
            valid = false;
        } else {
            password_field.setError(null);
        }
        return valid;
    }


    @Override
    public void onBackPressed(){
        startActivity(new Intent(Registration.this, LogIn.class));
    }


    //--- WRITE USER TO AUTH DATABASE
    private void writeNewUser(String name, String email) {
        Farmer newFarmer = new Farmer(name, email);
        Toast.makeText(Registration.this, "Successful" , Toast.LENGTH_SHORT).show();
        mDB.child("farmers").child(uid).setValue(newFarmer);
    }
}

