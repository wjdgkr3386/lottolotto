package com.example.demo.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.model.Lotto;

@Mapper
public interface LottoDAO {

	public int getMaxNumber();
	
	public void lottoInsert(Lotto lotto);

	public Map<Integer, Integer> getNumGroup();
	
	public Map<Integer, Integer> getCoolHot();
	
	
	
}
