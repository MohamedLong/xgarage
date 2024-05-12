package com.xgarage.app.event;

public record ChangeUserRoleEvent(Long userId, String roleName) {
}
