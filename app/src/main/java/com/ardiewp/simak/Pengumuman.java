package com.ardiewp.simak;

import com.google.firebase.database.ServerValue;

public class Pengumuman {

    public String gambar;
    public String kepada;
    public String judul;
    public String isi;
    public Object timestamp;
    public String penggunaId;
    public String pembuat;

    public Pengumuman() {
    }

    public Pengumuman(String gambar, String kepada, String judul, String isi, String penggunaId, String pembuat) {
        this.gambar = gambar;
        this.kepada = kepada;
        this.judul = judul;
        this.isi = isi;
        this.penggunaId = penggunaId;
        this.pembuat = pembuat;
        this.timestamp = ServerValue.TIMESTAMP;
    }
}
