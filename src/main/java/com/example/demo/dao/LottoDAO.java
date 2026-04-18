package com.example.demo.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.model.Lotto;

@Mapper
public interface LottoDAO {

    public int getMaxNumber();

    public int lottoInsert(Lotto lotto);

    public List<Map<String, Object>> numGroup();

    public Map<String, Object> getCoolHot();

    public int latestNumber();

    public List<Integer> timeNumber();
}