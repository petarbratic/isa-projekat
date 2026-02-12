-- Lozinke su hesovane pomocu BCrypt algoritma https://www.dailycred.com/article/bcrypt-calculator
-- Lozinka za sva tri user-a je 123

INSERT INTO USERS (username, password, first_name, last_name, email, address, enabled, last_password_reset_date) VALUES ('user', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'Marko', 'Markovic', 'user@example.com', 'adresa1', true, '2017-10-01 21:58:58.508-07');
INSERT INTO USERS (username, password, first_name, last_name, email, address, enabled, last_password_reset_date) VALUES ('user2', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'Nikola', 'Nikolic', 'user2@example.com', 'adresa2', true, '2017-10-01 18:57:58.508-07');
INSERT INTO USERS (username, password, first_name, last_name, email, address, enabled, last_password_reset_date) VALUES ('user3', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'Petar', 'Petrovic', 'user3@example.com', 'adresa3', true, '2017-10-01 18:57:58.508-07');

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

INSERT INTO video_comments (user_id, video_post_id, text, created_at, version) VALUES (1, 5, 'Ovo mi je baš prijalo da poslušam.', '2026-01-11 12:00:01.000', 0);
INSERT INTO video_comments (user_id, video_post_id, text, created_at, version) VALUES (2, 5, 'Odlična energija, svaka čast!', '2026-01-11 12:00:12.000', 0);
INSERT INTO video_comments (user_id, video_post_id, text, created_at, version) VALUES (3, 5, 'Pesma je odlično odsvirana, bravo!', '2026-01-11 12:00:25.000', 0);
INSERT INTO video_comments (user_id, video_post_id, text, created_at, version) VALUES (1, 5, 'Kvalitet zvuka je super, samo tako nastavi.', '2026-01-11 12:00:39.000', 0);
INSERT INTO video_comments (user_id, video_post_id, text, created_at, version) VALUES (2, 5, 'Ovo me je podsetilo na stare dane.', '2026-01-11 12:00:52.000', 0);
INSERT INTO video_comments (user_id, video_post_id, text, created_at, version) VALUES (3, 5, 'Baš dobro, imaš odličan osećaj za ritam.', '2026-01-11 12:01:07.000', 0);
INSERT INTO video_comments (user_id, video_post_id, text, created_at, version) VALUES (1, 5, 'Kako si birao tempo? Deluje baš pogođeno.', '2026-01-11 12:01:21.000', 0);
INSERT INTO video_comments (user_id, video_post_id, text, created_at, version) VALUES (2, 5, 'Sviđa mi se interpretacija, mnogo je prirodno.', '2026-01-11 12:01:35.000', 0);
INSERT INTO video_comments (user_id, video_post_id, text, created_at, version) VALUES (3, 5, 'Može neki tutorial sledeći put?', '2026-01-11 12:01:49.000', 0);
INSERT INTO video_comments (user_id, video_post_id, text, created_at, version) VALUES (1, 5, 'Odličan video, svaka čast.', '2026-01-11 12:02:03.000', 0);
INSERT INTO video_comments (user_id, video_post_id, text, created_at, version) VALUES (2, 1, 'Odličan video, vratio me u detinjstvo!', '2026-01-11 11:15:49.479', 0);
INSERT INTO video_comments (user_id, video_post_id, text, created_at, version) VALUES (3, 1, 'Jednostavno i lepo objašnjeno, svaka čast.', '2026-01-11 11:17:02.112', 0);
INSERT INTO video_comments (user_id, video_post_id, text, created_at, version) VALUES (1, 2, 'Baš lep zvuk harmonike, podseća na stare dane.', '2026-01-11 11:18:44.905', 0);
INSERT INTO video_comments (user_id, video_post_id, text, created_at, version) VALUES (3, 2, 'Zanimljiv instrument, nisam znao da postoji ovakva verzija.', '2026-01-11 11:19:31.330', 0);
INSERT INTO video_comments (user_id, video_post_id, text, created_at, version) VALUES (2, 3, 'Snimak izgleda nestvarno, odličan kadar.', '2026-01-11 11:21:10.701', 0);
INSERT INTO video_comments (user_id, video_post_id, text, created_at, version) VALUES (1, 4, 'Ovo mi je ulepšalo dan', '2026-01-11 11:22:58.440', 0);
INSERT INTO video_comments (user_id, video_post_id, text, created_at, version) VALUES (2, 4, 'Nostalgija na maksimumu!', '2026-01-11 11:23:41.006', 0);
INSERT INTO video_comments (user_id, video_post_id, text, created_at, version) VALUES (3, 5, 'Pesma je odlično odsvirana, bravo!', '2026-01-11 11:25:09.880', 0);
INSERT INTO video_comments (user_id, video_post_id, text, created_at, version) VALUES (1, 5, 'Ovo mi je baš prijalo da poslušam.', '2026-01-11 11:26:34.215', 0);

INSERT INTO video_views (video_id, viewed_at) VALUES (5, NOW() - INTERVAL '1 day');
INSERT INTO video_views (video_id, viewed_at) VALUES (5, NOW() - INTERVAL '1 day' + INTERVAL '10 minutes');
INSERT INTO video_views (video_id, viewed_at) VALUES (5, NOW() - INTERVAL '1 day' + INTERVAL '20 minutes');
INSERT INTO video_views (video_id, viewed_at) VALUES (5, NOW() - INTERVAL '2 day');
INSERT INTO video_views (video_id, viewed_at) VALUES (5, NOW() - INTERVAL '2 day' + INTERVAL '15 minutes');
INSERT INTO video_views (video_id, viewed_at) VALUES (5, NOW() - INTERVAL '3 day');
INSERT INTO video_views (video_id, viewed_at) VALUES (5, NOW() - INTERVAL '3 day' + INTERVAL '25 minutes');
INSERT INTO video_views (video_id, viewed_at) VALUES (5, NOW() - INTERVAL '4 day');
INSERT INTO video_views (video_id, viewed_at) VALUES (4, NOW() - INTERVAL '1 day');
INSERT INTO video_views (video_id, viewed_at) VALUES (4, NOW() - INTERVAL '2 day');
INSERT INTO video_views (video_id, viewed_at) VALUES (4, NOW() - INTERVAL '2 day' + INTERVAL '30 minutes');
INSERT INTO video_views (video_id, viewed_at) VALUES (4, NOW() - INTERVAL '3 day');
INSERT INTO video_views (video_id, viewed_at) VALUES (4, NOW() - INTERVAL '4 day');
INSERT INTO video_views (video_id, viewed_at) VALUES (3, NOW() - INTERVAL '1 day');
INSERT INTO video_views (video_id, viewed_at) VALUES (3, NOW() - INTERVAL '3 day');
INSERT INTO video_views (video_id, viewed_at) VALUES (3, NOW() - INTERVAL '5 day');
INSERT INTO video_views (video_id, viewed_at) VALUES (1, NOW() - INTERVAL '2 day');
INSERT INTO video_views (video_id, viewed_at) VALUES (2, NOW() - INTERVAL '6 day');
INSERT INTO video_views (video_id, viewed_at) VALUES (1, NOW() - INTERVAL '7 day');
INSERT INTO video_views (video_id, viewed_at) VALUES (2, NOW() - INTERVAL '7 day' + INTERVAL '15 minutes');