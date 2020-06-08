package com.jd.megrez.supervise.common;

/**
 * @auther yuanjingshen
 * @date 2020/5/20 10:53
 * @desc
 */
public class JarUtil {
    public String getJarPath()
    {
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        if(System.getProperty("os.name").contains("dows"))
        {
            path = path.substring(1,path.length());
        }
        if(path.contains("jar"))
        {
            path = path.substring(0,path.lastIndexOf("."));
            return path.substring(0,path.lastIndexOf("/"));
        }
        return path.replace("target/classes/", "");
    }
}
