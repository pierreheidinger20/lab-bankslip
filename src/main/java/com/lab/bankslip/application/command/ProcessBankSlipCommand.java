package com.lab.bankslip.application.command;

import com.lab.bankslip.application.command.dto.SaveBankSlipDto;
import com.lab.bankslip.domain.model.BankSlip;

public record ProcessBankSlipCommand(SaveBankSlipDto saveBankSlipDto)
        implements Command<BankSlip> {
}
