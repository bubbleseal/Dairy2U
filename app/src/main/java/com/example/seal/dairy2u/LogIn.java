package com.example.seal.dairy2u;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LogIn extends AppCompatActivity {

    private EditText email_field;
    private EditText password_field;

    private Button b_forgotPW;
    private Button b_sign;
    private Button b_register;

    //private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //Custom typefaces
        Typeface mainfont = Typeface.createFromAsset(getAssets(), "fonts/Neon.ttf");

        //Views
        email_field = (EditText) findViewById(R.id.signin_email);
        password_field = (EditText) findViewById(R.id.signin_password);

        //Button
        b_forgotPW = (Button) findViewById(R.id.link_pwdforgot);
        b_register = (Button) findViewById(R.id.link_reg);
        b_sign = (Button) findViewById(R.id.b_sign);
        b_sign.setTypeface(mainfont);

        b_forgotPW.setOnClickListener(new LogIn.ButtonHandler());
        b_register.setOnClickListener(new LogIn.ButtonHandler());
        b_sign.setOnClickListener(new LogIn.ButtonHandler());
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    class ButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            final String email = email_field.getText().toString();
            final String pw = password_field.getText().toString();

            int i = v.getId();
            if (i == R.id.b_sign) {
                signIn(email, pw);
            } else if (i == R.id.link_reg) {
                startActivity(new Intent(LogIn.this, Registration.class));
                finish();
            } else if (i == R.id.link_pwdforgot){

            }
        }
    }


    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(LogIn.this, "Log In Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void onAuthSuccess(FirebaseUser user) {
        // --- Go to Product List page
        startActivity(new Intent(LogIn.this, ItemList.class));
    }


    //Form validation for email and password
    private boolean validateForm() {
        boolean valid = true;

        String email = email_field.getText().toString();
        String password = password_field.getText().toString();

        if (TextUtils.isEmpty(email)) {
            email_field.setError("Required.");
            valid = false;
        } else {
            email_field.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            password_field.setError("Required.");
            valid = false;
        } else {
            password_field.setError(null);
        }
        return valid;
    }

}

