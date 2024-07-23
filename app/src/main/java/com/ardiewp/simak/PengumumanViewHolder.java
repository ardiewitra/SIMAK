package com.ardiewp.simak;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PengumumanViewHolder extends RecyclerView.ViewHolder {
    FirebaseAuth mAuth;
    DatabaseReference mRootRef, mPengumuanRef;

    ImageView mIvPreview, ivDelete;
    TextView mTvKepada, mTvDeksripsi, mTvNamaPengirim, mTvTimeStamp;

    Pengumuman displayMessage = new Pengumuman();
    String TAG = "PegumumanViewHolder";

    String kepada, deskripsi, judul, waktu, pembuat, imageUrl, penggunaId;

    public String postKey;

    public PengumumanViewHolder(final View itemView) {
        super(itemView);

        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mPengumuanRef = mRootRef.child("pengumuman");

        mIvPreview = itemView.findViewById(R.id.iv_preview);
        mTvKepada = itemView.findViewById(R.id.tv_kepada);
        mTvDeksripsi = itemView.findViewById(R.id.tv_deskripsi);
        mTvNamaPengirim = itemView.findViewById(R.id.tv_nama_pengirim);
        mTvTimeStamp = itemView.findViewById(R.id.tv_waktu);


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
                intent.putExtra("post_key", postKey);
                intent.putExtra("imageUrl", imageUrl);
                Log.d(TAG, "imageUrl :"+imageUrl);
                intent.putExtra("deskripsi", deskripsi);
                intent.putExtra("judul", judul);
                intent.putExtra("kepada", kepada);
                intent.putExtra("pembuat", pembuat);
                intent.putExtra("waktu", waktu);
                intent.putExtra("penggunaId", penggunaId);
                Log.d(TAG, "post_ket :"+postKey);
                itemView.getContext().startActivity(intent);
            }
        });

    }

    public void bindToPost(final Pengumuman pengumuman, String postKey){
        this.postKey = postKey;

        displayMessage = pengumuman;

        kepada = pengumuman.kepada;
        deskripsi = pengumuman.isi;
        pembuat = pengumuman.pembuat;
        imageUrl = pengumuman.gambar;
        penggunaId = pengumuman.penggunaId;
        judul = pengumuman.judul;

        Utility util = new Utility();
        waktu = util.getPostTime((Long)pengumuman.timestamp);

                    mTvDeksripsi.setText(deskripsi);
                    mTvKepada.setText(kepada);
                    mTvNamaPengirim.setText(pembuat);
                    mTvTimeStamp.setText(waktu);
                    Picasso.with(itemView.getContext()).load(imageUrl).into(mIvPreview);

        Log.d(TAG, "postKey 2 : " + postKey);

    }

}
