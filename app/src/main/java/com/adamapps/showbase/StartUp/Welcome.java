package com.adamapps.showbase.StartUp;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.adamapps.showbase.R;
import com.adamapps.showbase.User.Host;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Welcome extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_INN = 9001;
    ImageView burnsView;
    Button googleSignIn;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        burnsView = findViewById(R.id.background);
        AnimationDrawable animationDrawable = (AnimationDrawable) burnsView.getBackground();
        animationDrawable.start();

        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user != null) {
                    startActivity(new Intent(Welcome.this, Host.class));
                    finish();
                }
            }
        };
        googleSignIn = findViewById(R.id.google_sign_in);

        YoYo.with(Techniques.SlideInDown).duration(1000).playOn(googleSignIn);
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.RubberBand).duration(500).playOn(googleSignIn);
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_INN);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {

            if (requestCode == RC_SIGN_INN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                }
            }
        }
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        if (authResult.getAdditionalUserInfo().isNewUser()) {
                            Toast.makeText(Welcome.this, "Happy To Have You " + auth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Welcome.this, "Welcome Back "
                                    + auth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                        HashMap<String, Object> map = new HashMap<>();
                        assert user != null;
                        map.put("name", user.getDisplayName());
                        //map.put("image", user.getPhotoUrl());
                        map.put("image", "https://api.adorable.io/avatars/285/"
                                + user.getDisplayName().replace(" ", "") + ".png");
                        map.put("email", user.getEmail());
                        FirebaseDatabase.getInstance().getReference().child("User")
                                .child(user.getUid()).updateChildren(map);
                        startActivity(new Intent(Welcome.this, Host.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Welcome.this, "Failed " + e.getCause(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "" + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authStateListener);
    }

    public void OpenEmail(View v){
        startActivity(new Intent(this,EmailActivity.class));
        YoYo.with(Techniques.RubberBand).duration(500).playOn(v);
    }
}
