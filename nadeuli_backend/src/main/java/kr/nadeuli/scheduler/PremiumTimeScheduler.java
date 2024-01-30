package kr.nadeuli.scheduler;

import kr.nadeuli.service.member.MemberService;
import kr.nadeuli.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

@Log4j2
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PremiumTimeScheduler {
    private final ProductService productService;
    private final ThreadPoolTaskScheduler scheduler;
    private ScheduledFuture<?> scheduledFuture;

    @Autowired
    public PremiumTimeScheduler(@Lazy ProductService productService, ThreadPoolTaskScheduler scheduler) {
        this.productService = productService;
        this.scheduler = scheduler;
    }

    public void startPremiumTimeScheduler(Long productId) {
        // 초기 스케줄링 로직
        scheduledFuture = scheduler.schedule(() -> updatePremiumTime(productId), new CronTrigger("0 0 * * * *"));
        log.info("스케줄링 시작");
    }

    public void updatePremiumTime(Long productId) {
        try {
            if (!productService.updatePremiumTime(productId)) {
                stopPremiumTimeScheduler();
                log.info("스케일러 돌아가는중~");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void stopPremiumTimeScheduler() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(true);
        }
        log.info("스케줄링 끝!");
    }
}