package com.project.wallet_keeper.util.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SaveRegularIncomesEvent extends ApplicationEvent {
    public SaveRegularIncomesEvent(Object source) {
        super(source);
    }
}
