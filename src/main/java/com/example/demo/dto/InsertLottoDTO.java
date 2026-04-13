package com.example.demo.dto;

public class InsertLottoDTO {

	public InsertLottoDTO() {}
	
    private int drwNo;       // 회차
    private int drwtNo1;     // 번호1
    private int drwtNo2;     // 번호2
    private int drwtNo3;     // 번호3
    private int drwtNo4;     // 번호4
    private int drwtNo5;     // 번호5
    private int drwtNo6;     // 번호6
    private int bnusNo;      // 보너스 번호
    private Long win1Money;     // 1등 당첨금 (선택)
    private Integer win1Count;  // 1등 당첨자수 (선택)
    private Long win2Money;     // 2등 당첨금 (선택)
    private Integer win2Count;  // 2등 당첨자수 (선택)

    public int getDrwNo() { return drwNo; }
    public void setDrwNo(int drwNo) { this.drwNo = drwNo; }

    public int getDrwtNo1() { return drwtNo1; }
    public void setDrwtNo1(int drwtNo1) { this.drwtNo1 = drwtNo1; }

    public int getDrwtNo2() { return drwtNo2; }
    public void setDrwtNo2(int drwtNo2) { this.drwtNo2 = drwtNo2; }

    public int getDrwtNo3() { return drwtNo3; }
    public void setDrwtNo3(int drwtNo3) { this.drwtNo3 = drwtNo3; }

    public int getDrwtNo4() { return drwtNo4; }
    public void setDrwtNo4(int drwtNo4) { this.drwtNo4 = drwtNo4; }

    public int getDrwtNo5() { return drwtNo5; }
    public void setDrwtNo5(int drwtNo5) { this.drwtNo5 = drwtNo5; }

    public int getDrwtNo6() { return drwtNo6; }
    public void setDrwtNo6(int drwtNo6) { this.drwtNo6 = drwtNo6; }

    public int getBnusNo() { return bnusNo; }
    public void setBnusNo(int bnusNo) { this.bnusNo = bnusNo; }

    public Long getWin1Money() { return win1Money; }
    public void setWin1Money(Long win1Money) { this.win1Money = win1Money; }

    public Integer getWin1Count() { return win1Count; }
    public void setWin1Count(Integer win1Count) { this.win1Count = win1Count; }

    public Long getWin2Money() { return win2Money; }
    public void setWin2Money(Long win2Money) { this.win2Money = win2Money; }

    public Integer getWin2Count() { return win2Count; }
    public void setWin2Count(Integer win2Count) { this.win2Count = win2Count; }
    
	@Override
	public String toString() {
		return "InsertLottoDTO [drwNo=" + drwNo + ", drwtNo1=" + drwtNo1 + ", drwtNo2=" + drwtNo2 + ", drwtNo3="
				+ drwtNo3 + ", drwtNo4=" + drwtNo4 + ", drwtNo5=" + drwtNo5 + ", drwtNo6=" + drwtNo6 + ", bnusNo="
				+ bnusNo + ", win1Money=" + win1Money + ", win1Count=" + win1Count + ", win2Money=" + win2Money
				+ ", win2Count=" + win2Count + "]";
	}
    
    
    
}