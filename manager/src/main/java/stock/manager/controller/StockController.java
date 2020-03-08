package stock.manager.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import stock.manager.model.Product;

@RestController
public class StockController {
	private static Map<Long, Product> stock = new HashMap<>();

	@RequestMapping(value = "/stock")
	public ResponseEntity<Object> getProduct() {
		return new ResponseEntity<>(stock.values(), HttpStatus.OK);
	}

	/**
	 * creates a product with 100 items and inserts it into the stock
	 * 
	 * @param name of the product
	 * @return responseEntity with httpStatus 201 if product has been created
	 *         successfully
	 */
	@RequestMapping(value = "/stock", method = RequestMethod.POST)
	public ResponseEntity<Object> insertProduct(@RequestBody String name) {
		Product product = new Product(name);
		stock.put(product.getId(), product);
		return new ResponseEntity<>("Product has been created successfully", HttpStatus.CREATED);
	}
}
