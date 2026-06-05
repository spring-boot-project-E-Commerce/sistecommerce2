package com.example.java.orders.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOrders is a Querydsl query type for Orders
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrders extends EntityPathBase<Orders> {

    private static final long serialVersionUID = 785444802L;

    public static final QOrders orders = new QOrders("orders");

    public final StringPath address = createString("address");

    public final StringPath addressDetail = createString("addressDetail");

    public final NumberPath<Integer> couponDiscount = createNumber("couponDiscount", Integer.class);

    public final NumberPath<Double> currLatitude = createNumber("currLatitude", Double.class);

    public final NumberPath<Double> currLongitude = createNumber("currLongitude", Double.class);

    public final StringPath field = createString("field");

    public final NumberPath<Integer> finalPrice = createNumber("finalPrice", Integer.class);

    public final NumberPath<Integer> hotdealDiscount = createNumber("hotdealDiscount", Integer.class);

    public final NumberPath<Long> memberCouponSeq = createNumber("memberCouponSeq", Long.class);

    public final NumberPath<Long> memberSeq = createNumber("memberSeq", Long.class);

    public final DateTimePath<java.time.LocalDateTime> orderDate = createDateTime("orderDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> orderItemSeq = createNumber("orderItemSeq", Long.class);

    public final NumberPath<Integer> orderStatus = createNumber("orderStatus", Integer.class);

    public final StringPath orderUid = createString("orderUid");

    public final NumberPath<Integer> paymentStatus = createNumber("paymentStatus", Integer.class);

    public final NumberPath<Integer> productTotalPrice = createNumber("productTotalPrice", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> regdate = createDateTime("regdate", java.time.LocalDateTime.class);

    public final NumberPath<Integer> remainPrice = createNumber("remainPrice", Integer.class);

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final NumberPath<Integer> totalRefundPrice = createNumber("totalRefundPrice", Integer.class);

    public final StringPath zipcode = createString("zipcode");

    public QOrders(String variable) {
        super(Orders.class, forVariable(variable));
    }

    public QOrders(Path<? extends Orders> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOrders(PathMetadata metadata) {
        super(Orders.class, metadata);
    }

}

