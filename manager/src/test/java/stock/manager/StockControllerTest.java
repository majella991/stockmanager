package stock.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

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

	private static final String SLASH = "/";

	private static final String API_STOCK = "/api/stock";

	private static final int INITIAL_STOCK = 100;

	@Autowired
	private MockMvc mvc;

	@Test
	public void testInsertProduct() throws Exception {
		Product product = new Product("Olive");
		String inputJson = super.mapToJson(product);
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(API_STOCK).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus());
	}

	@Test
	public void testGetStock() throws Exception {
		Product product = new Product("Coconut");
		String inputJson = super.mapToJson(product);
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(API_STOCK).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		mvcResult = mvc.perform(MockMvcRequestBuilders.get(API_STOCK).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		String content = mvcResult.getResponse().getContentAsString();
		Product[] productlist = super.mapFromJson(content, Product[].class);
		assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
		assertTrue(productlist.length > 0);
	}

	@Test
	public void testGetNoProduct() throws Exception {
		MvcResult mvcResult = mvc
				.perform(
						MockMvcRequestBuilders.get(API_STOCK + SLASH + "Soap").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
	}

	@Test
	public void testGetProduct() throws Exception {
		String name = "Conditioner";
		Product product = new Product(name);
		String inputJson = super.mapToJson(product);
		mvc.perform(
				MockMvcRequestBuilders.post(API_STOCK).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		MvcResult mvcResult = mvc
				.perform(MockMvcRequestBuilders.get(API_STOCK + SLASH + name).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		String content = mvcResult.getResponse().getContentAsString();
		product = super.mapFromJson(content, Product.class);
		assertEquals(HttpStatus.FOUND.value(), mvcResult.getResponse().getStatus());
		assertEquals(INITIAL_STOCK, product.getQuantity());
	}

	@Test
	public void testGetProductsQuantity() throws Exception {
		String name = "Shampoo";
		Product product = new Product(name);
		String inputJson = super.mapToJson(product);
		mvc.perform(
				MockMvcRequestBuilders.post(API_STOCK).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(API_STOCK + SLASH + name + SLASH + "quantity")
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		String content = mvcResult.getResponse().getContentAsString();
		assertEquals(HttpStatus.FOUND.value(), mvcResult.getResponse().getStatus());
		assertEquals("" + INITIAL_STOCK, content);
	}

	@Test
	public void testBuyAndRefill() throws Exception {
		String name = "Lotion";
		Product product = new Product(name);
		String inputJson = super.mapToJson(product);
		mvc.perform(
				MockMvcRequestBuilders.post(API_STOCK).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(API_STOCK + SLASH + name + SLASH + "buy")
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		String content = mvcResult.getResponse().getContentAsString();
		assertEquals("" + (INITIAL_STOCK - 1), content);
		mvcResult = mvc.perform(MockMvcRequestBuilders.get(API_STOCK + SLASH + name + SLASH + "refill")
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		content = mvcResult.getResponse().getContentAsString();
		assertEquals(HttpStatus.FOUND.value(), mvcResult.getResponse().getStatus());
		assertEquals("" + INITIAL_STOCK, content);
	}

	@Test
	public void testBuyTooMany() throws Exception {
		String name = "Aftersun";
		Product product = new Product(name);
		String inputJson = super.mapToJson(product);
		mvc.perform(
				MockMvcRequestBuilders.post(API_STOCK).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		String url = API_STOCK + SLASH + name + SLASH + "buy";
		for (int i = 0; i < INITIAL_STOCK - 1; i++) {
			mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		}
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		String content = mvcResult.getResponse().getContentAsString();
		assertEquals(HttpStatus.FOUND.value(), mvcResult.getResponse().getStatus());
		mvcResult = mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		content = mvcResult.getResponse().getContentAsString();
		assertEquals(HttpStatus.FORBIDDEN.value(), mvcResult.getResponse().getStatus());
		assertEquals("0", content);
	}

	@Test
	public void testReserveTooMany() throws Exception {
		String name = "Brush";
		Product product = new Product(name);
		String inputJson = super.mapToJson(product);
		mvc.perform(
				MockMvcRequestBuilders.post(API_STOCK).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		for (int i = 0; i < INITIAL_STOCK - 1; i++) {
			mvc.perform(MockMvcRequestBuilders.get(API_STOCK + SLASH + name + SLASH + "buy")
					.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		}
		int seconds = 1;
		String url = API_STOCK + SLASH + name + SLASH + "reserve" + SLASH + seconds;
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		String content = mvcResult.getResponse().getContentAsString();
		assertEquals(HttpStatus.FOUND.value(), mvcResult.getResponse().getStatus());
		mvcResult = mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		content = mvcResult.getResponse().getContentAsString();
		assertEquals(HttpStatus.FORBIDDEN.value(), mvcResult.getResponse().getStatus());
		assertEquals("0", content);
		TimeUnit.SECONDS.sleep(seconds);
		mvcResult = mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		content = mvcResult.getResponse().getContentAsString();
		assertEquals(HttpStatus.FOUND.value(), mvcResult.getResponse().getStatus());
	}

}