SET AUTOCOMMIT FALSE;

INSERT INTO genre (genre_id, name) VALUES (next value for media_seq, 'Abenteuer');
INSERT INTO genre (genre_id, name) VALUES (next value for media_seq, 'Animation');
INSERT INTO genre (genre_id, name) VALUES (next value for media_seq, 'Familie');
INSERT INTO genre (genre_id, name) VALUES (next value for media_seq, 'Komödie');
INSERT INTO genre (genre_id, name) VALUES (next value for media_seq, 'Science Fiction');

INSERT INTO movie (idMovie, c00, c07, c08, c09, c14, c20, idSet) VALUES (next value for media_seq, '#9', 2009, '<thumb aspect="poster" preview="http://image.tmdb.org/t/p/w500/yVkU8L6HttPQlatFdI9bata7NX0.jpg">', 'tt0472033', 'Animation / Science Fiction', '', 0);
INSERT INTO genre_link (genre_id, media_type, media_id) VALUES (2, 'movie', current value for media_seq);
INSERT INTO genre_link (genre_id, media_type, media_id) VALUES (5, 'movie', current value for media_seq);

INSERT INTO tvshow (idShow, c00, c06, c08, c11, c12) VALUES (next value for media_seq, '2 Broke Girls', '<thumb aspect="banner">http://thetvdb.com/banners/graphical/248741-g5.jpg</thumb>', 'Komödie', '', '248741');
INSERT INTO genre_link (genre_id, media_type, media_id) VALUES (4, 'tvshow', current value for media_seq);

COMMIT;