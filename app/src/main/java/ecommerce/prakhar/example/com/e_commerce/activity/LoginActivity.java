package ecommerce.prakhar.example.com.e_commerce.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import ecommerce.prakhar.example.com.e_commerce.R;
import io.fabric.sdk.android.Fabric;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private CallbackManager callbackManager = null;
    private ImageButton mFacebook, mTwitter, mGoogle;
    private LoginButton fbloginButton;
    private TwitterLoginButton twitterLoginButton;
    SignInButton googleLoginButton;
    private int INTERNET_PERMISSION_CODE = 23;
    private Intent intent;
    private  String name, email, profile_pic;
    private  URL imageURL;

    private static final String MY_PREFS_NAME = "USER_CREDENTIALS";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "zs1Jv4lXEuuDIhB8tH5MEKO9b";
    private static final String TWITTER_SECRET = "suVMIQ9ziNpILkc8ajq75nAkEA4eysXzKMnilo4gDicHbqXyRG";

    private static final String TAG = "GoogleSignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        // Build a GoogleApiClient with access to the Google Sign-In API and the
    // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        checkLogin();
        setContentView(R.layout.login);
        AppEventsLogger.activateApp(this);
        //check internet permission
        internetPermission();
        callbackManager = CallbackManager.Factory.create();
        mFacebook = (ImageButton) findViewById(R.id.custom_fb_button);
        mTwitter = (ImageButton) findViewById(R.id.custom_twitter_button);
        mGoogle = (ImageButton) findViewById(R.id.custom_google_button);

        fbloginButton = (LoginButton) findViewById(R.id.fb_login_button);
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        googleLoginButton = (SignInButton) findViewById(R.id.google_login_button);

        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        fbloginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "gender", "id", "birthday"));

        mFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fbloginButton.performClick();
                fbloginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest
                                (loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        // Application code
                                            Log.e("Fb_Success", response.toString());
                                        try {
                                            imageURL = new URL("https://graph.facebook.com/" + object.getString("id") + "/picture?type=large");
                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            name = object.getString("name");
                                            email = object.getString("email");
                                            profile_pic = String.valueOf(imageURL);
                                            editor.putString("name", name);
                                            editor.putString("email", email);
                                            editor.putString("profile_pic", profile_pic);
                                            editor.commit();
                                            checkLogin();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {

                    }
                    @Override
                    public void onError(FacebookException error) {

                    }
                });
            }
        });

        mTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twitterLoginButton.setCallback(new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> result) {
                        // The TwitterSession is also available through:
                        // Twitter.getInstance().core.getSessionManager().getActiveSession()
                        TwitterSession session = result.data;
                        // TODO: Remove toast and use the TwitterSession's userID
                        // with your app's user model
                        final String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                        name = session.getUserName();
                        editor.putString("name", name);
                        TwitterAuthClient authClient = new TwitterAuthClient();
                        authClient.requestEmail(session, new Callback<String>() {
                            @Override
                            public void success(Result<String> result) {
                                // Do something with the result, which provides the email address
                                email = result.data;
                                editor.putString("email", email);
                                editor.putString("profile_pic", profile_pic);
                                Log.e("Twitter_Success", name + email + profile_pic);
                                editor.commit();
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                checkLogin();
                            }
                            @Override
                            public void failure(TwitterException exception) {
                                // Do something on failure
                            }
                        });
                    }
                    @Override
                    public void failure(TwitterException exception) {
                        Log.d("TwitterKit", "Login with Twitter failure", exception);
                    }
                });
                twitterLoginButton.performClick();
            }
        });

        mGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleLoginButton.performClick();
                signIn();
            }
        });

    }

    private void getTwitterEmail(TwitterSession session){
        TwitterAuthClient authClient = new TwitterAuthClient();
        authClient.requestEmail(session, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                // Do something with the result, which provides the email address
                email = result.data;
                editor.putString("email", email);
                editor.putString("profile_pic", profile_pic);
                Log.e("Twitter_Success", name + email + profile_pic);
                editor.commit();
                checkLogin();
            }
            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Uri personPhoto = acct.getPhotoUrl();
            name = acct.getDisplayName();
            email = acct.getEmail();
            profile_pic = String.valueOf(personPhoto);
            editor.putString("name", name);
            editor.putString("email", email);
            editor.putString("profile_pic", profile_pic);
            Log.e("Google_Success", name + email + profile_pic);
            editor.commit();
            checkLogin();
//            Log.e("Google_Success", personName + personGivenName + personFamilyName + personEmail + personId + personPhoto);
        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private  void internetPermission(){
        if(isInternetAllowed()){
            //If permission is already having then showing the toast
            Toast.makeText(LoginActivity.this,"You already have the permission",Toast.LENGTH_SHORT).show();
            //Existing the method with return
            return;
        }

        //If the app has not the permission then asking for the permission
        requestInternetPermission();
    }

    //We are calling this method to check the permission status
    private boolean isInternetAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    //Requesting permission
    private void requestInternetPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.INTERNET)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},INTERNET_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == INTERNET_PERMISSION_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                Toast.makeText(this,"Internet Permission granted",Toast.LENGTH_SHORT).show();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE){
            //  twitter related handling
            twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        }else if (requestCode == RC_SIGN_IN) {
            // google related handling
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else{
            // facebook related handling
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    public boolean isFBLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public boolean isTwitterLoggedIn() {
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        return twitterSession != null;
    }

    public boolean isGoogleLoggedIn() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        return opr.isDone();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLogin();
    }

    public void checkLogin(){
        if (isFBLoggedIn() || isTwitterLoggedIn() || isGoogleLoggedIn()) {
            intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
