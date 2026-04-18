package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.LottoDAO;
import com.example.demo.model.Lotto;

@Service
@Transactional
public class LottoService {

	@Autowired
	LottoDAO lottoDAO;
	
	// 회차 입력
	public int LottoInsert(Lotto lotto){
		int result = lottoDAO.lottoInsert(lotto);
		return result;
	}
}
