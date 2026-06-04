package com.example.java.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 313073260L;

    public static final QMember member = new QMember("member1");

    public final StringPath address = createString("address");

    public final StringPath addressDetail = createString("addressDetail");

    public final DatePath<java.time.LocalDate> birth = createDate("birth", java.time.LocalDate.class);

    public final StringPath ci = createString("ci");

    public final StringPath di = createString("di");

    public final StringPath email = createString("email");

    public final StringPath emailVerified = createString("emailVerified");

    public final StringPath gender = createString("gender");

    public final DateTimePath<java.time.LocalDateTime> joinedAt = createDateTime("joinedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> lastLoginAt = createDateTime("lastLoginAt", java.time.LocalDateTime.class);

    public final StringPath loginType = createString("loginType");

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final StringPath phone = createString("phone");

    public final StringPath phoneVerified = createString("phoneVerified");

    public final DateTimePath<java.time.LocalDateTime> pwChangedAt = createDateTime("pwChangedAt", java.time.LocalDateTime.class);

    public final StringPath role = createString("role");

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final NumberPath<Integer> status = createNumber("status", Integer.class);

    public final StringPath totp = createString("totp");

    public final StringPath twoFactor = createString("twoFactor");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final StringPath username = createString("username");

    public final DateTimePath<java.time.LocalDateTime> withdrawalRequestedAt = createDateTime("withdrawalRequestedAt", java.time.LocalDateTime.class);

    public final StringPath zipcode = createString("zipcode");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

