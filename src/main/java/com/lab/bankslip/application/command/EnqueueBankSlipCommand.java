package com.lab.bankslip.application.command;

import com.lab.bankslip.application.command.dto.BankSlipDto;
import com.lab.bankslip.application.command.dto.EnqueueBankSlipResultDto;

public record EnqueueBankSlipCommand(BankSlipDto bankSlipDto)
        implements Command<EnqueueBankSlipResultDto> {
}
