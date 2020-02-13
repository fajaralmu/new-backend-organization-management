//package net.mpimedia.service;
//
//import java.util.List;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import net.mpimedia.entity.ProductFlow;
//import net.mpimedia.repository.ProductFlowRepository;
//import net.mpimedia.repository.ProductRepository;
//
//@RunWith(SpringJUnit4ClassRunner.class)
////@ContextConfiguration(classes={ JpaTestConfig.class, TestConfig.class })
//@ContextConfiguration({ "file:src/test/webapp/WEB-INF/shop-servlet.xml" })
//public class TransactionServiceTest {
//
//	@Autowired
//	private ProductFlowRepository productFlowRepository;
//	
//	@Autowired
//	private FileService fileService;
//	
//	@Autowired
//	private ProductRepository productRepository;
//	
//	@Test
//	public void editDate() {
//		List<ProductFlow> flows = productFlowRepository.findByPriceIsNull();
//		for (ProductFlow productFlow : flows) {
//			productFlow.setPrice(productFlow.getProduct().getPrice());
//			productFlowRepository.save(productFlow);
//		}
//	}
//}
