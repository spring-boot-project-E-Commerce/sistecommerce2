package com.example.java.product.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOptions is a Querydsl query type for Options
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOptions extends EntityPathBase<Options> {

    private static final long serialVersionUID = -1789903059L;

    public static final QOptions options = new QOptions("options");

    public final NumberPath<Integer> additionalPrice = createNumber("additionalPrice", Integer.class);

    public final StringPath color = createString("color");

    public final StringPath connectionType = createString("connectionType");

    public final StringPath materialType = createString("materialType");

    public final StringPath memory = createString("memory");

    public final StringPath optionsSize = createString("optionsSize");

    public final StringPath optionsType = createString("optionsType");

    public final NumberPath<Long> productSeq = createNumber("productSeq", Long.class);

    public final StringPath quantitySet = createString("quantitySet");

    public final NumberPath<Integer> safetyStock = createNumber("safetyStock", Integer.class);

    public final StringPath scentIngredient = createString("scentIngredient");

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final StringPath sizeSpec = createString("sizeSpec");

    public final NumberPath<Integer> stock = createNumber("stock", Integer.class);

    public final StringPath storageCapacity = createString("storageCapacity");

    public final StringPath storageType = createString("storageType");

    public final StringPath switchAxis = createString("switchAxis");

    public final StringPath taste = createString("taste");

    public final StringPath voltage = createString("voltage");

    public final StringPath volumeWeight = createString("volumeWeight");

    public final StringPath wearableSpec = createString("wearableSpec");

    public QOptions(String variable) {
        super(Options.class, forVariable(variable));
    }

    public QOptions(Path<? extends Options> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOptions(PathMetadata metadata) {
        super(Options.class, metadata);
    }

}

