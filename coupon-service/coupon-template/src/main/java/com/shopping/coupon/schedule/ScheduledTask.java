package com.shopping.coupon.schedule;

import com.shopping.coupon.dao.CouponTemplateDao;
import com.shopping.coupon.entity.CouponTemplate;
import com.shopping.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;


@Slf4j
@Component
public class ScheduledTask {

    private CouponTemplateDao templateDao;

    @Autowired
    public ScheduledTask(CouponTemplateDao templateDao) {
        this.templateDao = templateDao;
    }

    @Scheduled(fixedRate = 60 * 60 * 100)
    public void cleanExpiredCouponTemplate() {
        log.info("Start to clean expired coupon template");

        List<CouponTemplate> templates = templateDao.findAllByExpired(false);
        if (CollectionUtils.isEmpty(templates)) {
            log.info("Finish cleaning expired coupon template");
        }

        Date cur = new Date();
        List<CouponTemplate> expireTemplates = new ArrayList<>(templates.size());
        templates.forEach(template -> {
            // check template by rule
            TemplateRule rule = template.getRule();
            if (rule.getExpiration().getExpiredDate() < cur.getTime()) {
                template.setExpired(true);
                expireTemplates.add(template);
            }
        });

        if (CollectionUtils.isNotEmpty(expireTemplates)) {
            log.info("Expired Coupon template Num:{}",
                    templateDao.saveAll(expireTemplates));
        }
        log.info("Finish cleaning expired coupon template");
    }


}
