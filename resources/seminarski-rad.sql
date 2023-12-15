/*
SQLyog Community v13.2.0 (64 bit)
MySQL - 10.4.28-MariaDB : Database - seminarski-rad
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`seminarski-rad` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;

USE `seminarski-rad`;

/*Table structure for table `app_user` */

DROP TABLE IF EXISTS `app_user`;

CREATE TABLE `app_user` (
  `id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `app_user` */

insert  into `app_user`(`id`,`username`,`password`) values 
(6,'stanmil','bcrypt+sha512$d0ef5d08ea0ca8d37b5ed7707a2e9d0b$12$c7da695cfd64c5343f0c88cc673e369be54f678177ad1ddc'),
(7,'ppetar','bcrypt+sha512$a0fe7b43a52cc19cf51194f96975f560$12$26df5d8873f84f23592cc86c9170f9d159596bceac54bd63'),
(8,'saraa','bcrypt+sha512$aedc6450da3ad5555b69a4a8cd1dfe57$12$64eeb775042b47054cde8b8dfc7149e59614f83f09058037');

/*Table structure for table `board` */

DROP TABLE IF EXISTS `board`;

CREATE TABLE `board` (
  `id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `size` bigint(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `board` */

insert  into `board`(`id`,`size`) values 
(4,5),
(5,7),
(6,9),
(7,3);

/*Table structure for table `game_session` */

DROP TABLE IF EXISTS `game_session`;

CREATE TABLE `game_session` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `app_user_id` bigint(10) unsigned NOT NULL,
  `board_id` bigint(10) unsigned NOT NULL,
  `won` varchar(1) DEFAULT NULL,
  `human_score` bigint(3) NOT NULL,
  `computer_score` bigint(10) DEFAULT NULL,
  `human_color` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`app_user_id`,`board_id`),
  KEY `app_user_id` (`app_user_id`),
  KEY `board_id` (`board_id`),
  CONSTRAINT `game_session_ibfk_1` FOREIGN KEY (`app_user_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `game_session_ibfk_2` FOREIGN KEY (`board_id`) REFERENCES `board` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `game_session` */

insert  into `game_session`(`id`,`app_user_id`,`board_id`,`won`,`human_score`,`computer_score`,`human_color`) values 
(1,6,4,'H',3,0,'R'),
(2,7,5,'H',2,0,'B'),
(3,8,5,'C',0,3,'B'),
(4,6,5,'C',0,5,'R'),
(5,6,5,'H',2,0,'B'),
(6,7,4,'C',5,6,'R'),
(7,6,4,'C',12,12,'B'),
(8,6,7,'H',3,0,'R'),
(9,7,6,'C',40,40,'R'),
(10,6,7,'H',3,0,'R'),
(11,6,4,'H',10,0,'R'),
(12,6,4,'C',0,12,'B'),
(13,6,4,'C',0,8,'B'),
(14,6,4,'C',0,10,'B'),
(15,6,4,'C',0,10,'B'),
(16,6,4,'C',0,9,'B');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;