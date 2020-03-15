package stock.manager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Product {

	private static final int INITIAL_STOCK = 100;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String name;
	
	@OneToMany(targetEntity=StockItem.class, fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	private List<StockItem> stock; 
	
	public Product() {
	}

	public Product(String name) {
		setName(name);
		refill();
	}
	
	public Product(String name, int quantity) {
		setName(name);
		setQuantity(quantity);
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * counts available items
	 * @return number of available items
	 */
	public long getQuantity() {
		return getAvailableItems().count();
	}
	
	private Stream<StockItem> getAvailableItems() {
		return getStock().parallelStream().filter(StockItem::isAvailable);
	}

	private List<StockItem> getStock() {
		if (stock == null) {
			stock = new ArrayList<>();
		}
		return stock; 
	}

	public void setQuantity(long quantity) {
		long numberOfItemsToAdd = quantity - getQuantity();
		addItems(numberOfItemsToAdd);
	}

	private void addItems(long numberOfItemsToAdd) {
		getStock().addAll(Stream.generate(StockItem::new).limit(numberOfItemsToAdd).collect(Collectors.toList()));
	}

	public void refill() {
		setQuantity(INITIAL_STOCK);
	}

	/**
	 * buys an item of this product if there is still at least one available
	 * @return an optional with the bought item if an item has been bought
	 */
	public Optional<StockItem> buy() {
		Optional<StockItem> item = getAvailableItems().findAny();
		if (item.isPresent()) {
			item.get().setAvailable(false);
		}
		return item;
	}



}