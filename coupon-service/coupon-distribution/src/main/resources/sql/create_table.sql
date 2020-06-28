-- Create Coupon Table
CREATE TABLE IF NOT EXISTS `coupon_data`.`coupon` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Increment Primary Key',
  `template_id` int(11) NOT NULL DEFAULT '0' COMMENT 'Coupon Template Id',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'User Who Collect the Coupon',
  `coupon_code` varchar(64) NOT NULL DEFAULT '' COMMENT 'Coupon Code',
  `assign_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT 'Collect Time',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT 'Coupon Status',
  PRIMARY KEY (`id`),
  KEY `idx_template_id` (`template_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='Coupon(Users Collect Coupon Record)';

-- Clear Table
-- truncate coupon;