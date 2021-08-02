package com.example.daydreamp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.TokenResponse;

public class AuthAcceptedA extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AuthorizationService authService = new AuthorizationService(this);
        SharedPreferences sharedPref = getSharedPreferences("authState", Context.MODE_PRIVATE);
        String authStateS = sharedPref.getString("authStateV", "no key");
        try {
            AuthState authState = new AuthState();
            authState = AuthState.jsonDeserialize(authStateS);
        } catch (Exception ex) {

        }

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_auth_accepted);
        AuthorizationResponse resp = AuthorizationResponse.fromIntent(getIntent());
        AuthorizationException ex = AuthorizationException.fromIntent(getIntent());
        //I have some concerns about security due to this client secret being hard coded into the application, I will look into moving the code transfer to our backend in the future. This is likely plenty secure for now though. This still seems better than just a public exposed API with no secret though, even if marginally.
        if (resp != null) {
            int x = 2 + 2;
            //authState.update(resp, ex);
            Intent nintent = new Intent(this, MainActivity.class);
            //startActivity(nintent);
            ClientAuthentication clientAuth = new ClientSecretBasic("A8G%(SJT&#");
            authService.performTokenRequest(
                    resp.createTokenExchangeRequest(),
                    clientAuth,
                    new AuthorizationService.TokenResponseCallback() {
                        @Override public void onTokenRequestCompleted(
                                TokenResponse resp, AuthorizationException ex) {
                            if (resp != null) {
                                int x = 2 + 2;
                            } else {
                                int x = 2 + 2;
                            }
                        }
                    });

        } else {
            int x = 2 + 2;
        }
        int x = 2 + 2;
    }
}