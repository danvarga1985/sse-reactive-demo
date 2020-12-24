package com.danvarga.SSE_MVC_Demo;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

//Enables asynchronous execution.
@EnableAsync
@SpringBootApplication
public class SSE_MVC_DemoApplication implements AsyncConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(SSE_MVC_DemoApplication.class, args);
	}

	// Executor config.
	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(100);
		executor.setQueueCapacity(5);
		executor.initialize();
		return executor;
	}

	// Dedicated Exception handler for exceptions thrown from the asynchronous execution.
	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new SimpleAsyncUncaughtExceptionHandler();
	}
}



