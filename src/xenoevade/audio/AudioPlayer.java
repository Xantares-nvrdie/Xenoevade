/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: AudioPlayer.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Utility class to handle audio playback (Polyphonic Support)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.audio;

import javax.sound.sampled.AudioInputStream; //untuk input audio
import javax.sound.sampled.AudioSystem; //untuk sistem audio
import javax.sound.sampled.Clip; //untuk klip audio
import javax.sound.sampled.AudioFormat; //untuk format audio
import javax.sound.sampled.DataLine; //untuk data line audio
import javax.sound.sampled.FloatControl; //untuk kontrol volume
import java.net.URL; //untuk URL resource
import java.util.ArrayList; //untuk list
import java.util.List; //untuk list

public class AudioPlayer {
    // gunakan List of Clips untuk menangani suara tumpang tindih (Pooling)
    private List<Clip> clips; // daftar klip audio
    private int currentClipIndex = 0; // indeks klip saat ini untuk diputar

    public AudioPlayer(String filename) {
        /*
         * Constructor: AudioPlayer
         * Memuat resource audio dan membuat pool (duplikat) agar bisa di-spam
         */
        clips = new ArrayList<>();
        try {
            URL url = getClass().getResource("/assets/audio/" + filename);
            if (url == null) {
                System.err.println("Audio Resource not found: " + filename);
                return;
            }

            // Membaca file audio sekali ke dalam memori (biar ringan)
            AudioInputStream originalStream = AudioSystem.getAudioInputStream(url);
            AudioFormat format = originalStream.getFormat();

            // Konversi data audio ke byte array agar bisa dibuat banyak Clip
            int size = (int) (originalStream.getFrameLength() * format.getFrameSize());
            byte[] audioData = new byte[size];
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            originalStream.read(audioData, 0, size);

            // Buat 20 duplikat Clip (Sound Pool) untuk setiap file audio
            // Ini memungkinkan 20 suara yang sama bunyi berbarengan
            int poolSize = 20;
            for (int i = 0; i < poolSize; i++) {
                Clip clip = (Clip) AudioSystem.getLine(info);
                // Buka clip menggunakan data byte array yang sudah diload
                clip.open(format, audioData, 0, size);
                clips.add(clip);
            }

        } catch (Exception e) {
            // Gagal memuat audio
            System.err.println("Audio initialization failed: " + e.getMessage());
        }
    }

    public void play() {
        /*
         * Method: play (Updated for Polyphony)
         * Mencari Clip yang sedang nganggur. Jika semua terpakai,
         * gunakan clip berikutnya dalam antrian.
         */
        if (clips.isEmpty())
            return;

        Clip clipToPlay = clips.get(currentClipIndex);

        // Jika clip ini sedang jalan, stop dulu dan rewind
        if (clipToPlay.isRunning()) {
            clipToPlay.stop();
        }
        clipToPlay.setFramePosition(0);
        clipToPlay.start();

        // Geser index ke clip berikutnya untuk tembakan selanjutnya
        currentClipIndex = (currentClipIndex + 1) % clips.size();
    }

    public void loop() {
        /*
         * Method: loop
         * Memutar audio secara terus menerus (Hanya menggunakan clip pertama)
         */
        if (clips.isEmpty())
            return;

        Clip bgmClip = clips.get(0);
        if (bgmClip.isRunning()) {
            bgmClip.stop();
        }
        bgmClip.setFramePosition(0);
        bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        /*
         * Method: stop
         * Menghentikan SEMUA clip dalam pool
         */
        if (clips.isEmpty())
            return;

        for (Clip c : clips) {
            if (c.isRunning()) {
                c.stop();
            }
        }
    }

    public void setVolume(float value) {
        /*
         * Method: setVolume
         * Mengatur volume untuk SEMUA clip dalam pool
         */
        if (clips.isEmpty())
            return;

        for (Clip c : clips) {
            try {
                // Cek apakah kontrol volume didukung
                if (c.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    // Dapatkan kontrol volume
                    FloatControl gainControl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
                    gainControl.setValue(value);
                }
            } catch (Exception e) {
                // Abaikan jika kontrol volume tidak didukung
            }
        }
    }
}
