/*
Navicat MySQL Data Transfer

Source Server         : 172.10.100.204
Source Server Version : 50622
Source Host           : 172.10.100.204:12306
Source Database       : sixin_app

Target Server Type    : MYSQL
Target Server Version : 50622
File Encoding         : 65001

Date: 2015-10-10 09:51:51
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `sixin_friend`
-- ----------------------------
DROP TABLE IF EXISTS `sixin_friend`;
CREATE TABLE `sixin_friend` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `uid` varchar(50) NOT NULL DEFAULT '' COMMENT '用户id',
  `fid` varchar(50) NOT NULL DEFAULT '' COMMENT '好友的id',
  `remark` varchar(50) DEFAULT '' COMMENT '好友备注',
  `bids` text COMMENT '黑名单列表，id使用,分隔',
  PRIMARY KEY (`id`),
  UNIQUE KEY `friend_uidfid_uni` (`uid`,`fid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sixin_friend
-- ----------------------------

-- ----------------------------
-- Table structure for `skt_chat_group`
-- ----------------------------
DROP TABLE IF EXISTS `skt_chat_group`;
CREATE TABLE `skt_chat_group` (
  `group_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '群id',
  `group_name` varchar(50) NOT NULL COMMENT '群名称',
  `icon` varchar(255) DEFAULT '' COMMENT '群头像',
  `group_beizhu` varchar(20) DEFAULT NULL COMMENT '数据库管理员内部备注',
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='群编号表';

-- ----------------------------
-- Records of skt_chat_group
-- ----------------------------
INSERT INTO `skt_chat_group` VALUES ('1', '思埠聊天群', null, null);
INSERT INTO `skt_chat_group` VALUES ('2', '思埠聊天群', 'kjkl', null);
INSERT INTO `skt_chat_group` VALUES ('3', '思埠聊天群', 'kjkl', null);

-- ----------------------------
-- Table structure for `skt_group_user`
-- ----------------------------
DROP TABLE IF EXISTS `skt_group_user`;
CREATE TABLE `skt_group_user` (
  `auto_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '返回的结果auto_id排序，主要用于群员显示的先后顺序。区分谁是群主',
  `group_id` int(11) NOT NULL COMMENT '群id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `admin` int(11) DEFAULT '0' COMMENT '是否管理员 0：普通成员 1：管理员 >1待定',
  PRIMARY KEY (`auto_id`),
  UNIQUE KEY `gu_gu_index` (`group_id`,`user_id`),
  KEY `gu_uid_index` (`user_id`) USING BTREE,
  KEY `gu_gid_index` (`group_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COMMENT='【群聊详细人员详情表】';

-- ----------------------------
-- Records of skt_group_user
-- ----------------------------
INSERT INTO `skt_group_user` VALUES ('1', '1', '2', '1');
INSERT INTO `skt_group_user` VALUES ('2', '1', '1', '0');
INSERT INTO `skt_group_user` VALUES ('3', '1', '3', '0');
INSERT INTO `skt_group_user` VALUES ('4', '2', '1', '1');
INSERT INTO `skt_group_user` VALUES ('5', '2', '2', '0');
INSERT INTO `skt_group_user` VALUES ('6', '2', '3', '0');
INSERT INTO `skt_group_user` VALUES ('7', '3', '1', '1');
INSERT INTO `skt_group_user` VALUES ('8', '3', '2', '0');
INSERT INTO `skt_group_user` VALUES ('9', '3', '3', '0');

-- ----------------------------
-- Table structure for `skt_notice`
-- ----------------------------
DROP TABLE IF EXISTS `skt_notice`;
CREATE TABLE `skt_notice` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` varchar(50) NOT NULL DEFAULT '0' COMMENT '通知类型 ',
  `operator_uid` int(11) DEFAULT NULL COMMENT '操作人id',
  `related_uids` varchar(255) DEFAULT NULL COMMENT '被操作人id,多个id之间用'',''分隔',
  `related_gid` int(11) DEFAULT NULL COMMENT '操作相关的群id',
  `user_id` varchar(100) NOT NULL DEFAULT '' COMMENT '通知接收人',
  `createtime` bigint(11) NOT NULL DEFAULT '0' COMMENT '添加通知时间',
  `content` varchar(255) DEFAULT '' COMMENT '通知内容',
  `msgid` varchar(100) DEFAULT '' COMMENT '消息id',
  PRIMARY KEY (`id`),
  KEY `notice_uid_index` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of skt_notice
-- ----------------------------
INSERT INTO `skt_notice` VALUES ('1', 'inviteToG', '2', null, '1', '3', '1443521115384', null, '');
INSERT INTO `skt_notice` VALUES ('2', 'inviteToG', '1', null, '2', '2', '1443595754780', null, '');
INSERT INTO `skt_notice` VALUES ('3', 'inviteToG', '1', null, '2', '3', '1443595754780', null, '');
INSERT INTO `skt_notice` VALUES ('4', 'inviteToG', '1', null, '3', '2', '1443595759191', null, '');
INSERT INTO `skt_notice` VALUES ('5', 'inviteToG', '1', null, '3', '3', '1443595759191', null, '');

-- ----------------------------
-- Table structure for `skt_record`
-- ----------------------------
DROP TABLE IF EXISTS `skt_record`;
CREATE TABLE `skt_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `talker_id` int(11) NOT NULL COMMENT '说话人id',
  `listener_id` int(11) DEFAULT NULL COMMENT '接收人id',
  `time` bigint(13) NOT NULL COMMENT '1441082813聊天时间（精确到毫秒）',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '消息体',
  `group_id` int(11) DEFAULT NULL COMMENT '群id',
  `msgid` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '消息id',
  PRIMARY KEY (`id`),
  KEY `record_lid_index` (`listener_id`) USING BTREE,
  KEY `record_time_index` (`time`) USING BTREE,
  KEY `record_gid_index` (`group_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='未读消息记录，客户端上线了重发【超级重要】';

-- ----------------------------
-- Records of skt_record
-- ----------------------------

-- ----------------------------
-- Table structure for `skt_record_alllog`
-- ----------------------------
DROP TABLE IF EXISTS `skt_record_alllog`;
CREATE TABLE `skt_record_alllog` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `talker_id` int(11) NOT NULL COMMENT '说话人id',
  `listener_id` int(11) DEFAULT NULL COMMENT '接收人id',
  `time` bigint(13) NOT NULL COMMENT '聊天时间（精确到毫秒）',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '消息体',
  `group_id` int(11) DEFAULT NULL COMMENT '群id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='全部消息记录【主要用于VIP、店小二处理纠纷，必须用】';

-- ----------------------------
-- Records of skt_record_alllog
-- ----------------------------
INSERT INTO `skt_record_alllog` VALUES ('1', '1', null, '1443521542221', 0xE59388E59388, '1');

-- ----------------------------
-- Table structure for `skt_user`
-- ----------------------------
DROP TABLE IF EXISTS `skt_user`;
CREATE TABLE `skt_user` (
  `auto_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '可以看到用户总数，用户唯一自增长ID',
  `id` int(11) NOT NULL COMMENT '用户id',
  `client_type` varchar(20) NOT NULL COMMENT '客户端类型（这个也是动态的，最后一次是登录的什么手机类型）',
  `device_token` varchar(150) DEFAULT NULL COMMENT '【超级重要】设备码（动态更新，主要用于IOS推送标记）苹果手机全靠这个推送了【一个手机上不同的APP是不一样的】',
  `app_name` varchar(20) NOT NULL COMMENT '程序名称【注意：：这个绝对不能为空！！！否则会导致发信息的人skt登录失败！！！！】',
  `login_time` bigint(13) NOT NULL COMMENT '最后登录时间（精确到毫秒）',
  `is_dev` tinyint(2) DEFAULT '0' COMMENT '是否开发环境1，还是生产环境（is_product），开发环境和生产环境苹果是两套证书存放推送（0和NULL生产环境）',
  `is_push` tinyint(2) DEFAULT '0' COMMENT '【针对IOS】苹果手机注销登陆时告诉我不能推送0，登录时要告诉我可以推送1',
  `badge` int(11) DEFAULT '0' COMMENT '推送气泡数量',
  `name` varchar(255) DEFAULT '' COMMENT '用户昵称',
  PRIMARY KEY (`auto_id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='私信消息库';

-- ----------------------------
-- Records of skt_user
-- ----------------------------
INSERT INTO `skt_user` VALUES ('1', '1', 'ios', '', 'KuaiGou', '1443595722501', '0', '1', '0', '呵呵');
INSERT INTO `skt_user` VALUES ('2', '2', 'ios', '', 'KuaiGou', '1443521524996', '0', '1', '1', '呵呵');
INSERT INTO `skt_user` VALUES ('5', '3', 'ios', '', 'KuaiGou', '1443521564565', '0', '1', '0', '呵呵');
