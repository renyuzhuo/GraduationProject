/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50709
Source Host           : localhost:3306
Source Database       : chat

Target Server Type    : MYSQL
Target Server Version : 50709
File Encoding         : 65001

Date: 2016-05-03 22:12:45
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for chat_feedback
-- ----------------------------
DROP TABLE IF EXISTS `chat_feedback`;
CREATE TABLE `chat_feedback` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userinfo_id` int(11) DEFAULT NULL,
  `message` text,
  `time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userinfo_id` (`userinfo_id`),
  CONSTRAINT `chat_feedback_ibfk_1` FOREIGN KEY (`userinfo_id`) REFERENCES `chat_userinfo` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of chat_feedback
-- ----------------------------
INSERT INTO `chat_feedback` VALUES ('17', '26', '最好能访问手机通讯录，自己输入手机号添加好友太不方便了，现在的人绝大多数不会记朋友们的手机号', '2016-04-27 17:29:46');
INSERT INTO `chat_feedback` VALUES ('18', '26', '群组如何创建？', '2016-04-27 17:32:46');
INSERT INTO `chat_feedback` VALUES ('19', '28', ' 反馈内容的字体太大了。。。', '2016-04-28 00:27:01');
INSERT INTO `chat_feedback` VALUES ('20', '24', '字体太大会修改的', '2016-04-28 01:29:51');
INSERT INTO `chat_feedback` VALUES ('21', '24', '现在好友添加支持模糊手机号查询 后期会去掉 如果觉得不方便可以使用二维码添加 通讯录读取也添加到了任务中', '2016-04-28 01:31:36');
INSERT INTO `chat_feedback` VALUES ('22', '24', '默认账号仅用于测试，我不清楚这个是谁反馈的，但是也谢谢了', '2016-04-28 01:32:44');
INSERT INTO `chat_feedback` VALUES ('23', '24', '群组入口暂时未开放', '2016-04-28 01:33:34');
INSERT INTO `chat_feedback` VALUES ('24', '27', '当我清空账号时候，密码设置成跟着清空', '2016-04-28 10:33:21');
INSERT INTO `chat_feedback` VALUES ('25', '27', '在界面上默认的添几个好友，和群组会好玩些', '2016-04-28 10:35:11');
INSERT INTO `chat_feedback` VALUES ('26', '24', '理由反馈的有道理 不过还没有做完 还有就是我知道理由是谁', '2016-04-28 10:47:59');
INSERT INTO `chat_feedback` VALUES ('27', '24', '测试账号下周一关闭', '2016-04-28 10:48:53');

-- ----------------------------
-- Table structure for chat_friend
-- ----------------------------
DROP TABLE IF EXISTS `chat_friend`;
CREATE TABLE `chat_friend` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `friend_me` int(11) NOT NULL COMMENT 'friend1',
  `friend_you` int(11) NOT NULL COMMENT 'friend2',
  `time` datetime NOT NULL COMMENT '时间',
  `state` varchar(30) NOT NULL COMMENT 'friend:朋友\r\ninvite:发出邀请\r\nrefuse:拒绝\r\nrejected:被拒绝\r\nunfriend:拉黑',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `friend1` (`friend_me`),
  KEY `friend2` (`friend_you`),
  CONSTRAINT `friend1` FOREIGN KEY (`friend_me`) REFERENCES `chat_userinfo` (`id`),
  CONSTRAINT `friend2` FOREIGN KEY (`friend_you`) REFERENCES `chat_userinfo` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=96 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of chat_friend
-- ----------------------------
INSERT INTO `chat_friend` VALUES ('53', '26', '24', '2016-04-27 17:30:44', 'friend', '任玉琢');
INSERT INTO `chat_friend` VALUES ('54', '28', '24', '2016-04-28 00:25:29', 'friend', '任玉琢');
INSERT INTO `chat_friend` VALUES ('74', '28', '26', '2016-04-28 08:21:46', 'friend', '达达天下V');
INSERT INTO `chat_friend` VALUES ('75', '28', '27', '2016-04-28 08:21:50', 'friend', '…理由');
INSERT INTO `chat_friend` VALUES ('76', '28', '25', '2016-04-28 08:22:33', 'friend', '25');
INSERT INTO `chat_friend` VALUES ('79', '24', '25', '2016-04-28 10:39:18', 'friend', '25');
INSERT INTO `chat_friend` VALUES ('80', '27', '28', '2016-04-28 10:42:00', 'friend', '');
INSERT INTO `chat_friend` VALUES ('81', '27', '24', '2016-04-28 10:42:01', 'friend', '');
INSERT INTO `chat_friend` VALUES ('86', '24', '26', '2016-05-03 11:39:41', 'friend', '达达天下V');
INSERT INTO `chat_friend` VALUES ('87', '24', '28', '2016-05-03 11:39:45', 'friend', 'zzucwy');
INSERT INTO `chat_friend` VALUES ('88', '24', '29', '2016-05-03 11:39:47', 'friend', 'eamonn');
INSERT INTO `chat_friend` VALUES ('89', '24', '30', '2016-05-03 11:39:49', 'friend', '任玉琢测试号');
INSERT INTO `chat_friend` VALUES ('93', '23', '24', '2016-05-03 12:53:05', 'friend', '任玉琢');
INSERT INTO `chat_friend` VALUES ('94', '24', '23', '2016-05-03 12:53:43', 'friend', '备注');
INSERT INTO `chat_friend` VALUES ('95', '24', '27', '2016-05-03 21:30:18', 'friend', '…理由');

-- ----------------------------
-- Table structure for chat_friend_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_friend_message`;
CREATE TABLE `chat_friend_message` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id主键',
  `fromuser` int(11) NOT NULL COMMENT '发送者',
  `touser` int(11) NOT NULL COMMENT '接受者',
  `message` text CHARACTER SET utf8mb4 COMMENT '消息',
  `time` datetime DEFAULT NULL COMMENT '消息发送时间',
  `type` int(11) NOT NULL COMMENT '消息类型',
  `path` varchar(255) DEFAULT NULL COMMENT '非文本消息保存路径',
  `fromstate` varchar(10) DEFAULT NULL,
  `tostate` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fromuser` (`fromuser`),
  KEY `touser` (`touser`),
  KEY `type` (`type`),
  CONSTRAINT `fromuser` FOREIGN KEY (`fromuser`) REFERENCES `chat_userinfo` (`id`),
  CONSTRAINT `touser` FOREIGN KEY (`touser`) REFERENCES `chat_userinfo` (`id`),
  CONSTRAINT `type` FOREIGN KEY (`type`) REFERENCES `chat_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of chat_friend_message
-- ----------------------------
INSERT INTO `chat_friend_message` VALUES ('1', '23', '24', '', '2016-05-02 11:37:28', '3', 'http://o6gbyli25.bkt.clouddn.com/Fm9kTqNm_gAVumt6CcViq6lYojor', 'read', 'read');
INSERT INTO `chat_friend_message` VALUES ('2', '24', '23', '还是感受感受', '2016-05-03 12:52:15', '1', '', 'read', 'read');
INSERT INTO `chat_friend_message` VALUES ('3', '23', '24', '哈哈哈', '2016-05-03 12:53:14', '1', '', 'read', 'read');

-- ----------------------------
-- Table structure for chat_register
-- ----------------------------
DROP TABLE IF EXISTS `chat_register`;
CREATE TABLE `chat_register` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `phoneNumber` varchar(13) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of chat_register
-- ----------------------------
INSERT INTO `chat_register` VALUES ('8', '15538398960', '2016-04-27 22:54:30', '870142');

-- ----------------------------
-- Table structure for chat_room
-- ----------------------------
DROP TABLE IF EXISTS `chat_room`;
CREATE TABLE `chat_room` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id主键',
  `team` int(11) NOT NULL COMMENT 'teamid',
  `user` int(11) NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`),
  KEY `user` (`user`),
  KEY `team` (`team`),
  CONSTRAINT `team` FOREIGN KEY (`team`) REFERENCES `chat_team` (`id`),
  CONSTRAINT `user` FOREIGN KEY (`user`) REFERENCES `chat_userinfo` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of chat_room
-- ----------------------------
INSERT INTO `chat_room` VALUES ('7', '3', '24');
INSERT INTO `chat_room` VALUES ('8', '3', '25');
INSERT INTO `chat_room` VALUES ('9', '3', '26');
INSERT INTO `chat_room` VALUES ('10', '3', '28');
INSERT INTO `chat_room` VALUES ('11', '3', '29');
INSERT INTO `chat_room` VALUES ('12', '3', '30');
INSERT INTO `chat_room` VALUES ('13', '3', '23');
INSERT INTO `chat_room` VALUES ('14', '3', '27');

-- ----------------------------
-- Table structure for chat_team
-- ----------------------------
DROP TABLE IF EXISTS `chat_team`;
CREATE TABLE `chat_team` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) NOT NULL COMMENT '群组名称',
  `heap` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL COMMENT '0.公开，1.非公开',
  `creater` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `creater` (`creater`),
  CONSTRAINT `chat_team_ibfk_1` FOREIGN KEY (`creater`) REFERENCES `chat_userinfo` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of chat_team
-- ----------------------------
INSERT INTO `chat_team` VALUES ('1', 'Chat用户公开默认群', 'http://7xswvn.com1.z0.glb.clouddn.com/Fpi_OLpXsdgVraF8TNuZmOPyXWZ8', '0', null);
INSERT INTO `chat_team` VALUES ('3', '测试私有群1', 'http://7xswvn.com1.z0.glb.clouddn.com/FldzuNJd467Ygc5biwYovgj1_8JA', '1', null);

-- ----------------------------
-- Table structure for chat_team_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_team_message`;
CREATE TABLE `chat_team_message` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `fromuser` int(11) NOT NULL COMMENT '发消息的用户',
  `toteam` int(11) NOT NULL COMMENT '发送到的群组',
  `message` text COMMENT '发送的文本消息',
  `time` datetime DEFAULT NULL COMMENT '发送时间',
  `type` int(11) NOT NULL COMMENT '消息类型',
  `path` varchar(255) DEFAULT NULL COMMENT '非文本消息文件路径',
  PRIMARY KEY (`id`),
  KEY `team_fromuser` (`fromuser`),
  KEY `toteam` (`toteam`),
  KEY `type2` (`type`),
  CONSTRAINT `team_fromuser` FOREIGN KEY (`fromuser`) REFERENCES `chat_userinfo` (`id`),
  CONSTRAINT `toteam` FOREIGN KEY (`toteam`) REFERENCES `chat_team` (`id`),
  CONSTRAINT `type2` FOREIGN KEY (`type`) REFERENCES `chat_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of chat_team_message
-- ----------------------------
INSERT INTO `chat_team_message` VALUES ('27', '24', '1', '6666', '2016-05-02 16:05:32', '1', '');
INSERT INTO `chat_team_message` VALUES ('28', '24', '1', '哈哈哈哈哈', '2016-05-02 16:05:37', '1', '');
INSERT INTO `chat_team_message` VALUES ('29', '24', '1', '', '2016-05-02 16:06:08', '2', 'http://7xswvn.com1.z0.glb.clouddn.com/Fs_cpBLQrK_UqMsRgZ7hBAC8tnsC');
INSERT INTO `chat_team_message` VALUES ('30', '24', '1', '', '2016-05-02 16:39:38', '3', 'http://o6gbyli25.bkt.clouddn.com/FsN7rQV12bL75aJKMJ0pu8SACDp5');
INSERT INTO `chat_team_message` VALUES ('31', '24', '3', '', '2016-05-02 16:40:20', '3', 'http://o6gbyli25.bkt.clouddn.com/FoXhGwx9nrHLQwkHGwx6ztSX7eeQ');
INSERT INTO `chat_team_message` VALUES ('32', '30', '1', '哈哈', '2016-05-02 16:54:48', '1', '');
INSERT INTO `chat_team_message` VALUES ('33', '23', '1', '洋洋默认号', '2016-05-02 16:56:43', '1', '');
INSERT INTO `chat_team_message` VALUES ('34', '30', '1', '哈哈哈哈哈', '2016-05-02 16:58:01', '1', '');
INSERT INTO `chat_team_message` VALUES ('35', '24', '1', '哈哈哈哈哈', '2016-05-02 17:11:15', '1', '');
INSERT INTO `chat_team_message` VALUES ('36', '24', '1', '', '2016-05-02 17:11:49', '2', 'http://7xswvn.com1.z0.glb.clouddn.com/FsKjaUN8YHKknVRtRNmXe0Z_EhjW');

-- ----------------------------
-- Table structure for chat_type
-- ----------------------------
DROP TABLE IF EXISTS `chat_type`;
CREATE TABLE `chat_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id主键',
  `name` varchar(30) NOT NULL COMMENT '类别名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of chat_type
-- ----------------------------
INSERT INTO `chat_type` VALUES ('1', '文本');
INSERT INTO `chat_type` VALUES ('2', '图片');
INSERT INTO `chat_type` VALUES ('3', '音乐');
INSERT INTO `chat_type` VALUES ('4', '文件');

-- ----------------------------
-- Table structure for chat_userinfo
-- ----------------------------
DROP TABLE IF EXISTS `chat_userinfo`;
CREATE TABLE `chat_userinfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id，主键',
  `username` varchar(30) NOT NULL COMMENT '登陆用户名',
  `password` varchar(30) NOT NULL COMMENT '登陆密码',
  `nickname` varchar(255) DEFAULT NULL COMMENT '昵称',
  `heap` varchar(255) DEFAULT NULL COMMENT '头像',
  `type` int(255) NOT NULL DEFAULT '1' COMMENT '用户类别，0系统管理员，1普通用户',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of chat_userinfo
-- ----------------------------
INSERT INTO `chat_userinfo` VALUES ('23', '15617867618', '15617867618', '测试私密账号', 'http://7xswvn.com1.z0.glb.clouddn.com/FpW3G7SixZgIztjIo6M2Wi0b2osM', '0');
INSERT INTO `chat_userinfo` VALUES ('24', '18337136329', '18337136329', '任玉琢', 'http://7xswvn.com1.z0.glb.clouddn.com/Flu3cVFHfbH8NxVcjGBmfhQqDBF8', '0');
INSERT INTO `chat_userinfo` VALUES ('25', '18300676288', '18300676288', '', null, '1');
INSERT INTO `chat_userinfo` VALUES ('26', '15236627089', '12315whd', '达达天下V', null, '1');
INSERT INTO `chat_userinfo` VALUES ('27', '15538398960', '15538398960', '…理由', null, '1');
INSERT INTO `chat_userinfo` VALUES ('28', '18627867808', '18337104183chen', 'zzucwy', null, '1');
INSERT INTO `chat_userinfo` VALUES ('29', '13849076163', 'jcznnx361du', 'eamonn', null, '1');
INSERT INTO `chat_userinfo` VALUES ('30', '18337136328', '18337136328', '任玉琢测试号', 'http://7xswvn.com1.z0.glb.clouddn.com/FlBE38EBqYmTUpwtJpiYFUo3CKhS', '1');
