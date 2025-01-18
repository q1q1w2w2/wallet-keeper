package com.project.wallet_keeper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAspectJAutoProxy
@EnableScheduling
@EnableCaching
@SpringBootApplication
public class WalletKeeperApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalletKeeperApplication.class, args);
	}

}
