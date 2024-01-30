package kr.nadeuli.scheduler;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;
import kr.nadeuli.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BlockScheduler {

  private final MemberService memberService;

  private final ThreadPoolTaskScheduler scheduler;
  private ScheduledFuture<?> scheduledFuture;

  private BlockScheduler(@Lazy MemberService memberService, ThreadPoolTaskScheduler scheduler){
    this.memberService = memberService;
    this.scheduler = scheduler;

  }

  public void startBlockDayScheduler(String tag) {
    // 초기 스케줄링 로직
    scheduledFuture = scheduler.schedule(() -> updateBlock(tag), new CronTrigger("0 0 0 * * *"));
    log.info("스케줄링 시작");
  }

  public void updateBlock(String tag) {
    try {
      LocalDateTime blockEnd = memberService.getMember(tag).getBlockEnd();
      if (blockEnd != null && blockEnd.isBefore(LocalDateTime.now())) {
        stopBlockScheduler();
        memberService.deleteBlockMember(tag);
      }
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  private void stopBlockScheduler() {
    if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
      scheduledFuture.cancel(true);
    }
    log.info("스케줄링 끝!");
  }
}
