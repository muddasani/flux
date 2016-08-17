/*
 * Copyright 2012-2016, the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.flux.redriver.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

/**
 * A message that will be sent to a particular actor denoted by the url at the scheduled time
 */
// TODO : Keep this as a generic message that takes a Serializable "data". Presently binding it only to our usecase
@Entity
public class ScheduledMessage implements Serializable {

    @Id
    private Long taskId;
    private long scheduledTime;

    /* For Hibernate */
    ScheduledMessage() {
    }

    public ScheduledMessage(Long taskId, Long scheduledTime) {
        this();
        this.taskId = taskId;
        this.scheduledTime = scheduledTime;
    }

    public long getScheduledTime() {
        return scheduledTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScheduledMessage that = (ScheduledMessage) o;

        if (scheduledTime != that.scheduledTime) return false;
        return taskId.equals(that.taskId);

    }

    @Override
    public int hashCode() {
        int result = taskId.hashCode();
        result = 31 * result + (int) (scheduledTime ^ (scheduledTime >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ScheduledMessage{" +
            ", taskId=" + taskId +
            ", scheduledTime=" + scheduledTime +
            '}';
    }

    public boolean shouldRunNow() {
        return scheduledTime <= System.currentTimeMillis();
    }

    public Long timeLeftToRun() {
        return scheduledTime - System.currentTimeMillis();
    }

    public Long getTaskId() {
        return taskId;
    }
}
