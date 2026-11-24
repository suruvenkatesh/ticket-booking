package com.farmonboard.onboarding.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.farmonboard.onboarding.domain.FarmerApplication;
import com.farmonboard.onboarding.service.FarmerApplicationService;

import jakarta.validation.Valid;

// Answers web requests about farmer applications. Only web work here: JSON, @Valid, status codes.
@RestController
public class FarmerApplicationController {

    private final FarmerApplicationService service;

    public FarmerApplicationController(FarmerApplicationService service) {
        this.service = service;
    }

    // POST http://localhost:8081/api/applications with {"aadhaarNumber": "123412341234"}.
    // @Valid checks the request's rules first; if one fails, Spring replies 400 and this never runs.
    // Replies 201 Created with the new application.
    @PostMapping("/api/applications")
    public ResponseEntity<ApplicationResponse> create(@Valid @RequestBody CreateApplicationRequest request) {
        FarmerApplication application = service.create(request.aadhaarNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApplicationResponse.from(application));
    }

    // GET http://localhost:8081/api/applications/6
    // @PathVariable: takes the {id} part of the URL and passes it in as a Long.
    // Not found -> the service throws ApplicationNotFoundException -> 404.
    @GetMapping("/api/applications/{id}")
    public ApplicationResponse findById(@PathVariable Long id) {
        return ApplicationResponse.from(service.findById(id));
    }

    // GET http://localhost:8081/api/applications?page=0&size=50 (the worklist)
    // Spring builds the Pageable from the URL: ?page= (counts from 0), ?size= and ?sort=.
    // @PageableDefault: used when the URL leaves them out. 50 per page, like the app, and
    // newest first by id. The id is unique and indexed, so every page has a stable order.
    @GetMapping("/api/applications")
    public PageResponse<ApplicationResponse> findPage(
            @PageableDefault(size = 50, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<FarmerApplication> page = service.findPage(pageable);
        List<ApplicationResponse> content = new ArrayList<>();
        for (FarmerApplication application : page.getContent()) {
            content.add(ApplicationResponse.from(application));
        }
        return new PageResponse<>(content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}
