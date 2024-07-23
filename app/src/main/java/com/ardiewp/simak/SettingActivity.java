package com.ardiewp.simak;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    DatabaseReference mRootRef, mUser;

    EditText mEtNama, mEtNomorTelpon;
    Button mBtnSimpan, mBtnUbahPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();

        final String uid = mAuth.getCurrentUser().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUser = mRootRef.child("pengguna");

        mEtNama = findViewById(R.id.et_nama);
        mEtNomorTelpon = findViewById(R.id.et_nomor_telpon);
        mBtnSimpan = findViewById(R.id.btn_simpan);
        mBtnUbahPassword = findViewById(R.id.btn_ubah_password);

        mUser.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nama = dataSnapshot.child("nama").getValue().toString();
                String telpon = dataSnapshot.child("telpon").getValue().toString();

                mEtNama.setText(nama);
                mEtNomorTelpon.setText(telpon);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mBtnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = mEtNama.getText().toString();
                String telpon = mEtNomorTelpon.getText().toString();
                mUser.child(uid).child("nama").setValue(nama);
                mUser.child(uid).child("telpon").setValue(telpon);
                Toast.makeText(getApplicationContext(), "Data berhasil diubah" , Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SettingActivity.this, GuruActivity.class));
                finish();
            }
        });

    }

}
