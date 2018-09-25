package com.adamapps.showbase.StartUp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.adamapps.showbase.R;
import com.adamapps.showbase.User.Host;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EmailActivity extends AppCompatActivity {

    EditText emailText, passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        emailText = findViewById(R.id.emailField);
        passwordText = findViewById(R.id.passwordField);
    }

    public void EmailLogin(View view) {

        if (emailText.getText().toString().isEmpty()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(emailText);
            Toast.makeText(this, "Email Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (passwordText.length() < 6) {
            YoYo.with(Techniques.Shake).duration(500).playOn(passwordText);
            Toast.makeText(this, "Password Must Be 6 Letter Long", Toast.LENGTH_SHORT).show();
            return;
        }
        if (emailText.getText().toString() != null && passwordText.getText().toString() != null) {
            YoYo.with(Techniques.RubberBand).duration(500).playOn(view);
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent intent = new Intent(EmailActivity.this, Host.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EmailActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void EmailSignUp(View view) {

        if (emailText.getText().toString().isEmpty()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(emailText);
            Toast.makeText(this, "Email Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (passwordText.length() < 6) {
            YoYo.with(Techniques.Shake).duration(500).playOn(passwordText);
            Toast.makeText(this, "Password Must Be 6 Letter Long", Toast.LENGTH_SHORT).show();
            return;
        }
        if (emailText.getText().toString() != null && passwordText.getText().toString() != null) {
            YoYo.with(Techniques.RubberBand).duration(500).playOn(view);
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent intent = new Intent(EmailActivity.this, Host.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EmailActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void ResetEmail(View view) {
        YoYo.with(Techniques.RubberBand).duration(500).playOn(view);
        startActivity(new Intent(this, PasswordReset.class));
    }
}
