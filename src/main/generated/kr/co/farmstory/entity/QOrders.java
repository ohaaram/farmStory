package kr.co.farmstory.entity;

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

    private static final long serialVersionUID = 1013825446L;

    public static final QOrders orders = new QOrders("orders");

    public final StringPath memo = createString("memo");

    public final NumberPath<Integer> orderNo = createNumber("orderNo", Integer.class);

    public final StringPath payment = createString("payment");

    public final DateTimePath<java.time.LocalDateTime> rdate = createDateTime("rdate", java.time.LocalDateTime.class);

    public final StringPath recaddr = createString("recaddr");

    public final StringPath rechp = createString("rechp");

    public final StringPath reciver = createString("reciver");

    public final StringPath status = createString("status");

    public final StringPath uid = createString("uid");

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

