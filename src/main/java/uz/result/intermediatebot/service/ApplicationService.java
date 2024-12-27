package uz.result.intermediatebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.result.intermediatebot.bot.IntermediateBot;
import uz.result.intermediatebot.model.ApiResponse;
import uz.result.intermediatebot.model.Application;
import uz.result.intermediatebot.model.ApplicationByUser;
import uz.result.intermediatebot.repository.ApplicationByUserRepository;
import uz.result.intermediatebot.repository.ApplicationRepository;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final IntermediateBot intermediateBot;

    private final ApplicationRepository applicationRepository;

    private final ApplicationByUserRepository applicationByUserRepository;

    public ResponseEntity<ApiResponse<Application>> save(Application application) {
        ApiResponse<Application> response = new ApiResponse<>();
        Application save = applicationRepository.save(application);
        intermediateBot.handleSendApplication(save);
        response.setData(save);
        response.setMessage("Application has been saved successfully");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<ApplicationByUser>> saveAppUser(ApplicationByUser application) {
        ApiResponse<ApplicationByUser> response = new ApiResponse<>();
        ApplicationByUser save = applicationByUserRepository.save(application);
        intermediateBot.handleSendApplicationByUser(save);
        response.setData(save);
        response.setMessage("Application has been saved successfully");
        return ResponseEntity.ok(response);
    }


}
