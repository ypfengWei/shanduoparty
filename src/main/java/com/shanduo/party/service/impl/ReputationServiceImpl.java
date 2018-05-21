package com.shanduo.party.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.party.controller.ActivityController;
import com.shanduo.party.mapper.ShanduoReputationMapper;
import com.shanduo.party.service.ReputationService;

@Service
@Transactional(rollbackFor = Exception.class)
public class ReputationServiceImpl implements ReputationService {

	private static final Logger log = LoggerFactory.getLogger(ActivityController.class);
	
	@Autowired
	private ShanduoReputationMapper shanduoReputationMapper;
	

}
