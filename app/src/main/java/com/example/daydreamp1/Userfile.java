package com.example.daydreamp1;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
//This is unfinished, I managed to get a file to amazon s3 but I had some inconsistency with this.
public class Userfile {
    public Userfile() {

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String createFile(AbstractMap.SimpleEntry<String, String> pairing) {
        switch (pairing.getKey()) {
            case "bio":
                try {
                    File file = new File("bio.txt");
                    file.createNewFile();
                    byte[] data1=pairing.getValue().getBytes(StandardCharsets.UTF_8);
//write the bytes in file
                    if(file.exists())
                    {
                        OutputStream fo = new FileOutputStream(file);
                        fo.write(data1);
                        fo.close();
                        System.out.println("file created: "+file);
                    }

//deleting the file
                    return "bio.txt";
                } catch (IOException ioe)
                {ioe.printStackTrace();}
            default:
                return "";
        }

    }
    public String readFile(String path) {
        File file = new File(path);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text.toString();
    }
}
