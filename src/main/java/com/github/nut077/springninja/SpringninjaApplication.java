package com.github.nut077.springninja;

import com.github.nut077.springninja.entity.Product;
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

	public static void main(String[] args) {
		SpringApplication.run(SpringninjaApplication.class, args);
	}

	@Override
	public void run(String... args) {
		testImmutable();
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
