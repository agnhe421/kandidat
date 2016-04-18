package com.networksockettest.game;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Andreas on 2016-04-15.
 */

//EX: Logger.get().print(hej);

public class Logger {
    private Logger(){}
    private static int mlineCounter = 0, elineCounter = 0;
    static Logger _logger;
    static File msglog, errorlog;

    public static Logger get()
    {
        if(_logger == null)
            _logger = new Logger();
        return _logger;
    }

    public synchronized void print(String msg, String filename)
    {
        if(msglog == null)
            msglog = new File("sdcard/msglog.txt");
        if(errorlog == null)
            errorlog = new File("sdcard/errorlog.txt");
        if(!msglog.exists())
        {
            try
            {
                msglog.createNewFile();
            }catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        if(!errorlog.exists())
        {
            try
            {
                errorlog.createNewFile();
            }catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        if(mlineCounter > 100 && msglog.exists())
        {
            try {
                BufferedWriter del = new BufferedWriter(new FileWriter(msglog, false));
                del.write("");
                del.close();
            }catch(IOException e)
            {
                e.printStackTrace();
            }
            mlineCounter = 0;
        }
        if(elineCounter > 100 && errorlog.exists())
        {
            try {
                BufferedWriter del = new BufferedWriter(new FileWriter(errorlog, false));
                del.write("");
                del.close();
            }catch(IOException e)
            {
                e.printStackTrace();
            }
            elineCounter = 0;
        }
        if(filename.equals("msglog"))
        {
            try
            {
                BufferedWriter buf = new BufferedWriter(new FileWriter(msglog, true));
                buf.append(msg);
                buf.newLine();
                buf.close();
                ++mlineCounter;
            }catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        else if(filename.equals("errorlog"))
        {
            try
            {
                BufferedWriter buf = new BufferedWriter(new FileWriter(errorlog, true));
                buf.append(msg);
                buf.newLine();
                buf.close();
                ++elineCounter;
            }catch(IOException e)
            {
                e.printStackTrace();
            }
        }

    }
}
