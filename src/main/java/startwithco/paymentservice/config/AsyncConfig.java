package startwithco.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "paymentExecutor")
    public Executor paymentExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);               // 항상 유지할 최소 스레드 수
        executor.setMaxPoolSize(30);                // 최대 스레드 수
        executor.setQueueCapacity(500);             // 대기 큐 용량
        executor.setThreadNamePrefix("payment-");   // 디버깅용 접두사
        executor.setWaitForTasksToCompleteOnShutdown(true); // 종료 시 작업 완료 대기
        executor.setAwaitTerminationSeconds(60);    // 최대 대기 시간
        executor.initialize();

        return executor;
    }
}
