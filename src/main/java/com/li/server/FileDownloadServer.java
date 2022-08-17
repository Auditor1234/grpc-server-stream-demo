package com.li.server;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class FileDownloadServer {
    private Server server = null;
    private int port = 8081;

    public void start() throws IOException{
        server = ServerBuilder
                .forPort(this.port)
                .addService(new FileDownloadServiceImpl())
                .build()
                .start();
        System.out.println("Server started at " + this.port);
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                shutdown();
            }
        });
    }

    private void shutdown(){
        if(this.server != null)
            this.server.shutdown();
    }

    public void blockUntilShutdown() throws InterruptedException{
        if(this.server != null){
            this.server.awaitTermination();
        }
    }

}
