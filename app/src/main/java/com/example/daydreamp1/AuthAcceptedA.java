package com.example.daydreamp1;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.TokenResponse;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class AuthAcceptedA extends AppCompatActivity {
    private java.net.URL url;
    private AuthState authState;
    private UserContext UC;
    private final class SendAccessToken extends AsyncTask<LinkedHashMap<String, String>, Void, String> {
        protected String doInBackground(LinkedHashMap<String, String>... jsob) {

            try {
                String s1 = authState.getAccessToken();
                url = new URL("https://www.davidcac.net/connect/userinfo");
                LinkedHashMap<String, String> vals = new LinkedHashMap<>();
                //vals.put("client_id", "interactive");
                //vals.put("client_secret", "A8G%(SJT&#");
                //vals.put("grant_type", "authorization_code");
                //vals.put("code", s1);
                //vals.put("redirect_uri", "daydream://redirect/oauth");
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String,String> param : vals.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                //byte[] postDataBytes = postData.toString().getBytes("UTF-8");
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", "Bearer" + s1);
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; utf-8");
                con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
                con.setRequestProperty("Accept", "application/json");
               //con.setDoOutput(true);
               // con.getOutputStream().write(postDataBytes);
                String res = "";
                Reader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                for (int c; (c = in.read()) >= 0;)
                    res += (char)c;
                res = res;
                Map<String, String> DataMap = new HashMap<String, String>();
                res = res.replaceAll("\"", "");
                res = res.replaceAll("\\{", "");
                res = res.replaceAll("\\}", "");
                String[] pairs = res.split(",");

                for (int i=0;i<pairs.length;i++) {
                    String pair = pairs[i];
                    String[] keyValue = pair.split(":");
                    DataMap.put(keyValue[0], keyValue[1]);
                }
                UserContext.UserDetails UD = new UserContext.UserDetails(DataMap);
                UC.UD = UD;
                SharedPreferences sharedPref = getSharedPreferences("UserContext", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("UserContextOBJ", UC.ContextToJSON());
                editor.apply();
                return "Completed";
                //}
            } catch (Exception ex1) {
                System.out.println(ex1);
                //TODO: I really need better exception handling here but that feels like an end step
            }
            return "";

        }
    }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            AuthorizationService authService = new AuthorizationService(this);
            Gson gson = new Gson();
            SharedPreferences sharedPref = getSharedPreferences("UserContext", Context.MODE_PRIVATE);
            String UserContextS = sharedPref.getString("UserContextOBJ", "no key");
            UC = gson.fromJson(UserContextS, UserContext.class);

            try {
                authState = UC.getAuthState();
            } catch (Exception ex) {

            }

            super.onCreate(savedInstanceState);
            //setContentView(R.layout.activity_auth_accepted);
            AuthorizationResponse resp = AuthorizationResponse.fromIntent(getIntent());
            AuthorizationException ex = AuthorizationException.fromIntent(getIntent());
            //I have some concerns about security due to this client secret being hard coded into the application, I will look into moving the code transfer to our backend in the future. This is likely plenty secure for now though. This still seems better than just a public exposed API with no secret though, even if marginally.
            if (resp != null) {
                int x = 2 + 2;


                Intent nintent = new Intent(this, com.example.daydreamp1.MainActivity.class);
                ClientAuthentication clientAuth = new ClientSecretBasic("A8G%(SJT&#");
                authService.performTokenRequest(
                        resp.createTokenExchangeRequest(),
                        clientAuth,
                        new AuthorizationService.TokenResponseCallback() {
                            @Override
                            public void onTokenRequestCompleted(
                                    TokenResponse resp, AuthorizationException ex) {
                                if (resp != null) {
                                    authState.update(resp, ex);
                                    UC.UAuthState(authState);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("UserContextOBJ", UC.ContextToJSON());
                                    int x = 2 + 2;
                                    authState.performActionWithFreshTokens(authService, new AuthState.AuthStateAction() {
                                        @Override
                                        public void execute(
                                                String accessToken,
                                                String idToken,
                                                AuthorizationException ex) {
                                            if (ex != null) {
                                                // negotiation for fresh tokens failed, check ex for more details
                                                return;
                                            }
                                            LinkedHashMap<String, String> vals = new LinkedHashMap<>();
                                            new SendAccessToken().execute(vals);
                                            //this async task uses the access token to get data from the userinfo endpoint and store it in shared preferences
                                        }
                                    });
                                    startActivity(nintent);
                                    MainActivity.ma.finish();
                                    finish();

                                } else {

                                }
                            }
                        });

            } else {
                int x = 2 + 2;
            }
            int x = 2 + 2;
        }
    }

