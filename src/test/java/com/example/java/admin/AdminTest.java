package com.example.java.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.java.admin.repository.AdminProductQueryRepository;
import com.example.java.product.entity.Product;

@SpringBootTest(
	properties = {
        "spring.batch.job.enabled=false"
    }
)
public class AdminTest {

	@Autowired
	private AdminProductQueryRepository productQueryRepository;
	
	@Test
	void queryDslTest() {
		Product product = productQueryRepository.QueryDslTest(1051L);
		System.out.println(product.getProductName());
		System.out.println(product.getSellerSeq());
		System.out.println(product.getCategorySeq());
	}
}
