package com.github.nut077.springninja;

import com.github.nut077.springninja.entity.Order;
import com.github.nut077.springninja.entity.OrderId;
import com.github.nut077.springninja.entity.Product;
import com.github.nut077.springninja.repository.OrderRepository;
import com.github.nut077.springninja.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
@Log4j2
public class SpringninjaApplication implements CommandLineRunner {

	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;

	public static void main(String[] args) {
		SpringApplication.run(SpringninjaApplication.class, args);
	}

	@Override
	public void run(String... args) {
		//testImmutable();
		//testListener();
		testDynamicInsertAndUpdate();
	}

	private void testDynamicInsertAndUpdate() {
		Order o1 = new Order();
		o1.setOrderId(new OrderId(1L, 1L));
		o1.setQuantity(2);
		o1 = orderRepository.save(o1);
		o1.setDetails("new Detail");
		orderRepository.save(o1);
	}

	private void testListener() {
		Product p1 = new Product();
		p1.setName("AAA");
		p1.setCode("01");
		p1.setStatus(Product.Status.APPROVED);
		p1 = productRepository.save(p1);
		p1.setDetail("new update");
		productRepository.save(p1);

		Product p2 = new Product();
		p2.setName("BBB");
		p2.setStatus(Product.Status.NOT_APPROVED);
		productRepository.save(p2);

		Product p3 = new Product();
		p3.setName("CCC");
		p3.setStatus(Product.Status.PENDING);
		productRepository.save(p3);

		// class Order used @ExcludeSuperclassListeners เพิ่มไม่ให้ได้ความสามารถในการอัพเดตวันที่
		Order o1 = new Order();
		o1.setOrderId(new OrderId(1L, 1L));
		o1.setQuantity(2);
		o1 = orderRepository.save(o1);
		o1.setQuantity(3);
		orderRepository.save(o1);
	}

	private void testImmutable() {
		// INSERT
		log.info(() -> "BEGIN INSERT");
		Product p = new Product();
		p.setCode("C01");
		p.setName("INSERT");
		p = productRepository.save(p);
		log.info(() -> "AFTER INSERT: " + productRepository.findAll());

		// UPDATE BY JPA
		log.info(() -> "BEGIN UPDATE BY JPA METHOD");
		p.setName("JPA METHOD");
		productRepository.saveAndFlush(p);
		log.info(() -> "AFTER UPDATE BY JPA METHOD: " + productRepository.findAll());

		// UPDATE BY JPQL
		log.info(() -> "BEGIN UPDATE BY JPQL");
		try {
			productRepository.jpqlUpdate("JPQL", "C01");
			log.info(() -> "AFTER UPDATE BY JPQL: " + productRepository.findAll());
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		// UPDATE BY SQL
		log.info(() -> "BEGIN UPDATE BY SQL");
		try {
			productRepository.sqlUpdate("SQL", "C01");
			log.info(() -> "AFTER UPDATE BY SQL: " + productRepository.findAll());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
