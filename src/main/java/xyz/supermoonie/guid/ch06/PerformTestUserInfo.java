package xyz.supermoonie.guid.ch06;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

/**
 *
 *
 * @author Administrator
 * @date 2018/2/28 0028
 */
public class PerformTestUserInfo {

    public static void main(String[] args) throws IOException {
        UserInfo info = new UserInfo();
        info.setUserId(100);
        info.setUserName("netty");
        int loop = 1000000;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < loop; i ++) {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(info);
            os.flush();
            os.close();
            byte[] bytes = bos.toByteArray();
            bos.close();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("the java jdk serializable cost time is: " + (endTime - startTime) + " ms");
        System.out.println("-----------------------------------------------------");
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        startTime = System.currentTimeMillis();
        for (int i = 0; i < loop; i ++) {
            byte[] b = info.encode(buffer);
        }
        endTime = System.currentTimeMillis();
        System.out.println("the java jdk serializable cost time is: " + (endTime - startTime) + " ms");
    }
}
