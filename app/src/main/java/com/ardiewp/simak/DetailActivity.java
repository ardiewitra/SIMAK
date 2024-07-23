package com.ardiewp.simak;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    TextView mTvJudul, mTvKepada, mTvWaktu, mTvIsi, mTvPembuat;
    ImageView mIvPreview;
    Button delete;
    boolean isImageFitToScreen = false;
    FirebaseAuth mAuth;
    DatabaseReference mRootRef, mPengumuanRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTvIsi = findViewById(R.id.tv_isi);
        mTvJudul = findViewById(R.id.tv_judul);
        mTvKepada = findViewById(R.id.tv_kepada_content);
        mTvWaktu = findViewById(R.id.tv_waktu);
        mTvPembuat = findViewById(R.id.tv_pembuat_content);
        delete = findViewById(R.id.deletePost);

        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mPengumuanRef = mRootRef.child("pengumuman");
        mIvPreview = findViewById(R.id.iv_preview);

        Intent intent = getIntent();

        String isi = intent.getStringExtra("deskripsi");
        String judul = intent.getStringExtra("judul");
        String kepada = intent.getStringExtra("kepada");
        String waktu = intent.getStringExtra("waktu");
        String pembuat = intent.getStringExtra("pembuat");
        final String imageUrl = intent.getStringExtra("imageUrl");
        final String penggunaId = intent.getStringExtra("penggunaId");
        final String postKey = intent.getStringExtra("post_key");


        if (penggunaId.equals(mAuth.getCurrentUser().getUid())) {
            delete.setVisibility(View.VISIBLE);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPengumuanRef.child(postKey).removeValue();
                startActivity(new Intent(getApplicationContext(), GuruActivity.class));
                Toast.makeText(getApplicationContext(), "Pengumuman berhasil dihapus" , Toast.LENGTH_SHORT).show();
                finish();

            }
        });


        mTvIsi.setText(isi);
        mTvJudul.setText(judul);
        mTvKepada.setText(kepada);
        mTvWaktu.setText(waktu);
        mTvPembuat.setText(pembuat);

        Log.d("Detail", "gambar : " + imageUrl);
        Picasso.with(DetailActivity.this).load(imageUrl).into(mIvPreview);

        mIvPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog nagDialog = new Dialog(DetailActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                nagDialog.setCancelable(false);
                nagDialog.setContentView(R.layout.preview_image);
                Button btnClose = nagDialog.findViewById(R.id.btnIvClose);
                ImageView ivPreview = nagDialog.findViewById(R.id.iv_preview_image);
                Picasso.with(DetailActivity.this).load(imageUrl).into(ivPreview);

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nagDialog.dismiss();
                    }
                });
                nagDialog.show();
            }
        });

    }
}
