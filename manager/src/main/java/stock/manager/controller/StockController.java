package stock.manager.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import stock.manager.model.StockItem;
import stock.manager.transfer.ProductDTO;
import stock.manager.model.Product;

@RestController
public class StockController {
	private static Map<Long, Product> productRepo = new HashMap<>();

	@GetMapping(value = "/stock")
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
	@PostMapping(value = "/stock")
	public ResponseEntity<String> insertProduct(@RequestBody ProductDTO productDto) {
		Product product = new Product(productDto.getName());
		productRepo.put(product.getId(), product);
		return new ResponseEntity<>("Product has been added successfully.", HttpStatus.OK);
	}

	/**
	 * searches a product by its id and returns it
	 * 
	 * @param id of the product
	 * @return product with the given id or empty response
	 */
	@GetMapping(value = "/stock/{id}")
	public ResponseEntity<Product> getProduct(@PathVariable("id") Long id) {
		Product product = productRepo.get(id);
		HttpStatus status = getStatus(product);
		return new ResponseEntity<>(product, status);
	}

	private HttpStatus getStatus(Product product) {
		if (product == null) {
			return HttpStatus.NOT_FOUND;
		}
		return HttpStatus.FOUND;
	}

	/**
	 * searches a product by its id and returns its stock information
	 * 
	 * @param id of the product
	 * @return quantity of the product i.e. how many items are available in the
	 *         stock
	 */
	@GetMapping(value = "/stock/{id}/quantity")
	public ResponseEntity<Integer> getProductQuantity(@PathVariable("id") Long id) {
		Product product = productRepo.get(id);
		return getQuantityResponse(product);
	}
	
	private ResponseEntity<Integer> getQuantityResponse(Product product) {
		return new ResponseEntity<>(getOptionalQuantity(product), getStatus(product));
	}
	
	private Integer getOptionalQuantity(Product product) {
		if (product != null) {
			return product.getQuantity();
		}
		return null;
	}
	
	/**
	 * searches a product by its id and refills its stock
	 * 
	 * @param id of the product
	 * @return quantity of the product i.e. how many items are available in the
	 *         stock
	 */
	@GetMapping(value = "/stock/{id}/refill")
	public ResponseEntity<Integer> refill(@PathVariable("id") Long id) {
		Product product = productRepo.get(id);
		if (product == null) {
			return getQuantityResponse(product);
		}
		product.refill();
		return getQuantityResponse(product);
	}
	
	/**
	 * searches a product by its id and reduces its stock by quantity (default: 1) items
	 * 
	 * @param id of the product
	 * @param quantity 
	 * @return quantity of the product i.e. how many items are still available in the
	 *         stock
	 */
	@GetMapping(value = "/stock/{id}/buy")
	public ResponseEntity<Integer> buy(@PathVariable("id") Long id) {
		Product product = productRepo.get(id);
		if (product == null) {
			return getQuantityResponse(product);
		}
		StockItem item = product.buy();
		if (item == null) {
			return new ResponseEntity<>(getOptionalQuantity(product), HttpStatus.NO_CONTENT);
		}
		return getQuantityResponse(product);
	}
	

}
