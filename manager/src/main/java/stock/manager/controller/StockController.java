package stock.manager.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import stock.manager.model.StockItem;
import stock.manager.transfer.ProductDTO;
import stock.manager.model.Product;
import stock.manager.model.ProductRepository;

@RestController
@RequestMapping("/api")
public class StockController {

	private ProductRepository productRepo;

	public StockController(ProductRepository groupRepository) {
		this.productRepo = groupRepository;
	}

	@GetMapping("/stock")
	public Iterable<Product> stock() {
		return productRepo.findAll();
	}

	/**
	 * stores the product in a repository
	 * 
	 * @param product
	 * @return responseEntity with URI to the created product
	 * @throws URISyntaxException
	 */
	@PostMapping(value = "/stock")
	public ResponseEntity<Product> insertProduct(@RequestBody ProductDTO productDto) throws URISyntaxException {
		Product product = new Product(productDto.getName());
		product = productRepo.save(product);
		return ResponseEntity.created(new URI("/api/group/" + product.getId())).body(product);
	}

	/**
	 * searches a product by its name and returns it
	 * 
	 * @param name of the product
	 * @return product with the given id or empty response
	 */
	@GetMapping(value = "/stock/{name}")
	public ResponseEntity<Product> getProduct(@PathVariable("name") String name) {
		Product product = productRepo.findByName(name);
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
	 * searches a product by its name and returns its stock information
	 * 
	 * @param name of the product
	 * @return quantity of the product i.e. how many items are available in the
	 *         stock
	 */
	@GetMapping(value = "/stock/{name}/quantity")
	public ResponseEntity<Long> getProductQuantity(@PathVariable("name") String name) {
		Product product = productRepo.findByName(name);
		return getQuantityResponse(product);
	}

	private ResponseEntity<Long> getQuantityResponse(Product product) {
		return new ResponseEntity<>(getOptionalQuantity(product), getStatus(product));
	}

	private Long getOptionalQuantity(Product product) {
		if (product != null) {
			return product.getQuantity();
		}
		return null;
	}

	/**
	 * searches a product by its name and refills its stock
	 * 
	 * @param name of the product
	 * @return quantity of the product i.e. how many items are available in the
	 *         stock
	 */
	@GetMapping(value = "/stock/{name}/refill")
	public ResponseEntity<Long> refill(@PathVariable("name") String name) {
		Product product = productRepo.findByName(name);
		if (product == null) {
			return getQuantityResponse(product);
		}
		product.refill();
		product = productRepo.save(product);
		return getQuantityResponse(product);
	}

	/**
	 * searches a product by its name and reduces its stock by quantity (default: 1)
	 * items
	 * 
	 * @param name       of the product
	 * @param quantity
	 * @return quantity of the product i.e. how many items are still available in
	 *         the stock
	 */
	@GetMapping(value = "/stock/{name}/buy")
	public ResponseEntity<Long> buy(@PathVariable("name") String name) {
		Product product = productRepo.findByName(name);
		if (product == null) {
			return getQuantityResponse(product);
		}
		Optional<StockItem> item = product.buy();
		product = productRepo.save(product);
		if (item.isPresent()) {
			return getQuantityResponse(product);
		}
		return new ResponseEntity<>(getOptionalQuantity(product), HttpStatus.FORBIDDEN);
	}

}
