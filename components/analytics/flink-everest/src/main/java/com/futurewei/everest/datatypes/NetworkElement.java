/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.futurewei.everest.datatypes;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * A NetworkElement is a network element event.
 * still TODO
 *
 * A NetworkElement consists of
 * - the networkElementId of the event
 * - the type of the event (TBD)
 * - the time of the event
 * - the number of bytes
 * - the number of errors
 * - the number of dropped
 * - the time of forwarding
 * - the locationId
 * - the description
 *
 */
public class NetworkElement implements Comparable<NetworkElement>, Serializable {
    private static final long serialVersionUID = 800684152047607833L;
    private static transient DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.US).withZoneUTC();

    public long networkElementId;
    public boolean type;
    public DateTime eventTime;
    public long bytes;
    public long errors;
    public long dropped;
    public long forwarding; // nanosec
    public long locationId;
    public String description;


    public NetworkElement(long networkElementId) {
        this.networkElementId = networkElementId;
    }

    public static NetworkElement fromString(@org.jetbrains.annotations.NotNull String line) {

        String[] tokens = line.split(",");
        if (tokens.length != 11) {
            throw new RuntimeException("Invalid record: " + line);
        }
        NetworkElement ne = null;
        try {
            long networkElementId = Long.parseLong(tokens[0]);
            ne = new NetworkElement(networkElementId);

        } catch (NumberFormatException nfe) {
            throw new RuntimeException("Invalid record: " + line, nfe);
        }

        return ne;
    }
    
    public NetworkElement(long networkElementId, boolean type, DateTime eventTime, long bytes, long errors, long dropped, long forwarding, long locationId, String description) {
        this.networkElementId = networkElementId;
        this.type = type;
        this.eventTime = eventTime;
        this.bytes = bytes;
        this.errors = errors;
        this.dropped = dropped;
        this.forwarding = forwarding;
        this.locationId = locationId;
        this.description = description;
    }

    @Override
    public String toString() {
        return "NetworkElement{" +
                "networkElementId=" + networkElementId +
                ", type=" + type +
                ", eventTime=" + eventTime +
                ", bytes=" + bytes +
                ", errors=" + errors +
                ", dropped=" + dropped +
                ", forwarding=" + forwarding +
                ", locationId=" + locationId +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public int compareTo(NetworkElement o) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkElement that = (NetworkElement) o;
        return networkElementId == that.networkElementId &&
                type == that.type &&
                bytes == that.bytes &&
                errors == that.errors &&
                dropped == that.dropped &&
                forwarding == that.forwarding &&
                locationId == that.locationId &&
                Objects.equals(eventTime, that.eventTime) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(networkElementId, type, eventTime, bytes, errors, dropped, forwarding, locationId, description);
    }
}
