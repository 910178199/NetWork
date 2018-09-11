package com.tools.networks.download;

/**
 * 下载监听
 */
public interface ProgressListener {

    /**
     * @param byteRead      已下载大小
     * @param contentLength 文件大小
     * @param progress      下载进度
     * @param done          是否下载完成
     * @param filePath      下载文件路径
     */
    void onResponseProgress(long byteRead, long contentLength, int progress, boolean done, String filePath);

}
