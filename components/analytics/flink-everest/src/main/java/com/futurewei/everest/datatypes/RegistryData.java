/*
 *
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
 *
 *
 */

package com.futurewei.everest.datatypes;

public class RegistryData<I, SID, L> {
    private I id;
    private SID sid;
    private L location;
    private String description;
    private String owner;
    private String ownerEmail;
    private String ownerPhone;
    private String action;

    public RegistryData() {} // this is a requirement for Flink POJO

    public RegistryData(I id, SID sid, L location) {
        this.id = id;
        this.sid = sid;
        this.location = location;
    }

    public RegistryData(I id, SID sid, L location, String description, String owner, String ownerEmail, String ownerPhone, String action) {
        this.id = id;
        this.sid = sid;
        this.location = location;
        this.description = description;
        this.owner = owner;
        this.ownerEmail = ownerEmail;
        this.ownerPhone = ownerPhone;
        this.action = action;
    }

    public I getId() {
        return id;
    }

    public void setId(I id) {
        this.id = id;
    }

    public SID getSid() {
        return sid;
    }

    public void setSid(SID sid) {
        this.sid = sid;
    }

    public L getLocation() {
        return location;
    }

    public void setLocation(L location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "RegistryData{" +
                "id=" + id +
                ", sid=" + sid +
                ", location=" + location +
                ", description='" + description + '\'' +
                ", owner='" + owner + '\'' +
                ", ownerEmail='" + ownerEmail + '\'' +
                ", ownerPhone='" + ownerPhone + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
