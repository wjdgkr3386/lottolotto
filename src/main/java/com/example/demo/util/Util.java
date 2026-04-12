package com.example.demo.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Util {

	// 현재 가장 최신 회차 계산
	public static int getLatestDrwNo() {
	    ZoneId koreaZone = ZoneId.of("Asia/Seoul");
	    LocalDate now = LocalDate.now(koreaZone);
	    
	    // 로또 시작일 (1회차 추첨일)
	    LocalDate baseDate = LocalDate.of(2002, 12, 7);

	    // 기준일로부터 현재까지 전체 경과 주수 계산
	    long weeks = ChronoUnit.WEEKS.between(baseDate, now);
	    int drwNo = (int) weeks + 1;

	    DayOfWeek dayOfWeek = now.getDayOfWeek();

	    // 토요일 또는 일요일인 경우, 아직 API에 반영되지 않았으므로 1회차 차감
	    if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
	        drwNo--;
	    }

	    return drwNo;
	}
	
	// 연속된 숫자가 3개 이상인지 체크
	public String checkConsecutiveNumbers(Map<String, Integer> lottoMap) {
	    List<Integer> numbers = new ArrayList<>(lottoMap.values());
	    Collections.sort(numbers);

	    int consecutiveCount = 1;

	    for (int i = 0; i < numbers.size() - 1; i++) {
	        if (numbers.get(i) + 1 == numbers.get(i + 1)) {
	            consecutiveCount++;
	            if (consecutiveCount >= 3) {
	                return "연속된 숫자가 3개 이상 존재합니다 : " + numbers.toString();
	            }
	        } else {
	            consecutiveCount = 1; // 연속이 끊기면 초기화
	        }
	    }

	    return null; // 통과
	}
	
	// 범위(89~184) 체크
	public String checkSumRange(Map<String, Integer> lottoMap) {
	    int sum = 0;
	    List<Integer> list = new ArrayList<>(lottoMap.values());
	    for (int num : list) {
	        sum += num;
	    }

	    if (sum < 89 || sum > 184) {
	    	return "합계 범위를 벗어났습니다 (89~184) : " + sum + " " + list;
	    }

	    return null;
	}
	
	// 이전 회차와 이번 회차에 중복된 숫자가 3개 이상인지 체크
	public String checkDuplicateNumbers(Map<String, Integer> lottoMap1, Map<String, Integer> lottoMap2) {
	    List<Integer> duplicates = lottoMap2.values().stream()
	            .filter(lottoMap1.values()::contains)
	            .collect(Collectors.toList());

	    if (duplicates.size() >= 3) {
	        return "중복된 숫자가 3개 이상 존재합니다 : " + duplicates;
	    }

	    return null;
	}
}
