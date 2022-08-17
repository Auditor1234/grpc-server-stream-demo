package com.li.client;

public class Client {
    private ClientServices clientServices;
    
    public Client(){
        this.clientServices = new ClientServices();
    }

    public static void main(String[] args) {
        String fileName = "twelvesd";
        Client client = new Client();
        client.downloadFile(fileName);
    }

    public void downloadFile(String fileName){
        this.clientServices.downloadFile(fileName);
    }

}
