package com.adamapps.muvi.StartUp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.adamapps.muvi.R;
import com.adamapps.muvi.TvShow.Show;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EmailActivity extends AppCompatActivity {

    EditText emailText,passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        emailText = findViewById(R.id.emailField);
        passwordText = findViewById(R.id.passwordField);
    }

    public void EmailLogin(View view) {
        if(passwordText.length()<6){
            Toast.makeText(this, "Password Must Be 6 Letter Long", Toast.LENGTH_SHORT).show();
            return;
        }
        if(emailText.getText().toString()!=null && passwordText.getText().toString()!=null){
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailText.getText().toString(),passwordText.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            startActivity(new Intent(EmailActivity.this, Show.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EmailActivity.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
