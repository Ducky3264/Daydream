package com.example.daydreamp1;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.AbstractMap;
import java.util.LinkedHashMap;

public class ProfileFunctions {
    private final class UserfileToAWS extends AsyncTask<AbstractMap.SimpleEntry<String, String>, Void, Boolean> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        protected Boolean doInBackground(AbstractMap.SimpleEntry<String, String>... pairing) {
            Userfile UF = new Userfile();
            String file = UF.createFile(pairing[0]);
            return false;
        }
    }
}
