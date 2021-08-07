package com.cts.pension.disbursement.service;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cts.pension.disbursement.exception.AadharNumberNotFound;
import com.cts.pension.disbursement.exception.AuthorizationException;
import com.cts.pension.disbursement.feignclient.PensionDisbursementFeignClient;
import com.cts.pension.disbursement.model.PensionerDetail;
import com.cts.pension.disbursement.model.ProcessPensionInput;
import com.cts.pension.disbursement.model.ProcessPensionResponse;

@Service
public class PensionDisbursementServiceImpl implements PensionDisbursementService {
	
	@Autowired
	private PensionDisbursementFeignClient pensionDisbursementFeignClient;
	private static final Logger LOGGER = LoggerFactory.getLogger(PensionDisbursementServiceImpl.class);
	
	
	@Override
	public ProcessPensionResponse getResponce(String token,ProcessPensionInput processPensionInput) throws AuthorizationException, AadharNumberNotFound
	{
		PensionerDetail pensionerDetail = null;
		try
		{
		pensionerDetail = pensionDisbursementFeignClient.getPensionerDetailByAadhaar(token, processPensionInput.getAadharNumber());
		}
		catch(AadharNumberNotFound e)
		{
			throw new AadharNumberNotFound("Aadhar Number Not found");
			
		}
		ProcessPensionResponse processPensionResponse = new ProcessPensionResponse();
		double serviceCharge = processPensionInput.getBankCharge();
		
		double checkAmount=0;
		if(pensionerDetail.getBankType().equalsIgnoreCase("public"))
		{
			checkAmount = 500;
		}
		else if(pensionerDetail.getBankType().equalsIgnoreCase("private"))
		{
			checkAmount = 550;
		}
		
		LOGGER.debug("checkamounttt"+checkAmount);
		LOGGER.debug("serviceChargee"+serviceCharge);
		
		if(checkAmount == serviceCharge)
		{
			processPensionResponse.setProcessPensionStatusCode(10);
		}
		else
		{
			processPensionResponse.setProcessPensionStatusCode(21);
		}
		
		LOGGER.debug("processPensionResponce........."+processPensionResponse);
		return processPensionResponse;
	}

}
