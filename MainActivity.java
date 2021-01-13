package com.example.project2.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project2.Model.InventoryDBHelper;
import com.example.project2.Model.UserModel;
import com.example.project2.R;

public class MainActivity extends AppCompatActivity {
    Button loginButton;
    Button signupButton;
    EditText userName;
    EditText password;
    InventoryDBHelper inventoryDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton = findViewById(R.id.loginButtonID);
        signupButton = findViewById(R.id.signUpButtonID);
        userName = findViewById(R.id.username);
        password = findViewById(R.id.password);
        inventoryDBHelper = new InventoryDBHelper(this);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userNameText = userName.getText().toString();
                String passwordText = password.getText().toString();
                if(userNameText != "" && passwordText != ""){
                    if(inventoryDBHelper.getUser(userNameText)){
                        Toast.makeText(MainActivity.this
                                , "The username already exists in the database. Please try a different username.", Toast.LENGTH_SHORT).show();
                    }else{
                        inventoryDBHelper.addUser(new UserModel(userNameText, passwordText));
                        Toast.makeText(MainActivity.this
                                , "User has been successfully added into the database. Please try to login now.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this
                            , "Please fill in the blanks.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userNameText = userName.getText().toString();
                String passwordText = password.getText().toString();

                if(!userNameText.isEmpty() && !passwordText.isEmpty()){
                    if(passwordText.length() >= 6){
                        if(inventoryDBHelper.getUser(userNameText)){
                            if(inventoryDBHelper.loginUser(userNameText, passwordText)){
                                Intent intent = new Intent(MainActivity.this, HomePage.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(MainActivity.this
                                        , "The username or password is wrong.", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(MainActivity.this
                                    , "The username does not exist in the database. Please try signing up first.", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this
                                , "The password must be at least 6-character long.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this
                    , "Please fill in the blanks.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


}
