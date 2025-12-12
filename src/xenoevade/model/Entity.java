/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: Entity.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Class to represent game objects (Player, Alien, Bullet, Obstacle)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.model;

import java.awt.Rectangle; //untuk collision detection

public class Entity {
    public int x; //posisi x entity
    public int y; //posisi y entity
    public int width; //lebar entity
    public int height; //tinggi entity

    public Entity() {
        /* Method Entity
        Konstruktor default untuk inisialisasi atribut entity*/

        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
    }

    public Entity(int x, int y, int width, int height) {
        /* Method Entity
        Konstruktor untuk inisialisasi atribut entity
        Menerima masukan berupa posisi x, posisi y, lebar, dan tinggi entity*/

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle getBounds() {
        /* Method getBounds
        Method untuk mendapatkan bounding box entity
        Mengembalikan objek Rectangle yang merepresentasikan bounding box entity*/

        return new Rectangle(x, y, width, height);
    }
}
