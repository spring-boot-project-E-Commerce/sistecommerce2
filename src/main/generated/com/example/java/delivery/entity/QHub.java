package com.example.java.delivery.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QHub is a Querydsl query type for Hub
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHub extends EntityPathBase<Hub> {

    private static final long serialVersionUID = -1414572151L;

    public static final QHub hub = new QHub("hub");

    public final StringPath detailAddress = createString("detailAddress");

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final StringPath name = createString("name");

    public final StringPath roadAddress = createString("roadAddress");

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final StringPath zipCode = createString("zipCode");

    public QHub(String variable) {
        super(Hub.class, forVariable(variable));
    }

    public QHub(Path<? extends Hub> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHub(PathMetadata metadata) {
        super(Hub.class, metadata);
    }

}

