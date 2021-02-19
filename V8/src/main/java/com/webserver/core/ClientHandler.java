package com.webserver.core;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 * 负责与指定的客户端进行HTTP交互
 * HTTP协议要求与客户端的交互规则采取一问一答的方式，因此，处理客户端交互以3步形式完成：
 * 1：解析请求(一问)
 * 2：处理请求
 * 3：发送响应(一答)
 */
public class ClientHandler implements Runnable{
    private Socket socket;
    public ClientHandler(Socket socket){
        this.socket = socket;
    }
    public void run(){
        try {
            /*
                获取IP的命令
                Windows：ipconfig
                mac：ifconfig
             */
            //1解析请求
            HttpRequest request = new HttpRequest(socket);
            HttpResponse response = new HttpResponse(socket);
            //2处理请求
            //首先通过request获取请求中的抽象路径
            String path = request.getUri();
            //3发送响应
            File file = new File("./webapps"+path);
            if(file.exists()&&file.isFile()) {
                System.out.println("该资源已找到");
                response.setEntity(file);
                //若资源不存在
            }else{
                System.out.println("该资源不存在");
                file = new File("./webapps/root/404.html");
                response.setStatusCode(404);
                response.setStatusReason("NotFound");
                response.setEntity(file);
            }
            response.flush();
            System.out.println("响应发送完毕");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //处理完毕后与客户端断开连接
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
