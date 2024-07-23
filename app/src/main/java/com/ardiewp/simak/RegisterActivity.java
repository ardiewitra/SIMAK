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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef, mUserRef;

    EditText etNama, etEmail, etTelpon, etPassword;
    RadioGroup rgLevel;
    RadioButton rbLevel;
    Button btnDaftar;
    TextView tvMasuk;
    ProgressBar mProgressBar;

    String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        mUserRef = mRef.child("pengguna");

        etNama = findViewById(R.id.et_nama);
        etEmail = findViewById(R.id.et_email);
        etTelpon = findViewById(R.id.et_telpon);
        etPassword = findViewById(R.id.et_password);
        rgLevel = findViewById(R.id.rg_Level);
        btnDaftar = findViewById(R.id.btn_daftar);
        tvMasuk = findViewById(R.id.tv_masuk);
        mProgressBar = findViewById(R.id.progress_bar);

        tvMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(etNama.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(etEmail.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(etTelpon.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "Nomor telpon tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(etPassword.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "Kata sandi tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else{

                    mProgressBar.setVisibility(View.VISIBLE);
                    int selectedId = rgLevel.getCheckedRadioButtonId();
                    rbLevel = findViewById(selectedId);
                    String level = rbLevel.getText().toString();

                    String status;
                    if (level.equalsIgnoreCase("Guru")){
                        Log.d(TAG, "status :"+level);
                        status = "Belum Terverifikasi";
                    }else{
                        Log.d(TAG, "status :"+level);
                        status = "Terverifikasi";
                    }

                    createUser(etNama.getText().toString(), etEmail.getText().toString(), etTelpon.getText().toString(), etPassword.getText().toString(),
                            level, status);

                }
            }
        });

    }

    private void createUser(final String nama, final String email, final String telpon, final String password, final String level, final String status) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    writeUser(nama, email, telpon, password, level, status);
                }else {
                    Toast.makeText(RegisterActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void writeUser(final String nama, String email, String telpon, String password, final String level, final String status) {
        String penggunaId = mAuth.getCurrentUser().getUid();

        Pengguna pengguna = new Pengguna(nama, email, telpon, password, level, status);

        mUserRef.child(penggunaId).setValue(pengguna).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Register Berhasil", Toast.LENGTH_SHORT).show();
                    if(level.equalsIgnoreCase("Wali Siswa")){
                        Intent intent = new Intent(RegisterActivity.this, WaliSiswaActivity.class);
                        startActivity(intent);
                        finish();
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }else if(level.equalsIgnoreCase("Admin")){
                        Intent intent = new Intent(RegisterActivity.this, VerifikasiActivity.class);
                        startActivity(intent);
                        finish();
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }else{
                        if (status.equalsIgnoreCase("Belum Terverifikasi")){
                            Toast.makeText(RegisterActivity.this, "Akun anda belum terverifikasi silahkan hubungi admin", Toast.LENGTH_SHORT).show();
                            BackgroundMail.newBuilder(RegisterActivity.this)
                                    .withUsername("simakiba.app@gmail.com")
                                    .withPassword("simakiba01@")
                                    .withMailto("ardiewp@gmail.com")
                                    .withType(BackgroundMail.TYPE_PLAIN)
                                    .withSubject("Seorang User Telah Mendaftar Sebagai Guru")
                                    .withBody("Seorang user telah berhasil mendaftar sebagai guru dengan nama "+nama+" dan menunggu anda untuk memverifikasi akunnya")
                                    .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                                        @Override
                                        public void onSuccess() {
                                            //do some magic
                                        }
                                    })
                                    .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                                        @Override
                                        public void onFail() {
                                            //do some magic
                                        }
                                    })
                                    .send();
                            mProgressBar.setVisibility(View.INVISIBLE);
                            mAuth.signOut();
                        }else{
                            Intent intent = new Intent(RegisterActivity.this, GuruActivity.class);
                            startActivity(intent);
                            finish();
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                }else {
                    Toast.makeText(RegisterActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        moveTaskToBack(true);
    }
}
