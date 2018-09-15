package com.example.akash.safestreet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Akash on 15-09-2018.
 */

public class TotalDistance {

    private double distance;
    private double extra_dis;
    private String line;
    private String output;
    private RandomAccessFile raf;

    public TotalDistance(String path, double extra)
    {
        output=path;
        extra_dis=distance;
    }

    public void calculate()
    {
        try {
            raf=new RandomAccessFile(output,"rw");
            line=raf.readLine();
            distance=Double.parseDouble(line);
            distance=distance+extra_dis;
            raf.setLength(0);
            raf.write(String.valueOf(distance).getBytes());
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
