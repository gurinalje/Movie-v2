/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 8.0.33 : Database - cinema
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`cinema` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `cinema`;

/*Table structure for table `activity` */

DROP TABLE IF EXISTS `activity`;

CREATE TABLE `activity` (
  `id` int NOT NULL AUTO_INCREMENT,
  `activity_name` varchar(45) NOT NULL,
  `a_description` varchar(255) NOT NULL,
  `end_time` datetime DEFAULT NULL,
  `coupon_id` int DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `activity` */

insert  into `activity`(`id`,`activity_name`,`a_description`,`end_time`,`coupon_id`,`start_time`) values (2,'情侣套票优惠','情人节当天购买双人票赠送爆米花','2026-02-15 00:00:00',NULL,'2026-02-14 00:00:00'),(17,'111','111','2026-03-19 00:00:00',15,'2026-03-18 15:01:00'),(20,'庆祝blg夺冠','庆祝blg夺冠','2026-04-02 00:00:00',18,'2026-03-31 19:00:00');

/*Table structure for table `activity_movie` */

DROP TABLE IF EXISTS `activity_movie`;

CREATE TABLE `activity_movie` (
  `activity_id` int NOT NULL COMMENT '活动ID',
  `movie_id` int NOT NULL COMMENT '参与活动的电影ID',
  PRIMARY KEY (`activity_id`,`movie_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='活动与电影关联表';

/*Data for the table `activity_movie` */

/*Table structure for table `coupon` */

DROP TABLE IF EXISTS `coupon`;

CREATE TABLE `coupon` (
  `id` int NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `target_amount` float DEFAULT NULL,
  `discount_amount` float DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `coupon` */

insert  into `coupon`(`id`,`description`,`name`,`target_amount`,`discount_amount`,`start_time`,`end_time`) values (16,'测试','测试专属券',50,10,'2026-03-27 14:16:00','2026-03-28 00:00:00'),(18,'全场五折','庆祝blg夺冠专属券',50,25,'2026-03-31 19:00:00','2026-04-02 00:00:00');

/*Table structure for table `coupon_user` */

DROP TABLE IF EXISTS `coupon_user`;

CREATE TABLE `coupon_user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `coupon_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `coupon_user` */

/*Table structure for table `hall` */

DROP TABLE IF EXISTS `hall`;

CREATE TABLE `hall` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `column` int DEFAULT NULL,
  `row` int DEFAULT NULL,
  `status` int DEFAULT '1' COMMENT '营业状态：1正常 0停用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `hall` */

insert  into `hall`(`id`,`name`,`column`,`row`,`status`) values (2,'2号 VIP 情侣厅',8,6,1),(3,'3号 杜比全景声厅',10,8,1),(6,'1号激光IMAX厅',10,8,1);

/*Table structure for table `history` */

DROP TABLE IF EXISTS `history`;

CREATE TABLE `history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `kind` int NOT NULL,
  `time` datetime DEFAULT NULL,
  `money` double NOT NULL,
  `description` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `history` */

insert  into `history`(`id`,`user_id`,`kind`,`time`,`money`,`description`) values (1,9,2,NULL,-40,'购买《流浪地球2》电影票'),(2,9,2,NULL,-40,'购买《流浪地球2》电影票'),(3,9,2,NULL,-40,'购买《流浪地球2》电影票'),(4,9,2,NULL,-40,'购买《流浪地球2》电影票'),(5,9,2,NULL,-40,'VIP卡购买《满江红》'),(6,9,2,NULL,-40,'VIP卡购买《满江红》'),(7,1,1,NULL,100,'会员卡充值 (实际到账 ￥100.0)'),(8,9,2,NULL,-40,'VIP卡购买《满江红》'),(9,1,1,NULL,100,'会员卡充值 (实际到账 ￥100.0)'),(10,1,1,NULL,200,'会员卡充值 (实际到账 ￥200.0)'),(11,12,0,NULL,-25,'购买会员卡'),(12,12,1,NULL,150,'会员卡充值 (实际到账 ￥150.0)'),(13,12,1,NULL,150,'会员卡充值 (实际到账 ￥150.0)'),(14,11,0,NULL,-25,'购买会员卡'),(15,11,1,NULL,100,'会员卡充值 (实际到账 ￥100.0)'),(16,11,2,NULL,-40,'VIP卡购买《满江红》'),(17,12,1,NULL,100,'会员卡充值 (实际到账 ￥120.0)'),(18,12,2,NULL,-40,'VIP卡购买《满江红》'),(19,12,1,NULL,100,'会员卡充值 (实际到账 ￥120.0)'),(20,12,2,'2026-03-31 10:50:26',-40,'VIP卡购买《满江红》'),(21,12,2,'2026-03-31 10:58:50',-40,'VIP卡购买《满江红》'),(22,12,2,'2026-03-31 10:59:12',-40,'VIP卡购买《满江红》'),(23,9,2,'2026-03-31 17:10:54',-40,'VIP卡购买《满江红》'),(24,9,2,'2026-03-31 17:21:38',-40,'VIP卡购买《玄鉴仙族》'),(25,9,2,'2026-03-31 17:24:25',-280,'VIP卡购买《深海》'),(26,9,2,'2026-03-31 17:24:43',-45,'银行卡购买《泰坦尼克号》'),(27,9,2,'2026-03-31 17:36:34',-40,'VIP卡购买《满江红》'),(28,9,2,'2026-03-31 18:48:53',-70,'VIP卡购买《深海》'),(29,9,1,'2026-03-31 18:54:22',200,'会员卡充值 (实际到账 ￥250.0)'),(30,9,2,'2026-03-31 19:20:17',-70,'银行卡购买《深海》');

/*Table structure for table `movie` */

DROP TABLE IF EXISTS `movie`;

CREATE TABLE `movie` (
  `id` int NOT NULL AUTO_INCREMENT,
  `poster_url` varchar(255) DEFAULT NULL,
  `director` varchar(255) DEFAULT NULL,
  `screen_writer` varchar(255) DEFAULT NULL,
  `starring` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `language` varchar(255) DEFAULT NULL,
  `length` int NOT NULL,
  `start_date` datetime DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `description` text,
  `status` int DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `movie` */

insert  into `movie`(`id`,`poster_url`,`director`,`screen_writer`,`starring`,`type`,`country`,`language`,`length`,`start_date`,`name`,`description`,`status`) values (1,'/images/avatar.jpg','詹姆斯·卡梅隆','詹姆斯·卡梅隆','萨姆·沃辛顿 / 佐伊·索尔达娜','科幻/冒险','美国','英语',192,'2026-02-01 00:00:00','阿凡达：水之道','影片设定在《阿凡达》的剧情落幕十余年后...',0),(2,'/images/earth.jpg','郭帆','郭帆 / 龚格尔','吴京 / 刘德华 / 李雪健','科幻/灾难','中国大陆','汉语普通话',173,'2026-01-22 00:00:00','流浪地球2','太阳即将毁灭，人类在地球表面建造出巨大的推进器...',0),(3,'/images/titnaic.jpg','詹姆斯·卡梅隆','詹姆斯·卡梅隆','莱昂纳多·迪卡普里奥 / 凯特·温丝莱特','剧情/爱情','美国','英语',194,'2026-02-14 00:00:00','泰坦尼克号','1912年4月10日，号称 “世界工业史上的奇迹”的“泰坦尼克号”...',0),(4,'/images/avatar.jpg','1',NULL,'1','玄幻','中国','国语',120,'2026-03-19 00:00:00','玄鉴仙族','1',0),(5,'/images/mjh.jpg','张艺谋','陈宇','沈腾 / 易烊千玺 / 张译','剧情 / 喜剧 / 悬疑','中国大陆','汉语普通话',159,'2026-01-22 08:00:00','满江红','南宋绍兴年间，岳飞死后四年，秦桧率兵与金国会谈。会谈前夜，金国使者死在宰相驻地...',0),(6,'/images/sh.jpg','田晓鹏','田晓鹏','苏鑫 / 王亭文','动画 / 奇幻','中国大陆','汉语普通话',112,'2026-01-22 08:00:00','深海','在大海的最深处，藏着所有秘密。一位现代少女误入梦幻的深海世界，却因此邂逅了一段独特的生命旅程...',0),(7,'/images/wm.jpg','程耳','程耳','梁朝伟 / 王一博 / 周迅','剧情 / 悬疑 / 历史','中国大陆','汉语普通话',128,'2026-01-22 08:00:00','无名','上世纪二十年代开始，隐蔽战线的地下工作者们冒着生命危险送出情报，用生命与热血保卫祖国...',0),(8,'/images/lyzl.jpg','新海诚','新海诚','原菜乃华 / 松村北斗','动画 / 奇幻 / 冒险','日本','日语',122,'2026-03-24 08:00:00','铃芽之旅','故事讲述生活在日本九州田舍的17岁少女铃芽遇见了为了寻找“门”而踏上旅途的青年...',0),(9,'/images/zzx.jpg','乔伊姆·多斯·桑托斯','菲尔·罗德','沙梅克·摩尔 / 海莉·斯坦菲尔德','动画 / 动作 / 冒险','美国','英语',140,'2026-06-02 08:00:00','蜘蛛侠：纵横宇宙','新生代蜘蛛侠迈尔斯誓言打破每个蜘蛛侠都会失去至亲的命运魔咒，因此引来蜘蛛联盟的围攻...',0),(10,'/images/fs.jpg','乌尔善','冉平 / 冉甲男','费翔 / 李雪健 / 黄渤','动作 / 战争 / 奇幻','中国大陆','汉语普通话',148,'2026-07-20 08:00:00','封神第一部：朝歌风云','商王殷寿与狐妖妲己勾结，暴虐无道，引发天谴。昆仑仙人姜子牙携“封神榜”下山，寻找天下共主...',0);

/*Table structure for table `movie_like` */

DROP TABLE IF EXISTS `movie_like`;

CREATE TABLE `movie_like` (
  `movie_id` int NOT NULL,
  `user_id` int NOT NULL,
  `like_time` datetime DEFAULT NULL,
  PRIMARY KEY (`movie_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `movie_like` */

insert  into `movie_like`(`movie_id`,`user_id`,`like_time`) values (1,1,'2026-02-08 21:22:23'),(2,9,NULL),(2,10,NULL),(3,9,NULL),(5,10,NULL),(6,9,NULL),(6,10,NULL),(7,10,NULL);

/*Table structure for table `refund_info` */

DROP TABLE IF EXISTS `refund_info`;

CREATE TABLE `refund_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `limit_time` int NOT NULL DEFAULT '2' COMMENT '退票时限(小时)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `refund_info` */

insert  into `refund_info`(`id`,`limit_time`) values (1,4);

/*Table structure for table `refundpolicy` */

DROP TABLE IF EXISTS `refundpolicy`;

CREATE TABLE `refundpolicy` (
  `id` int NOT NULL AUTO_INCREMENT,
  `movie_id` int DEFAULT NULL,
  `movie_name` varchar(255) NOT NULL,
  `time_limit` int DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `rate` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `refundpolicy` */

insert  into `refundpolicy`(`id`,`movie_id`,`movie_name`,`time_limit`,`start_time`,`end_time`,`rate`) values (1,2,'流浪地球2',5,'2026-03-31 15:17:09','2026-04-01 00:00:00',0.2),(2,1,'阿凡达：水之道',24,'2021-03-31 17:20:05','2026-03-31 17:20:21',0.1);

/*Table structure for table `schedule` */

DROP TABLE IF EXISTS `schedule`;

CREATE TABLE `schedule` (
  `id` int NOT NULL AUTO_INCREMENT,
  `hall_id` int NOT NULL,
  `movie_id` int NOT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `fare` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `schedule` */

insert  into `schedule`(`id`,`hall_id`,`movie_id`,`start_time`,`end_time`,`fare`) values (1,1,1,'2026-02-10 14:00:00','2026-02-10 17:12:00',80),(2,1,1,'2026-02-10 19:00:00','2026-02-10 22:12:00',90),(3,2,2,'2026-02-11 10:00:00','2026-02-11 12:53:00',45),(4,2,2,'2026-02-11 14:00:00','2026-02-11 16:53:00',45),(5,3,3,'2026-02-14 13:14:00','2026-02-14 16:28:00',99),(6,2,2,'2026-03-19 00:00:00','2026-03-19 04:00:00',40),(7,3,3,'2026-03-18 16:00:00','2026-03-18 21:00:00',40),(8,2,1,'2026-03-28 00:00:00','2026-03-28 04:00:00',40),(9,6,2,'2026-03-27 20:00:00','2026-03-27 23:00:00',40),(10,3,5,'2026-03-28 00:00:00','2026-03-28 04:00:00',40),(11,2,6,'2026-03-27 19:00:00','2026-03-27 23:00:00',60),(12,6,7,'2026-03-30 10:32:33','2026-03-30 17:00:00',40),(13,3,2,'2026-03-30 11:58:23','2026-03-30 18:55:35',40),(14,2,2,'2026-03-31 00:00:00','2026-03-31 04:00:00',40),(15,3,5,'2026-04-01 00:00:00','2026-04-01 04:00:00',40),(16,6,4,'2026-03-31 18:19:24','2026-03-31 23:19:33',40),(17,6,6,'2026-04-01 00:00:00','2026-04-01 04:00:00',70),(18,6,3,'2026-04-02 00:00:00','2026-04-02 03:30:00',45),(19,6,2,'2026-04-03 00:00:00','2026-04-03 05:00:00',45);

/*Table structure for table `schedule_view` */

DROP TABLE IF EXISTS `schedule_view`;

CREATE TABLE `schedule_view` (
  `day` int NOT NULL DEFAULT '7' COMMENT '允许观众看到未来几天的排片'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `schedule_view` */

insert  into `schedule_view`(`day`) values (7);

/*Table structure for table `ticket` */

DROP TABLE IF EXISTS `ticket`;

CREATE TABLE `ticket` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `schedule_id` int DEFAULT NULL,
  `column_index` int DEFAULT NULL,
  `row_index` int DEFAULT NULL,
  `state` int DEFAULT NULL COMMENT '订单状态：0未支付，1已支付，2已失效',
  `time` datetime DEFAULT NULL COMMENT '下单时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `ticket` */

insert  into `ticket`(`id`,`user_id`,`schedule_id`,`column_index`,`row_index`,`state`,`time`) values (7,9,14,7,2,1,NULL),(8,9,14,0,5,1,NULL),(9,9,14,0,3,1,NULL),(11,9,14,2,0,1,NULL),(13,9,14,4,2,1,NULL),(16,9,14,7,3,1,NULL),(17,9,15,9,7,1,NULL),(19,9,15,9,0,1,NULL),(20,9,15,8,1,1,NULL),(21,9,15,9,1,1,NULL),(24,9,15,9,5,1,NULL),(25,9,15,9,6,1,NULL),(26,10,15,0,0,0,NULL),(27,9,15,7,2,1,NULL),(28,11,15,0,1,0,NULL),(29,11,15,7,4,1,NULL),(30,12,15,3,0,1,NULL),(31,12,15,4,0,1,NULL),(37,12,15,5,6,1,NULL),(46,12,15,4,3,1,NULL),(47,9,15,0,7,1,'2026-03-31 17:10:52'),(48,9,16,5,2,1,'2026-03-31 17:21:36'),(49,9,17,6,2,1,'2026-03-31 17:24:25'),(50,9,17,7,2,1,'2026-03-31 17:24:25'),(51,9,17,6,3,1,'2026-03-31 17:24:25'),(52,9,17,7,3,1,'2026-03-31 17:24:25'),(53,9,18,9,7,1,'2026-03-31 17:24:41'),(54,9,15,1,3,1,'2026-03-31 17:36:32'),(55,9,17,0,0,1,'2026-03-31 18:48:52'),(57,9,17,9,7,1,'2026-03-31 19:20:11');

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(128) NOT NULL,
  `kind` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id_uindex` (`id`),
  UNIQUE KEY `user_username_uindex` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `user` */

insert  into `user`(`id`,`username`,`password`,`kind`) values (8,'root','$2b$12$qnKHAMtJBnN0PBzpg/1QrOd67dySgPPVGbpsL8rn8N8Fo8eCY3m2W',1),(9,'faker','$2b$12$FIMxCqNDHyzoavw31qlMleVg2n5DwSKTaVSRY8JR0NVD1y/4UaiUS',2),(10,'zhangsan','$2b$12$Ytd2QufMQ0H704kwi/QOoONjoShlhzQ.O37DK6NRquq9jkSy14oUm',2),(11,'111','$2b$12$52F/gDIH4odxPwr5G/3hoe3kDz/3Mk2epIjn1E7D4HLCAEjuKBATS',2),(12,'222','$2b$12$oXH21JmJS6TJpZz7vfpQju/e0xF9ArBBV5kKZWjijMmU0Szo9Xxfi',2);

/*Table structure for table `vip_card` */

DROP TABLE IF EXISTS `vip_card`;

CREATE TABLE `vip_card` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `user_id` int DEFAULT NULL,
  `balance` double NOT NULL,
  `join_time` datetime DEFAULT NULL,
  `total` double NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `vip_card_user_id_uindex` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `vip_card` */

insert  into `vip_card`(`id`,`username`,`user_id`,`balance`,`join_time`,`total`) values (1,'VIP用户',9,1560,NULL,2325),(2,'VIP用户',1,500,NULL,525),(3,'222',12,380,NULL,525),(4,'111',11,60,NULL,125);

/*Table structure for table `vip_strategy` */

DROP TABLE IF EXISTS `vip_strategy`;

CREATE TABLE `vip_strategy` (
  `id` int NOT NULL AUTO_INCREMENT,
  `charge_limit` int DEFAULT NULL,
  `gift_amount` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `vip_strategy` */

insert  into `vip_strategy`(`id`,`charge_limit`,`gift_amount`) values (5,100,20),(6,200,50),(7,300,100);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
