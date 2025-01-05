package org.got.web.gotweb.user.dto.request;

import java.util.Set;

public record RoleUpdateDTO(
    String name,
    String description,
    Set<String> permissions
) {}
