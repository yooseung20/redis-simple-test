package dev.sia.springredis.domain.coupon;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 쿠폰 이름
	 */
	private String name;

	/**
	 * 사용 가능한 재고수량
	 */
	private Long availableStock;

	public Coupon(String name, Long availableStock) {
		this.name = name;
		this.availableStock = availableStock;
	}

	public void decrease() {
		validateStockCount();
		this.availableStock -= 1;
	}

	private void validateStockCount() {
		if (availableStock < 1) {
			throw new IllegalArgumentException();
		}
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Long getAvailableStock() {
		return availableStock;
	}
}