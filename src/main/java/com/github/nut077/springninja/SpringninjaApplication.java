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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

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
	public void run(String... args) throws Exception {
		//testImmutable();
		//testListener();
		//testDynamicInsertAndUpdate();
		//testElementCollection();
		//testTransactional();
		queryByExample();
	}

	private void queryByExample() {
		/*
			การใช้งาน query by example ประกอบไปด้วย 3 ส่วน
			1. Probe ตัว entity ของเรา
			2. ExampleMatcher ตัวนี้เอาไว้ใส่ลูกเล่นพวก startWith endWith contain
			3. Example จะประกอบไปด้วย 2 ส่วนคือ Probe ExampleMatcher  แล้วก็เอาไปโยสใส่พวก count findAll ใน repository

			Limitation
			 - don't support nested group เช่น firstname=?1 or (firstname=?2 or lastname=?3)
			 - support starts / contains /ends / regex เฉพาะ string เท่านั้น นอกนั้นต้องเป็น exact matching เท่านั้น
		 */

		log.info("Inseting multiple Products");

		productRepository.saveAll(Arrays.asList(
			Product.builder().code("101").name("A1").status(Product.Status.APPROVED).build(),
			Product.builder().code("102").name("a2").status(Product.Status.NOT_APPROVED).build(),
			Product.builder().code("103").name("B1").status(Product.Status.PENDING).build(),
			Product.builder().code("104").name("b2").status(Product.Status.APPROVED).build()
		));

		log.info("Count number of all products : {}", productRepository.count());
		productRepository.findAll().forEach(System.out::println);

		log.info("Find all 'APPROVED' products");
		Product probe1 = new Product();  // Create Probe
		probe1.setStatus(Product.Status.APPROVED);
		ExampleMatcher matcher1 = ExampleMatcher.matching() // Create ExampleMatcher
			.withIgnorePaths("id")
			.withIgnorePaths("version");
		Example<Product> example1 = Example.of(probe1, matcher1);  // Create Example
		productRepository.findAll(example1).forEach(System.out::println); // Find all by QueryByExample

		log.info("Find all products that name contains 'a' or 'A'");
		Product probe2 = Product.builder().name("a").build();
		ExampleMatcher matcher2 = ExampleMatcher.matching()
			.withIgnorePaths("id")
			.withIgnorePaths("version")
			.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
			.withIgnoreCase();
		Example<Product> example2 = Example.of(probe2, matcher2); // Create Example
		productRepository.findAll(example2).forEach(System.out::println);  // Find all by QueryByExample

		log.info("Find all products that code contains '0' and name startWith 'b'");
		Product probe3 = Product.builder().code("0").name("b").build();  // Create Probe
		ExampleMatcher matcher3 = ExampleMatcher.matching()  // Create ExampleMatcher
			.withIgnorePaths("id")
			.withIgnorePaths("version")
			.withMatcher("code", ExampleMatcher.GenericPropertyMatcher::contains)
			.withMatcher("name", ExampleMatcher.GenericPropertyMatcher::startsWith);
		Example<Product> example3 = Example.of(probe3, matcher3); // Create Example
		productRepository.findAll(example3).forEach(System.out::println);  // Find all by QueryByExample
	}

	@Transactional(
		// ค่าทั้งหมดคือ default
		readOnly = false, // transaction Read and Write แต่ถ้าเป็น true จะ Read ได้อย่างเดียว
		propagation = Propagation.REQUIRED, // ใช้ transaction เดิม
		isolation = Isolation.DEFAULT, // เชื่อ database ว่าจะทำงานยังไง คือบอกให้ spring ตัดสินใจว่าแต่ละ transaction จะมองเห็น value ของ transaction อื่นหรือเปล่า จะทำงานเมื่อ propagation เป็น REQUIRED or NEW เท่านั้น
		timeout = -1, // หมายถึงไม่มี timeout
		rollbackFor = {RuntimeException.class}, // ใช้คู่กับ noRollbackFor ค่า default คือ จะทำการ rollback เฉพาะ class ที่เป็น unchecked exception หรือก็คือ class ที่ extends มาจาก RuntimeException
		noRollbackFor = {Exception.class} // จะไม่ rollback ทุก exception ที่ extends มาจาก Exception หรือ class ที่เป็น checked exception พวก business exception ของเราต่างๆ
		// IOException -->> checked exception
		// NullPointerException -->> unChecked exception
	)
	private void testTransactional() throws Exception {
		/*
    ขั้นตอนการทำงาน
    1. spring จะทำการสร้าง proxy class ที่มัน scan เจอว่า class ไหน หรือ ใน method ไหน ที่มี annotation @Transactional
    โดยจะทำการ implement interface ที่เหมือนกับ class ต้นฉบับเป๊ะๆ

    2. Transactional Aspect
    ตัวนี้จะมี class ที่ชื่อว่า TransactionInterceptor จะทำการ aspect (Around) business method ของเราที่ proxy class ที่ถูกสร้างจาก step แรก
    จะทำ 2 อย่าง
      - เวลามีคำสั่งเข้ามา จะทำการเช็คว่าคำสั่งนี้จะทำการสร้าง Transaction ใหม่หรือเปล่าโดยการถามไปที่ step ถัดไป คือถามไปที่ Transaction Manager
      จากนั้นจะทำการตัดสินในว่าจะ commit or rollback

    3. Transaction Manager สิ่งที่ทำมีอย่างเดียวคือ ทำการตัดสินใจว่าตัว entity manager แล้วก็ตัว database transaction ถูกสร้างแล้วหรือยัง แล้วทำการเช็คว่าตัว Propagation
    เป้น REQUIRE or REQUIRE_NEW ถ้าเป็น NEW จะทำการสร้าง transaction ใหม่อยู่เสมอ ถ้าเป็น REQUIRE ก็จะทำการเช็คว่า current transaction ยังทำงานได้อยู่ไหม หรือว่ายังไม่มี ถึงจะทำการ
    ตัดสินใจว่าจะสร้างใหม่หรือไม่สร้าง

   */

		Product p = new Product();
		p.setName("not save");
		productRepository.save(p);
		throw new RuntimeException("test exception");
	}

	private void testElementCollection() {
		Product p = new Product();
		p.setName("ElementCollection");
		p.getAliasNames().add("Java");
		p.getAliasNames().add("Go");
		productRepository.save(p);

		Product p2 = new Product();
		p2.setName("ElementCollection");
		p2.getAliasNames().add("PHP");
		productRepository.save(p2);
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
