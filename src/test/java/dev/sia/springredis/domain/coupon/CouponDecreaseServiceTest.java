package dev.sia.springredis.domain.coupon;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CouponDecreaseServiceTest {

	@Autowired
	CouponDecreaseServiceImpl couponDecreaseService;

	@Autowired
	CouponRepository couponRepository;

	Coupon coupon;


	@BeforeEach
	void setUp() {
		coupon = new Coupon("SIA_001", 100L);
		couponRepository.save(coupon);
	}

	/**
	 * Feature: 쿠폰 차감 동시성 테스트
	 * Given SIA_001 라는 이름의 쿠폰 100장 등록
	 * Scenario: 100장의 쿠폰을 100명의 사용자가 동시에 접근해 발급 요청
	 *           Lock의 이름은 쿠폰명(SIA_001)으로 설정함
	 * Then 사용자들의 요청만큼 정확히 쿠폰의 개수가 차감되어야 함
	 */
	@Test
	void 쿠폰차감_분산락_적용_동시성100명_테스트() throws InterruptedException {
		int numberOfThreads = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);

		for (int i = 0; i < numberOfThreads; i++) {
			executorService.submit(() -> {
				try {
					couponDecreaseService.couponDecrease(coupon.getName(), coupon.getId());
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		Coupon persistCoupon = couponRepository.findById(coupon.getId())
			.orElseThrow(IllegalArgumentException::new);

		assertThat(persistCoupon.getAvailableStock()).isZero();
	}

	/**
	 * Feature: 쿠폰 차감 동시성 테스트
	 * Given SIA_001 라는 이름의 쿠폰 100장 등록
	 * Scenario: 100장의 쿠폰을 100명의 사용자가 동시에 접근해 발급 요청
	 * Then 동시성 문제로 사용자들의 요청만큼 정확히 쿠폰의 개수가 차감되지 않음
	 */
	@Test
	void 쿠폰차감_분산락_미적용_동시성100명_테스트() throws InterruptedException {
		int numberOfThreads = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);

		for (int i = 0; i < numberOfThreads; i++) {
			executorService.submit(() -> {
				try {
					// 분산락 미적용 메서드 호출
					couponDecreaseService.couponDecrease(coupon.getId());
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		Coupon persistCoupon = couponRepository.findById(coupon.getId())
			.orElseThrow(IllegalArgumentException::new);

		assertThat(persistCoupon.getAvailableStock()).isNotZero();
	}

}
