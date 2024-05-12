package com.xgarage.app.dto;

import java.util.List;

public record MultipleBids(List<Long> bids, boolean processOrder) {
}
