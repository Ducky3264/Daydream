package com.example.daydreamp1;
import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import net.openid.appauth.AuthState;

import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserContext {
    public UserDetails UD;
    public JSONObject authState;
    public UserContext() {

    }
    public UserContext(@NonNull AuthState AU) {
        UD = null;
        authState = AU.jsonSerialize();
    }

    public static class UserDetails {
        public String Uname;
        public String Email;
        public String Name;
        public String GName;
        public UserDetails() {
            Uname = null;
            Email = null;
            Name = null;
        }
        public UserDetails(String uname) {
            Uname = uname;
        }
        public UserDetails(String uname, String email) {
            Uname = uname;
            Email = email;
        }

        public UserDetails(String uname, String email, String name) {
            Uname = uname;
            Email = email;
            Name = name;
        }
        public UserDetails(Map<String, String> HM) {
            Uname = HM.get("sub");
            Name = HM.get("name");
            Email = HM.get("email");
            GName = HM.get("given_name");
        }
    }
    public AuthState getAuthState() {
        try {
            return AuthState.jsonDeserialize(authState);
        } catch (Exception ex) {
            return new AuthState();
        }
    }
    public UserDetails getUserDetails() {
        if (UD == null) {
            return new UserDetails();
        } else {
            return UD;
        }
    }
    public String ContextToJSON() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }
    public void UAuthState(AuthState AU) {
        authState = AU.jsonSerialize();
    }
}