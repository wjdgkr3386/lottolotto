package com.example.demo.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.LottoDAO;
import com.example.demo.model.Lotto;
import com.example.demo.util.Util;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Transactional
@Service
public class LottoAPIService {

    @Autowired
    LottoDAO lottoDAO;

    public void LottoInit() {
        ZoneId koreaZone = ZoneId.of("Asia/Seoul");
        LocalDateTime nowDateTime = LocalDateTime.now(koreaZone);
        DayOfWeek dayOfWeek = nowDateTime.getDayOfWeek();

        // 시간 관계없이 토요일/일요일은 사전에 차단 (간소화 페이지 운영 기간)
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            System.out.println("주말(토/일)은 간소화 페이지 운영으로 API 호출을 시도하지 않습니다.");
            return;
        }

        try {
            int dbMaxDrwNo = lottoDAO.getMaxNumber();
            int latestDrwNo = Util.getLatestDrwNo();

            System.out.println("DB 최신 회차: " + dbMaxDrwNo);
            System.out.println("API 최신 회차: " + latestDrwNo);

            if (dbMaxDrwNo >= latestDrwNo) {
                System.out.println("이미 최신 데이터입니다. 호출 종료.");
                return;
            }

            HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

            ObjectMapper mapper = new ObjectMapper();

            for (int drwNo = dbMaxDrwNo + 1; drwNo <= latestDrwNo; drwNo++) {
                System.out.println("회차 호출 중: " + drwNo);

                String url = "https://dhlottery.co.kr/common.do?method=getLottoNumber&drwNo=" + drwNo;

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Accept", "application/json, text/plain, */*")
                    .header("Referer", "https://dhlottery.co.kr/")
                    .GET()
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String body = response.body();

                // 평일이라도 서버 상태에 따라 HTML이 올 수 있으므로 한 번 더 체크
                if (body == null || body.trim().startsWith("<!DOCTYPE") || body.trim().startsWith("<html")) {
                    System.err.println("[" + drwNo + "회차] JSON이 아닌 HTML 응답이 수신되었습니다. 작업을 중단합니다.");
                    break;
                }

                // JSON 파싱 및 데이터 처리
                JsonNode json = mapper.readTree(body);
                String returnValue = json.path("returnValue").stringValue();

                if (!"success".equals(returnValue)) {
                    System.err.println("[" + drwNo + "회차] API 응답 실패: " + returnValue);
                    break;
                }

                // 데이터 추출 및 저장 로직 (기존과 동일)
                Lotto lotto = new Lotto();
                lotto.setDrwNo(drwNo);
                lotto.setDrwtNo1(json.path("drwtNo1").intValue());
                lotto.setDrwtNo2(json.path("drwtNo2").intValue());
                lotto.setDrwtNo3(json.path("drwtNo3").intValue());
                lotto.setDrwtNo4(json.path("drwtNo4").intValue());
                lotto.setDrwtNo5(json.path("drwtNo5").intValue());
                lotto.setDrwtNo6(json.path("drwtNo6").intValue());
                lotto.setBnusNo(json.path("bnusNo").intValue());
                lotto.setWin1Money(json.path("firstWinamnt").longValue());
                lotto.setWin1Count(json.path("firstPrzwnerCo").intValue());
                lotto.setWin2Money(json.path("scndWinamnt").longValue());
                lotto.setWin2Count(json.path("scndPrzwnerCo").intValue());
                
                lottoDAO.lottoInsert(lotto);
                System.out.println(drwNo + "회차 저장 완료");
            }

        } catch (Exception e) {
            System.err.println("LottoInit 오류: " + e.getMessage());
        }
    }
}