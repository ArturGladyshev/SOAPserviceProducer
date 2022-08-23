CREATE DATABASE IF NOT EXISTS `soap`;
USE `soap`;

CREATE TABLE IF NOT EXISTS `users` (
    `login` varchar (200) NOT NULL,
    `name` varchar(200) NOT NULL,
    `password` varchar(100) NOT NULL,
    PRIMARY KEY (`login`)
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `roles` (
    `roleId` bigint(5) NOT NULL AUTO_INCREMENT,
    `name` enum ('Admin', 'Operator', 'Analyst', 'Moderator', 'Editor') NOT NULL,
    `user_login` varchar (200) DEFAULT NULL,
    PRIMARY KEY (`roleId`)
    ) ENGINE=InnoDB;

INSERT INTO users (name, password)
VALUES ('Ivan', 'Passw1'), ('Semen', 'Passw2'), ('Fedor', 'Passw3');
