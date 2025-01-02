package uz.result.intermediatebot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.result.intermediatebot.domain.dto.ApiResponse;
import uz.result.intermediatebot.domain.model.Application;
import uz.result.intermediatebot.domain.model.ApplicationByUser;
import uz.result.intermediatebot.service.ApplicationService;

@RestController
@RequestMapping("/v1/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping(value = "/register")
    public ResponseEntity<ApiResponse<Application>> create(
            @RequestBody Application application
    ) {
        return applicationService.save(application);
    }

    @PostMapping(value = "/")
    public ResponseEntity<ApiResponse<ApplicationByUser>> createAppByUser(
            @RequestBody ApplicationByUser application
    ) {
        return applicationService.saveAppUser(application);
    }

}
