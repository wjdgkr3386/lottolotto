package com.example.demo.model;

/**
 * 번호별 통계 피처 DTO
 * 알고리즘 추천에 사용되는 모든 계산값을 담는다.
 */
public class NumFeature {

    private int num;          // 번호 (1~45)
    private int freq;         // 누적 출현 횟수
    private int gapNow;       // 현재 미출현 간격 (max_drw_no - last_drw_no)
    private double avgGap;    // 역대 평균 출현 간격
    private double stdGap;    // 표준편차
    private double consecRate;// 연속 출현 비율
    private int inTiming;     // gap_now가 avg_gap ± std_gap 범위 내이면 1, 아니면 0
    private double overdue;   // (gap_now - avg_gap) / avg_gap  (음수는 0으로 고정)
    private double score;     // 최종 스코어
    private boolean isPrev;   // 직전 회차에 출현했는지

    public NumFeature() {}

    public int getNum()               { return num; }
    public void setNum(int num)       { this.num = num; }

    public int getFreq()              { return freq; }
    public void setFreq(int freq)     { this.freq = freq; }

    public int getGapNow()            { return gapNow; }
    public void setGapNow(int g)      { this.gapNow = g; }

    public double getAvgGap()         { return avgGap; }
    public void setAvgGap(double v)   { this.avgGap = v; }

    public double getStdGap()         { return stdGap; }
    public void setStdGap(double v)   { this.stdGap = v; }

    public double getConsecRate()     { return consecRate; }
    public void setConsecRate(double v){ this.consecRate = v; }

    public int getInTiming()          { return inTiming; }
    public void setInTiming(int v)    { this.inTiming = v; }

    public double getOverdue()        { return overdue; }
    public void setOverdue(double v)  { this.overdue = v; }

    public double getScore()          { return score; }
    public void setScore(double v)    { this.score = v; }

    public boolean isPrev()           { return isPrev; }
    public void setPrev(boolean v)    { this.isPrev = v; }
}