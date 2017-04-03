package com.blogspot.sontx.bottle.system.event;

import lombok.Data;

@Data
public class ChangeCurrentUserEvent {
    private String newCurrentUserId;
}
