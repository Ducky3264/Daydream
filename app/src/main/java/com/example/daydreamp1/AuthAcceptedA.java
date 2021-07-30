package com.example.daydreamp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;

public class AuthAcceptedA extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_accepted);
        AuthorizationResponse resp = AuthorizationResponse.fromIntent(getIntent());
        AuthorizationException ex = AuthorizationException.fromIntent(getIntent());
        //This was hit after an open with occured on the android phone, note that http was being used at this point... not sure if that is relevant
        if (resp != null) {
            int x = 2 + 2;
            startActivity(new Intent(this, MainActivity.class));
        } else {
            int x = 2 + 2;
        }
        int x = 2 + 2;
    }
}