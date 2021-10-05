-- auto-generated definition
create database daily_code;
use daily_code;
CREATE TABLE `email_subscribe` (
                                   `id` int NOT NULL AUTO_INCREMENT,
                                   `email` varchar(30) NOT NULL DEFAULT '',
                                   `subscribe_time` datetime NOT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



-- auto-generated definition
CREATE TABLE `send_record` (
                               `id` int NOT NULL AUTO_INCREMENT,
                               `email_id` int NOT NULL,
                               `send_date` date NOT NULL,
                               `send_time` datetime NOT NULL,
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci


