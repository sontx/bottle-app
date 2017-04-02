package com.blogspot.sontx.bottle.system.event;

import lombok.Data;

@Data
public class ServiceStateChangedEvent {
    private ServiceState serviceState;
}
