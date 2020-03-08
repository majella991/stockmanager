package stock.manager.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import stock.manager.model.Product;

@RestController
public class StockController {
	private static Map<Long, Product> productRepo = new HashMap<>();

	@RequestMapping(value = "/stock")
	public ResponseEntity<Object> getProduct() {
		return new ResponseEntity<>(productRepo.values(), HttpStatus.OK);
	}

	/**
	 * creates a product with 100 items and inserts it into the stock
	 * 
	 * @param name of the product
	 * @return responseEntity with httpStatus 201 if product has been created
	 *         successfully
	 */
	@RequestMapping(value = "/stock", method = RequestMethod.POST)
	public ResponseEntity<String> insertProduct(@RequestBody String name) {
		Product product = new Product(name);
		productRepo.put(product.getId(), product);
		return new ResponseEntity<>("Product has been created successfully", HttpStatus.CREATED);
	}

	@RequestMapping(value = "/stock/{id}")
	public ResponseEntity<Product> getProduct(@PathVariable("id") Long id) {
		return new ResponseEntity<>(productRepo.get(id), HttpStatus.OK);
	}
}
