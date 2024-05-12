package com.xgarage.app.controller;

import com.xgarage.app.model.Principle;
import com.xgarage.app.service.PrincipleService;
import com.xgarage.app.utils.OperationCode;
import com.xgarage.app.utils.UserHelperService;
import genericlibrary.lib.generic.GenericController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/core/api/v1/principle")
public class PrincipleController extends GenericController<Principle> {

    @Autowired private UserHelperService userHelper;

    @Autowired private OperationCode operationCode;

    @Autowired private PrincipleService principleService;

    public PrincipleController(PrincipleService service) {
        super(service);
    }


    @GetMapping("/tenant")
    public ResponseEntity<?> findByTenant() {
        try{
            Long tenant = userHelper.getTenant();
            if(tenant != null ) {
                List<Principle> principles = principleService.findByTenent(tenant);
                return ResponseEntity.ok(principles);
            }
            return operationCode.craftResponse("operation.notfound", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            e.printStackTrace();
            return operationCode.craftResponse("operation.forbidden", HttpStatus.FORBIDDEN);
        }
    }
}
