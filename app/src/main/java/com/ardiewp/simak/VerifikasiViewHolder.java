package com.ardiewp.simak;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class VerifikasiViewHolder extends RecyclerView.ViewHolder {
    FirebaseAuth mAuth;
    DatabaseReference mRootRef, mPenggunaRef;

    ImageView mIvVerif, mIvBlmVerif;
    TextView mTvNama, mTvEmail, mTvNoTelp, mTvTerverifikasi, mTvBelumTerverifikasi;

    Pengguna displayMessage = new Pengguna();
    String TAG = "PenggunaViewHolder";


    public String postKey;

    public VerifikasiViewHolder(final View itemView) {
        super(itemView);

        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mPenggunaRef = mRootRef.child("pengguna");
        mTvTerverifikasi = itemView.findViewById(R.id.tv_terverifikasi);
        mTvBelumTerverifikasi = itemView.findViewById(R.id.tv_belum_terverifikasi);
        mTvEmail = itemView.findViewById(R.id.tv_email);
        mTvNama = itemView.findViewById(R.id.tv_nama);
        mTvNoTelp = itemView.findViewById(R.id.tv_nomor_telpon);
        mIvVerif = itemView.findViewById(R.id.iv_verif);
        mIvBlmVerif = itemView.findViewById(R.id.iv_unverif);

        mIvVerif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPenggunaRef.child(postKey).child("status").setValue("Terverifikasi");
                mPenggunaRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String email = dataSnapshot.child("email").getValue().toString();
                        BackgroundMail.newBuilder(itemView.getContext())
                                .withUsername("simakiba.app@gmail.com")
                                .withPassword("simakiba01@")
                                .withMailto(email)
                                .withType(BackgroundMail.TYPE_PLAIN)
                                .withSubject("Akun Anda Telah Diverifikasi")
                                .withBody("Selamat akun anda telah terverifikasi, sekarang anda telah dapat melakukan login di aplikasi SIMAK IBA")
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                itemView.getContext().startActivity(new Intent(itemView.getContext(), ProfileActivity.class));
            }
        });

        mIvBlmVerif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPenggunaRef.child(postKey).child("status").setValue("Belum Terverifikasi");
                mPenggunaRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String email = dataSnapshot.child("email").getValue().toString();
                        BackgroundMail.newBuilder(itemView.getContext())
                                .withUsername("simakiba.app@gmail.com")
                                .withPassword("simakiba01@")
                                .withMailto(email)
                                .withType(BackgroundMail.TYPE_PLAIN)
                                .withSubject("Akun Anda Telah Diunverifikasi")
                                .withBody("Maaf kami melakukan unverifikasi terhadap akun anda, dan anda tidak dapat melakukan login kembali")
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                itemView.getContext().startActivity(new Intent(itemView.getContext(), ProfileActivity.class));
            }
        });


    }

    public void bindToPost(final Pengguna pengguna, String postKey){
        this.postKey = postKey;

        displayMessage = pengguna;


        mPenggunaRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "dataSnapshot :"+dataSnapshot.toString());
                String nama = dataSnapshot.child("nama").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String noTelp = dataSnapshot.child("telpon").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();


                mTvNama.setText(nama);
                mTvEmail.setText(email);
                mTvNoTelp.setText(noTelp);

                if (status.equalsIgnoreCase("Terverifikasi")){
                    mTvTerverifikasi.setVisibility(View.VISIBLE);
                    mIvBlmVerif.setVisibility(View.VISIBLE);
                    mTvBelumTerverifikasi.setVisibility(View.GONE);
                    mIvVerif.setVisibility(View.GONE);
                }else if (status.equalsIgnoreCase("Belum Terverifikasi")){
                    mTvBelumTerverifikasi.setVisibility(View.VISIBLE);
                    mIvVerif.setVisibility(View.VISIBLE);
                    mTvTerverifikasi.setVisibility(View.GONE);
                    mIvBlmVerif.setVisibility(View.GONE);

                }

                Utility util = new Utility();
//                waktu = util.getPostTime((Long)pengguna.timestamp);
//
//                mTvDeksripsi.setText(deskripsi);
//                mTvKepada.setText(kepada);
//                mTvNamaPengirim.setText(pembuat);
//                mTvTimeStamp.setText(waktu);
//                Picasso.with(itemView.getContext()).load(imageUrl).into(mIvPreview);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
