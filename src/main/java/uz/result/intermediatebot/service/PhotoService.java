package uz.result.intermediatebot.service;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.result.intermediatebot.domain.dto.ApiResponse;
import uz.result.intermediatebot.domain.dto.PhotoDto;
import uz.result.intermediatebot.domain.model.Photo;
import uz.result.intermediatebot.exception.IllegalPhotoTypeException;
import uz.result.intermediatebot.exception.NotFoundException;
import uz.result.intermediatebot.repository.PhotoRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;

    private final Logger log = LoggerFactory.getLogger(PhotoService.class);

    @Value("${photo.upload.path}")
    private String uploadPath;

    @Value("${server.base.url}")
    private String baseUrl;

    public Photo save(MultipartFile file) {
        if (file.getContentType() != null && !(file.getContentType().equals("image/png") ||
                file.getContentType().equals("image/jpeg") ||
                file.getContentType().equals("image/svg+xml"))) {
            throw new IllegalPhotoTypeException("Unsupported image type: " + file.getContentType());
        }
        try {
            Photo photo = photoRepository.save(new Photo());
            saveToFile(file, photo);
            return photoRepository.save(photo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Photo getEmpty() {
        return photoRepository.save(new Photo());
    }

    public ResponseEntity<byte[]> findByName(String name) {
        try {
            Photo photo = photoRepository.findByName(name).orElseThrow(() -> new NotFoundException("Photo not found: " + name));
            Path imagePath = Paths.get(photo.getFilePath());
            byte[] imageBytes = Files.readAllBytes(imagePath);

            switch (photo.getType()) {
                case "image/png" -> {
                    return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageBytes);
                }
                case "image/jpeg" -> {
                    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
                }
                case "image/svg+xml" -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_TYPE, "image/svg+xml");
                    return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new NotFoundException(e.getMessage());
        }
        return null;
    }

    public ResponseEntity<ApiResponse<PhotoDto>> update(Long id, MultipartFile file) {
        ApiResponse<PhotoDto> response = new ApiResponse<>();
        Photo fromDb = photoRepository.findById(id).orElseThrow(() -> new NotFoundException("Photo not found: " + id));

        if (file.getContentType() != null && !(file.getContentType().equals("image/png") ||
                file.getContentType().equals("image/jpeg") ||
                file.getContentType().equals("image/svg+xml"))) {
            throw new IllegalPhotoTypeException("Unsupported image type: " + file.getContentType() + ", Support only image/png, image/jpeg, image/svg+xml");
        }
        try {
            if (fromDb.getFilePath() != null && fromDb.getFilePath().isEmpty())
                deleteFromFile(fromDb.getFilePath());
            saveToFile(file, fromDb);
            response.setMessage("Updated");
            response.setData(new PhotoDto(photoRepository.save(fromDb)));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteFromFile(String filePath) throws IOException {
        try {
            if (filePath != null)
                Files.delete(Paths.get(filePath));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void saveToFile(MultipartFile file, Photo photo) throws IOException {
        String originalFileName = photo.getId() + "-" + Objects.requireNonNull(file.getOriginalFilename()).replaceAll(" ", "-");
        Path filePath = Paths.get(uploadPath + File.separator + originalFileName);
        file.transferTo(filePath);
        photo.setName(originalFileName);
        photo.setFilePath(filePath.toFile().getAbsolutePath());
        photo.setType(file.getContentType());
        photo.setHttpUrl(baseUrl + "/v1/photo/" + photo.getName());
    }

    public ResponseEntity<ApiResponse<List<Photo>>> upload(List<MultipartFile> photo){
        if (photo == null || photo.isEmpty())
            throw new NotFoundException("Photo is null or empty");
        ApiResponse<List<Photo>> response=new ApiResponse<>();

        response.setData(new ArrayList<>());
        photo.forEach(i -> response.getData().add(save(i)));

        response.setMessage("Uploaded");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<?>> delete(Long id) {
        ApiResponse<?> response=new ApiResponse<>();
        Photo photo = photoRepository.findById(id).orElseThrow(() -> new NotFoundException("Photo not found: " + id));
        try {
            photoRepository.deleteByCustom(photo.getId());
        }catch (Exception e){
            Photo photo1=new Photo();
            photo1.setId(id);
            photoRepository.save(photo1);
        }

        try {
            deleteFromFile(photo.getFilePath());
        }catch (IOException e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        response.setMessage("Deleted");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
