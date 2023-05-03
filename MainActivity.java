package com.example.myapplication;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.security.SecureRandom;


public class MainActivity extends AppCompatActivity {

    private TextView passwordTextView;
    private SeekBar passwordLengthSeekBar;
    private Button generatePasswordButton;
    private Button copyToClipboardButton;

    private TextView passwordLengthTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        passwordTextView = findViewById(R.id.passwordTextView);
        passwordLengthTextView = findViewById(R.id.passwordLengthLabel);
        passwordLengthSeekBar = findViewById(R.id.passwordLengthSeekBar);
        generatePasswordButton = findViewById(R.id.generatePasswordButton);
        copyToClipboardButton = findViewById(R.id.copyToClipboardButton);

        getSupportActionBar().setTitle("Password Generator");

        passwordLengthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int passwordLength = progress ;
                passwordLengthTextView.setText("Password Length: " + passwordLength);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        generatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int passwordLength = passwordLengthSeekBar.getProgress() ;
                String password = generatePassword(passwordLength);
                passwordTextView.setText(password);
            }
        });

        copyToClipboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordTextView.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("password", password);
                clipboard.setPrimaryClip(clip);
            }
        });
    }

    private String generatePassword(int length) {
        SecureRandom random = new SecureRandom();
        String charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_+{}[];:<>,.?/|";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charset.length());
            password.append(charset.charAt(index));
        }
        return password.toString();
    }
}