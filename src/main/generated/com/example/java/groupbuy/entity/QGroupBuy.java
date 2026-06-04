package com.example.java.groupbuy.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QGroupBuy is a Querydsl query type for GroupBuy
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGroupBuy extends EntityPathBase<GroupBuy> {

    private static final long serialVersionUID = 428369702L;

    public static final QGroupBuy groupBuy = new QGroupBuy("groupBuy");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> endAt = createDateTime("endAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> finalPrice = createNumber("finalPrice", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> finishedAt = createDateTime("finishedAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> maxCount = createNumber("maxCount", Integer.class);

    public final NumberPath<Integer> minCount = createNumber("minCount", Integer.class);

    public final NumberPath<Integer> originalPrice = createNumber("originalPrice", Integer.class);

    public final NumberPath<Long> productSeq = createNumber("productSeq", Long.class);

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final DateTimePath<java.time.LocalDateTime> startAt = createDateTime("startAt", java.time.LocalDateTime.class);

    public final EnumPath<GroupBuyStatus> status = createEnum("status", GroupBuyStatus.class);

    public QGroupBuy(String variable) {
        super(GroupBuy.class, forVariable(variable));
    }

    public QGroupBuy(Path<? extends GroupBuy> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGroupBuy(PathMetadata metadata) {
        super(GroupBuy.class, metadata);
    }

}

