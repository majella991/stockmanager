package stock.manager.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String name;
	
	private Item[] stock;
	
	public Product() {
		setQuantity(100);
	}

	public Product(String name) {
		setName(name);
		setQuantity(100);
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
	
	public int getQuantity() {
		return stock.length;
	}
	
	private void setQuantity(int quantity) {
		this.stock = new Item[quantity];
		for (int i = 0; i < quantity; i++) {
			this.stock[i] = new Item();
		}
	}


}