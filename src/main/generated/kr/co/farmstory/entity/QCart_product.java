package kr.co.farmstory.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCart_product is a Querydsl query type for Cart_product
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCart_product extends EntityPathBase<Cart_product> {

    private static final long serialVersionUID = -887433519L;

    public static final QCart_product cart_product = new QCart_product("cart_product");

    public final NumberPath<Integer> cart_prodNo = createNumber("cart_prodNo", Integer.class);

    public final NumberPath<Integer> cartNo = createNumber("cartNo", Integer.class);

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    public final NumberPath<Integer> prodNo = createNumber("prodNo", Integer.class);

    public QCart_product(String variable) {
        super(Cart_product.class, forVariable(variable));
    }

    public QCart_product(Path<? extends Cart_product> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCart_product(PathMetadata metadata) {
        super(Cart_product.class, metadata);
    }

}

