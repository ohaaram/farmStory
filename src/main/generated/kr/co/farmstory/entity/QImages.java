package kr.co.farmstory.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QImages is a Querydsl query type for Images
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QImages extends EntityPathBase<Images> {

    private static final long serialVersionUID = 837345081L;

    public static final QImages images = new QImages("images");

    public final NumberPath<Integer> imgNo = createNumber("imgNo", Integer.class);

    public final NumberPath<Integer> prodno = createNumber("prodno", Integer.class);

    public final StringPath thumb240 = createString("thumb240");

    public final StringPath thumb750 = createString("thumb750");

    public QImages(String variable) {
        super(Images.class, forVariable(variable));
    }

    public QImages(Path<? extends Images> path) {
        super(path.getType(), path.getMetadata());
    }

    public QImages(PathMetadata metadata) {
        super(Images.class, metadata);
    }

}

