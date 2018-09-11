package com.tools.networks.download;



import com.tools.networks.HttpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;

/**
 * 下载管理类
 */
public class DownloadManager {

    /**
     * 保存文件
     *
     * @param responseBody
     * @param destFileName     文件名称
     * @param progressListener 下载进度监听
     * @return
     * @throws IOException
     */
    public File saveFile(ResponseBody responseBody, final String destFileName, ProgressListener progressListener) throws IOException {
        String destFileDir = HttpUtils.getContext().getExternalFilesDir(null) + File.separator;

        long contentLength = responseBody.contentLength();
        int len = 0;
        long sum = 0L;
        byte[] buf = new byte[2048];
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = responseBody.byteStream();

            File dir = new File(destFileDir);
            //不存在文件夹
            if (!dir.exists()) {
                //创建多个文件夹
                dir.mkdirs();
            }

            //destFileName名称
            File file = new File(dir, destFileName);
            fileOutputStream = new FileOutputStream(file);
            while ((len = inputStream.read(buf)) != -1) {
                sum += len;
                fileOutputStream.write(buf, 0, len);
                final long finalSum = sum;
                progressListener.onResponseProgress(finalSum, contentLength, (int) ((finalSum * 1.0f / contentLength) * 100), finalSum == contentLength, file.getAbsolutePath());
            }
            fileOutputStream.close();
            return file;
        } finally {
            try {
                responseBody.close();
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
            }
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
            }
        }
    }

}
