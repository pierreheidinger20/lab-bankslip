package com.lab.bankslip.application.command;

public interface CommandHandler<C extends Command<R>, R> {
    R handle(C command);
}
