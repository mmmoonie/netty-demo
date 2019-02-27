package xyz.supermoonie.nio;

import javax.swing.*;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * @author wangchao
 * @date 2019/2/21 23:43
 */
public class Demo02 {

    public static void main(String[] args) throws IOException {
        RandomAccessFile file = new RandomAccessFile("/Users/wangchao/Downloads/Qt5.pdf", "rw");
        FileChannel channel = file.getChannel();
        FileLock lock = channel.lock();
        JOptionPane.showMessageDialog(null, "stop");
        lock.release();
    }
}
