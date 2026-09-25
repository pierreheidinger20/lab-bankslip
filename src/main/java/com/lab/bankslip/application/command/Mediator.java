package com.lab.bankslip.application.command;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.core.ResolvableType;

@Component
public class Mediator {

    private final Map<Class<?>, CommandHandler<?, ?>> handlers;

    public Mediator(List<CommandHandler<?, ?>> commandHandlers) {

        this.handlers = commandHandlers.stream()
                .collect(Collectors.toUnmodifiableMap(
                        this::getCommandType,
                        Function.identity()
                ));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <R> R send(Command<R> command) {

        CommandHandler handler = handlers.get(command.getClass());

        if (handler == null) {
            throw new IllegalArgumentException(
                    "No handler registered for "
                            + command.getClass().getName()
            );
        }

        return (R) handler.handle(command);
    }

    private Class<?> getCommandType(
            CommandHandler<?, ?> handler) {

        ResolvableType type = ResolvableType
                .forClass(handler.getClass())
                .as(CommandHandler.class);

        Class<?> commandType = type
                .getGeneric(0)
                .resolve();

        if (commandType == null) {
            throw new IllegalStateException(
                    "Could not resolve command type for handler "
                            + handler.getClass().getName()
            );
        }

        return commandType;
    }
}