package com.jd.megrez.supervise.common;

import com.jd.megrez.supervise.exception.SubmitBusinessException;

import java.io.*;

/**
 * @auther yuanjingshen
 * @date 2020/5/20 11:44
 * @desc
 */
public class BufferedWriterUtil {
    public BufferedWriter newBufferedWriter(String fileFullPathName) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fileFullPathName))));
        } catch (FileNotFoundException e) {
            throw new SubmitBusinessException(e.getMessage(), e);
        }
        return writer;
    }
}
