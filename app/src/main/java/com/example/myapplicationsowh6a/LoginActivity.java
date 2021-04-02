package com.example.myapplicationsowh6a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    EditText EmailEditlogin , PasswordEditLogin;
    TextView HaventRegistered, RecoverPassword;
    Button bntLogin;
    SignInButton googlebutton ;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog2Login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        mAuth = FirebaseAuth.getInstance();
        EmailEditlogin = findViewById(R.id.EmailEdit2);
        PasswordEditLogin = findViewById(R.id.PasswordEdit2); // if error its without 2
        HaventRegistered = findViewById(R.id.HavenotRegister);
        RecoverPassword = findViewById(R.id.RecoverPasswordTxt);
        googlebutton = findViewById(R.id.Googleidbtn);
        bntLogin = findViewById(R.id.LogintnNow);

        bntLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(LoginActivity.this,));
                String EmailLogin = EmailEditlogin.getText().toString().trim();
                String PasswordLogin = PasswordEditLogin.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(EmailLogin).matches())
                {
                    EmailEditlogin.setError("Invalid Email");
                    EmailEditlogin.setFocusable(true);
                }
                else
                    {
                        UserLogin(EmailLogin,PasswordLogin);
                    }

            }
        });
        HaventRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        RecoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             ShowPasswordRecoveryDialog();
            }
        });

        googlebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });


        progressDialog2Login = new ProgressDialog(this);

    }

    private void ShowPasswordRecoveryDialog() {
        AlertDialog.Builder builder  = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");

        LinearLayout linearLayout = new LinearLayout(this);
        EditText recoverEmail = new EditText(this);
        recoverEmail.setHint("Email");
        recoverEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        recoverEmail.setMinEms(16);
        linearLayout.addView(recoverEmail);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String GETEMAIl = recoverEmail.getText().toString().trim();
                BeginResetting(GETEMAIl);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            }
        });
        builder.create().show();;
    }

    private void BeginResetting(String getemaIl) {
        progressDialog2Login.setMessage("Sending Email...");
        progressDialog2Login.show();
        mAuth.sendPasswordResetEmail(getemaIl).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog2Login.dismiss();
            if(task.isSuccessful())
            {
               Toast.makeText(LoginActivity.this, "Email sent",Toast.LENGTH_SHORT).show();
            }
            else
                {
                    Toast.makeText(LoginActivity.this, "Failed...",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog2Login.dismiss();
                Toast.makeText(LoginActivity.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void UserLogin(String emailLogin, String passwordLogin) {
        progressDialog2Login.setMessage("Logining in...");
        progressDialog2Login.show();
        mAuth.signInWithEmailAndPassword(emailLogin, passwordLogin)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog2Login.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(task.getResult().getAdditionalUserInfo().isNewUser())
                            {
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
                            }




                            startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog2Login.dismiss();
                            Toast.makeText(LoginActivity.this, "Authentication failed.",  Toast.LENGTH_SHORT).show();
                            ;
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog2Login.dismiss();
                Toast.makeText(LoginActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, ""+ user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
                            finish();

                        }
                        else
                            {
                            // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Login Failed...", Toast.LENGTH_SHORT).show();


                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}