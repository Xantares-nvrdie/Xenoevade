/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: Entity.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Abstract class to represent game objects (Player, Alien, etc)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.model;

import java.awt.Graphics;//untuk menggambar
import java.awt.Image; //untuk menampung aset gambar
import java.awt.Rectangle; //untuk collision detection

public abstract class Entity {
    public int x; // Posisi x entity
    public int y; // Posisi y entity
    public int width; // Lebar entity
    public int height; // Tinggi entity

    //untuk menyimpan gambar yang sudah di-load
    protected Image sprite;

    public Entity() {
        /* Method Entity (Default Constructor) */
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
    }

    public Entity(int x, int y, int width, int height) {
        /* Method Entity (Parameterized Constructor) */
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    // method abstrak untuk update logika entity jika entity dapat bergerak
    public abstract void update(); 

    // method abstrak untuk menggambar entity
    public abstract void render(Graphics g);
    
    public Rectangle getBounds() {
        /* Method getBounds for collision */
        return new Rectangle(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public java.awt.Image getImage() {
        /*
         * Method getImage
         * Mengembalikan sprite sebagai Image generic agar kompatibel
         */
        return sprite;
    }
}
