package xyz.supermoonie.guid.ch06;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author moonie
 * @date 2018/2/28
 */
public class TestUserInfo {

    public static void main(String[] args) throws IOException {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(10);
        userInfo.setUserName("netty");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(userInfo);
        os.flush();
        os.close();
        byte[] b = bos.toByteArray();
        System.out.println("java jdk serializable length: " + b.length);
        bos.close();
        System.out.println("byte array serializable length: " + userInfo.encode().length);
    }
}
