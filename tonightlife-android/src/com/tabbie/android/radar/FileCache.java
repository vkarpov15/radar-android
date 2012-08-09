package com.tabbie.android.radar;

/**
 *  FileCache.java
 *
 *  Created on: August 9, 2012
 *      Author: Fedor Vlasov, edited Justin Knutson
 *      Source: https://github.com/thest1/LazyList/blob/master/src/com/fedorvlasov/lazylist/FileCache.java
 *      
 *  Semi-permanent storage for objects that would otherwise be cached
 */

import java.io.File;
import java.net.URLEncoder;

import android.content.Context;

public class FileCache {
    
    private File cacheDir;
    
    public FileCache(Context context) {
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),"LazyList");
        else
            cacheDir = context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
    
    /**
     * Obtain the file specified by the URL passed (as a String)
     * @param url - Currently a String but should be a URL later on
     * @return The appropriate file
     */
    public File getFile(String url) { // TODO This should probably take URL instead of String for Tabbie
        String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;   
    }
    
    /**
     * Delete all the files
     */
    public void clear() {
        File[] files = cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}