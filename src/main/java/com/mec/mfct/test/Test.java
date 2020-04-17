 package com.mec.mfct.test;

import com.mec.util.CreateFileUtil;

public class Test {

    public static void main(String[] args) {
      
      boolean ok = CreateFileUtil.createDir("I:\\upload\\origin\\a.b");
      System.out.println(ok);
    }

}
