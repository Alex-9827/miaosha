package com.miaosha.service.impl;

import com.miaosha.dao.SequenceDOMapper;
import com.miaosha.dataobject.SequenceDO;
import com.miaosha.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SequenceServiceImpl implements SequenceService {
    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Override
    public Integer offerSequenceByName(String name) {
        SequenceDO sequenceDO = sequenceDOMapper.selectByPrimaryKey(name);
        Integer sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequence + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKey(sequenceDO);
        return sequence;
    }
}
