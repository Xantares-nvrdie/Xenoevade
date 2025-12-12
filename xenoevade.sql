CREATE DATABASE IF NOT EXISTS xenoevade;
USE xenoevade;

CREATE TABLE IF NOT EXISTS tbenefit (
    username VARCHAR(50) NOT NULL,
    skor INT DEFAULT 0,
    peluru_meleset INT DEFAULT 0,
    sisa_peluru INT DEFAULT 0,
    PRIMARY KEY (username)
);

INSERT INTO tbenefit (username, skor, peluru_meleset, sisa_peluru) 
VALUES 
('PlayerSatu', 100, 5, 10),
('AlienHunter', 250, 12, 3);
