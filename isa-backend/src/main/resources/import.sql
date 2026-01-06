-- Lozinke su hesovane pomocu BCrypt algoritma https://www.dailycred.com/article/bcrypt-calculator
-- Lozinka za oba user-a je 123

INSERT INTO USERS (username, password, first_name, last_name, email, address, enabled, last_password_reset_date) VALUES ('user', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'Marko', 'Markovic', 'user@example.com', 'adresa1', true, '2017-10-01 21:58:58.508-07');
INSERT INTO USERS (username, password, first_name, last_name, email, address, enabled, last_password_reset_date) VALUES ('user2', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'Nikola', 'Nikolic', 'user2@example.com', 'adresa2', true, '2017-10-01 18:57:58.508-07');

INSERT INTO ROLE (name) VALUES ('ROLE_USER');

INSERT INTO USER_ROLE (user_id, role_id) VALUES (1, 1); -- user-u dodeljujemo rolu USER
INSERT INTO USER_ROLE (user_id, role_id) VALUES (2, 1); -- admin-u dodeljujemo rolu USER
--INSERT INTO USER_ROLE (user_id, role_id) VALUES (2, 1); -- user-u dodeljujemo rolu ADMIN - ne vise

INSERT INTO video_posts (id, owner_id, title, description, location, thumbnail_path, video_path, created_at, views) VALUES (1, 1, 'Cocomelon Theme | BEGINNER PIANO TUTORIAL + SHEET MUSIC by Betacustic', 'In this piano tutorial you can learn HOW TO PLAY "Cocomelon Theme" by Cocomelon, BEGINNER version', 'Novi Sad', 'uploads\\thumbnails\\1.png', 'uploads\\videos\\1.mp4', '2026-01-03 14:58:38.322',0);
INSERT INTO video_posts (id, owner_id, title, description, location, thumbnail_path, video_path, created_at, views) VALUES (2, 1, 'Woodstock Kids Accordion', 'This colorful accordion from the makers of Woodstock Chimes comes with playing instructions and 8 easy-to-play songs. ', 'Novi Sad', 'uploads\\thumbnails\\2.png', 'uploads\\videos\\2.mp4', '2026-01-03 14:59:20.521',0);
INSERT INTO video_posts (id, owner_id, title, description, location, thumbnail_path, video_path, created_at, views) VALUES (3, 1, 'Snimak neba', 'Deset hiljada metara na nebu.', 'Novi Sad', 'uploads\\thumbnails\\3.png', 'uploads\\videos\\3.mp4', '2026-01-03 14:59:55.250',0);
INSERT INTO video_posts (id, owner_id, title, description, location, thumbnail_path, video_path, created_at, views) VALUES (4, 1, 'Spongebob Krusty Krab song on recorder', 'Song - Rake Hornpipe', 'Novi Sad', 'uploads\\thumbnails\\4.png', 'uploads\\videos\\4.mp4', '2026-01-03 15:00:59.689',0);
INSERT INTO video_posts (id, owner_id, title, description, location, thumbnail_path, video_path, created_at, views) VALUES (5, 1, 'In the Jungle, the mighty jungle...', 'OK I know that this is not the most original thing in the world but it is the whole song or that''s what I believe. Hope u lik it Bye, XokAs', 'Novi Sad', 'uploads\\thumbnails\\5.png', 'uploads\\videos\\5.mp4', '2026-01-03 15:02:01.817',0);

INSERT INTO video_tags (video_id, tag) VALUES (1, 'adasas');
INSERT INTO video_tags (video_id, tag) VALUES (2, 'fdssa');
INSERT INTO video_tags (video_id, tag) VALUES (3, 'iuzui');
INSERT INTO video_tags (video_id, tag) VALUES (4, 'nbvnbv');
INSERT INTO video_tags (video_id, tag) VALUES (5, 'hghfggh');

SELECT setval(pg_get_serial_sequence('video_posts','id'), (SELECT MAX(id) FROM video_posts), true);