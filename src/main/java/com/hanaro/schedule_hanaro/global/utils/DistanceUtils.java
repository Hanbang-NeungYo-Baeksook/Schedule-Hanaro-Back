package com.hanaro.schedule_hanaro.global.utils;

import org.springframework.stereotype.Component;

@Component
public class DistanceUtils {
	private static final int EARTH_RADIUS = 6371; // 지구 반지름

	// lat : 위도, lon : 경도
	public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
			+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
			* Math.sin(dLon / 2) * Math.sin(dLon / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return EARTH_RADIUS * c; // 결과 거리 (km)
	}
}
