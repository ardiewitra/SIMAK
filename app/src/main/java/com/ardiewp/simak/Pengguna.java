package com.ardiewp.simak;

public class Pengguna {
    public String nama;
    public String email;
    public String telpon;
    public String kataSandi;
    public String level;
    public String status;

    public Pengguna() {
    }

    public Pengguna(String nama, String email, String telpon, String kataSandi, String level, String status) {
        this.nama = nama;
        this.email = email;
        this.telpon = telpon;
        this.kataSandi = kataSandi;
        this.level = level;
        this.status = status;
    }
}
