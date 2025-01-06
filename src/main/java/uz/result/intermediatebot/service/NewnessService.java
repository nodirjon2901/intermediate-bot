package uz.result.intermediatebot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.result.intermediatebot.domain.dto.*;
import uz.result.intermediatebot.domain.model.NewOption;
import uz.result.intermediatebot.domain.model.Newness;
import uz.result.intermediatebot.domain.model.Photo;
import uz.result.intermediatebot.exception.LanguageNotSupportedException;
import uz.result.intermediatebot.exception.NotFoundException;
import uz.result.intermediatebot.repository.NewOptionRepository;
import uz.result.intermediatebot.repository.NewnessRepository;
import uz.result.intermediatebot.repository.PhotoRepository;
import uz.result.intermediatebot.util.SlugUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewnessService {

    private final NewnessRepository newnessRepository;

    private final NewOptionRepository optionRepository;

    private final PhotoRepository photoRepository;

    private final PhotoService photoService;

    private final ObjectMapper objectMapper;

    private final Logger log = LoggerFactory.getLogger(NewnessService.class);

    public ResponseEntity<ApiResponse<NewnessResponseDto>> create(String json, MultipartHttpServletRequest request) {
        ApiResponse<NewnessResponseDto> response = new ApiResponse<>();
        try {
            NewnessCreateDto createDto = objectMapper.readValue(json, NewnessCreateDto.class);
            checkOptionList(createDto.getOptionList());
            Newness newness = new Newness(createDto);
            Iterator<String> fileNames = request.getFileNames();
            while (fileNames.hasNext()) {
                String key = fileNames.next();
                MultipartFile photo = request.getFile(key);
                setNewnessPhoto(key, photo, newness);
            }
            newness.setActive(true);
            List<NewOption> optionList = newness.getOptionList();
            for (int i = 0; i < optionList.size(); i++) {
                optionList.get(i).setOrderNum(i);
            }
            Newness save = newnessRepository.save(newness);
            String slug = save.getId() + "-" + SlugUtil.makeSlug(save.getOptionList().get(0).getTitleRu());
            newnessRepository.updateSlug(save.getId(), slug);
            save.setSlug(slug);
            NewnessResponseDto responseDto = new NewnessResponseDto(save);
            responseDto.getOptionList().sort(Comparator.comparing(NewOptionResponseDto::getOrderNum));
            response.setData(responseDto);
            response.setMessage("Successfully created");
            return ResponseEntity.status(201).body(response);
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON for blog creation", e);
            response.setMessage(e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    private void setNewnessPhoto(String key, MultipartFile photo, Newness newness) {
        int index = Integer.parseInt(key.substring(12));
        int size = newness.getOptionList().size();
        if ((index + 1) > size) {
            throw new NotFoundException("The number of photos has exceeded the number of options.");
        }
        NewOption option = newness.getOptionList().get(index);
        option.setPhoto(photoService.save(photo));
        System.out.println(option);
    }

    private void checkOptionList(List<NewOptionCreateDto> optionList) {
        if (optionList == null || optionList.isEmpty()) {
            throw new NotFoundException("There must be at least one option in the news to upload the news");
        }
    }

    public ResponseEntity<ApiResponse<?>> findBySlug(String slug, String lang) {
        Newness newness = newnessRepository.findBySlug(slug).orElseThrow(() -> {
            log.warn("Newness is not found with SLUG: {}", slug);
            return new NotFoundException("Newness is not found with slug: " + slug);
        });
        String message = "Successfully found";
        if (lang == null || lang.equals("-")) {
            ApiResponse<NewnessResponseDto> response = new ApiResponse<>();
            NewnessResponseDto responseDTO = new NewnessResponseDto(newness);
            responseDTO.getOptionList().sort(Comparator.comparing(NewOptionResponseDto::getOrderNum));
            response.setData(responseDTO);
            response.setMessage(message);
            return ResponseEntity.ok(response);
        }
        ApiResponse<NewnessMapper> response = new ApiResponse<>();
        NewnessMapper mapper = new NewnessMapper(newness, lang);
        mapper.getOptionList().sort(Comparator.comparing(NewOptionMapper::getOrderNum));
        response.setData(mapper);
        response.setMessage(message);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<?>> findById(Long id, String lang) {
        Newness newness = newnessRepository.findById(id).orElseThrow(() -> {
            log.warn("Newness is not found with id: {}", id);
            return new NotFoundException("Newness is not found with id: " + id);
        });
        String message = "Successfully found";
        if (lang == null || lang.equals("-")) {
            ApiResponse<NewnessResponseDto> response = new ApiResponse<>();
            NewnessResponseDto responseDTO = new NewnessResponseDto(newness);
            responseDTO.getOptionList().sort(Comparator.comparing(NewOptionResponseDto::getOrderNum));
            response.setData(responseDTO);
            response.setMessage(message);
            return ResponseEntity.ok(response);
        }
        ApiResponse<NewnessMapper> response = new ApiResponse<>();
        NewnessMapper mapper = new NewnessMapper(newness, lang);
        mapper.getOptionList().sort(Comparator.comparing(NewOptionMapper::getOrderNum));
        response.setData(mapper);
        response.setMessage(message);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<?>> findAll(String lang, Integer page, Integer size) {
        List<Newness> newnessList = new ArrayList<>();
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page - 1, size);
            newnessList = newnessRepository.findAll(pageable).getContent();
        } else {
            newnessList = newnessRepository.findAll();
        }
        String message = "Successfully found number: " + newnessList.size();
        if (lang == null || lang.equals("-")) {
            ApiResponse<List<NewnessResponseDto>> response = new ApiResponse<>();
            response.setData(new ArrayList<>());
            newnessList.forEach(newness -> {
                NewnessResponseDto responseDTO = new NewnessResponseDto(newness);
                responseDTO.getOptionList().sort(Comparator.comparing(NewOptionResponseDto::getOrderNum));
                response.getData().add(responseDTO);
            });
            response.setMessage(message);
            return ResponseEntity.ok(response);
        }
        ApiResponse<List<NewnessMapper>> response = new ApiResponse<>();
        response.setData(new ArrayList<>());
        newnessList.forEach(newness -> {
            NewnessMapper mapper = new NewnessMapper(newness, lang);
            mapper.getOptionList().sort(Comparator.comparing(NewOptionMapper::getOrderNum));
            response.getData().add(mapper);
        });
        response.setMessage(message);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<NewnessResponseDto>> update(NewnessUpdateDto updateDTO, String lang) {
        ApiResponse<NewnessResponseDto> response = new ApiResponse<>();
        Newness fromDb = newnessRepository.findById(updateDTO.getId()).orElseThrow(() -> {
            log.warn("Newness is not found with id: {}", updateDTO.getId());
            return new NotFoundException("Newness is not found with id: " + updateDTO.getId());
        });
        if (updateDTO.isActive() != fromDb.isActive()) {
            fromDb.setActive(updateDTO.isActive());
        }
        if (updateDTO.getOptionList() != null && !updateDTO.getOptionList().isEmpty()) {
            List<NewOption> dbOptionList = fromDb.getOptionList();
            List<NewOptionUpdateDto> optionList = updateDTO.getOptionList();
            List<NewOption> removeOptionList = new ArrayList<>();
            for (NewOptionUpdateDto newUpdateOption : optionList) {
                if (newUpdateOption.getId() != null) {
                    for (NewOption dbOption : dbOptionList) {
                        if (dbOption.getId().equals(newUpdateOption.getId())) {
                            if (newUpdateOption.getOrderNum() != null) {
                                dbOption.setOrderNum(newUpdateOption.getOrderNum());
                            }
                            if (lang != null) {
                                switch (lang.toLowerCase()) {
                                    case "uz":{
                                        if (newUpdateOption.getTitle() != null) {
                                            dbOption.setTitleUz(newUpdateOption.getTitle());
                                        }
                                        if (newUpdateOption.getBody() != null) {
                                            dbOption.setBodyUz(newUpdateOption.getBody());
                                        }
                                        break;
                                    }
                                    case "en":{
                                        if (newUpdateOption.getTitle() != null) {
                                            dbOption.setTitleEn(newUpdateOption.getTitle());
                                        }
                                        if (newUpdateOption.getBody() != null) {
                                            dbOption.setBodyEn(newUpdateOption.getBody());
                                        }
                                        break;
                                    }
                                    case "ru":{
                                        if (newUpdateOption.getTitle() != null) {
                                            dbOption.setTitleRu(newUpdateOption.getTitle());
                                            if (dbOption.getOrderNum() == 0) {
                                                String slug = dbOption.getId() + "-" + SlugUtil.makeSlug(newUpdateOption.getTitle());
                                                fromDb.setSlug(slug);
                                            }
                                        }
                                        if (newUpdateOption.getBody() != null) {
                                            dbOption.setBodyRu(newUpdateOption.getBody());
                                        }
                                        break;
                                    }
                                    default:
                                        throw new LanguageNotSupportedException("Language is not supported: " + lang);
                                }
                            }
                            if (newUpdateOption.getPhoto() != null) {
                                Photo photo = newUpdateOption.getPhoto();
                                if (photo.getHttpUrl() != null && photo.getId() == null) {
                                    Photo newPhoto = photoRepository.findByHttpUrl(photo.getHttpUrl()).orElseThrow(() -> {
                                        log.warn("Photo is not found with url: {}", photo.getHttpUrl());
                                        return new NotFoundException("Photo is not found with url: " + photo.getHttpUrl());
                                    });
                                    dbOption.setPhoto(newPhoto);
                                }
                            }
                            if (newUpdateOption.getPhoto() == null && newUpdateOption.getTitle() == null &&
                                    newUpdateOption.getBody() == null) {
                                removeOptionList.add(dbOption);
                            }
                        }
                    }
                }
            }
            for (NewOption removeOption : removeOptionList) {
                dbOptionList.remove(removeOption);
                optionRepository.deleteByCustom(removeOption.getId());
            }
        }
        NewnessResponseDto responseDTO = new NewnessResponseDto(newnessRepository.save(fromDb));
        responseDTO.getOptionList().sort(Comparator.comparing(NewOptionResponseDto::getOrderNum));
        response.setData(responseDTO);
        response.setMessage("Successfully updated");
        return ResponseEntity.ok(response);
    }

//    public ResponseEntity<ApiResponse<NewnessResponseDto>> update(NewnessUpdateDto updateDTO) {
//        ApiResponse<NewnessResponseDto> response = new ApiResponse<>();
//        Newness fromDb = newnessRepository.findById(updateDTO.getId()).orElseThrow(() -> {
//            log.warn("Newness is not found with id: {}", updateDTO.getId());
//            return new NotFoundException("Newness is not found with id: " + updateDTO.getId());
//        });
//        Newness newness = NewnessMapper.updateDtoToEntity(updateDTO);
//        if (newness.isActive() != fromDb.isActive()) {
//            fromDb.setActive(newness.isActive());
//        }
//        if (newness.getOptionList() != null && !newness.getOptionList().isEmpty()) {
//            List<NewOption> dbOptionList = fromDb.getOptionList();
//            List<NewOption> optionList = newness.getOptionList();
//            List<NewOption> removeOptionList = new ArrayList<>();
//            for (NewOption newOption : optionList) {
//                if (newOption.getId() != null) {
//                    for (NewOption dbOption : dbOptionList) {
//                        if (dbOption.getId().equals(newOption.getId())) {
//                            if (newOption.getOrderNum() != null) {
//                                dbOption.setOrderNum(newOption.getOrderNum());
//                            }
//                            if (newOption.getTitleUz() != null) {
//                                dbOption.setTitleUz(newOption.getTitleUz());
//                            }
//                            if (newOption.getTitleEn() != null) {
//                                dbOption.setTitleEn(newOption.getTitleEn());
//                            }
//                            if (newOption.getTitleRu() != null) {
//                                dbOption.setTitleRu(newOption.getTitleRu());
//                                if (dbOption.getOrderNum() == 0) {
//                                    String slug = dbOption.getId() + "-" + SlugUtil.makeSlug(newOption.getTitleRu());
//                                    fromDb.setSlug(slug);
//                                }
//                            }
//                            if (newOption.getBodyUz() != null) {
//                                dbOption.setBodyUz(newOption.getBodyUz());
//                            }
//                            if (newOption.getBodyEn() != null) {
//                                dbOption.setBodyEn(newOption.getBodyEn());
//                            }
//                            if (newOption.getBodyRu() != null) {
//                                dbOption.setBodyRu(newOption.getBodyRu());
//                            }
//                            if (newOption.getPhoto() != null) {
//                                Photo photo = newOption.getPhoto();
//                                if (photo.getHttpUrl() != null && photo.getId() == null) {
//                                    Photo newPhoto = photoRepository.findByHttpUrl(photo.getHttpUrl()).orElseThrow(() -> {
//                                        log.warn("Photo is not found with url: {}", photo.getHttpUrl());
//                                        return new NotFoundException("Photo is not found with url: " + photo.getHttpUrl());
//                                    });
//                                    dbOption.setPhoto(newPhoto);
//                                }
//                            }
//                            if (newOption.getPhoto() == null && newOption.getTitleUz() == null && newOption.getTitleRu() == null
//                                    && newOption.getBodyUz() == null && newOption.getBodyRu() == null) {
//                                removeOptionList.add(dbOption);
//                            }
//                        }
//                    }
//                } else {
//                    newOption.setNewness(fromDb);
//                    dbOptionList.add(newOption);
//                }
//            }
//            for (NewOption removeOption : removeOptionList) {
//                dbOptionList.remove(removeOption);
//                optionRepository.deleteByCustom(removeOption.getId());
//            }
//        }
//        NewnessResponseDto responseDTO = new NewnessResponseDto(newnessRepository.save(fromDb));
//        responseDTO.getOptionList().sort(Comparator.comparing(NewOptionResponseDto::getOrderNum));
//        response.setData(responseDTO);
//        response.setMessage("Successfully updated");
//        return ResponseEntity.ok(response);
//    }

    public ResponseEntity<ApiResponse<?>> delete(Long id) {
        ApiResponse<?> response = new ApiResponse<>();
        Newness newness = newnessRepository.findById(id).orElseThrow(() -> {
            log.warn("Newness is not found with id: {}", id);
            return new NotFoundException("Newness is not found with id: " + id);
        });
        newnessRepository.delete(newness);
        response.setMessage("Successfully deleted");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<?>> deleteNewnessOption(Long id) {
        ApiResponse<?> response = new ApiResponse<>();
        NewOption option = optionRepository.findById(id).orElseThrow(() -> {
            log.warn("Block is not found with id: " + id);
            return new NotFoundException("Block is not found with id: " + id);
        });
        if (option.getPhoto() != null) {
            Long photoId = option.getPhoto().getId();
            optionRepository.deleteByCustom(id);
            photoService.delete(photoId);
        } else {
            optionRepository.deleteByCustom(id);
        }
        response.setMessage("Successfully deleted");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<NewOptionResponseDto>> updateOptionPhoto(Long optionId, MultipartFile photo) {
        ApiResponse<NewOptionResponseDto> response = new ApiResponse<>();
        NewOption option = optionRepository.findById(optionId).orElseThrow(() -> new NotFoundException("Block is not found with id: " + optionId));
        option.setPhoto(photoService.save(photo));
        NewOption save = optionRepository.save(option);
        response.setMessage("Success");
        response.setData(new NewOptionResponseDto(save));
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<NewnessResponseDto>> addOption(Long newnessId, String json, MultipartFile photo) {
        ApiResponse<NewnessResponseDto> response = new ApiResponse<>();
        Newness newness = newnessRepository.findById(newnessId).orElseThrow(() -> new NotFoundException("Newness is not found with id: " + newnessId));
        try {
            NewOptionCreateDto createDto = objectMapper.readValue(json, NewOptionCreateDto.class);
            NewOption option = new NewOption(createDto);
            if (photo != null) {
                option.setPhoto(photoService.save(photo));
            }
            option.setNewness(newness);
            if (newness.getOptionList() == null) {
                newness.setOptionList(new ArrayList<>());
            }
            newness.getOptionList().add(option);
            Newness saved = newnessRepository.save(newness);
            NewnessResponseDto responseDTO = new NewnessResponseDto(saved);
            responseDTO.getOptionList().sort(Comparator.comparing(NewOptionResponseDto::getOrderNum));
            response.setData(responseDTO);
            response.setMessage("Success");
            return ResponseEntity.ok(response);
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON for blog creation", e);
            response.setMessage(e.getMessage());
            return ResponseEntity.status(400).body(response);
        }

    }
}
