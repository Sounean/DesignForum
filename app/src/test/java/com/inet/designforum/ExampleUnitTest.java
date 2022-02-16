package com.inet.designforum;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.inet.designforum.util.Util;

import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws IOException {
//        byte[] bytes = Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAC0AAAAuCAYAAAC8jpA0AAAABHNCSVQICAgIfAhkiAAAAu9JREFU" +
//                        "aIHtmU9IFGEUwN+bmZ1Fc5UM1yyR6LDpulJIp8WkXF3XDhZ5CeqkQXQKt+xgdAoSNaM/UAjrJepq" +
//                        "IWSbuhK0aQchwl21PUVK5d9DG1s7M7tfl04zO/rNOq5DzO82b97j/fjmm/lgHoCJiSpIk0QI4YPz" +
//                        "qcY3n6WmT4vpYytxYhdThKNtks9jYn8h86OyDBeaj3Djvip2AhGFHZMemxe9fZNC19cNaMy2iZyK" +
//                        "Ypi40cD3e6ssY9nUq0oTQrj+kOQPTCU7ANGRvaJqg9glt3Woy8PdQ0RJS6nqI+4PSf7AtNALSLWD" +
//                        "tIPoCEwLvf+u+rSUMpmCwajoC0wlO7YtRkFgKtkRjIo+LTWKZSSE8PUPfoeW46ROP7XNsdsw/O5q" +
//                        "nof25VSs9MhcypdLYQCAlTipezmbOk2br5CemJea9FWiIxSTPLS5CunZb2mXvjp0RDT0VUiv/iJ2" +
//                        "fXXoWI3T91VIixL9SacnmU7YpXVS7h/+MyCP74qgGkvrpPxgMax9XATXi1nhTPNg4tyhvcwXeZ5x" +
//                        "pBEdDY8Ti/KwswxHX8liGQ8XI+E+zL2XxwwtbeUgUl/DTsrjhpZuO8oN70P8KY8bVtpegGF/Hf8o" +
//                        "0z1DSlsYmHvYltdZWIhrme4bTro4Hz4Mnucv11bgjFqOcT55hMSunOCftNdanhYV4cZmqYqV3mPF" +
//                        "xM6ZqWNhUeo8xd/fShjAgNuDBlM6V5jSucKUzhWmdK5QSJcUwMpuiJTYkLqvQvpIKRvTV4cO1wEm" +
//                        "QpurkG6s4sb11aHD4+BCtLkK6VYnGyy1YVhfpc2x2zB8toYdpc1XSCOi0O3le4CQ3GwTQmI3vXyP" +
//                        "lslAxq9Hi5MbbXdbh/QzU6fdbR1qcXLUqwywxSTg9muh+9mMeGGnJgEXj1ue32rh72idBGz5mz8Y" +
//                        "FX133wrX9J65XD/JD/iqLcFs6qmnWyORVHJ8QYLo9zQsxwlIafomHANQakOoLmOgqZKDVhdr3c50" +
//                        "y8Tkf+Mv8YvruPTs8WgAAAAASUVORK5CYII=");
//
//        FileOutputStream fos = new FileOutputStream(new File("D:\\1.png"));
//        fos.write(bytes);
//        fos.close();

//        String img = "C:\\Users\\Chienli\\Desktop\\small_coures.png";
//
//        File file = new File(img);
//        FileInputStream fis = new FileInputStream(file);
//        Bitmap bitmap = BitmapFactory.decodeStream(fis);
//        String base64 = Util.bitmapToStr(bitmap);
//
//        File base64Txt =  new File("D:\\imgBase64.txt");
//        BufferedWriter writer = new BufferedWriter(new FileWriter(base64Txt));
//        writer.write(base64);
//        writer.close();
//
//        File base64Img = new File("D:\\imgBase64.png");
//        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(base64Img));
//        byte[] bytes = Base64.getUrlDecoder().decode(base64);
//        bos.write(bytes);
//        bos.close();
//
//        fis.close();

//        String s = "AAA.JPEG";
//        StringBuilder sb = new StringBuilder(s);
//        sb.insert(sb.lastIndexOf("."),"_compress");
//        System.out.println(sb.toString());
//
//
//        Base64.getUrlDecoder()
    }
}