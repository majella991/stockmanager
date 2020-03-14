package stock.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import stock.manager.model.Product;

@SpringBootTest
@AutoConfigureMockMvc
public class StockControllerTest extends AbstractTest {

	private static final int INITIAL_STOCK = 100;

	@Autowired
	private MockMvc mvc;

	@Test
	public void testGetNoStock() throws Exception {
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/stock").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		String content = mvcResult.getResponse().getContentAsString();
		Product[] productlist = super.mapFromJson(content, Product[].class);
		assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
		assertTrue(productlist.length == 0);
	}

	@Test
	public void testInsertProduct() throws Exception {
		Product product = new Product("Soap");
		String inputJson = super.mapToJson(product);
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post("/stock").contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		String content = mvcResult.getResponse().getContentAsString();
		assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
		assertEquals("Product has been added successfully.", content);
	}

	@Test
	public void testGetStock() throws Exception {
		Product product = new Product("Coconut");
		String inputJson = super.mapToJson(product);
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post("/stock").contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		mvcResult = mvc.perform(MockMvcRequestBuilders.get("/stock").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		String content = mvcResult.getResponse().getContentAsString();
		Product[] productlist = super.mapFromJson(content, Product[].class);
		assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
		assertTrue(productlist.length > 0);
	}

	@Test
	public void testGetNoProduct() throws Exception {
		MvcResult mvcResult = mvc
				.perform(MockMvcRequestBuilders.get("/stock/1").accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
	}

	@Test
	public void testGetProduct() throws Exception {
		Product product = new Product("Soap");
		String inputJson = super.mapToJson(product);
		mvc.perform(
				MockMvcRequestBuilders.post("/stock").contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		MvcResult mvcResult = mvc
				.perform(MockMvcRequestBuilders.get("/stock/0").accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		String content = mvcResult.getResponse().getContentAsString();
		product = super.mapFromJson(content, Product.class);
		assertEquals(HttpStatus.FOUND.value(), mvcResult.getResponse().getStatus());
		assertTrue(product.getId() == 0);
		assertEquals(INITIAL_STOCK, product.getQuantity());
	}

	@Test
	public void testGetProductsQuantity() throws Exception {
		Product product = new Product("Shampoo");
		String inputJson = super.mapToJson(product);
		mvc.perform(
				MockMvcRequestBuilders.post("/stock").contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		MvcResult mvcResult = mvc
				.perform(MockMvcRequestBuilders.get("/stock/0/quantity").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		String content = mvcResult.getResponse().getContentAsString();
		assertEquals(HttpStatus.FOUND.value(), mvcResult.getResponse().getStatus());
		assertEquals("" + INITIAL_STOCK, content);
	}

	@Test
	public void testBuyAndRefill() throws Exception {
		Product product = new Product("Lotion");
		String inputJson = super.mapToJson(product);
		mvc.perform(
				MockMvcRequestBuilders.post("/stock").contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		MvcResult mvcResult = mvc
				.perform(MockMvcRequestBuilders.get("/stock/0/buy").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		String content = mvcResult.getResponse().getContentAsString();
		assertEquals("" + (INITIAL_STOCK - 1), content);
		mvcResult = mvc.perform(MockMvcRequestBuilders.get("/stock/0/refill").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		content = mvcResult.getResponse().getContentAsString();
		assertEquals(HttpStatus.FOUND.value(), mvcResult.getResponse().getStatus());
		assertEquals("" + INITIAL_STOCK, content);
	}

	@Test
	public void testBuyTooMany() throws Exception {
		Product product = new Product("Lotion");
		String inputJson = super.mapToJson(product);
		mvc.perform(
				MockMvcRequestBuilders.post("/stock").contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		for (int i = 0; i < INITIAL_STOCK - 1; i++) {
			mvc.perform(MockMvcRequestBuilders.get("/stock/0/buy").accept(MediaType.APPLICATION_JSON_VALUE))
					.andReturn();
		}
		MvcResult mvcResult = mvc
				.perform(MockMvcRequestBuilders.get("/stock/0/buy").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		String content = mvcResult.getResponse().getContentAsString();
		assertEquals(HttpStatus.FOUND.value(), mvcResult.getResponse().getStatus());
		assertEquals("0", content);
		mvcResult = mvc.perform(MockMvcRequestBuilders.get("/stock/0/buy").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		content = mvcResult.getResponse().getContentAsString();
		assertEquals(HttpStatus.FORBIDDEN.value(), mvcResult.getResponse().getStatus());
		assertEquals("0", content);
	}

}