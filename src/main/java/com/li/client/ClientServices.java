package com.li.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.li.rpc.FileInfo;
import com.li.rpc.RouteGuideGrpc;
import com.li.rpc.TargetFile;
import com.li.rpc.RouteGuideGrpc.RouteGuideStub;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public class ClientServices {
    private static Logger logger = Logger.getLogger(ClientServices.class.getName());
    private final RouteGuideStub asyncStub;
    private final ClientNetService netService;
    private final String ip = "localhost";
    private final int port = 8081;

    public ClientServices(){
        this.netService = new ClientNetService(ip, port);
        this.asyncStub = RouteGuideGrpc.newStub(this.netService.getChannel());
    }

    public void downloadFile(String fileName) {
        final CountDownLatch finishLatch = new CountDownLatch(1);
        logger.info("Start download file...");
        StreamObserver<FileInfo> responseObserver;
        File file = new File(fileName);
        try {
            responseObserver = new StreamObserver<FileInfo>() {
                OutputStream os = new FileOutputStream(file);
                @Override
                public void onNext(FileInfo fileInfo) {
                    logger.info("Accept " + fileName + "'s number " + fileInfo.getIndex() + " block");
                    try {
                        fileInfo.getArrs().writeTo(os);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable arg0) {
                    try {
                        os.close();  // 关闭流，否则无法删除临时生成的文件
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    file.delete();
                    logger.log(Level.WARNING, "File download cancelled, file does not exists!");
                    finishLatch.countDown();
                }

                @Override
                public void onCompleted() {
                    // TODO Auto-generated method stub
                    logger.info("File download success!");
                    finishLatch.countDown();
                }
            };
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            responseObserver = null;
            e.printStackTrace();
        }
        TargetFile targetFile = TargetFile.newBuilder()
                                .setFilename(fileName)
                                .build();
        this.asyncStub.download(targetFile, responseObserver);
        // Receiving happens asynchronously
        try {
            if (!finishLatch.await(1, TimeUnit.MINUTES)) {
                logger.info("send file can not finish within 1 minutes");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
