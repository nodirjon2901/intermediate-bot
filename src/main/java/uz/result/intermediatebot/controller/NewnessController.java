package uz.result.intermediatebot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.result.intermediatebot.domain.dto.ApiResponse;
import uz.result.intermediatebot.domain.dto.NewOptionResponseDto;
import uz.result.intermediatebot.domain.dto.NewnessResponseDto;
import uz.result.intermediatebot.domain.dto.NewnessUpdateDto;
import uz.result.intermediatebot.service.NewnessService;

@RestController
@RequestMapping("/v1/newness")
@RequiredArgsConstructor
public class NewnessController {

    private final NewnessService newnessService;

    @PostMapping(value = "/create")
    public ResponseEntity<ApiResponse<NewnessResponseDto>> create(
            @RequestPart(value = "json") String json,
            MultipartHttpServletRequest photos
    ) {
        return newnessService.create(json, photos);
    }

    @GetMapping("/get/{slug}")
    public ResponseEntity<ApiResponse<?>> findBySlug(
            @RequestHeader(value = "Accept-Language", required = false) String lang,
            @PathVariable String slug
    ) {
        return newnessService.findBySlug(slug, lang);
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<ApiResponse<?>> findById(
            @RequestHeader(value = "Accept-Language", required = false) String lang,
            @PathVariable Long id
    ) {
        return newnessService.findById(id, lang);
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<?>> findAll(
            @RequestHeader(value = "Accept-Language", required = false) String lang,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "page", required = false) Integer page
    ) {
        return newnessService.findAll(lang, size, page);
    }

    @PutMapping(value = "/update", consumes = {"application/json"})
    public ResponseEntity<ApiResponse<NewnessResponseDto>> update(
            @RequestHeader(value = "Accept-Language", required = false) String lang,
            @RequestBody NewnessUpdateDto updateDTO
    ) {
        return newnessService.update(updateDTO, lang);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<?>> delete(
            @PathVariable Long id
    ) {
        return newnessService.delete(id);
    }

    @PostMapping("/block/add/{newnessId}")
    public ResponseEntity<ApiResponse<NewnessResponseDto>> addBlock(
            @PathVariable Long newnessId,
            @RequestPart(value = "json") String json,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) {
        return newnessService.addOption(newnessId, json, photo);
    }

    @DeleteMapping("/block/delete/{id}")
    public ResponseEntity<ApiResponse<?>> deleteBlock(
            @PathVariable Long id
    ) {
        return newnessService.deleteNewnessOption(id);
    }

    @PutMapping("/block/update/photo/{id}")
    public ResponseEntity<ApiResponse<NewOptionResponseDto>> updateBlockPhoto(
            @PathVariable Long id,
            @RequestPart(value = "photo") MultipartFile photo
    ) {
        return newnessService.updateOptionPhoto(id, photo);
    }

}
