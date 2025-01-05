package org.got.web.gotweb.user.dto.request;

import java.util.Set;

public record RoleCreateDTO(
    String name,
    String description,
    Set<String> permissions
) {}
