package com.example.demo.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class Util {

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
}
