package stock.manager.controller;

import org.springframework.web.bind.annotation.RestController;

import stock.manager.model.Product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class StockController {
	@RequestMapping("/")
	public String index() {
		return "Stock Manager";
	}
	
	/**
	 * inserts a product with a certain quantity into the stock
	 * @param name of the product
	 * @param quantity of the product i.e. how many items to insert into the stock
	 * @return the newly created product
	 */
	@GetMapping("/insertProduct")
	public Product insertProduct(
			@RequestParam(value = "name") String name, 
			@RequestParam(value = "quantity", defaultValue = "100") int quantity) {
		return new Product(name, quantity);
	}

}