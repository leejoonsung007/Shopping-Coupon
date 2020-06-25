package com.shopping.coupon.service.impl;

import com.shopping.coupon.dao.CouponTemplateDao;
import com.shopping.coupon.entity.CouponTemplate;
import com.shopping.coupon.exception.CouponException;
import com.shopping.coupon.service.IAsyncCreateCouponCodeService;
import com.shopping.coupon.service.ICreateTemplateService;
import com.shopping.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CreateTemplateServiceImpl implements ICreateTemplateService {

    private IAsyncCreateCouponCodeService asyncService;

    private CouponTemplateDao templateDao;

    @Autowired
    public CreateTemplateServiceImpl(IAsyncCreateCouponCodeService asyncService,
                                     CouponTemplateDao templateDao) {
        this.asyncService = asyncService;
        this.templateDao = templateDao;
    }

    @Override
    public CouponTemplate createTemplate(TemplateRequest request) throws CouponException {

        // Validate request
        if (!request.validate()) {
            //TODO indicate which param is not valid
            throw new CouponException("BuildTemplate Param Is not Valid");
        }

        if (templateDao.findByName(request.getName()) != null) {
            throw new CouponException("Coupon Template is already existed");
        }

        // Create coupon template and then save to database
        CouponTemplate template = request2Template(request);
        template = templateDao.save(template);

        // base on coupon template to generate coupon code
        asyncService.asyncCreateCouponCode(template);

        return template;
    }

    private CouponTemplate request2Template(TemplateRequest request) {
        return new CouponTemplate(request.getName(),
                request.getLogo(),
                request.getDescription(),
                request.getCategory(),
                request.getPlatform(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule());
    }
}
