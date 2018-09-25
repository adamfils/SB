package com.adamapps.showbase.StartUp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.adamapps.showbase.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordReset extends AppCompatActivity {

    EditText emailText;
    Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        emailText = findViewById(R.id.emailField);
        resetButton = findViewById(R.id.resetButton);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(500).playOn(v);
                if (emailText.getText().toString().isEmpty()) {
                    Toast.makeText(PasswordReset.this, "Email Cannot Be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (emailText.getText().toString() != null) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailText.getText().
                            toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            onBackPressed();
                            Toast.makeText(PasswordReset.this, "Check Your Email", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PasswordReset.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
}