package com.example.daydreamp1;
import android.app.Activity;
import android.app.Instrumentation;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
//import android.widget.Toolbar;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.customview.widget.Openable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.daydreamp1.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import org.jetbrains.annotations.Nullable;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import static android.content.ContentValues.TAG;

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
    /**private void trustEveryone() {
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
    }**/
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //trustEveryone();
        ma = this;
        super.onCreate(savedInstanceState);
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



        /**public void selectDrawerItem(MenuItem menuItem) {
         // Create a new fragment and specify the fragment to show based on nav item clicked
         Fragment fragment = null;
         Class fragmentClass;
         switch(menuItem.getItemId()) {
         case R.id.nav_first_fragment:

         break;
         case R.id.nav_second_fragment:

         break;
         default:
         fragmentClass = FirstFragment.class;
         }

         try {
         fragment = (Fragment) fragmentClass.newInstance();
         } catch (Exception e) {
         e.printStackTrace();
         }

         // Insert the fragment by replacing any existing fragment
         FragmentManager fragmentManager = getSupportFragmentManager();
         fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

         // Highlight the selected item has been done by NavigationView
         menuItem.setChecked(true);
         // Set action bar title
         setTitle(menuItem.getTitle());
         // Close the navigation drawer
         mDrawer.closeDrawers();
         }**/
        AuthorizationResponse resp = AuthorizationResponse.fromIntent(getIntent());
        AuthorizationException ex = AuthorizationException.fromIntent(getIntent());
        if (resp != null) {
            authState.update(resp, ex);
        } else {
            // authorization failed, check ex for more details
        }
    }


    private void doAuthorization() {

        AuthorizationService authService = new AuthorizationService(this);
        Intent authIntent = authService.getAuthorizationRequestIntent(authRequest);
        startForResult.launch(authIntent);
    }
    public void sendhttp(View view) {

        AuthorizationService authService = new AuthorizationService(this);
        authService.performAuthorizationRequest(
                authRequest,
                PendingIntent.getActivity(this, 0, new Intent(this, AuthAcceptedA.class), 0),
                PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));
    }
    private void configureAuthenticationHeader() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        View headerLayout = navigationView.getHeaderView(0);
        TextView hText = headerLayout.findViewById(R.id.HeadText);
        SharedPreferences sharedPref = getSharedPreferences("UserContext", Context.MODE_PRIVATE);
        String Status = sharedPref.getString("UserContextOBJ", "Guest");
        hText.setText("Loading...");
        if (!(Status == null)) {
            Gson gson = new Gson();
            UC = gson.fromJson(Status, UserContext.class);
            if (UC.getUserDetails().Uname == null) {
                hText.setText("Guest");
                updateProfileData();
            } else {
                hText.setText(UC.getUserDetails().Uname);
            }
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
        count = 0;
        final Handler handler = new Handler();
        final int delay = 1000; // 1000 milliseconds == 1 second
        //TODO: Add handling so that in the case this runs 15 times with no success it sends a refresh token to the identityserver
        handler.postDelayed(new Runnable() {
            public void run() {
                if (UC.getUserDetails().Uname == null && count <= 15) {
                    count++;
                    configureAuthenticationHeader();
                } else {

                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }
}