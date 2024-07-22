package com.easyarch.FindingPetsSys.util;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.SnowballObject;
import io.minio.UploadSnowballObjectsArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.MinioException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
@Slf4j
@Component
public class MinioUtil {
    @Autowired
    private MinioClient minioClient;


    public void uploadFile(MultipartFile file, String fileName, String bucketName) throws RuntimeException {
        try {
            minioClient.putObject((PutObjectArgs) ((PutObjectArgs.Builder) ((PutObjectArgs.Builder) PutObjectArgs.builder()
                    .bucket(bucketName))
                    .object(fileName))
                    .stream(file.getInputStream(), file.getSize(), -1L)
                    .contentType(file.getContentType())
                    .build());
            log.info("Upload file {} to bucket {} success", fileName, bucketName);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | MinioException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void uploadFiles(MultipartFile[] files, String dir, String bucketName) throws RuntimeException {
        List<SnowballObject> objects = new ArrayList<>();
        int index = 1;

        try {
            int len = files.length;

            for (int i = 0; i < len; ++i) {
                MultipartFile file = files[i];
                objects.add(new SnowballObject(dir + "/" + index + "." + FileTypeUtil.getFileExtension(file),
                        new ByteArrayInputStream(file.getBytes()),
                        file.getSize(),
                        ZonedDateTime.now()));
                ++index;
            }

            minioClient.uploadSnowballObjects((UploadSnowballObjectsArgs) ((UploadSnowballObjectsArgs.Builder) UploadSnowballObjectsArgs.builder().bucket(bucketName)).objects(objects).build());
            log.info("Upload files {} to bucket {} success", objects.size(), bucketName);
        } catch (InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException |
                 IOException | NoSuchAlgorithmException | ServerException | XmlParserException |
                 ErrorResponseException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFile(String bucketName, String fileName) {
        try {
            minioClient.removeObject((RemoveObjectArgs) ((RemoveObjectArgs.Builder) ((RemoveObjectArgs.Builder) RemoveObjectArgs.builder().bucket(bucketName)).object(fileName)).build());
            log.info("Remove file {} to bucket {} success", fileName, bucketName);
        } catch (InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException |
                 IOException | NoSuchAlgorithmException | ServerException | XmlParserException |
                 ErrorResponseException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFileAll(String bucketName, String[] fileNames) {
        List<DeleteObject> objects = new LinkedList();
        String[] var4 = fileNames;
        int var5 = fileNames.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            String fileName = var4[var6];
            objects.add(new DeleteObject(fileName));
        }

        try {
            Iterable<Result<DeleteError>> results = this.minioClient.removeObjects((RemoveObjectsArgs) ((RemoveObjectsArgs.Builder) RemoveObjectsArgs.builder().bucket(bucketName)).objects(objects).build());

            for (Result<DeleteError> deleteErrorResult : results) {
                Result result = deleteErrorResult;
                DeleteError error = (DeleteError) result.get();
                log.error("Error in deleting object " + error.objectName() + "; " + error.message());
            }

        } catch (Exception var8) {
            throw new RuntimeException(var8);
        }
    }
}
