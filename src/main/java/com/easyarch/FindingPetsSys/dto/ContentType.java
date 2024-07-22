package com.easyarch.FindingPetsSys.dto;

public enum ContentType {
    JPG("jpg", "image/jpeg"),
    TIFF("tiff", "image/tiff"),
    GIF("gif", "image/gif"),
    JFIF("jfif", "image/jpeg"),
    PNG("png", "image/png"),
    TIF("tif", "image/tiff"),
    ICO("ico", "image/x-icon"),
    JPEG("jpeg", "image/jpeg"),
    WBMP("wbmp", "image/vnd.wap.wbmp"),
    FAX("fax", "image/fax"),
    NET("net", "image/pnetvue"),
    JPE("jpe", "image/jpeg"),
    RP("rp", "image/vnd.rn-realpix"),
    MP4("mp4", "video/mp4");

    public final String type;
    public final String contentType;

    ContentType(String type, String contentType) {
        this.type = type;
        this.contentType = contentType;
    }
}
