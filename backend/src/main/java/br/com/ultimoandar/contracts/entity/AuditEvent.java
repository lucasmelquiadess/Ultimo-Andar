package br.com.ultimoandar.contracts.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_events")
public class AuditEvent extends AuditableEntity {

    @Column(nullable = false, length = 80, updatable = false)
    private String actor;

    @Column(nullable = false, length = 80, updatable = false)
    private String action;

    @Column(name = "resource_type", nullable = false, length = 80, updatable = false)
    private String resourceType;

    @Column(name = "resource_id", length = 120, updatable = false)
    private String resourceId;

    @Column(length = 500, updatable = false)
    private String details;

    @Column(name = "ip_address", length = 80, updatable = false)
    private String ipAddress;

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
