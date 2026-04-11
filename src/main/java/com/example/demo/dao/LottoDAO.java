package com.example.demo.dao;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.model.Lotto;

@Mapper
public interface LottoDAO {

	public int getMaxNumber();
	
	public int lottoInsert(Lotto lotto);
}
