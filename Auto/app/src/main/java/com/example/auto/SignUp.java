package com.example.auto;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.ArrayList;

public class SignUp extends AppCompatActivity {
    Button SignUp;

    EditText NameSignUp, EmailSignUp, PasswordSignUp, UsernameSignUp;
    TextView LogIn;
    private static final String LOCAL_HOST = "http://192.168.1.2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        NameSignUp = (EditText) findViewById(R.id.editTextSignUpName);
        EmailSignUp = (EditText) findViewById(R.id.editTextSignUpEmail);
        PasswordSignUp = (EditText) findViewById(R.id.editTextSignUpPassword);
        UsernameSignUp = (EditText) findViewById(R.id.editTextSignUpUsername);

        SignUp = (Button) findViewById(R.id.buttonSignUpSignUp);
        LogIn = (TextView) findViewById(R.id.textViewSignUpLogIn);








        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = NameSignUp.getText().toString();
                String email = EmailSignUp.getText().toString();
                String password = PasswordSignUp.getText().toString();
                String username = UsernameSignUp.getText().toString();


                if (name.isEmpty() || email.isEmpty() || password.isEmpty())
                    Toast.makeText(getApplicationContext(), "All fields are mandatory!", Toast.LENGTH_SHORT).show();
                else {
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field = new String[4];
                            field[0] = "fullname";
                            field[1] = "username";
                            field[2] = "password";
                            field[3] = "email";
                            String[] data = new String[4];
                            data[0] = name;
                            data[1] = username;
                            data[2] = password;
                            data[3] = email;
                            PutData putData = new PutData(LOCAL_HOST.concat("/DatabaseAuto/sign_up.php"), "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    if (result.equals("Sign Up Success")){
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                        Toast.makeText(getApplicationContext(), "Unsuccessful Sign Up!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });


        LogIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}
