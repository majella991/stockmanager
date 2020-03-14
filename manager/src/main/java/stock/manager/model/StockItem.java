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
	
	private long availableUntil;

	public boolean isAvailable() {
		if (availableUntil == 0) {
			return true;
		}
		return availableUntil > System.currentTimeMillis();
	}

	public void setAvailable(boolean available) {
		if (available) {
			availableUntil = 0;
		} else {
			availableUntil = System.currentTimeMillis();
		}
	}

}
