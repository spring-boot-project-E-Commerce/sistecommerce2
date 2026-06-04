package com.example.java.delivery.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDeliveryCompany is a Querydsl query type for DeliveryCompany
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDeliveryCompany extends EntityPathBase<DeliveryCompany> {

    private static final long serialVersionUID = 2053087869L;

    public static final QDeliveryCompany deliveryCompany = new QDeliveryCompany("deliveryCompany");

    public final NumberPath<Integer> base_delivery_fee = createNumber("base_delivery_fee", Integer.class);

    public final StringPath customer_service_phone = createString("customer_service_phone");

    public final NumberPath<Integer> monthly_fee = createNumber("monthly_fee", Integer.class);

    public final StringPath name = createString("name");

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public QDeliveryCompany(String variable) {
        super(DeliveryCompany.class, forVariable(variable));
    }

    public QDeliveryCompany(Path<? extends DeliveryCompany> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDeliveryCompany(PathMetadata metadata) {
        super(DeliveryCompany.class, metadata);
    }

}

