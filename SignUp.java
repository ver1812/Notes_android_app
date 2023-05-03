package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {

    EditText editTextTextEmailAddress,editTextTextPassword,editTextTextPassword2;
    Button  button,button2;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextTextEmailAddress = findViewById(R.id.editTextTextEmailAddress);
        editTextTextPassword = findViewById((R.id.editTextTextPassword));
        editTextTextPassword2 = findViewById((R.id.editTextTextPassword2));
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        progressDialog = new ProgressDialog(this);
        mAuth =FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        getSupportActionBar().setTitle("Sign Up");


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerforAuth();

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this,SignIn.class));

            }
        });


    }

    private void PerforAuth() {
        String email = editTextTextEmailAddress.getText().toString();
        String Password = editTextTextPassword.getText().toString();
        String CoPassword = editTextTextPassword2.getText().toString();

        if (!email.matches(emailPattern))
        {
            editTextTextEmailAddress.setError("Enter correct email ");
        }else if(!Password.matches("^(?=.*[0-9]{2})(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])(?=.*[A-Z]).{8,}$"))

        {
            editTextTextPassword.setError("Enter proper password (at least 8 characters, 2 digits, 1 special character, and 1 uppercase letter)");
            Toast.makeText(SignUp.this, "Please enter a password with at least 8 characters, 2 digits, 1 special character, and 1 uppercase letter", Toast.LENGTH_LONG).show();

        }else if(!Password.equals(CoPassword))
        {
            editTextTextPassword2.setError(("Password does not match "));
        }else
        {
            progressDialog.setMessage("Please wait while registration ... ");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        sendUserToNextActivity();
                        Toast.makeText(SignUp.this, "Registration Success",Toast.LENGTH_SHORT).show();
                    }else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(SignUp.this, ""+task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }



    }

    private void sendUserToNextActivity() {
        Intent intent = new Intent(SignUp.this,Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}