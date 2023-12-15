-- CREATE DATABASE IF NOT EXISTS seminarski_rad;
-- USE seminarski_rad;

/* Table structure for table app_user */

DROP TABLE IF EXISTS game_session;
DROP TABLE IF EXISTS app_user;
DROP TABLE IF EXISTS board;

CREATE TABLE app_user (
  id bigint NOT NULL AUTO_INCREMENT,
  username varchar(255) NOT NULL,
  password varchar(255) NOT NULL,
  PRIMARY KEY (id)
);

/* Data for the table app_user */

INSERT INTO app_user(id, username, password) VALUES 
(6, 'stanmil', 'bcrypt+sha512$d0ef5d08ea0ca8d37b5ed7707a2e9d0b$12$c7da695cfd64c5343f0c88cc673e369be54f678177ad1ddc'),
(7, 'ppetar', 'bcrypt+sha512$a0fe7b43a52cc19cf51194f96975f560$12$26df5d8873f84f23592cc86c9170f9d159596bceac54bd63'),
(8, 'saraa', 'bcrypt+sha512$aedc6450da3ad5555b69a4a8cd1dfe57$12$64eeb775042b47054cde8b8dfc7149e59614f83f09058037');

/* Table structure for table board */

CREATE TABLE board (
  id bigint NOT NULL AUTO_INCREMENT,
  size bigint NOT NULL,
  PRIMARY KEY (id)
);

/* Data for the table board */

INSERT INTO board(id, size) VALUES 
(4, 5),
(5, 7),
(6, 9),
(7, 3);

/* Table structure for table game_session */

CREATE TABLE game_session (
  id bigint NOT NULL AUTO_INCREMENT,
  app_user_id bigint NOT NULL,
  board_id bigint NOT NULL,
  won varchar(1) DEFAULT NULL,
  human_score bigint NOT NULL,
  computer_score bigint DEFAULT NULL,
  human_color varchar(1) DEFAULT NULL,
  PRIMARY KEY (id, app_user_id, board_id),
  CONSTRAINT game_session_ibfk_1 FOREIGN KEY (app_user_id) REFERENCES app_user (id),
  CONSTRAINT game_session_ibfk_2 FOREIGN KEY (board_id) REFERENCES board (id)
);

/* Data for the table game_session */

INSERT INTO game_session(id, app_user_id, board_id, won, human_score, computer_score, human_color) VALUES 
(1, 6, 4, 'H', 3, 0, 'R'),
(2, 7, 5, 'H', 2, 0, 'B'),
(3, 8, 5, 'C', 0, 3, 'B'),
(4, 6, 5, 'C', 0, 5, 'R'),
(5, 6, 5, 'H', 2, 0, 'B'),
(6, 7, 4, 'C', 5, 6, 'R'),
(7, 6, 4, 'C', 12, 12, 'B'),
(8, 6, 7, 'H', 3, 0, 'R'),
(9, 7, 6, 'C', 40, 40, 'R'),
(10, 6, 7, 'H', 3, 0, 'R'),
(11, 6, 4, 'H', 10, 0, 'R'),
(12, 6, 4, 'C', 0, 12, 'B'),
(13, 6, 4, 'C', 0, 8, 'B'),
(14, 6, 4, 'C', 0, 10, 'B'),
(15, 6, 4, 'C', 0, 10, 'B'),
(16, 6, 4, 'C', 0, 9, 'B');
