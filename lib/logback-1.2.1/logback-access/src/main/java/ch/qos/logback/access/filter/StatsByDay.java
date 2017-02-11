/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.access.filter;

import ch.qos.logback.core.util.TimeUtil;

public class StatsByDay extends PeriodicStats {

    StatsByDay() {
        super();
    }

    StatsByDay(long now) {
        super(now);
    }

    @Override
    long computeStartOfNextPeriod(long now) {
        return TimeUtil.computeStartOfNextDay(now);
    }

}
