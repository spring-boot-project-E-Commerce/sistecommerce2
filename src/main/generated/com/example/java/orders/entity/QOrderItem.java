package com.example.java.orders.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOrderItem is a Querydsl query type for OrderItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderItem extends EntityPathBase<OrderItem> {

    private static final long serialVersionUID = 203131268L;

    public static final QOrderItem orderItem = new QOrderItem("orderItem");

    public final NumberPath<Integer> couponDiscount = createNumber("couponDiscount", Integer.class);

    public final NumberPath<Integer> finalPrice = createNumber("finalPrice", Integer.class);

    public final NumberPath<Integer> hotdealDiscount = createNumber("hotdealDiscount", Integer.class);

    public final NumberPath<Integer> itemStatus = createNumber("itemStatus", Integer.class);

    public final NumberPath<Long> optionsSeq = createNumber("optionsSeq", Long.class);

    public final NumberPath<Integer> originalPrice = createNumber("originalPrice", Integer.class);

    public final NumberPath<Integer> participationDiscount = createNumber("participationDiscount", Integer.class);

    public final NumberPath<Long> participationSeq = createNumber("participationSeq", Long.class);

    public final StringPath productName = createString("productName");

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final NumberPath<Integer> refundPrice = createNumber("refundPrice", Integer.class);

    public final NumberPath<Integer> refundQuantity = createNumber("refundQuantity", Integer.class);

    public final NumberPath<Integer> returnQuantity = createNumber("returnQuantity", Integer.class);

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final NumberPath<Integer> subTotalPrice = createNumber("subTotalPrice", Integer.class);

    public QOrderItem(String variable) {
        super(OrderItem.class, forVariable(variable));
    }

    public QOrderItem(Path<? extends OrderItem> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOrderItem(PathMetadata metadata) {
        super(OrderItem.class, metadata);
    }

}

