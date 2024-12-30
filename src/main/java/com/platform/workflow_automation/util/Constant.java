package com.platform.workflow_automation.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constant {
    public static final String ACTIVE = "ACTIVE";

    public static final String COMPLETED = "COMPLETED";

    public static final String CRON_EXPRESSION = "0 00 12 * * ?";

    public static final String JSON = "json";

    public static final String PENDING = "PENDING";

    public static final String TIME_ZONE = "Asia/Kolkata";

    public static final String REDIS_QUEUE_KEY = "triggerQueue";

    public static final String YAML = "yaml";

    public static final String YML = "yml";
}
