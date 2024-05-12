package com.xgarage.app.utils;

import org.springframework.orm.hibernate5.support.OpenSessionInterceptor;

import java.util.Arrays;

public class RequestStatusConstants {
    public static final long OPEN_STATUS = 1L;
    public static final long INITIAL_APPROVE = 2L;
    public static final long ONHOLD_STATUS = 3L;
    public static final long COMPLETED_STATUS = 4L;
    public static final long REJECTED_STATUS = 5L;
    public static final long APPROVED_STATUS = 6L;
    public static final long CANCELED_STATUS = 7L;
    public static final long REVISION_STATUS = 8L;
    public static final long LOST_STATUS = 9L;
    public static final long REVISED_STATUS = 10L;

    public static final long CONFIRMED_STATUS = 11L;

    public static final long WAITING_FOR_APPROVAL = 12L;
    public static final long WAITING_FOR_SURVEY = 13L;

    private static final long[][] statusTransitionTable = {
            {OPEN_STATUS, CONFIRMED_STATUS},
            {OPEN_STATUS, INITIAL_APPROVE},
            {OPEN_STATUS, REVISION_STATUS},
            {OPEN_STATUS, ONHOLD_STATUS},
            {OPEN_STATUS, REJECTED_STATUS},
            {OPEN_STATUS, CANCELED_STATUS},
            {OPEN_STATUS, WAITING_FOR_SURVEY},
            {CONFIRMED_STATUS, INITIAL_APPROVE},
            {CONFIRMED_STATUS, CANCELED_STATUS},
            {INITIAL_APPROVE, APPROVED_STATUS},
            {INITIAL_APPROVE, REJECTED_STATUS},
            {INITIAL_APPROVE, CANCELED_STATUS},
            {APPROVED_STATUS, CANCELED_STATUS},
            {APPROVED_STATUS, COMPLETED_STATUS},
            {ONHOLD_STATUS, OPEN_STATUS},
            {ONHOLD_STATUS, LOST_STATUS},
            {REVISION_STATUS, REVISED_STATUS},
            {REVISED_STATUS, REVISION_STATUS},
            {REVISION_STATUS, CANCELED_STATUS},
            {WAITING_FOR_SURVEY, WAITING_FOR_APPROVAL},
            {WAITING_FOR_APPROVAL, CONFIRMED_STATUS}
    };

    public static boolean isAllowedStatusTransition(long current, long next) {
        return Arrays.stream(statusTransitionTable).anyMatch(a -> (a[0] == current) && (a[1] == next));
    }

}
