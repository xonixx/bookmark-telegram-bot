package com.cmlteam.bookmark_telegram_bot

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import java.util.concurrent.Executor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.util.concurrent.Executors

@Configuration
@EnableAutoConfiguration(exclude = [JacksonAutoConfiguration::class])
@ConfigurationPropertiesScan(basePackages = ["com.cmlteam"])
@EnableScheduling
@EnableAsync
@EnableMongoAuditing
class AppConfig : AsyncConfigurer, SchedulingConfigurer {
    @Bean
    fun messageSource(): ReloadableResourceBundleMessageSource {
        val messageSource = ReloadableResourceBundleMessageSource()
        messageSource.setBasename("classpath:messages")
        messageSource.setCacheSeconds(3600) // refresh cache once per hour
        return messageSource
    }

    override fun getAsyncExecutor(): Executor? {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 3
        executor.maxPoolSize = 20
        executor.setQueueCapacity(100)
        executor.setThreadNamePrefix("AppAsyncExecutor-")
        executor.initialize()
        return executor
    }

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler? {
        return SimpleAsyncUncaughtExceptionHandler()
    }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor())
    }

    @Bean(destroyMethod = "shutdown")
    fun taskExecutor(): Executor {
        return Executors.newScheduledThreadPool(10)
    }
}