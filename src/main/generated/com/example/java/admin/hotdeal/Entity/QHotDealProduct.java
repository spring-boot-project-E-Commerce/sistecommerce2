package com.example.java.admin.hotdeal.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHotDealProduct is a Querydsl query type for HotDealProduct
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHotDealProduct extends EntityPathBase<HotDealProduct> {

    private static final long serialVersionUID = -1940541326L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHotDealProduct hotDealProduct = new QHotDealProduct("hotDealProduct");

    public final QHotDeal hotDeal;

    public final NumberPath<Integer> hotDealStock = createNumber("hotDealStock", Integer.class);

    public final com.example.java.product.entity.QOptions options;

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final NumberPath<Integer> soldQuantity = createNumber("soldQuantity", Integer.class);

    public QHotDealProduct(String variable) {
        this(HotDealProduct.class, forVariable(variable), INITS);
    }

    public QHotDealProduct(Path<? extends HotDealProduct> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHotDealProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHotDealProduct(PathMetadata metadata, PathInits inits) {
        this(HotDealProduct.class, metadata, inits);
    }

    public QHotDealProduct(Class<? extends HotDealProduct> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.hotDeal = inits.isInitialized("hotDeal") ? new QHotDeal(forProperty("hotDeal")) : null;
        this.options = inits.isInitialized("options") ? new com.example.java.product.entity.QOptions(forProperty("options")) : null;
    }

}

