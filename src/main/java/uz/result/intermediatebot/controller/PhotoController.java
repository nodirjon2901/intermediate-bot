package uz.result.intermediatebot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.result.intermediatebot.domain.dto.ApiResponse;
import uz.result.intermediatebot.domain.dto.PhotoDto;
import uz.result.intermediatebot.domain.model.Photo;
import uz.result.intermediatebot.service.PhotoService;

import java.util.List;

@RestController
@RequestMapping("/v1/photo")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping(value = "/create")
    public ResponseEntity<ApiResponse<List<Photo>>> upload(
            @RequestPart(value = "photos") List<MultipartFile> photos
    ) {
        return photoService.upload(photos);
    }

    @GetMapping("/{name}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable String name) {
        return photoService.findByName(name);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<PhotoDto>> updatePhoto(
            @PathVariable Long id,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        return photoService.update(id, photo);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<?>> deletePhoto(@PathVariable Long id) {
        return photoService.delete(id);
    }

}
