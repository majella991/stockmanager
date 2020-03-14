package stock.manager.model;

import java.util.ArrayList;
import java.util.List;

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
	
	@OneToMany(targetEntity=StockItem.class, fetch=FetchType.EAGER)
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
	public int getQuantity() {
		int n = 0;
		for (int i = 0; i < getStock().size(); i++) {
			if (getStock().get(i).isAvailable()) {
				n++;
			}
		}
		return n;
	}

	private List<StockItem> getStock() {
		if (stock == null) {
			stock = new ArrayList<>();
		}
		return stock; 
	}

	public void setQuantity(int quantity) {
		int numberOfItemsToAdd = quantity - getQuantity();
		addItems(numberOfItemsToAdd);
	}

	private void addItems(int numberOfItemsToAdd) {
		for (int i = 0; i < numberOfItemsToAdd; i++) {
			getStock().add(new StockItem());
		}
	}

	public void refill() {
		setQuantity(INITIAL_STOCK);
	}

	public StockItem buy() {
		StockItem item = getAvailableItem();
		if (item != null) {
			item.setAvailable(false);
		}
		return item;
	}

	private StockItem getAvailableItem() {
		for (StockItem item : getStock()) {
			if (item.isAvailable()) {
				return item;
			}
		}
		return null;
	}


}