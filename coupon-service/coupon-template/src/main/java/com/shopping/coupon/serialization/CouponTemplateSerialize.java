package com.shopping.coupon.serialization;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.shopping.coupon.entity.CouponTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class CouponTemplateSerialize extends JsonSerializer<CouponTemplate> {

    @Override
    public void serialize(CouponTemplate template, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        // start
        generator.writeStartObject();

        generator.writeStringField("id", template.getId().toString());
        generator.writeStringField("name", template.getName());
        generator.writeStringField("logo", template.getLogo());
        generator.writeStringField("description", template.getDescription());
        generator.writeStringField("category", template.getCategory().getDescription());
        generator.writeStringField("platform", template.getCategory().getDescription());
        generator.writeStringField("count", template.getCategory().toString());
        generator.writeStringField("createTime",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(template.getCreateTime()));
        generator.writeStringField("userId", template.getUserId().toString());
        generator.writeStringField("key", template.getUserId() + String.format("%4d", template.getId()));
        generator.writeStringField("target", template.getTarget().getDescription());
        generator.writeStringField("rule", JSON.toJSONString(template.getRule()));

        //end
        generator.writeEndObject();
    }
}
