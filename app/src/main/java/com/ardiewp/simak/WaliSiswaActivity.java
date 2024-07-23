package com.ardiewp.simak;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WaliSiswaActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mRootRef, mUserRef, mPengumumanRef;

    FirebaseRecyclerAdapter<Pengumuman, PengumumanViewHolder> mAdapter;
    LinearLayoutManager mManager;

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wali_siswa);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d("LoginActivity", "------------------------>user :" + user);
                if (user != null) {

                }else {
                    startActivity(new Intent(WaliSiswaActivity.this, LoginActivity.class));
                }
            }
        };

        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserRef = mRootRef.child("pengguna");
        mPengumumanRef = mRootRef.child("pengumuman");

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mManager);

        mAdapter = new FirebaseRecyclerAdapter<Pengumuman, PengumumanViewHolder>(
                Pengumuman.class,
                R.layout.list_pengumuman,
                PengumumanViewHolder.class,
                mPengumumanRef
        ) {
            @Override
            protected void populateViewHolder(PengumumanViewHolder viewHolder, Pengumuman model, int position) {
                final DatabaseReference postRef = getRef(position);
                final String postKey = postRef.getKey();
                viewHolder.bindToPost(model, postKey);
            }
        };
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                mRecyclerView.setAdapter(mAdapter);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.it_setting){
            Intent intent = new Intent(WaliSiswaActivity.this, SettingActivity.class);
            startActivity(intent);

        }else if(id == R.id.it_logout){
            mAuth.signOut();
            Intent intent = new Intent(WaliSiswaActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        moveTaskToBack(true);
    }
}
