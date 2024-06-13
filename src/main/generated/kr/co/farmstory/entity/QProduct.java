package kr.co.farmstory.entity;

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

    private static final long serialVersionUID = -2033514418L;

    public static final QProduct product = new QProduct("product");

    public final NumberPath<Integer> amount = createNumber("amount", Integer.class);

    public final StringPath cate = createString("cate");

    public final NumberPath<Integer> delCost = createNumber("delCost", Integer.class);

    public final NumberPath<Integer> delType = createNumber("delType", Integer.class);

    public final NumberPath<Integer> discount = createNumber("discount", Integer.class);

    public final StringPath etc = createString("etc");

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final StringPath prodname = createString("prodname");

    public final NumberPath<Integer> prodno = createNumber("prodno", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> rdate = createDateTime("rdate", java.time.LocalDateTime.class);

    public final NumberPath<Integer> recount = createNumber("recount", Integer.class);

    public final NumberPath<Integer> stock = createNumber("stock", Integer.class);

    public final StringPath thumb = createString("thumb");

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

