package com.project.wallet_keeper.util.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SaveRegularExpensesEvent extends ApplicationEvent {
    public SaveRegularExpensesEvent(Object source) {
        super(source);
    }
}
