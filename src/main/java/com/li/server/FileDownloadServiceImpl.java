package com.li.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.ByteString;
import com.li.rpc.FileInfo;
import com.li.rpc.TargetFile;
import com.li.rpc.RouteGuideGrpc.RouteGuideImplBase;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public class FileDownloadServiceImpl extends RouteGuideImplBase{
    private static Logger logger = Logger.getLogger(FileDownloadServiceImpl.class.getName());

    @Override
    public void download(TargetFile request, StreamObserver<FileInfo> responseObserver) {
        final long startTime = System.nanoTime();
        String filename = request.getFilename();
        logger.info("Accpted download of file: " + filename);
        InputStream is;
        try {
            is = new FileInputStream(new File("storage/" + filename));
            byte[] buff = new byte[2048];  // 每次发送2KB的内容
            int len;
            int index = 0;
            while ((len = is.read(buff)) != -1) {
                responseObserver.onNext(FileInfo.newBuilder().setFilename(filename).setIndex(index).setArrs(ByteString.copyFrom(buff)).build());
                index++;
            }
            long seconds = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime);
            logger.info("File: " + filename + "transported successfully!");
            logger.info("Transport time:" + seconds + "seconds.");
        } catch (IOException e) {
            logger.log(Level.WARNING ,"File: " + filename + " not found");
            responseObserver.onError(new RuntimeException(filename + "is a directory or not exists"));
            e.printStackTrace();
        }
        responseObserver.onCompleted();   
    }
}
