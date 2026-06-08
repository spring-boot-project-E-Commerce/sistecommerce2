package com.example.java.admin.hotdeal.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QHotDeal is a Querydsl query type for HotDeal
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHotDeal extends EntityPathBase<HotDeal> {

    private static final long serialVersionUID = 2111387069L;

    public static final QHotDeal hotDeal = new QHotDeal("hotDeal");

    public final NumberPath<Integer> discountPrice = createNumber("discountPrice", Integer.class);

    public final NumberPath<Integer> discountRate = createNumber("discountRate", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public final NumberPath<Integer> status = createNumber("status", Integer.class);

    public QHotDeal(String variable) {
        super(HotDeal.class, forVariable(variable));
    }

    public QHotDeal(Path<? extends HotDeal> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHotDeal(PathMetadata metadata) {
        super(HotDeal.class, metadata);
    }

}

