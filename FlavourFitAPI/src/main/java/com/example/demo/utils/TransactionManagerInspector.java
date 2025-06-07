package com.example.demo.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
public class TransactionManagerInspector {

    private final ApplicationContext context;

    public TransactionManagerInspector(ApplicationContext context) {
        this.context = context;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("=== TransactionManager registrati ===");
        String[] names = context.getBeanNamesForType(PlatformTransactionManager.class);
        for (String name : names) {
            System.out.println("➡️ " + name + " : " + context.getBean(name).getClass().getName());
        }
    }
}
