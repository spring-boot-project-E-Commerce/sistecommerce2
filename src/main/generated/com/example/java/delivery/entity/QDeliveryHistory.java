package com.example.java.delivery.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDeliveryHistory is a Querydsl query type for DeliveryHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDeliveryHistory extends EntityPathBase<DeliveryHistory> {

    private static final long serialVersionUID = 2029537940L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDeliveryHistory deliveryHistory = new QDeliveryHistory("deliveryHistory");

    public final DateTimePath<java.time.LocalDateTime> arrivedAt = createDateTime("arrivedAt", java.time.LocalDateTime.class);

    public final NumberPath<Double> currLatitude = createNumber("currLatitude", Double.class);

    public final NumberPath<Double> currLongitude = createNumber("currLongitude", Double.class);

    public final QDelivery delivery;

    public final QHub hub;

    public final StringPath location = createString("location");

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public QDeliveryHistory(String variable) {
        this(DeliveryHistory.class, forVariable(variable), INITS);
    }

    public QDeliveryHistory(Path<? extends DeliveryHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDeliveryHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDeliveryHistory(PathMetadata metadata, PathInits inits) {
        this(DeliveryHistory.class, metadata, inits);
    }

    public QDeliveryHistory(Class<? extends DeliveryHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.delivery = inits.isInitialized("delivery") ? new QDelivery(forProperty("delivery"), inits.get("delivery")) : null;
        this.hub = inits.isInitialized("hub") ? new QHub(forProperty("hub")) : null;
    }

}

