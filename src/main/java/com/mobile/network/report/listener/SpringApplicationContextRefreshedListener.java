package com.mobile.network.report.listener;

import com.mobile.network.report.facade.CommutatorWorkFacade;
import com.mobile.network.report.facade.CustomerCreateFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Класс слушающий обновление спринг контекста, и когда контекст собран вызывает фасады генерирующие записи в бд
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringApplicationContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {
    private static boolean executed = false;
    private final CommutatorWorkFacade commutatorFacade;
    private final CustomerCreateFacade customerFacade;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!executed) {
            customerFacade.generateAndSaveCustomers();
            commutatorFacade.generateAndSaveRecords();
            executed = true;
        }
    }
}

