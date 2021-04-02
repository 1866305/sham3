package com.example.myapplicationsowh6a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {
    EditText EmailEditm, PasswordEditm;
    TextView HavealreadyRegistered;
    Button Registerbtnm;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        HavealreadyRegistered = findViewById(R.id.HaveRegister);
        EmailEditm = findViewById(R.id.EmailEdit);
        PasswordEditm = findViewById(R.id.PasswordEdit);
        Registerbtnm = findViewById(R.id.RegisterbtnNow);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");
        mAuth = FirebaseAuth.getInstance();
        Registerbtnm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = EmailEditm.getText().toString().trim();
                String password = PasswordEditm.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches())
                {
                    EmailEditm.setError("Invaild Email");
                    EmailEditm.setFocusable(true);

                }
                else if(password.length() < 8)
                {
                    PasswordEditm.setError("Password characters less than 8 ");
                    PasswordEditm.setFocusable(true);
                }
                else
                    {

                        RegisterUser(Email, password);
                    }
            }
        });
        HavealreadyRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void RegisterUser(String email, String password) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();

                            String getEmail = user.getEmail();
                            String getUserID = user.getUid();

                            HashMap<Object, String> hashMap = new HashMap<>();
                            hashMap.put("Email",getEmail);
                            hashMap.put("UserID",getUserID);
                            hashMap.put("Name","");
                            hashMap.put("Phone","");
                            hashMap.put("Image","");
                            hashMap.put("Cover","");

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("Users");
                            reference.child(getUserID).setValue(hashMap);

                            Toast.makeText(RegisterActivity.this, "Registered...\n"+ user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, DashBoardActivity.class));
                            finish();
                        }
                        else
                            {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this,""+ e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}