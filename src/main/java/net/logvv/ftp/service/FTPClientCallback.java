package net.logvv.ftp.service;

import java.io.IOException;  

import org.apache.commons.net.ftp.FTPClient;  
/** 
 * FTPCLient回调 
 * @author longgangabai 
 * 
 * @param <T> 
 */  
public interface FTPClientCallback<T> {  
      
    public T doTransfer(FTPClient ftp)throws IOException;  
  
}  