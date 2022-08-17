package com.li.client;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
public class ClientNetService {
    
    private ManagedChannel channel = null;
    public ClientNetService(String ip, int port){
        this.channel = ManagedChannelBuilder.forTarget(ip + ":" + port).usePlaintext().build();
        System.out.println("connected to server at " + ip + ":" + port);
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                try {
                    channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    public ManagedChannel getChannel(){
        return this.channel;
    }

    public void shutdown() throws InterruptedException{
        this.channel.shutdown().awaitTermination(5L, TimeUnit.SECONDS);
    }
}
