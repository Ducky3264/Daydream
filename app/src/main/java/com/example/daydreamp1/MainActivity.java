package com.example.daydreamp1;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.Toolbar;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.daydreamp1.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.daydreamp1.MESSAGE";
    private Toolbar toolbar;
    private DrawerLayout mDrawer;
    private ActivityMainBinding binding;
    private ActionBarDrawerToggle drawerToggle;
    private AuthState authState;
    private AuthorizationRequest authRequest;
    private ActivityResultLauncher<Intent> startForResult;
    private UserContext UC;
    private int count;
    public static Activity ma;
    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }
    //This is exclusively for debugging purposes and should absolutely never be used in a production app
    private void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }});
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        trustEveryone();
        ma = this;
        super.onCreate(savedInstanceState);
        Userfile UF = new Userfile();
        //Todo: move these into the appropriate file


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        drawerToggle = setupDrawerToggle();
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        mDrawer.addDrawerListener(drawerToggle);
        configureAuthenticationHeader();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_messages, R.id.navigation_settings)
                .build();
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        fetchissuer();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);

        View headerLayout = navigationView.getHeaderView(0);
        String imageUri = "https://www.davidcac.net/GetPFP?type=bio.txt";
        ImageView pfpiv = (ImageView) headerLayout.findViewById(R.id.imageView4);
       // Picasso.get().load(imageUri).into(pfpiv);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        //Fragment fragment = null;
        //Class fragmentClass;
        //fragmentClass = MessagesFragment.class;
        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                Intent SI = new Intent(this, ProfileActivity.class);
                startActivity(SI);
                break;
            case R.id.nav_second_fragment:

                break;
            case R.id.nav_third_fragment:

                break;
            case R.id.nav_fourth_fragment:
                //fragmentClass = MessagesFragment.class;
                sendhttp(findViewById(android.R.id.content).getRootView());
                break;

        }

        try {
            //fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
       // FragmentManager fragmentManager = getSupportFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        //setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }
    public void sendhttp(View view) {
        try {
            AuthorizationService authService = new AuthorizationService(this);
            authService.performAuthorizationRequest(
                    authRequest,
                    PendingIntent.getActivity(this, 0, new Intent(this, AuthAcceptedA.class), 0),
                    PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));
        } catch (Exception ex) {
            Log.d("BACKEND_ERROR", "Server may be down, retrying once...");
            try {
                fetchissuer();
                AuthorizationService authService = new AuthorizationService(this);
                Intent AcceptedI = new Intent(this, AuthAcceptedA.class);
                authService.performAuthorizationRequest(
                        authRequest,
                        PendingIntent.getActivity(this, 0, AcceptedI, 0),
                        PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));
            } catch (Exception ex1) {
                Log.d("BACKEND_ERROR", "Server is most likely down!");
            }
        }
    }
    private void configureAuthenticationHeader() {
        //These lists define every menu item each respective user type shouldn't see
        List<Integer> AnonymousUsers = new ArrayList<Integer>();
        List<Integer> AuthedUsers = new ArrayList<Integer>();
        AnonymousUsers.add(1);
        AnonymousUsers.add(2);
        AnonymousUsers.add(3);
        AuthedUsers.add(0);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        View headerLayout = navigationView.getHeaderView(0);
        TextView hText = headerLayout.findViewById(R.id.HeadText);
        setupDrawerContent(navigationView);
        TextView shText = headerLayout.findViewById(R.id.SubheadText);
        SharedPreferences sharedPref = getSharedPreferences("UserContext", Context.MODE_PRIVATE);
        String Status = sharedPref.getString("UserContextOBJ", "Guest");
        hText.setText("Loading...");
        if (!(Status == null)) {
            Gson gson = new Gson();
            if (Status == "Guest") {
                UC = new UserContext();
            } else {
                UC = gson.fromJson(Status, UserContext.class);
            }
            if (UC.getUserDetails().Uname == null) {
                hText.setText("Guest");
                updateProfileData();
                for (int id : AnonymousUsers) {
                    MenuItem MI = (MenuItem) navigationView.getMenu().getItem(id);
                    MI.setVisible(false);
                }
                for (int id : AuthedUsers) {
                    MenuItem MI = (MenuItem) navigationView.getMenu().getItem(id);
                    MI.setVisible(true);
                }
            } else {
                shText.setText(UC.getUserDetails().Uname);
                hText.setText(UC.getUserDetails().Name);
                for (int id : AnonymousUsers) {
                    MenuItem MI = (MenuItem) navigationView.getMenu().getItem(id);
                    MI.setVisible(true);
                }
                for (int id : AuthedUsers) {
                    MenuItem MI = (MenuItem) navigationView.getMenu().getItem(id);
                    MI.setVisible(false);
                }
                new SendUserfile().execute();
            }
        }
    }
    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void updateProfileData() {
        final Handler handler = new Handler();
        final int delay = 1000; // 1000 milliseconds == 1 second
        //TODO: Add handling so that in the case this runs 15 times with no success it sends a refresh token to the identityserver
        handler.postDelayed(new Runnable() {
            public void run() {
                if (UC.getUserDetails().Uname == null && count <= 10) {
                    count++;
                    configureAuthenticationHeader();
                } else {

                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String createFile(AbstractMap.SimpleEntry<String, String> pairing) {
        switch (pairing.getKey()) {
            case "bio":
                try {
                    File file = new File(getApplicationContext().getFilesDir().getPath().toString() + "/" + UC.getUserDetails().GName + "bio.txt");
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
                    return getApplicationContext().getFilesDir().getPath().toString() + "/" + UC.getUserDetails().GName + "bio.txt";
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
    public void fetchissuer() {
        AuthorizationServiceConfiguration.fetchFromIssuer(
                Uri.parse("https://www.davidcac.net:443"),
                new AuthorizationServiceConfiguration.RetrieveConfigurationCallback() {
                    public void onFetchConfigurationCompleted(
                            @Nullable AuthorizationServiceConfiguration serviceConfiguration,
                            @Nullable AuthorizationException ex) {
                        if (ex != null) {
                            Log.e(TAG, "failed to fetch configuration");
                            return;
                        }
                        authState = new AuthState(serviceConfiguration);
                        AuthorizationRequest.Builder authRequestBuilder =
                                new AuthorizationRequest.Builder(
                                        serviceConfiguration, // the authorization service configuration
                                        "interactive", // the client ID, typically pre-registered and static
                                        ResponseTypeValues.CODE, // the response_type value: we want a code
                                        Uri.parse("daydream://redirect/oauth")); // the redirect URI to which the auth response is sent
                        authRequest = authRequestBuilder.setScope("openid profile email").build();
                        UserContext UC = new UserContext(authState);
                        SharedPreferences sharedPref = getSharedPreferences("UserContext", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("UserContextOBJ", UC.ContextToJSON());
                        editor.apply();
                        // use serviceConfiguration as needed
                    }
                });
    }
    private final class SendUserfile extends AsyncTask<Void, Void, String> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        protected String doInBackground(Void... v) {
            String s = createFile(new AbstractMap.SimpleEntry<String, String>("bio", "upload"));
            String txt = readFile(s);
            try {
                //This currently is broken because the identityserver4 cookie is not being send with httpurlconnection. This is necessary to ensure that a user does not try to edit another user's profile. Commenting this out for now.
               // MultipartUtility MPU = new MultipartUtility("https://www.davidcac.net/UploadUserFile");
                //MPU.addFilePart("upload", new File(s));
                //String response = MPU.finish();
                String response = "";
                return response;

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return "";
        }
    }
}

