/*
Navicat MySQL Data Transfer

Source Server         : 172.16.150.70 3401
Source Server Version : 50717
Source Host           : 172.16.150.70:3401
Source Database       : rabbit_tx

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2017-06-22 10:03:18
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for msg_event_list
-- ----------------------------
DROP TABLE IF EXISTS `msg_event_list`;
CREATE TABLE `msg_event_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `unique_key` varchar(100) DEFAULT NULL COMMENT '消息ID',
  `serialize_message` blob COMMENT '消息主体',
  `message_level` tinyint(4) DEFAULT NULL COMMENT '消息等级1保证安全2失败忽略',
  `retrytimes` int(3) DEFAULT '0' COMMENT '重试次数',
  `sendtime` datetime DEFAULT NULL COMMENT '发送时间',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `routing_key` varchar(100) DEFAULT NULL COMMENT '消息唯一键',
  `exchange` varchar(100) DEFAULT NULL COMMENT '交换机',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息日志TODO-LIST记录';

-- ----------------------------
-- Table structure for msg_failed_list
-- ----------------------------
DROP TABLE IF EXISTS `msg_failed_list`;
CREATE TABLE `msg_failed_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `unique_key` varchar(100) DEFAULT NULL,
  `serialize_message` blob,
  `message_level` tinyint(4) DEFAULT NULL COMMENT '消息等级1保证安全2失败忽略',
  `retrytimes` int(3) DEFAULT '0' COMMENT '重试次数',
  `sendtime` datetime DEFAULT NULL COMMENT '发送时间',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `routing_key` varchar(100) DEFAULT NULL COMMENT '消息唯一键',
  `exchange` varchar(100) DEFAULT NULL COMMENT '交换机',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='失败消息记录';

-- ----------------------------
-- Table structure for msg_warn_list
-- ----------------------------
DROP TABLE IF EXISTS `msg_warn_list`;
CREATE TABLE `msg_warn_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `unique_key` varchar(100) DEFAULT NULL,
  `serialize_message` blob,
  `message_level` tinyint(4) DEFAULT NULL COMMENT '消息等级1保证安全2失败忽略',
  `retrytimes` int(3) DEFAULT '0' COMMENT '重试次数',
  `sendtime` datetime DEFAULT NULL COMMENT '发送时间',
  `routing_key` varchar(100) DEFAULT NULL COMMENT '消息唯一键',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `exchange` varchar(100) DEFAULT NULL COMMENT '交换机',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='重试多次失败消息记录';