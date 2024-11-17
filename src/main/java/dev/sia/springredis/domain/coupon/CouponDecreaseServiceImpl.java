package dev.sia.springredis.domain.coupon;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sia.springredis.aop.DistributedLock;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponDecreaseServiceImpl implements CouponDecreaseService {
	private final CouponRepository couponRepository;

	@Override
	@Transactional
	public void couponDecrease(Long couponId) {
		Coupon coupon = couponRepository.findById(couponId)
			.orElseThrow(IllegalArgumentException::new);
		coupon.decrease();
	}

	@Override
	@DistributedLock(key = "#couponName")
	public void couponDecrease(String couponName, Long couponId) {
		Coupon coupon = couponRepository.findById(couponId)
			.orElseThrow(IllegalArgumentException::new);

		coupon.decrease();
	}
}
