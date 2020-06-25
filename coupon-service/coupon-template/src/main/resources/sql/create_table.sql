-- create coupon_template data table
CREATE TABLE IF NOT EXISTS `coupon_data`.`coupon_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Increment primary key',
  `available` boolean NOT NULL DEFAULT false COMMENT 'Whether it is available; true: available, false: not available',
  `expired` boolean NOT NULL DEFAULT false COMMENT 'Whether expired; true: yes, false: no',
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT 'Coupon Name',
  `logo` varchar(256) NOT NULL DEFAULT '' COMMENT 'Coupon Logo',
  `intro` varchar(256) NOT NULL DEFAULT '' COMMENT 'Coupon Description',
  `category` varchar(64) NOT NULL DEFAULT '' COMMENT 'Coupon Category',
  `platform` int(11) NOT NULL DEFAULT '0' COMMENT 'Platform',
  `coupon_count` int(11) NOT NULL DEFAULT '0' COMMENT 'Count',
  `create_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT 'Create Time',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'User ID',
  `template_key` varchar(128) NOT NULL DEFAULT '' COMMENT 'Coupon Template key',
  `target` int(11) NOT NULL DEFAULT '0' COMMENT 'Target User',
  `rule` varchar(1024) NOT NULL DEFAULT '' COMMENT 'Coupon Template Rule json',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_user_id` (`user_id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='Coupon Template table';

-- Clear table data
-- truncate coupon_template;
