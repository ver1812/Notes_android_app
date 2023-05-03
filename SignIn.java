package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {

    TextView createnewaccount ;
    EditText editTextTextEmailAddress2,editTextTextPassword3;
    Button button3;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        createnewaccount = findViewById(R.id.createnewaccount);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        editTextTextEmailAddress2 = findViewById(R.id.editTextTextEmailAddress2);
        editTextTextPassword3 = findViewById((R.id.editTextTextPassword3));
        button3 = findViewById(R.id.button3);

        progressDialog = new ProgressDialog(this);
        mAuth =FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        getSupportActionBar().setTitle("Sign In");


        createnewaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignIn.this,SignUp.class ));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perforLogin();
            }
        });

    }

    private void perforLogin() {


        String email = editTextTextEmailAddress2.getText().toString();
        String Password = editTextTextPassword3.getText().toString();


        if (!email.matches(emailPattern))
        {
            editTextTextEmailAddress2.setError("Enter correct email ");
        }else if(Password.isEmpty() ||Password.length()<6)
        {
            editTextTextPassword3.setError("Enter proper password ");
        }else {
            progressDialog.setMessage("Please wait while login ... ");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            mAuth.signInWithEmailAndPassword(email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        sendUserToNextActivity();
                        Toast.makeText(SignIn.this, "Login Success",Toast.LENGTH_SHORT).show();
                    }else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(SignIn.this, "Please enter correct username and password ",Toast.LENGTH_SHORT).show();
                    }


                }
            });

        }
    }

    private void sendUserToNextActivity() {
        Intent intent = new Intent(SignIn.this,Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}