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
	 * stores the product in a repository
	 * 
	 * @param product
	 * @return responseEntity with httpStatus 201 if product has been added
	 *         successfully
	 */
	@RequestMapping(value = "/stock", method = RequestMethod.POST)
	public ResponseEntity<String> insertProduct(@RequestBody Product product) {
		productRepo.put(product.getId(), product);
		return new ResponseEntity<>("Product has been added successfully.", HttpStatus.OK);
	}

	/**
	 * searches a product by its id and returns it
	 * 
	 * @param id of the product
	 * @return product with the given id or empty response
	 */
	@RequestMapping(value = "/stock/{id}")
	public ResponseEntity<Product> getProduct(@PathVariable("id") Long id) {
		Product product = productRepo.get(id);
		if (product == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(product, HttpStatus.FOUND);
	}

	/**
	 * searches a product by its id and returns its stock information
	 * 
	 * @param id of the product
	 * @return quantity of the product i.e. how many items are available in the
	 *         stock
	 */
	@RequestMapping(value = "/stock/{id}/quantity")
	public ResponseEntity<Integer> getProductQuantity(@PathVariable("id") Long id) {
		Product product = productRepo.get(id);
		if (product == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(product.getQuantity(), HttpStatus.FOUND);
	}
	
	/**
	 * searches a product by its id and refills its stock
	 * 
	 * @param id of the product
	 * @return quantity of the product i.e. how many items are available in the
	 *         stock
	 */
	@RequestMapping(value = "/stock/{id}/refill")
	public ResponseEntity<Integer> refill(@PathVariable("id") Long id) {
		Product product = productRepo.get(id);
		if (product == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		product.refill();
		return new ResponseEntity<>(product.getQuantity(), HttpStatus.FOUND);
	}
}
