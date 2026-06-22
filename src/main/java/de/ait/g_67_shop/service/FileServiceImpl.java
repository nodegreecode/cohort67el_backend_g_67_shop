package de.ait.g_67_shop.service;

import de.ait.g_67_shop.config.DOProperties;
import de.ait.g_67_shop.exceptions.types.FileUploadException;
import de.ait.g_67_shop.service.interfaces.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private final S3Client client;
    private final String bucket;

    public FileServiceImpl(S3Client client, DOProperties doProperties) {
        this.client = client;
        this.bucket = doProperties.getBucket();
    }

    @Override
    public String uploadAndGetUrl(MultipartFile image) throws IOException {
        Objects.requireNonNull(image, "MultipartFile file cannot be null");

        if (image.isEmpty()) {
            throw new FileUploadException("File is empty");
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image")) {
            throw new FileUploadException("File is not an image");
        }

        String uniqueFileName = generateUniqueFileName(image);

        PutObjectRequest request = PutObjectRequest
                .builder()
                .bucket(bucket)
                .key(uniqueFileName)
                .contentType(contentType)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        RequestBody requestBody = RequestBody.fromInputStream(image.getInputStream(), image.getSize());

        client.putObject(request, requestBody);

        return client.utilities().getUrl(
                (x) -> x.bucket(bucket).key(uniqueFileName)
        ).toString();
    }

    private String generateUniqueFileName(MultipartFile file) {
        String randomPart = UUID.randomUUID().toString();
        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            return randomPart;
        }

        fileName = fileName.trim().replace(" ", "-").toLowerCase();
        int dotIndex = fileName.lastIndexOf(".");

        if (dotIndex == -1) {
            return String.format("%s-%s", fileName, randomPart);
        }

        String fileNameWithoutExtension = fileName.substring(0, dotIndex);
        String extension = fileName.substring(dotIndex);
        return String.format("%s-%s%s", fileNameWithoutExtension, randomPart, extension);
    }
}
