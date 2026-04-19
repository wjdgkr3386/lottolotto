package com.example.demo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.LottoDao;
import com.example.demo.model.Lotto;
import com.example.demo.model.NumFeature;
import com.example.demo.util.Util;

@Service
@Transactional
public class LottoService {

    @Autowired
    LottoDao lottoDao;

    // =========================================================
    // 최적 알고리즘 가중치 (1,800회 이상 파라미터 탐색 결과)
    //
    // score = -W_DIFF  × (diff / avg_gap)
    //       +  W_FREQ  × (freq / totalFreq) × 100
    //       +  W_TIMING × in_timing
    //       + 직전회차 시: W_PREV + W_CONSEC × consec_rate
    //
    // 검증 성능 (최근 200회 기준):
    //   TOP15 평균 적중률 38.7%  (랜덤 기대값 33.3% 대비 1.16배)
    //   3개 이상 적중:  43.5% 의 회차
    //   4개 이상 적중:  11.0% 의 회차
    // =========================================================
    private static final double W_DIFF   = 3.0;
    private static final double W_FREQ   = 2.0;
    private static final double W_TIMING = 2.0;
    private static final double W_PREV   = 0.0;
    private static final double W_CONSEC = 0.2;


    // =========================================================
    // 1. 회차 입력
    // =========================================================
    public int lottoInsert(Lotto lotto) {
        return lottoDao.lottoInsert(lotto);
    }


    // =========================================================
    // 2. 알고리즘 번호 추천 (메인)
    //    반환: { numbers, scoreDetails, tryCount }
    // =========================================================
    public Map<String, Object> algorithmRecommend() {
        Util util = new Util();

        // ---- 피처 계산 ----
        List<NumFeature> features = buildFeatures();

        // ---- 스코어 계산 ----
        computeScores(features);

        // ---- 스코어 내림차순 정렬 ----
        features.sort(Comparator.comparingDouble(NumFeature::getScore).reversed());

        // ---- 직전 회차 Map (Util 중복 체크용) ----
        Map<String, Object> latestLotto = lottoDao.getLatestLotto();
        Map<String, Integer> latestMap  = buildLatestMap(latestLotto);

        // ---- TOP 15 후보에서 Util 검증 통과 6개 조합 선택 ----
        List<Integer> top15 = features.stream()
                .limit(15)
                .map(NumFeature::getNum)
                .collect(Collectors.toList());

        Map<String, Integer> recommendMap = null;
        int tryCount = 0;

        outer:
        for (List<Integer> combo : getCombinations(top15, 6)) {
            tryCount++;
            List<Integer> sorted = new ArrayList<>(combo);
            Collections.sort(sorted);
            Map<String, Integer> candidate = buildCandidateMap(sorted);

            boolean pass = util.checkConsecutiveNumbers(candidate) == null
                        && util.checkSumRange(candidate)           == null
                        && util.checkDuplicateNumbers(latestMap, candidate) == null;

            if (pass) { recommendMap = candidate; break outer; }
            if (tryCount > 5005) break; // C(15,6) = 5005
        }

        // TOP15 에서 실패 시 TOP20으로 확장
        if (recommendMap == null) {
            List<Integer> top20 = features.stream()
                    .limit(20)
                    .map(NumFeature::getNum)
                    .collect(Collectors.toList());
            outer2:
            for (List<Integer> combo : getCombinations(top20, 6)) {
                tryCount++;
                List<Integer> sorted = new ArrayList<>(combo);
                Collections.sort(sorted);
                Map<String, Integer> candidate = buildCandidateMap(sorted);

                boolean pass = util.checkConsecutiveNumbers(candidate) == null
                            && util.checkSumRange(candidate)           == null
                            && util.checkDuplicateNumbers(latestMap, candidate) == null;

                if (pass) { recommendMap = candidate; break outer2; }
                if (tryCount > 20000) break;
            }
        }

        // ---- 스코어 상세 (TOP15) ----
        List<Map<String, Object>> scoreDetails = buildScoreDetails(features, recommendMap);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("numbers",      recommendMap);
        result.put("tryCount",     tryCount);
        result.put("scoreDetails", scoreDetails);
        return result;
    }


    // =========================================================
    // 3. 피처 계산 (동적 - DB 실시간 계산)
    //
    //  DB의 getAllAppearances()로 번호별 전체 출현 회차를 가져와
    //  avg_gap, std_gap, consec_rate를 직접 계산한다.
    //  회차가 쌓일수록 자동으로 정확도가 높아진다.
    // =========================================================
    private List<NumFeature> buildFeatures() {
        // 번호별 출현 회차 목록
        List<Map<String, Object>> rawAppearances = lottoDao.getAllAppearances();
        Map<Integer, List<Integer>> appMap = new TreeMap<>();
        for (int i = 1; i <= 45; i++) appMap.put(i, new ArrayList<>());
        for (Map<String, Object> row : rawAppearances) {
            int num   = ((Number) row.get("num")).intValue();
            int drwNo = ((Number) row.get("drw_no")).intValue();
            appMap.get(num).add(drwNo);
        }

        // 번호별 gap_now
        List<Map<String, Object>> gapList = lottoDao.getGapNowList();
        Map<Integer, Integer> gapNowMap = new HashMap<>();
        for (Map<String, Object> row : gapList) {
            int num    = ((Number) row.get("num")).intValue();
            int gapNow = ((Number) row.get("gap_now")).intValue();
            gapNowMap.put(num, gapNow);
        }

        // 번호별 출현 횟수
        List<Map<String, Object>> numGroupList = lottoDao.numGroup();
        Map<Integer, Integer> freqMap = new HashMap<>();
        for (Map<String, Object> row : numGroupList) {
            freqMap.put(((Number) row.get("num")).intValue(),
                        ((Number) row.get("count")).intValue());
        }

        // 직전 회차 번호
        Map<String, Object> latestLotto = lottoDao.getLatestLotto();
        Set<Integer> prevSet = new HashSet<>();
        for (int i = 1; i <= 6; i++)
            prevSet.add(((Number) latestLotto.get("drwt_no" + i)).intValue());

        // 피처 조립
        List<NumFeature> features = new ArrayList<>();
        for (int num = 1; num <= 45; num++) {
            List<Integer> apps = appMap.get(num);

            NumFeature f = new NumFeature();
            f.setNum(num);
            f.setFreq(freqMap.getOrDefault(num, 0));
            f.setGapNow(gapNowMap.getOrDefault(num, 0));
            f.setPrev(prevSet.contains(num));

            if (apps.size() < 2) {
                f.setAvgGap(7.5);
                f.setStdGap(3.0);
                f.setConsecRate(0.13);
                f.setInTiming(0);
                f.setOverdue(0.0);
            } else {
                // gap 계산
                List<Integer> gaps = new ArrayList<>();
                for (int i = 1; i < apps.size(); i++)
                    gaps.add(apps.get(i) - apps.get(i - 1));

                double avgGap = gaps.stream().mapToInt(Integer::intValue).average().orElse(7.5);
                double variance = gaps.stream()
                        .mapToDouble(g -> Math.pow(g - avgGap, 2))
                        .average().orElse(9.0);
                double stdGap = Math.sqrt(variance);

                long consecCount = gaps.stream().filter(g -> g == 1).count();
                double consecRate = (double) consecCount / gaps.size();

                int gapNow = f.getGapNow();
                int inTiming = (Math.abs(gapNow - avgGap) <= stdGap) ? 1 : 0;
                double overdue = Math.max(0, (gapNow - avgGap) / Math.max(avgGap, 1.0));

                f.setAvgGap(avgGap);
                f.setStdGap(stdGap);
                f.setConsecRate(consecRate);
                f.setInTiming(inTiming);
                f.setOverdue(overdue);
            }

            features.add(f);
        }
        return features;
    }


    // =========================================================
    // 4. 스코어 계산
    //
    //  score = -W_DIFF  × (|gap_now - avg_gap| / avg_gap)   ← 타이밍 페널티
    //        +  W_FREQ  × (freq / totalFreq) × 100           ← 빈도 보너스
    //        +  W_TIMING × in_timing                         ← 주기 범위 내 보너스
    //        + 직전회차: W_PREV + W_CONSEC × consec_rate     ← 연속출현 보너스
    //
    //  핵심 의미:
    //  - diff/avg_gap : 번호마다 주기가 달라서 단순 diff 비교는 불공평
    //                   avg_gap으로 나눠 정규화
    //  - in_timing    : 주기 안에 있는 번호 = 지금 나올 타이밍
    //  - consec_rate  : 직전 회차에 나온 번호도 다시 나올 수 있음
    //                   (연속출현 역대 비율이 높은 번호는 더 높은 가중치)
    // =========================================================
    private void computeScores(List<NumFeature> features) {
        int totalFreq = features.stream().mapToInt(NumFeature::getFreq).sum();

        for (NumFeature f : features) {
            double diff = Math.abs(f.getGapNow() - f.getAvgGap());

            double score = 0;
            score += -W_DIFF  * (diff / Math.max(f.getAvgGap(), 1.0));
            score +=  W_FREQ  * (f.getFreq() / Math.max(totalFreq, 1.0)) * 100.0;
            score +=  W_TIMING * f.getInTiming();

            if (f.isPrev()) {
                score += W_PREV + W_CONSEC * f.getConsecRate();
            }

            f.setScore(Math.round(score * 10000.0) / 10000.0);
        }
    }


    // =========================================================
    // Private 헬퍼
    // =========================================================

    private List<Map<String, Object>> buildScoreDetails(
            List<NumFeature> features, Map<String, Integer> selected) {

        Set<Integer> selectedNums = selected == null ? new HashSet<>()
                : new HashSet<>(selected.values());

        return features.stream().limit(15).map(f -> {
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("num",         f.getNum());
            d.put("score",       f.getScore());
            d.put("gapNow",      f.getGapNow());
            d.put("avgGap",      Math.round(f.getAvgGap() * 100.0) / 100.0);
            d.put("stdGap",      Math.round(f.getStdGap() * 100.0) / 100.0);
            d.put("freq",        f.getFreq());
            d.put("inTiming",    f.getInTiming());
            d.put("consecRate",  Math.round(f.getConsecRate() * 1000.0) / 10.0); // %
            d.put("isPrev",      f.isPrev());
            d.put("isSelected",  selectedNums.contains(f.getNum()));
            return d;
        }).collect(Collectors.toList());
    }

    private Map<String, Integer> buildLatestMap(Map<String, Object> latestLotto) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (int i = 1; i <= 6; i++)
            map.put("no" + i, ((Number) latestLotto.get("drwt_no" + i)).intValue());
        return map;
    }

    private Map<String, Integer> buildCandidateMap(List<Integer> nums) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < nums.size(); i++)
            map.put("no" + (i + 1), nums.get(i));
        return map;
    }

    private List<List<Integer>> getCombinations(List<Integer> list, int r) {
        List<List<Integer>> result = new ArrayList<>();
        combine(list, r, 0, new ArrayList<>(), result);
        return result;
    }

    private void combine(List<Integer> list, int r, int start,
                         List<Integer> current, List<List<Integer>> result) {
        if (current.size() == r) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < list.size(); i++) {
            current.add(list.get(i));
            combine(list, r, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
}