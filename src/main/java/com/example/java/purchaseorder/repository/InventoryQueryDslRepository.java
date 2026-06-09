package com.example.java.purchaseorder.repository;

import static com.example.java.product.entity.QOptions.options;
import static com.example.java.product.entity.QProduct.product;
import static com.example.java.product.entity.QSeller.seller;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.java.product.entity.Options;
import com.example.java.product.entity.Product;
import com.example.java.purchaseorder.dto.InventoryListDTO;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class InventoryQueryDslRepository {

	private final JPAQueryFactory queryFactory;
	
	public List<InventoryListDTO> findInventoryList() {

	    List<Tuple> results = queryFactory
	            .select(options, seller.name)
	            .from(options)
	            .join(options.product, product).fetchJoin()
	            .join(seller)
	                .on(product.sellerSeq.eq(seller.seq))
	            .orderBy(options.seq.desc())
	            .fetch();

	    return results.stream()
	            .map(tuple -> {

	                Options option = tuple.get(options);
	                Product product = option.getProduct();

	                return InventoryListDTO.builder()
	                        .optionsSeq(option.getSeq())
	                        .saleStatus(product.getSaleStatus())
	                        .productName(product.getProductName())
	                        .price(product.getPrice() + option.getAdditionalPrice())
	                        .optionsName(option.getDisplayName())
	                        .stock(option.getStock())
	                        .safetyStock(option.getSafetyStock())
	                        .sellerName(tuple.get(seller.name))
	                        .createdDate(product.getCreatedDate())
	                        .build();
	            })
	            .toList();
	}
}
