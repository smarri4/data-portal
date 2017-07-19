-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.1.10-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             9.1.0.4867
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping database structure for portaldb
CREATE DATABASE IF NOT EXISTS `portaldb` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `portaldb`;


-- Dumping structure for table portaldb.project_files
CREATE TABLE `project_files` (
	`fileid` VARCHAR(255) NOT NULL,
	`projectid` VARCHAR(32) NOT NULL,
	`releaseid` BIGINT(20) UNSIGNED NOT NULL,
	`type` ENUM('raw','report','result') DEFAULT NULL,
	`modified_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
	`modified_by` VARCHAR(32) DEFAULT NULL,
	`description` TEXT,
  	`hidden` boolean NOT NULL DEFAULT 0,
	PRIMARY KEY (`fileid`),
	INDEX `FK_project_files_release` (`releaseid`),
	INDEX `FK_project_files_project_membership` (`projectid`),
	CONSTRAINT `FK_project_files_project_membership` FOREIGN KEY (`projectid`) REFERENCES `project_membership` (`projectid`) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT `FK_project_files_release` FOREIGN KEY (`releaseid`) REFERENCES `project_release` (`releaseid`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- Data exporting was unselected.


-- Dumping structure for table portaldb.project_membership
CREATE TABLE IF NOT EXISTS `project_membership` (
  `projectid` varchar(32) NOT NULL,
  `userid` varchar(128) NOT NULL,
  `owner` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`projectid`,`userid`),
  KEY `FK_project_membership_users` (`userid`),
  CONSTRAINT `FK_project_membership_users` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table portaldb.release
CREATE TABLE IF NOT EXISTS `project_release` (
  `releaseid` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `released_by` varchar(128) NOT NULL,
  `release_date` date DEFAULT '1900-01-01',
  `description` text,
  PRIMARY KEY (`releaseid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table portaldb.users
CREATE TABLE IF NOT EXISTS `users` (
  `userid` varchar(128) NOT NULL,
  `name` varchar(32) DEFAULT NULL,
  `pass` varchar(32) NULL,
  `affiliation` varchar(32) DEFAULT NULL,
  `role` ENUM('admin','customer') DEFAULT 'customer',
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;


