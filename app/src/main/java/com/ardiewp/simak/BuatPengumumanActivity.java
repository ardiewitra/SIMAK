package com.ardiewp.simak;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class BuatPengumumanActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 3;

//    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final String TAG = "BPA" ;
    private static final String IMAGE_DIRECTORY_NAME = "SIMAK" ;
    FirebaseAuth mAuth;
    DatabaseReference mRootRef, mPengumumanRef, mUserRef;

    StorageReference storageReference;
    FirebaseStorage mStorage;

    Button btnGalery, btnKamera, btnKirim;
    EditText etKepada, etJudul, etIsi, etLama;
    ImageView ivPreview;

    private static final int RC_TAKE_PHOTO = 101;
    private static final int RC_OPEN_GALERY = 102;

    byte[] dataBaos;

    Bitmap photoBitmap;
    private Uri mFileUri;
    Uri file;

    String photoUrl;


    String tipePhoto = "foto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_pengumuman);

        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mPengumumanRef = mRootRef.child("pengumuman");
        mUserRef = mRootRef.child("pengguna");

        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();


        permission();

        btnGalery = findViewById(R.id.btn_galery);
        btnKamera = findViewById(R.id.btn_kamera);
        btnKirim = findViewById(R.id.btn_kirim);
        etKepada = findViewById(R.id.et_kepada);
        etJudul = findViewById(R.id.et_judul);
        etIsi = findViewById(R.id.et_isi);
        etLama = findViewById(R.id.et_lama);
        ivPreview = findViewById(R.id.iv_preview);

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (tipePhoto.equalsIgnoreCase("kamera")){
//                    Bitmap photo = photoBitmap;
//                }else {
//                    Uri photo = mFileUri;
//                }
                String penggunaId = mAuth.getCurrentUser().getUid();
                String kepada = etKepada.getText().toString();
                String judul = etJudul.getText().toString();
                String isi = etIsi.getText().toString();
                String lama = etLama.getText().toString();

                Log.d(TAG, "----------------->photoBitmap :"+photoBitmap);
                Log.d(TAG, "----------------->mFileUri :"+mFileUri);

                if (!judul.isEmpty() && !kepada.isEmpty() && !isi.isEmpty() && !lama.isEmpty()){
                    int num = Integer.parseInt(etLama.getText().toString());
                    if(num>0){
                        getPenggunaName(judul, kepada, isi);
                    }
                }

            }
        });

        btnKamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipePhoto = "kamera";
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                takePicture();
            }
        });


        btnGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipePhoto = "galery";
                openGallery();
            }
        });
    }

    private void openGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,RC_OPEN_GALERY);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void takePicture() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mFileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Log.d(TAG,"intent : " + intent);
            startActivityForResult(intent,RC_TAKE_PHOTO);
        } catch (Exception ex){
            ex.printStackTrace();
            System.out.println("GAGAAALLL : " + ex.getMessage().toString());
        }
    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(BuatPengumumanActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(BuatPengumumanActivity.this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user asynchronously -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(BuatPengumumanActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else if (ContextCompat.checkSelfPermission(BuatPengumumanActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(BuatPengumumanActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user asynchronously -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(BuatPengumumanActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else if (ContextCompat.checkSelfPermission(BuatPengumumanActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(BuatPengumumanActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user asynchronously -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(BuatPengumumanActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()){
            if (mediaStorageDir.mkdirs()){
                Log.d(TAG, "Failed create" + "SIMAK"+"directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type ==MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+timeStamp+".jpg");
        }else {
            return null;
        }
        return mediaFile;
    }



    private void getPenggunaName(final String judul, final String kepada, final String isi) {

        final String uid = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "------------------------------------->Masuk sini");

        if (tipePhoto.equalsIgnoreCase("kamera")){
            mUserRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final String name = dataSnapshot.child("nama").getValue().toString();
                    Log.d(TAG, "------------------------------------->Masuk sini 2");

                    if (mFileUri != null){
                        final ProgressDialog pd = new ProgressDialog(BuatPengumumanActivity.this);
                        pd.setTitle("Mengirim pengumuman");
                        pd.show();

                        final StorageReference ref = storageReference.child("image/" + mFileUri.getLastPathSegment());
                        ref.child("image");
                        file = Uri.fromFile(new File(mFileUri.getPath()));
                        Log.d(TAG, "file : " + file);
                        UploadTask uploadTask = ref.putFile(file);

                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                                if (!task.isSuccessful()){
                                    throw  task.getException();
                                }

                                Log.d(TAG, "downloadURL : " + ref.getDownloadUrl());
                                return ref.getDownloadUrl();

                            }
                        })

                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()){
                                            Uri downloadUri = task.getResult();
                                            Log.d(TAG, "downloadUri : " + downloadUri);
                                            photoUrl = downloadUri.toString();
                                            Log.d(TAG, "photoUrl : " + photoUrl);

                                            Log.d(TAG, "photoURL sebelum setValue : " + photoUrl);
                                            if (photoUrl.isEmpty() || photoUrl == null){
                                                photoUrl = "https://firebasestorage.googleapis.com/v0/b/simakiba-app.appspot.com/o/image%2Fpostdefault.jpg?alt=media&token=19faff4f-8752-4c94-a43b-588523daf7ca";
                                            }
                                            Pengumuman pengumuman = new Pengumuman(photoUrl, kepada, judul, isi,uid, name);

                                            mPengumumanRef.push().setValue(pengumuman).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(BuatPengumumanActivity.this, "Mengirim pengumuman berhasil", Toast.LENGTH_SHORT).show();
                                                        pd.dismiss();
                                                        startActivity(new Intent(getApplicationContext(), GuruActivity.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(BuatPengumumanActivity.this, "Mengirim pengumuman gagal :" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                })

                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(getApplicationContext(), "Mengirim pengumuman gagal" , Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "mFile Uri di upload gagal : " + mFileUri);

                                    }
                                });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else if (tipePhoto.equalsIgnoreCase("galery")){
            mUserRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final String name = dataSnapshot.child("nama").getValue().toString();

                    if (mFileUri != null){
                        final ProgressDialog pd = new ProgressDialog(BuatPengumumanActivity.this);
                        pd.setTitle("Mengirim pengumuman...");
                        pd.show();

                        final StorageReference ref = storageReference.child("image/" + UUID.randomUUID().toString());

                        UploadTask uploadTask = ref.putFile(mFileUri);

                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                                if (!task.isSuccessful()){
                                    throw  task.getException();
                                }

                                Log.d(TAG, "downloadURL : " + ref.getDownloadUrl());
                                return ref.getDownloadUrl();

                            }
                        })

                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()){
                                            Uri downloadUri = task.getResult();
                                            Log.d(TAG, "downloadUri : " + downloadUri);
                                            photoUrl = downloadUri.toString();
                                            Log.d(TAG, "photoUrl : " + photoUrl);

                                            if (photoUrl.isEmpty() || photoUrl == null){
                                                photoUrl = "https://firebasestorage.googleapis.com/v0/b/simakiba-app.appspot.com/o/image%2Fpostdefault.jpg?alt=media&token=19faff4f-8752-4c94-a43b-588523daf7ca";
                                            }
                                            Log.d(TAG, "photoURL sebelum setValue : " + photoUrl);
                                            Pengumuman pengumuman = new Pengumuman(photoUrl, kepada, judul, isi,uid, name);
//                if ( !photoUrl.isEmpty() && !judul.isEmpty() && !isi.isEmpty()) {
                                            mPengumumanRef.push().setValue(pengumuman).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(BuatPengumumanActivity.this, "Mengirim pengumuman berhasil", Toast.LENGTH_SHORT).show();
                                                        pd.dismiss();
                                                        startActivity(new Intent(getApplicationContext(), GuruActivity.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(BuatPengumumanActivity.this, "Mengirim pengumuman gagal :" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(getApplicationContext(), "Mengirim pengumuman gagal" , Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "mFile Uri di upload gagal : " + mFileUri);

                                    }
                                });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Log.d(TAG, "------------------------------------->Masuk sini 3");
            mUserRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final String name = dataSnapshot.child("nama").getValue().toString();
                    Log.d(TAG, "------------------------------------->Masuk sini 4");
                    if (photoUrl == null){
                        photoUrl = "https://firebasestorage.googleapis.com/v0/b/simakiba-app.appspot.com/o/image%2Fpostdefault.jpg?alt=media&token=19faff4f-8752-4c94-a43b-588523daf7ca";
                    }
                    Log.d(TAG, "photoURL sebelum setValue : " + photoUrl);
                    Pengumuman pengumuman = new Pengumuman(photoUrl, kepada, judul, isi,uid, name);
//                if ( !photoUrl.isEmpty() && !judul.isEmpty() && !isi.isEmpty()) {
                    mPengumumanRef.push().setValue(pengumuman).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(BuatPengumumanActivity.this, "Mengirim pengumuman berhasil", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), GuruActivity.class));
                                finish();
                            } else {
                                Toast.makeText(BuatPengumumanActivity.this, "Mengirim pengumuman gagal :" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {

                if (mFileUri.getPath() != null) {
                    String getPath = mFileUri.getPath();
                    Log.d(TAG, "path 1 : " + getPath);
                    if (getPath.isEmpty()) {
                        getPath = mFileUri.getPath();
                        Log.d(TAG, "path 2 : " + getPath);
                    }

                    resizePhoto(getPath);
                    previewCapturedImage();
                }


//                photoBitmap = (Bitmap) data.getExtras().get("data");
//                ivPreview.setImageBitmap(photoBitmap);

                if (!runtime_permissions());

            } else {
                Log.d("BuatPengumumanActivity", "Photos file is empty");
            }
        } else if (requestCode == RC_OPEN_GALERY) {
            if (resultCode == RESULT_OK) {
                mFileUri = data.getData();
                ivPreview.setImageURI(mFileUri);
                if (!runtime_permissions());
            } else {
                Log.d("BuatPengumumanActivity", "Photo files is empty");
            }
        }
    }


    private void previewCapturedImage() {

        // Get the dimensions of the View
        int targetW = ivPreview.getWidth();
        int targetH = ivPreview.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mFileUri.getPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        System.out.println("photoW: " + photoW + ", " + "photoH: " + photoH);

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mFileUri.getPath(), bmOptions);
        ivPreview.setImageBitmap(bitmap);
    }

    private void resizePhoto(String uri) {
        Bitmap photo = BitmapFactory.decodeFile(uri);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
        float scale = photo.getHeight() / 720f;
        int width = (int) (Math.round(photo.getWidth() / scale));

        photo = Bitmap.createScaledBitmap(photo, width, 720, false);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 80, bytes);

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        File resizedFile;
        resizedFile = new File(mediaStorageDir.getPath() + File.separator
                + mFileUri.getLastPathSegment());

        try {
            resizedFile.createNewFile();
            FileOutputStream fo = new FileOutputStream(resizedFile);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private boolean runtime_permissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

//            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
//                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

            return true;
        }
        return false;
    }
}
