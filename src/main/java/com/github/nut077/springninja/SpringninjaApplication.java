package com.github.nut077.springninja;

import com.github.nut077.springninja.config.CaffeineCacheConfig;
import com.github.nut077.springninja.config.testbean.CalculatorBean;
import com.github.nut077.springninja.dto.mapper.ProductMapper;
import com.github.nut077.springninja.entity.Order;
import com.github.nut077.springninja.entity.OrderId;
import com.github.nut077.springninja.entity.Product;
import com.github.nut077.springninja.repository.OrderRepository;
import com.github.nut077.springninja.repository.ProductRepository;
import com.github.nut077.springninja.repository.specification.ProductSpec;
import com.github.nut077.springninja.service.ProductService;
import com.github.nut077.springninja.service.testprofile.CalculatorProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.github.nut077.springninja.config.CaffeineCacheConfig.CacheName.PRODUCT;
import static com.github.nut077.springninja.config.CaffeineCacheConfig.CacheName.PRODUCTS;

@Log4j2
@SpringBootApplication
@RequiredArgsConstructor
@ConfigurationPropertiesScan
public class SpringninjaApplication implements CommandLineRunner {

	private final ProductRepository productRepository;
	private final ProductService productService;
	private final OrderRepository orderRepository;
	private final SimpleCacheManager simpleCacheManager;
	private final CaffeineCacheConfig caffeineCacheConfig;
	private final CalculatorProfile calculatorProfile;
	private final ProductMapper mapper;

	@Autowired
	@Qualifier("methodPlus")
	private CalculatorBean cal;

	DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

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
		//queryByExample();
		//queryMethod();
		//nameNativeQuery();
		//queryAnnotation();
		//dynamicQuery();
		//cacheCaffeine();
		//async();
		//retry();
		//retryTemplate();
		//profilesActive();
		//bean();
	}

	private void bean() {
		log.info(cal.calculate(5, 3)); // result -->> 8
	}

	private void profilesActive() {
		// profile dev
		log.info(calculatorProfile.calculate(5, 3)); // result -->> 8
		// profile uat
		//log.info(calculate.calculate(5, 3)); // result -->> 2
	}

	private void retryTemplate() {
		log.info(productService.retryTemplate());
	}

	private void retry() {
		productService.retry("str");
	}

	private void async() throws InterruptedException {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		productService.save(mapper.map(Product.builder().name("apple").code("F001").build()));
		productService.save(mapper.map(Product.builder().name("lemon").code("F002").build()));
		productService.save(mapper.map(Product.builder().name("coconut").code("F003").build()));

		CompletableFuture<Product> apple = productService.find("apple");
		CompletableFuture<Product> lemon = productService.find("lemon");
		CompletableFuture<Product> coconut = productService.find("coconut");

		List<CompletableFuture<Product>> completableFutures = Arrays.asList(apple, lemon, coconut);
		CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]))
			.whenComplete((aVoid, throwable) ->
				completableFutures.forEach(completableFuture -> log.info("Completed -->> " + completableFuture.join()))
			).join();
		stopWatch.stop();

		// test exception
		productService.voidMethod();

		log.info("Elapsed Time : " + stopWatch.getTotalTimeSeconds() + " Sec");
	}

	// ############## start scheduler ##############

	//@Scheduled(fixedDelay = 2000) // 2s
	public void scheduleFixedDelayTask() throws InterruptedException {
		// [1][1][1][1][1][1][1][1][1][1][1][1][1][1][1][1][1][1][1]
		// [      5s     ] task1
		//                      [      5s     ] task2
		//                                           [      5s     ] task3

		// ทำ Thread แรก 5 วิ รอ 2 วิ และทำ Thread ต่อไปอีก 5 วิ รออีก 2 วิ แบบนี้ต่อไปเรื่อยๆ
		TimeUnit.SECONDS.sleep(5);
		log.info(Thread.currentThread().getName() + " : FIXED-DELAY Task :: {}", dateTimeFormatter.format(LocalTime.now()));
	}

	//@Scheduled(fixedRate = 3000) // 3s
	public void scheduleFixedRateTask() throws InterruptedException {
		// [1][1][1][1][1][1][1][1][1][1][1][1][1][1][1][1][1][1][1]
		// [      5s     ] task1
		//          [      5s     ] task2
		//                   [      5s     ] task3

		// ทุกๆ 3 วิ จะเริ่มทำ 1 Thread
		TimeUnit.SECONDS.sleep(5);
		log.info(Thread.currentThread().getName() + " : FIXED-RATE Task :: {}", dateTimeFormatter.format(LocalTime.now()));
	}

	//@Scheduled(cron = "0/3 * * * * *") // วินาที นาที ชั่วโมง วัน เดือน วันใดบ้างในอาทิตย์  at https://crontab.guru/
	public void scheduCronExpressionTask() throws InterruptedException {
		// [1][1][1][1][1][1][1][1][1][1][1][1][1][1][1][1][1][1][1]
		// [   3s  ] task1
		//          [   3s  ] task2
		//                   [   3s  ] task3

		// ทำ Thread แต่ละ Thread ทุก 3 วิ
		TimeUnit.SECONDS.sleep(3);
		log.info(Thread.currentThread().getName() + " : CRON TASK :: {}", dateTimeFormatter.format(LocalTime.now()));
	}



	// ############## end scheduler ##############

	private void cacheCaffeine() {
		log.info("Inserting multiple Products");
		productRepository.saveAll(Arrays.asList(
			Product.builder().code("101").name("A1").status(Product.Status.APPROVED).score(99).build(),
			Product.builder().code("102").name("B2").status(Product.Status.APPROVED).score(80).detail("not null").build(),
			Product.builder().code("103").name("C1").status(Product.Status.PENDING).score(30).detail("Hi").build(),
			Product.builder().code("104").name("D2").status(Product.Status.NOT_APPROVED).score(70).build()
		));

		StopWatch stopWatchBeforeFindAllProducts = new StopWatch();
		stopWatchBeforeFindAllProducts.start();
		productsCache();
		log.info("Before cache find all products");
		log.info(productService.findAll().size());
		stopWatchBeforeFindAllProducts.stop();
		log.info(() -> "time -->> " + stopWatchBeforeFindAllProducts.getTotalTimeSeconds() + " Sec.");
		StopWatch stopWatchAfterFindAllProducts = new StopWatch();
		stopWatchAfterFindAllProducts.start();
		log.info("After cache find all products");
		log.info(productService.findAll().size());
		stopWatchAfterFindAllProducts.stop();
		log.info(() -> "time -->> " + stopWatchAfterFindAllProducts.getTotalTimeSeconds() + " Sec.");
		productsCache();
		log.info("#####################################################");

		productCache();
		log.info("Before find product id 1");
		log.info(productService.find(1L));
		log.info("After find product id 1");
		log.info(productService.find(1L));
		productCache();
		log.info("#####################################################");

		productCache();
		log.info("Before find product id 2");
		log.info(productService.find(2L));
		log.info("After find product id 2");
		log.info(productService.find(2L));
		productCache();
		log.info("#####################################################");

		productCache();
		log.info("Before find product id 3");
		log.info(productService.find(3L));
		log.info("After find product id 3 don't cache because socre < 50");
		log.info(productService.find(3L));
		productCache();
		log.info("#####################################################");

		Product product1 = productService.find(1L);
		product1.setScore(99.99);
		log.info("Before update product id 1 -->> {}", product1);
		productService.replace(1L, mapper.map(product1));
		log.info("After update product id 1 -->> {}", productService.find(1L));
		productCache();
		log.info("#####################################################");

		log.info("evict product 2L");
		productService.delete(2L);
		productCache();

		log.info("evict all");
		caffeineCacheConfig.evictAll();
		productCache();
		productsCache();
	}

	private void productsCache() {
		log.info("Cache name PRODUCTS {}", ((CaffeineCache) simpleCacheManager.getCache(PRODUCTS)).getNativeCache().asMap());
	}

	private void productCache() {
		log.info("Cache name PRODUCT {}", ((CaffeineCache) simpleCacheManager.getCache(PRODUCT)).getNativeCache().asMap());
	}

	private void dynamicQuery() {
		/*
		 ข้อดี
		   query ถูกสร้างตอน runtime จะยืดหยุ่นกว่าปกติ
		 ข้อเสีย
		   เปลือง performance เพราะว่าต้อง compile ทุกครั้ง
		 */
		log.info("Inserting multiple Products");
		productRepository.saveAll(Arrays.asList(
			Product.builder().code("101").name("A1").status(Product.Status.APPROVED).build(),
			Product.builder().code("102").name("B2").status(Product.Status.APPROVED).detail("not null").build(),
			Product.builder().code("103").name("C1").status(Product.Status.PENDING).detail("Hi").build(),
			Product.builder().code("104").name("D2").status(Product.Status.NOT_APPROVED).detail("Hello").build()
		));
		log.info("Count number of all products : {}", productRepository.count());

		log.info("Find products name equals B2");
		productRepository.findAll(ProductSpec.nameEquals("B2")).forEach(System.out::println);

		log.info("Find name like 1");
		productRepository.findAll(ProductSpec.nameLike("1")).forEach(System.out::println);

		log.info("Find status equals APPROVE");
		productRepository.findAll(ProductSpec.statusEquals(Product.Status.APPROVED)).forEach(System.out::println);

		log.info("Find status equals APPROVE and detail is not null");
		productRepository.findAll(ProductSpec.statusEqualsAndDetailIsNotNull(Product.Status.APPROVED)).forEach(System.out::println);

		log.info("Find name like 1 and status equals APPROVE");
		productRepository.findAll(
			Specification
				.where(ProductSpec.nameLike("1"))
				.and(ProductSpec.statusEquals(Product.Status.PENDING))
		).forEach(System.out::println);
	}

	private void queryAnnotation() {
		/*
		ข้อดี
			support jpql and sql default is jpql
			find method ทำอะไร
			อยากตั้งชื่อ method อะไรก็ได้ ไม่มีผลอะไร
			ถ้าเขียน query ก็เขียนไปที่ annotation ได้เลย

		ข้อเสีย
			ไม่ support dynamic query
			ไม่สามารถเปลี่ยน database ได้ ถ้าทำต้องทำการทดสอบใหม่ทั้งหมดว่าสามารถ run ได้หรือไม่
		 */

		log.info("Inserting multiple Products");
		productRepository.saveAll(Arrays.asList(
			Product.builder().code("101").name("A1").status(Product.Status.APPROVED).build(),
			Product.builder().code("102").name("B2").status(Product.Status.APPROVED).build(),
			Product.builder().code("103").name("C1").status(Product.Status.PENDING).detail("Hi").build(),
			Product.builder().code("104").name("D2").status(Product.Status.NOT_APPROVED).detail("Hello").build()
		));
		log.info("Count number of all products : {}", productRepository.count());
		log.info("Find all");
		productRepository.findAll().forEach(System.out::println);

		log.info("[JPQL-Indexing parameters] select all by name and status");
		productRepository.selectAllByNameAndStatus("C1", Product.Status.PENDING).forEach(System.out::println);

		log.info("[JPQL-Named parameters] select all by status and name");
		productRepository.selectAllByStatusAndName(Product.Status.APPROVED, "A1").forEach(System.out::println);

		log.info("[JPQL-Named parameters] select all by name endsWith");
		productRepository.selectAllByNameEndsWith("2").forEach(System.out::println);

		log.info("[SQL-Named parameters] select all by status and date");
		productRepository.selectAllByStatusAndDate(Product.Status.APPROVED.getCode(), DateTimeFormatter.ofPattern("yyyy-MM-dd").format(OffsetDateTime.now()))
			.forEach(System.out::println);

		log.info("Update status by Id");
		productRepository.updateStatusById(Product.Status.DELETED, "104");
		log.info(productRepository.findById(4L).get());

		log.info("remove all by status");
		productRepository.removeAllByStatus(Product.Status.DELETED.getCode());
		log.info(productRepository.findById(4L));
	}

	private void nameNativeQuery() {
		/*
		ข้อดี
		  jpql or sql จะถูก process ตอน application start query ของเราเป็น static คือไม่ต้อง compile ตอนเรียกใช้งาน จึงเป็นผลดีต่อ performance
		  สามารถใช้ resultSetMapping คือ custom query อย่างเช่นใน database มี 10 fields เราก็เอามาแสดงแค่ 2 ตัวได้ หรือทำการ concat field ได้

		ข้อเสีย
		   entity class ดูรกๆ
		   สมารถใช้ได้เฉพาะ query ที่เป็น select
		   ไม่ support dynamic queryม Dynamic query คือ คิวรีใดๆก็แล้วแต่ที่ ถูก compile ตอน runtime เช่นอย่างการเขียน query แล้วมีการเช็ค if else ถ้ามีตัวแปรจะ where อะไร
		 */
    productRepository.saveAll(Arrays.asList(
      Product.builder().code("101").name("A1").status(Product.Status.APPROVED).build(),
      Product.builder().code("102").name("a2").status(Product.Status.PENDING).detail("a").build(),
      Product.builder().code("103").name("B1").status(Product.Status.NOT_APPROVED).build(),
      Product.builder().code("104").name("b2").status(Product.Status.APPROVED).detail("detail").build()
    ));

    log.info("Count number of all products : {}", productRepository.count());
    log.info("Find all");
    productRepository.findAll().forEach(System.out::println);

    log.info("Fetch products where detail not null");
    productRepository.fetchDetailNotNull().orElse(Collections.EMPTY_LIST).forEach(System.out::println);

    log.info("Fetch products where detail length > 2");
    productRepository.fetchDetailLengthGreaterThan2().orElse(Collections.EMPTY_LIST).forEach(System.out::println);

    log.info("Custom fetch products to pojo");
    productRepository.customFetchProductToPojo().forEach(System.out::println);
	}

	private void queryMethod() {
		log.info("Inseting multiple Products");

		productRepository.saveAll(Arrays.asList(
			Product.builder().code("101").name("A1").status(Product.Status.APPROVED).build(),
			Product.builder().code("102").name("a2").status(Product.Status.PENDING).build(),
			Product.builder().code("103").name("B1").status(Product.Status.NOT_APPROVED).build(),
			Product.builder().code("104").name("b2").status(Product.Status.APPROVED).build()
		));
		log.info("Count number of all product : {}", productRepository.count());

		log.info("find all");
		productRepository.findAll().forEach(System.out::println);

		log.info("Find all 'APPROVED' products");
		productRepository.findAllByStatus(Product.Status.APPROVED).orElse(Collections.EMPTY_LIST).forEach(System.out::println);

		log.info("Find all 'APPROVED' products order by Id desc");
		productRepository.findAllByStatusOrderByIdDesc(Product.Status.APPROVED).orElse(Collections.EMPTY_LIST).forEach(System.out::println);

		log.info("Find all products that name contains 'b'");
		productRepository.findAllByNameContaining("b").orElse(Collections.EMPTY_LIST).forEach(System.out::println);

		log.info("Find all products that name contains 'b' or 'B'");
		productRepository.findAllByNameContainingIgnoreCase("b").orElse(Collections.EMPTY_LIST).forEach(System.out::println);

		log.info("Find all products that code contains '0' and name endsWith '2'");
		productRepository.findAllByCodeContainingAndNameEndingWith("0", "2").orElse(Collections.EMPTY_LIST).forEach(System.out::println);

		log.info("Find all products that code equals('101') or (code equals('103') and name equals('B1'))");
		productRepository.findAllByCodeOrCodeAndName("101", "103", "B1").orElse(Collections.EMPTY_LIST).forEach(System.out::println);
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
