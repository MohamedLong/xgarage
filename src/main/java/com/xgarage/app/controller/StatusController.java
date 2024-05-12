package com.xgarage.app.controller;

import com.xgarage.app.model.Part;
import com.xgarage.app.model.Status;
import com.xgarage.app.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/core/api/v1/status")
public class StatusController {

    @Autowired private StatusService statusService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllStatus() {
        try{
            List<Status> statusList = statusService.findAll();
            if(statusList.isEmpty()) {
                return new ResponseEntity<>("Statuses Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(statusList);
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Statuses");
        }
    }

}
