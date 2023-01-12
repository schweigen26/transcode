package com.pachira.POCTools.General_transcoding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Run {
  private static int i = 0;
  
  private static int cantTransNum = 0;
  
  private static ArrayList<String> outputFiles = new ArrayList<>();
  
  private static HashMap<String, String> map = new HashMap<>();
  
  public static void main(String[] paramArrayOfString) {
    String str1 = "usage:\tjava -jar xxx.jar order[1\\2\\3] threadnum inputdir(in) [outdir(out)]  ";
    int i = 0;
    String str2 = ".";
    try {
      i = Integer.parseInt(paramArrayOfString[0]);
    } catch (Exception exception) {
      System.err.println("命令格式错误！请重试");
      System.err.println(str1);
      System.exit(-1);
    } 
    if ((i == 1 && paramArrayOfString.length < 3) || (i != 1 && paramArrayOfString.length < 4)) {
      System.out.println(str1);
      System.exit(0);
    } 
    String str3 = paramArrayOfString[2];
    if (i != 1)
      str2 = paramArrayOfString[3]; 
    int j = 1;
    try {
      j = Integer.parseInt(paramArrayOfString[1]);
    } catch (Exception exception) {
      System.err.println("threadnum " + j + " is illegle");
      System.exit(-1);
    } 
    Run run = new Run();
    ArrayList<String> arrayList = Utils.getFiles(str3);
    ExecutorService executorService = Executors.newFixedThreadPool(j);
    for (byte b = 0; b < j + 10; b++) {
      run.getClass();
      executorService.execute(new transer(arrayList, str3, str2, i));
    } 
    executorService.shutdown();
    try {
      executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    } catch (InterruptedException interruptedException) {
      interruptedException.printStackTrace();
    } 
    if (i == 3) {
      countOutput(str2);
    } else if (i == 2) {
      countDetect(i, str3);
      copyFiles(str3, str2);
    } else {
      countDetect(i, str3);
    } 
  }
  
  private static void copyFiles(String paramString1, String paramString2) {
    try {
      String str1 = (new File(paramString1)).getCanonicalPath();
      String str2 = (new File(paramString2)).getCanonicalPath();
      Pattern pattern = Pattern.compile("[\\W]");
      for (String str3 : map.keySet()) {
        String str4 = map.get(str3);
        Matcher matcher = pattern.matcher(str4);
        String str5 = matcher.replaceAll("_").trim();
        String str6 = str3.replace(str1, str2 + "/" + str5);
        Utils.creatdir((new File(str6)).getParent());
        Utils.fileChannelCopy(new File(str3), new File(str6));
      } 
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
  }
  
  private static void countDetect(int paramInt, String paramString) {
    LinkedHashSet<String> linkedHashSet = new LinkedHashSet();
    Transcode transcode = new Transcode();
    String str1 = "pachira_test_-_x.wav";
    StringBuffer stringBuffer = new StringBuffer();
    for (String str : map.values())
      linkedHashSet.add(str); 
    for (String str3 : linkedHashSet) {
      HashSet<String> hashSet = new HashSet();
      for (String str : map.keySet()) {
        if (((String)map.get(str)).equals(str3))
          hashSet.add(str); 
      } 
      String str4 = "";
      Iterator<String> iterator = hashSet.iterator();
      if (!str3.equals("unknow"))
        if (str3.contains("Audio: pcm_s16le")) {
          if (str3.contains("8000") && str3.contains("128")) {
            str4 = "\twav8\t不需要转码";
          } else if (str3.contains("16000") && str3.contains("256")) {
            str4 = "\twav16\t不需要转码";
          } 
        } else if (transcode.General_trans(iterator.next(), str1)) {
          str4 = "\t可以直接转码";
        }  
      String str5 = "\n" + str3 + "\n\t:一共" + hashSet.size() + "个" + str4 + "\n";
      System.out.print(str5);
      stringBuffer.append(str5);
    } 
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String str2 = simpleDateFormat.format(new Date());
    Utils.write("\n###############\t" + str2 + "\t################\n", "DetectCount");
    Utils.write(stringBuffer.toString(), "DetectCount");
    File file1 = new File(str1);
    if (file1.exists())
      file1.delete(); 
    File file2 = new File("can_not_tran.list");
    if (file2.isFile())
      file2.delete(); 
  }
  
  private static void countOutput(String paramString) {
    int i = outputFiles.size();
    long l1 = 0L;
    for (String str : outputFiles) {
      File file = new File(str);
      if (file.exists() && file.isFile())
        l1 += file.length() - 44L; 
    } 
    long l2 = l1 / 16000L;
    String str1 = getTime(l2);
    System.out.println("文件个数:\t" + i);
    System.out.println("总时长:\t" + str1);
    String str2 = "统计结果:\n文件个数:\t" + i + "\n总时长:\t" + str1 + "\n失败个数:\t" + cantTransNum + "\n";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String str3 = simpleDateFormat.format(new Date());
    Utils.write("#############" + str3 + "###############\n", "outputCount");
    Utils.write(str2, "outputCount");
    Utils.deleteEmptyDir(paramString);
  }
  
  private static String getTime(long paramLong) {
    long l1 = paramLong / 3600L;
    long l2 = paramLong % 3600L / 60L;
    long l3 = paramLong % 60L;
    return "" + l1 + "小时" + l2 + "分" + l3 + "秒";
  }
  
  public synchronized String getwavloc(List<String> paramList) {
    if (i >= paramList.size())
      return null; 
    String str = paramList.get(i);
    i++;
    return str;
  }
  
  private synchronized void adddict(HashMap<String, String> paramHashMap) {
    map.putAll(paramHashMap);
  }
  
  private synchronized void addCantTrans() {
    cantTransNum++;
  }
  
  private synchronized void addOutputFiles(String paramString) {
    outputFiles.add(paramString);
  }
  
  class transer implements Runnable {
    Transcode t = new Transcode();
    
    private List<String> inputFiles;
    
    String outDir = "";
    
    String inDir = "";
    
    String acodec = "";
    
    int order = 0;
    
    transer(List<String> param1List, String param1String1, String param1String2, int param1Int) {
      this(param1List, param1String1, param1String2, param1Int, "");
    }
    
    transer(List<String> param1List, String param1String1, String param1String2, int param1Int, String param1String3) {
      this.inputFiles = param1List;
      this.outDir = param1String2;
      this.inDir = param1String1;
      this.acodec = param1String3;
      this.order = param1Int;
    }
    
    public void run() {
      String str;
      while ((str = Run.this.getwavloc(this.inputFiles)) != null) {
        try {
          if (this.order == 3) {
            String str1 = getOutputFile(str);
            if (this.t.General_trans(str, str1)) {
              Run.this.addOutputFiles(str1);
              continue;
            } 
            Run.this.addCantTrans();
            continue;
          } 
          HashMap<String, String> hashMap = this.t.General_Detect(str);
          Run.this.adddict(hashMap);
        } catch (Exception exception) {
          exception.printStackTrace();
        } 
      } 
    }
    
    private String getOutputFile(String param1String) {
      String str = null;
      try {
        String str1 = (new File(param1String)).getCanonicalPath();
        String str2 = null;
        String str3 = null;
        str2 = (new File(this.outDir)).getCanonicalPath();
        str3 = (new File(this.inDir)).getCanonicalPath();
        File file = new File(str1.replace(str3, str2));
        String str4 = file.getParent();
        String str5 = file.getName();
        Pattern pattern = Pattern.compile(".*\\.[^\\.]*$");
        Matcher matcher = pattern.matcher(str5);
        String str6 = "";
        if (matcher.matches()) {
          str6 = str5.replaceAll("\\.[^\\.]*$", ".wav");
        } else {
          str6 = str5 + ".wav";
        } 
        str = str4 + "/" + str6;
        Utils.creatdir(str4);
      } catch (IOException iOException) {
        iOException.printStackTrace();
      } 
      return str;
    }
  }
}


/* Location:              C:\Users\dell\Desktop\新建文件夹 (3)\TransCode.jar!\com\pachira\POCTools\General_transcoding\Run.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */