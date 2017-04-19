package net.logvv.ftp.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.log4j.Logger;

public class FileOperateUtils {
	private static final Logger logger = Logger.getLogger(FileOperateUtils.class);    
    /** 
     * 查找需要合并文件的临时文件信息  
     *  
     * @param directory 
     *          临时文件所在的目录 
     * @param prefix 
     *          临时文件的前缀 
     * @param caseSensitivity 
     *          临时文件的大小写敏感 
     * @return 
     */  
    public static Collection<File> searchPrefixFile(File directory,String prefix,boolean caseSensitivity){  
        IOCase iocase=IOCase.INSENSITIVE;  
        if(caseSensitivity){  
             iocase=IOCase.SENSITIVE;  
        }  
        //创建相关的过滤器  
        IOFileFilter fileFilter=FileFilterUtils.prefixFileFilter(prefix, iocase);  
        //检查相关的过滤信息  
        return FileUtils.listFiles(directory, fileFilter, FalseFileFilter.INSTANCE);  
    }  
      
    /** 
     *  查找目录下特定后缀的文件 
     * @param directory     
     *       特定的目录 
     * @param extensions 
     *      临时文件的后缀扩展 
     * @param recursive 
     *      是否查询临时文件所在的目录下的子目录 
     * @return 
     */  
    public static Collection<File> searchExtensionFile(File directory,String[] extensions,boolean recursive){  
        return FileUtils.listFiles(directory, extensions, recursive);  
    }  
    /** 
     * 文件追加功能 
     * @param lines 
     * @param tmpFilePath 
     */  
    public static void writeLinesToFile(Collection<String> lines,String tmpFilePath){  
        OutputStream output=null;  
        try {  
            output = new FileOutputStream(tmpFilePath,true);  
            IOUtils.writeLines(lines, "UTF-8", output);  
        } catch (Exception e) {  
            logger.error(tmpFilePath+"追加文件失败"+e.getMessage());  
        }  
          
    }  
}
