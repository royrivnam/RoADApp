package com.mcnrg.akash.safestreet;

import java.io.RandomAccessFile;

/**
 * Created by Akash on 15-09-2018.
 */

public class TotalDistance {

    private double distance;
    private double extra_dis;
    private String line;
    private String output;
    private RandomAccessFile raff;

    public TotalDistance(String path, double extra)
    {
        output=path;
        extra_dis=extra;
    }

    public void calculate()
    {
        try {
            raff=new RandomAccessFile(output,"rw");
            try {
                line = raff.readLine().trim();
            }
            catch (Exception e)
            {
                line="";
                e.printStackTrace();
            }
            if(!(line.equals(""))) {
                distance = Double.parseDouble(line);
                distance = distance + extra_dis;
            }
            else
            {
                distance=extra_dis;
            }
            raff.setLength(0);
            raff.write(String.valueOf(distance).getBytes());
            raff.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
