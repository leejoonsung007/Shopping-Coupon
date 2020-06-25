package com.shopping.coupon.service.impl;

import com.shopping.coupon.dao.CouponTemplateDao;
import com.shopping.coupon.entity.CouponTemplate;
import com.shopping.coupon.exception.CouponException;
import com.shopping.coupon.service.ITemplateBaseService;
import com.shopping.coupon.vo.CouponTemplateSDK;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TemplateBaseServiceImpl implements ITemplateBaseService {

    private CouponTemplateDao couponTemplateDao;

    @Autowired
    public TemplateBaseServiceImpl(CouponTemplateDao couponTemplateDao) {
        this.couponTemplateDao = couponTemplateDao;
    }

    @Override
    public CouponTemplate findCouponTemplateInfo(Integer id) throws CouponException {
        Optional<CouponTemplate> template = couponTemplateDao.findById(id);
        if(!template.isPresent()) {
            throw new CouponException("Template Is Not Exist " + id);
        }
        return template.get();
    }

    @Override
    public List<CouponTemplateSDK> findAllAvailableTemplates() {
        List<CouponTemplate> templates = couponTemplateDao
                .findAllByAvailableAndExpired(true, false);

        return templates.stream().map(this::template2TemplateSDK).collect(Collectors.toList());
    }

    @Override
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids) {
        List<CouponTemplate> templates = couponTemplateDao.findAllById(ids);

        return templates.stream()
                .map(this::template2TemplateSDK)
                .collect(Collectors.toMap(CouponTemplateSDK::getId, Function.identity()));
    }

    /**
     * convert coupon template to coupon sdk
     * @param template
     * @return
     */
    private CouponTemplateSDK template2TemplateSDK(CouponTemplate template) {
        return new CouponTemplateSDK(
                template.getId(),
                template.getName(),
                template.getLogo(),
                template.getDescription(),
                template.getCategory().getCode(),
                template.getPlatform().getCode(),
                template.getKey(),
                template.getTarget().getCode(),
                template.getRule()
        );
    }
}
