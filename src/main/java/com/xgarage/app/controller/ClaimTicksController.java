package com.xgarage.app.controller;

import com.xgarage.app.model.ClaimTicks;
import genericlibrary.lib.generic.GenericController;
import genericlibrary.lib.generic.GenericService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/core/api/v1/claimTicks")
public class ClaimTicksController extends GenericController<ClaimTicks> {
    public ClaimTicksController(GenericService<ClaimTicks> service) {
        super(service);
    }
}
