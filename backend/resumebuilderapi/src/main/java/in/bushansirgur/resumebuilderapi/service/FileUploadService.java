package in.bushansirgur.resumebuilderapi.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import in.bushansirgur.resumebuilderapi.document.Resume;
import in.bushansirgur.resumebuilderapi.dto.AuthResponse;
import in.bushansirgur.resumebuilderapi.exception.ExternalServiceException;
import in.bushansirgur.resumebuilderapi.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final Cloudinary cloudinary;
    private final AuthService authService;
    private final ResumeRepository resumeRepository;

    public Map<String, String> uploadSingleImage(MultipartFile file) throws IOException {
        try {
            Map<String, Object> imageUploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "image"));
            log.info("Inside FileUploadService - uploadSingleImage() {}", imageUploadResult.get("secure_url").toString());
            return Map.of("imageUrl", imageUploadResult.get("secure_url").toString());
        } catch (IOException ex) {
            throw new ExternalServiceException("Profile photo upload failed. Please try again without a photo.", ex);
        }
    }

    public Map<String, String> uploadResumeImages(String resumeId,
                                                  Object principal,
                                                  MultipartFile thumbnail,
                                                  MultipartFile profileImage) throws IOException {
        //Step 1: get the current profile
        AuthResponse response = authService.getProfile(principal);

        //Step 2: get the existing resume
        Resume existingResume = resumeRepository.findByUserIdAndId(response.getId(), resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        //Step 3: upload the resume images and set the resume
        Map<String, String> returnValue = new HashMap<>();
        Map<String, String> uploadResult;

        if (Objects.nonNull(thumbnail)) {
            uploadResult = uploadSingleImage(thumbnail);
            existingResume.setThumbnailLink(uploadResult.get("imageUrl"));
            returnValue.put("thumbnailLink", uploadResult.get("imageUrl"));
        }

        if (Objects.nonNull(profileImage)) {
            uploadResult = uploadSingleImage(profileImage);
            if (Objects.isNull(existingResume.getProfileInfo())) {
                existingResume.setProfileInfo(new Resume.ProfileInfo());
            }
            existingResume.getProfileInfo().setProfilePreviewUrl(uploadResult.get("imageUrl"));
            returnValue.put("profilePreviewUrl", uploadResult.get("imageUrl"));
        }

        //Step 4: update the details into database
        resumeRepository.save(existingResume);
        returnValue.put("message", "Images uploaded successfully");

        //Step 5: return the result
        return returnValue;
    }
}
