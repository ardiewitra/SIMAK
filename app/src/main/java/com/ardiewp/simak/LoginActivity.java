package com.ardiewp.simak;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnMasuk;
    TextView tvDaftar;
    ProgressBar mProgressBar;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mRootRef, mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserRef = mRootRef.child("pengguna");



        Log.d("LoginActivity", "----------------->user1 :");

        //check apakah user sudah login
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d("LoginActivity", "------------------------>user :" + user);
                if (user != null) {

                    final String penggunaId = mAuth.getCurrentUser().getUid();

                    //check user login sebagai apa
                    mUserRef.child(penggunaId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String status = dataSnapshot.child("status").getValue().toString();
                            String level = dataSnapshot.child("level").getValue().toString();

                            if (level.equalsIgnoreCase("Guru")) {

                                if (status.equalsIgnoreCase("Terverifikasi")) {
                                    signInGuru();
                                } else {
                                    mAuth.signOut();
                                }

                            } else if(level.equalsIgnoreCase("Admin")){
                                signInAdmin();
                            }else {
                                signInWali();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {

                    //set tampilan login
                    setContentView(R.layout.activity_login);
                    getSupportActionBar().hide();

                    mProgressBar = findViewById(R.id.progress_bar);
                    etEmail = findViewById(R.id.et_email);
                    etPassword = findViewById(R.id.et_password);
                    btnMasuk = findViewById(R.id.btn_masuk);
                    tvDaftar = findViewById(R.id.tv_daftar);

                    tvDaftar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    btnMasuk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            final String email = etEmail.getText().toString();
                            final String password = etPassword.getText().toString();

                            if (TextUtils.isEmpty(email)) {
                                Toast.makeText(LoginActivity.this, "Masukkan email", Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.INVISIBLE);
                            } else if (TextUtils.isEmpty(password)) {
                                Toast.makeText(LoginActivity.this, "Masukkan password", Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.INVISIBLE);
                            } else {

                                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            String penggunaId = mAuth.getCurrentUser().getUid();

                                            mUserRef.child(penggunaId).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    String status = dataSnapshot.child("status").getValue().toString();
                                                    String level = dataSnapshot.child("level").getValue().toString();

                                                    if (level.equalsIgnoreCase("Guru")) {

                                                        if (status.equalsIgnoreCase("Terverifikasi")) {
                                                            signInGuru();
                                                        } else {
                                                            Toast.makeText(LoginActivity.this, "Akun anda belum terverifikasi", Toast.LENGTH_SHORT).show();
                                                            mProgressBar.setVisibility(View.INVISIBLE);
                                                            mAuth.signOut();
                                                        }

                                                    } else if(level.equalsIgnoreCase("Admin")){
                                                        signInAdmin();
                                                    }else {
                                                        signInWali();
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                        } else {
                                            Toast.makeText(LoginActivity.this, "Email atau password yang anda masukkan salah", Toast.LENGTH_LONG).show();
                                            mProgressBar.setVisibility(View.INVISIBLE);

                                        }
                                    }
                                });
                            }
                        }
                    });

                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        super.onStop();
    }

    private void signInWali() {
//        mProgressBar.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(LoginActivity.this, WaliSiswaActivity.class);
        startActivity(intent);
        finish();
    }

    private void signInGuru() {
//        mProgressBar.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(LoginActivity.this, GuruActivity.class);
        startActivity(intent);
        finish();
    }

    private void signInAdmin(){
        Intent intent = new Intent(LoginActivity.this, VerifikasiActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        moveTaskToBack(true);
    }
}
