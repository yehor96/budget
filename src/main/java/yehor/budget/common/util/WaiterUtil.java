package yehor.budget.common.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.function.BooleanSupplier;

@UtilityClass
@Slf4j
public class WaiterUtil {

    private static final int DEFAULT_WAIT_LIMIT_IN_SECONDS = 15;

    public void waitFor(BooleanSupplier booleanSupplier, Duration pingRate) {
        waitFor(booleanSupplier, pingRate, Duration.ofSeconds(DEFAULT_WAIT_LIMIT_IN_SECONDS));
    }

    public void waitFor(BooleanSupplier booleanSupplier, Duration pingRate, Duration waitLimit) {
        long start = System.currentTimeMillis();
        while (!booleanSupplier.getAsBoolean()) {
            try {
                Thread.sleep(pingRate.toMillis());
                if (System.currentTimeMillis() - start > waitLimit.toMillis()) {
                    throw new IllegalStateException("Wait limit of " + waitLimit.toSeconds() + " seconds " +
                            "is finished without satisfying the condition");
                }
            } catch (InterruptedException e) {
                log.error("Wait was interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
