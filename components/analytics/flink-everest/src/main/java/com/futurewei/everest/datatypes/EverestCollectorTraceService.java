/*
 * Copyright 2018-2019 The Everest Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.futurewei.everest.datatypes;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonSerialize
public class EverestCollectorTraceService {
    Map<String, EverestCTProcess> processes;
    Map<String, EverestCTSpan> spans;

    @JsonSerialize
    static public class EverestTag {
        String key;
        String type;
        String value;

        public EverestTag() {
        }

        public EverestTag(String key, String type, String value) {
            this.key = key;
            this.type = type;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EverestTag)) return false;
            EverestTag that = (EverestTag) o;
            return key.equals(that.key) &&
                    type.equals(that.type) &&
                    value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, type, value);
        }
    }

    @JsonSerialize
    static public class EverestRef {
        String refType;
        String spanID;
        String traceID;


        public EverestRef() {
        }

        public String getRefType() {
            return refType;
        }

        public void setRefType(String refType) {
            this.refType = refType;
        }

        public String getSpanID() {
            return spanID;
        }

        public void setSpanID(String spanID) {
            this.spanID = spanID;
        }

        public String getTraceID() {
            return traceID;
        }

        public void setTraceID(String traceID) {
            this.traceID = traceID;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EverestRef)) return false;
            EverestRef that = (EverestRef) o;
            return refType.equals(that.refType) &&
                    spanID.equals(that.spanID) &&
                    traceID.equals(that.traceID);
        }

        @Override
        public int hashCode() {
            return Objects.hash(refType, spanID, traceID);
        }
    }
    @JsonSerialize
    static public class EverestLog {
        Map<String, List<EverestTag>> fields;
        long timestamp;


        public EverestLog() {
        }

        public EverestLog(Map<String, List<EverestTag>> fields, long timestamp) {
            this.fields = fields;
            this.timestamp = timestamp;
        }

        public Map<String, List<EverestTag>> getFields() {
            return fields;
        }

        public void setFields(Map<String, List<EverestTag>> fields) {
            this.fields = fields;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EverestLog)) return false;
            EverestLog that = (EverestLog) o;
            return timestamp == that.timestamp &&
                    fields.equals(that.fields);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fields, timestamp);
        }
    }
    @JsonSerialize
    static public class EverestSpan {
        long duration;
        List<EverestLog> logs;
        String operationName;
        String processID;
        List<EverestRef> references;
        String spanID;
        long startTime;
        String traceID;
        String warnings;
        List<EverestTag> tags;

        public EverestSpan() {
        }

        public EverestSpan(long duration, List<EverestLog> logs, String operationName, String processID, List<EverestRef> references, String spanID, long startTime, String traceID, String warnings, List<EverestTag> tags) {
            this.duration = duration;
            this.logs = logs;
            this.operationName = operationName;
            this.processID = processID;
            this.references = references;
            this.spanID = spanID;
            this.startTime = startTime;
            this.traceID = traceID;
            this.warnings = warnings;
            this.tags = tags;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public List<EverestLog> getLogs() {
            return logs;
        }

        public void setLogs(List<EverestLog> logs) {
            this.logs = logs;
        }

        public String getOperationName() {
            return operationName;
        }

        public void setOperationName(String operationName) {
            this.operationName = operationName;
        }

        public String getProcessID() {
            return processID;
        }

        public void setProcessID(String processID) {
            this.processID = processID;
        }

        public List<EverestRef> getReferences() {
            return references;
        }

        public void setReferences(List<EverestRef> references) {
            this.references = references;
        }

        public String getSpanID() {
            return spanID;
        }

        public void setSpanID(String spanID) {
            this.spanID = spanID;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public String getTraceID() {
            return traceID;
        }

        public void setTraceID(String traceID) {
            this.traceID = traceID;
        }

        public String getWarnings() {
            return warnings;
        }

        public void setWarnings(String warnings) {
            this.warnings = warnings;
        }

        public List<EverestTag> getTags() {
            return tags;
        }

        public void setTags(List<EverestTag> tags) {
            this.tags = tags;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EverestSpan)) return false;
            EverestSpan that = (EverestSpan) o;
            return duration == that.duration &&
                    startTime == that.startTime &&
                    logs.equals(that.logs) &&
                    operationName.equals(that.operationName) &&
                    processID.equals(that.processID) &&
                    references.equals(that.references) &&
                    spanID.equals(that.spanID) &&
                    traceID.equals(that.traceID) &&
                    warnings.equals(that.warnings) &&
                    tags.equals(that.tags);
        }

        @Override
        public int hashCode() {
            return Objects.hash(duration, logs, operationName, processID, references, spanID, startTime, traceID, warnings, tags);
        }
    }
    @JsonSerialize
    static public class EverestProcess {
        String serviceName;
        List<EverestTag> tags;


        public EverestProcess() {
        }

        public EverestProcess(String serviceName, List<EverestTag> tags) {
            this.serviceName = serviceName;
            this.tags = tags;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public List<EverestTag> getTags() {
            return tags;
        }

        public void setTags(List<EverestTag> tags) {
            this.tags = tags;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EverestProcess)) return false;
            EverestProcess that = (EverestProcess) o;
            return serviceName.equals(that.serviceName) &&
                    tags.equals(that.tags);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serviceName, tags);
        }
    }
    @JsonSerialize
    static public class EverestCTProcess {
        Map<String, EverestProcess> ctprocess;

        public EverestCTProcess() {
        }

        public EverestCTProcess(Map<String, EverestProcess> ctprocess) {
            this.ctprocess = ctprocess;
        }

        public Map<String, EverestProcess> getCtprocess() {
            return ctprocess;
        }

        public void setCtprocess(Map<String, EverestProcess> ctprocess) {
            this.ctprocess = ctprocess;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EverestCTProcess)) return false;
            EverestCTProcess that = (EverestCTProcess) o;
            return ctprocess.equals(that.ctprocess);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ctprocess);
        }
    }
    @JsonSerialize
    static public class EverestSpans {
        List<EverestSpan> span;

        public EverestSpans() {
        }

        public EverestSpans(List<EverestSpan> span) {
            this.span = span;
        }

        public List<EverestSpan> getSpan() {
            return span;
        }

        public void setSpan(List<EverestSpan> span) {
            this.span = span;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EverestSpans)) return false;
            EverestSpans that = (EverestSpans) o;
            return span.equals(that.span);
        }

        @Override
        public int hashCode() {
            return Objects.hash(span);
        }
    }
    @JsonSerialize
    static public class EverestCTSpan {
        Map<String, EverestSpans> ctspan;


        public EverestCTSpan() {
        }

        public EverestCTSpan(Map<String, EverestSpans> ctspan) {
            this.ctspan = ctspan;
        }

        public Map<String, EverestSpans> getCtspan() {
            return ctspan;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EverestCTSpan)) return false;
            EverestCTSpan that = (EverestCTSpan) o;
            return ctspan.equals(that.ctspan);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ctspan);
        }

        public void setCtspan(Map<String, EverestSpans> ctspan) {
            this.ctspan = ctspan;
        }
    }


    public EverestCollectorTraceService() {} // this is a requirement for Flink POJO

    public EverestCollectorTraceService(Map<String, EverestCTProcess> processes, Map<String, EverestCTSpan> spans) {
        this.processes = processes;
        this.spans = spans;
    }

    public Map<String, EverestCTProcess> getProcesses() {
        return processes;
    }

    public void setProcesses(Map<String, EverestCTProcess> processes) {
        this.processes = processes;
    }

    public Map<String, EverestCTSpan> getSpans() {
        return spans;
    }

    public void setSpans(Map<String, EverestCTSpan> spans) {
        this.spans = spans;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EverestCollectorTraceService)) return false;
        EverestCollectorTraceService that = (EverestCollectorTraceService) o;
        return processes.equals(that.processes) &&
                spans.equals(that.spans);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processes, spans);
    }
}
