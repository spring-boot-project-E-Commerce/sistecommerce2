package com.example.java.product.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProduct is a Querydsl query type for Product
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProduct extends EntityPathBase<Product> {

    private static final long serialVersionUID = -849902210L;

    public static final QProduct product = new QProduct("product");

    public final StringPath approvalStatus = createString("approvalStatus");

    public final NumberPath<Double> avgRating = createNumber("avgRating", Double.class);

    public final NumberPath<Long> categorySeq = createNumber("categorySeq", Long.class);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final StringPath hideYn = createString("hideYn");

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final StringPath productName = createString("productName");

    public final NumberPath<Long> reviewCount = createNumber("reviewCount", Long.class);

    public final NumberPath<Long> salesCount = createNumber("salesCount", Long.class);

    public final StringPath saleStatus = createString("saleStatus");

    public final NumberPath<Long> sellerSeq = createNumber("sellerSeq", Long.class);

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final StringPath status = createString("status");

    public final DateTimePath<java.time.LocalDateTime> updatedDate = createDateTime("updatedDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> viewCount = createNumber("viewCount", Long.class);

    public QProduct(String variable) {
        super(Product.class, forVariable(variable));
    }

    public QProduct(Path<? extends Product> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProduct(PathMetadata metadata) {
        super(Product.class, metadata);
    }

}

