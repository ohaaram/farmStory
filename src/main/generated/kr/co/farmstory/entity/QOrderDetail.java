package kr.co.farmstory.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOrderDetail is a Querydsl query type for OrderDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderDetail extends EntityPathBase<OrderDetail> {

    private static final long serialVersionUID = 1042795806L;

    public static final QOrderDetail orderDetail = new QOrderDetail("orderDetail");

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    public final NumberPath<Integer> detailno = createNumber("detailno", Integer.class);

    public final NumberPath<Integer> orderNo = createNumber("orderNo", Integer.class);

    public final NumberPath<Integer> prodno = createNumber("prodno", Integer.class);

    public QOrderDetail(String variable) {
        super(OrderDetail.class, forVariable(variable));
    }

    public QOrderDetail(Path<? extends OrderDetail> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOrderDetail(PathMetadata metadata) {
        super(OrderDetail.class, metadata);
    }

}

