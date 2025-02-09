package org.got.web.gotweb.user.dto.permission.search;

public record PermissionSearchCriteria(
    String name,
    String description,
    String type
) {}
