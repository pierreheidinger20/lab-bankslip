package com.lab.bankslip.domain.service;

import com.lab.bankslip.domain.model.BankSlip;
import com.lab.bankslip.domain.model.BankSlipFailed;

public interface BankSlipWebhook {
    void notify(BankSlip bankSlip);
    void notifyFailure(BankSlipFailed bankSlip,String error);
}
