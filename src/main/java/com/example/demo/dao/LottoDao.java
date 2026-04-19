package com.example.demo.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.model.Lotto;

@Mapper
public interface LottoDao {

    // 최근 저장된 회차
    int getMaxNumber();

    // 회차 입력
    int lottoInsert(Lotto lotto);

    // 번호별 출현 횟수 (1~45)
    List<Map<String, Object>> numGroup();

    // 핫번호 / 쿨번호
    Map<String, Object> getCoolHot();

    // 최장 미출현 번호
    int latestNumber();

    // 타이밍 번호 6개
    List<Integer> timeNumber();

    // 직전 회차 번호
    Map<String, Object> getLatestLotto();

    // 번호별 현재 미출현 간격 (gap_now)
    List<Map<String, Object>> getGapNowList();

    // 번호별 출현 회차 전체 목록 (avg_gap 동적 계산용)
    List<Map<String, Object>> getAllAppearances();
}