package stock.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import stock.manager.model.Product;

@SpringBootTest
@AutoConfigureMockMvc
public class StockControllerTest extends AbstractTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void getStock() throws Exception {
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/stock").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		assertEquals(200, mvcResult.getResponse().getStatus());
		String content = mvcResult.getResponse().getContentAsString();
		Product[] productlist = super.mapFromJson(content, Product[].class);
		assertTrue(productlist.length > 0);
	}

	@Test
	public void insertProduct() throws Exception {
		Product product = new Product("Soap");
		String inputJson = super.mapToJson(product);
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post("/stock").contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		assertEquals(201, mvcResult.getResponse().getStatus());
		String content = mvcResult.getResponse().getContentAsString();
		assertEquals(content, "Product has been created successfully");
	}

	@Test
	public void getNoProduct() throws Exception {
		MvcResult mvcResult = mvc
				.perform(MockMvcRequestBuilders.get("/stock/0").accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

		assertEquals(200, mvcResult.getResponse().getStatus());
		String content = mvcResult.getResponse().getContentAsString();
		assertEquals("", content);
	}
	
	@Test
	public void getProduct() throws Exception {
		Product product = new Product("Soap");
		String inputJson = super.mapToJson(product);
		mvc.perform(
				MockMvcRequestBuilders.post("/stock").contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/stock/0").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		assertEquals(200, mvcResult.getResponse().getStatus());
		String content = mvcResult.getResponse().getContentAsString();
		Product productlist = super.mapFromJson(content, Product.class);
		assertTrue(productlist.getId() == 0);
	}

}