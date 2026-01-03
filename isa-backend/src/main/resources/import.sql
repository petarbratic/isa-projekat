-- Lozinke su hesovane pomocu BCrypt algoritma https://www.dailycred.com/article/bcrypt-calculator
-- Lozinka za oba user-a je 123

INSERT INTO USERS (username, password, first_name, last_name, email, address, enabled, last_password_reset_date) VALUES ('user', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'Marko', 'Markovic', 'user@example.com', 'adresa1', true, '2017-10-01 21:58:58.508-07');
INSERT INTO USERS (username, password, first_name, last_name, email, address, enabled, last_password_reset_date) VALUES ('user2', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'Nikola', 'Nikolic', 'user2@example.com', 'adresa2', true, '2017-10-01 18:57:58.508-07');

INSERT INTO ROLE (name) VALUES ('ROLE_USER');

INSERT INTO USER_ROLE (user_id, role_id) VALUES (1, 1); -- user-u dodeljujemo rolu USER
INSERT INTO USER_ROLE (user_id, role_id) VALUES (2, 1); -- admin-u dodeljujemo rolu USER
--INSERT INTO USER_ROLE (user_id, role_id) VALUES (2, 1); -- user-u dodeljujemo rolu ADMIN - ne vise

INSERT INTO video_posts (id, owner_id, title, description, location, thumbnail_path, video_path, created_at) VALUES (1, 1, 'hemija 1', 'hemija opis 1', 'Novi Sad', 'uploads\\thumbnails\\1.png', 'uploads\\videos\\1.mp4', '2026-01-03 14:58:38.322');
INSERT INTO video_posts (id, owner_id, title, description, location, thumbnail_path, video_path, created_at) VALUES (2, 1, 'hemija 2', 'hemija opis 2', 'Novi Sad', 'uploads\\thumbnails\\2.png', 'uploads\\videos\\2.mp4', '2026-01-03 14:59:20.521');
INSERT INTO video_posts (id, owner_id, title, description, location, thumbnail_path, video_path, created_at) VALUES (3, 1, 'hemija 3', 'hemija opis 3', 'Novi Sad', 'uploads\\thumbnails\\3.png', 'uploads\\videos\\3.mp4', '2026-01-03 14:59:55.250');
INSERT INTO video_posts (id, owner_id, title, description, location, thumbnail_path, video_path, created_at) VALUES (4, 1, 'hemija 4', 'hemija opis 4', 'Novi Sad', 'uploads\\thumbnails\\4.png', 'uploads\\videos\\4.mp4', '2026-01-03 15:00:59.689');
INSERT INTO video_posts (id, owner_id, title, description, location, thumbnail_path, video_path, created_at) VALUES (5, 1, 'hemija 5', 'hemija opis 5', 'Novi Sad', 'uploads\\thumbnails\\5.png', 'uploads\\videos\\5.mp4', '2026-01-03 15:02:01.817');

INSERT INTO video_tags (video_id, tag) VALUES (1, 'adasas');
INSERT INTO video_tags (video_id, tag) VALUES (2, 'fdssa');
INSERT INTO video_tags (video_id, tag) VALUES (3, 'iuzui');
INSERT INTO video_tags (video_id, tag) VALUES (4, 'nbvnbv');
INSERT INTO video_tags (video_id, tag) VALUES (5, 'hghfggh');

