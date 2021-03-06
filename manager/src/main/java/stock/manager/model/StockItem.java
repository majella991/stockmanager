package stock.manager.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class StockItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private long availableFrom;

	public boolean isAvailable() {
		return availableFrom < System.currentTimeMillis();
	}

	public void setAvailable(boolean available) {
		if (available) {
			availableFrom = System.currentTimeMillis();
		} else {
			availableFrom = Long.MAX_VALUE;
		}
	}

	public void reserve(int seconds) {
		availableFrom = System.currentTimeMillis() + seconds*1000;
	}

}
