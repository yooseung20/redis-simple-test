package dev.sia.springredis.domain.coupon;

public interface CouponDecreaseService {
	/**
	 * 분산락 적용 안한 메서드
	 * couponId
	 */
	void couponDecrease(Long couponId);

	/**
	 * 분산락 적용 메서드
	 * couponName :Lock key 값으로 활용
	 * couponId
	 */
	void couponDecrease(String couponName, Long couponId);
}
