
package com.easyarch.FindingPetsSys.util;

import com.easyarch.FindingPetsSys.dto.ContentType;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileTypeUtil {

    public static boolean isImageFile(MultipartFile file) {
        String fileExtension = getFileExtension(file);
        return isJpg(fileExtension) || isPng(fileExtension) || isJpeg(fileExtension);
    }

    public static String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        return fileName != null ? FilenameUtils.getExtension(fileName).toLowerCase() : null;
    }

    private static boolean isJpg(String fileExtension) {
        return ContentType.JPG.type.equals(fileExtension);
    }

    private static boolean isPng(String fileExtension) {
        return ContentType.PNG.type.equals(fileExtension);
    }

    private static boolean isJpeg(String fileExtension) {
        return ContentType.JPEG.type.equals(fileExtension);
    }
}
