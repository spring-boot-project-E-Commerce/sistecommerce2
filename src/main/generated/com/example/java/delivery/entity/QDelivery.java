package com.example.java.delivery.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDelivery is a Querydsl query type for Delivery
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDelivery extends EntityPathBase<Delivery> {

    private static final long serialVersionUID = -767699488L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDelivery delivery = new QDelivery("delivery");

    public final DateTimePath<java.time.LocalDateTime> completed_at = createDateTime("completed_at", java.time.LocalDateTime.class);

    public final NumberPath<Integer> delayHours = createNumber("delayHours", Integer.class);

    public final QDeliveryCompany deliveryCompany;

    public final DateTimePath<java.time.LocalDateTime> dispatch_at = createDateTime("dispatch_at", java.time.LocalDateTime.class);

    public final NumberPath<Integer> distance_surcharge = createNumber("distance_surcharge", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> estimated_date = createDateTime("estimated_date", java.time.LocalDateTime.class);

    public final com.example.java.orders.controller.entity.QOrders orders;

    public final NumberPath<Long> purchaseOrderSeq = createNumber("purchaseOrderSeq", Long.class);

    public final StringPath recipient_name = createString("recipient_name");

    public final StringPath recipient_phone = createString("recipient_phone");

    public final StringPath request_memo = createString("request_memo");

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final StringPath status = createString("status");

    public final NumberPath<Integer> total_delivery_fee = createNumber("total_delivery_fee", Integer.class);

    public final StringPath tracking_number = createString("tracking_number");

    public QDelivery(String variable) {
        this(Delivery.class, forVariable(variable), INITS);
    }

    public QDelivery(Path<? extends Delivery> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDelivery(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDelivery(PathMetadata metadata, PathInits inits) {
        this(Delivery.class, metadata, inits);
    }

    public QDelivery(Class<? extends Delivery> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.deliveryCompany = inits.isInitialized("deliveryCompany") ? new QDeliveryCompany(forProperty("deliveryCompany")) : null;
        this.orders = inits.isInitialized("orders") ? new com.example.java.orders.controller.entity.QOrders(forProperty("orders")) : null;
    }

}

