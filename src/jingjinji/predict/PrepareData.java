package jingjinji.predict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TimeZone;




/**
 * 
 * ��ȡ��ǰʱ��֮ǰ��
 * 
 * @author jq
 * 
 *
 */
public class PrepareData {

	private static HashMap<String, double[]> reportSpeed = new HashMap<String, double[]>(); 
	 
	
	private static int historySize = 48 ; //4*60/5;
	
	
	/**
	 * ָ��ǰҪ���µ�����  ÿ����Ӷ�ȡһ������Ȼ�����nowIndexָ�������
	 * ��ʼ���ݵĶ�ȡ��nowIndex�����µ����ݡ�
	 */
	private static int nowIndex = 0 ;
	
	private static int timeInterval = 5 * 60; 
	
	private static String[] filePrefix = {"RTIC_CU_BEJ_","RTIC_CU_HEB_","RTIC_CU_TAJ_"};
	                      
//	private static String [][] roadList={{"G1","G2","G4","G6","G7"},{"G0401","G18","G1811","G1812","G2","G20","G3","G4"},
//										{"G1","G18","G2","G25","G2501","G3"}};
	
	private static String [][] roadList={{"G1","G2","G4","G6"},{"G18","G2","G20","G3","G4"},
		{"G1","G18","G2","G3"}};
	private static String localRitcPath = "F:\\ftp_rtic\\";
	
	private  static Calendar cal ;
	/**
	 * curTimeStamp ��ǰ��ȡ���ĸ�Сʱ�����ݺ��ڸû������������ȡ����ӵ����ݣ�
	 * Ҳ����curTimeStamp �Ļ����ϣ���������ӡ�
	 */
	private static long curTimeStamp;
	
	
	/**
	 *  ��ϵͳ������ʱ����Ҫ��ȡ֮ǰ�ĸ�Сʱ�����ݡ�
	 *  �������ÿ�ζ�ȡ����ӵ����ݾ����ˡ�
	 *  ��ʼ nowIndex = his����0��λ��ʼ�������ݡ�
	 *  
	 */
	public void setInitData(){
	 
		// ��ȡ��ǰʱ��ʱ���
		curTimeStamp = getCurrentTime();
		
		long last;
		String fileNameTimeString;
		String tempFileName;
		for(int i =historySize ;i > 0  ;  i--){
			// ǰ�����
			last = curTimeStamp - i*timeInterval;
			cal.setTimeInMillis(last*1000);
			fileNameTimeString = format.format(cal.getTime());
			
			for(int m = 0; m < filePrefix.length ; m++){

				for(int k = 0 ; k < roadList[m].length ; k++){
					
					tempFileName = filePrefix[m]+roadList[m][k]+"_"+fileNameTimeString+".txt";
					//System.out.println(tempFileName);
					getOneFileData(tempFileName);
					//return;
				}
			}
			nowIndex++;
		}
		System.out.println("current nowIndex"+nowIndex);
		System.out.println("Get inital data done: "+reportSpeed.size());
		nowIndex = 0;// ָ�����ϵ�����
	}
	
	/**
	 *  ÿ��һ���Ӽ��һ�Σ�����Ҫ�ĵ�ǰ�����Ƿ��Ѿ�������ϣ��������Ԥ�⡣
	 *  
	 */
	public void updateSpeed(){
		
		if(!waitUntilDataReady()) return;
		// ��nowIndexλ���ϵ����ݸ���
		for(File temp:files){
			getOneFileData(temp.getAbsolutePath());
		}
		nowIndex = (++nowIndex)%historySize;
		curTimeStamp += timeInterval;
	}
	
	/**
	 * �����ļ����б��ж��Ƿ��Ѿ��������
	 * @return
	 */
	private long sleepTime = 30000;  // 1 ����
	private LinkedList<File> files;
	public boolean waitUntilDataReady(){
		files = currentUpdataDataFilesList();
		
		int c =0;
		int size = files.size();
		do{
			c = 0;
			for(File tempFile: files){
				if(tempFile.exists())c++;
			}
			if( c < size-2){
				// sleep 30 s
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}while( c < size -2);
		return true;
	}
	
	public LinkedList<File> currentUpdataDataFilesList(){
		cal.setTimeInMillis(curTimeStamp*1000);
		String time = format.format(cal.getTime());
		String tempFileName;
		LinkedList<File> ans = new LinkedList<File>();
		for(int m = 0; m < filePrefix.length ; m++){

			for(int k = 0 ; k < roadList[m].length ; k++){
				
				tempFileName = filePrefix[m]+roadList[m][k]+"_"+time+".txt";
				ans.add(new File(localRitcPath+tempFileName));
			}
		}
		return ans;
	}
	// 
	private static File oneFile;
	private static BufferedReader bufferedReader;
	public boolean getOneFileData(String filename){
		//System.out.println(localRitcPath+filename);
		oneFile = new File(localRitcPath+filename);
		if(!oneFile.exists()) {
			System.out.println("file not exists: "+ filename);
			return false;
		}
		try {
			bufferedReader = new BufferedReader(new FileReader(oneFile));
			String oneLine;
			String []oneLineSplit;
			double []reportSpeedDouble;
			while((oneLine = bufferedReader.readLine())!=null){
				oneLineSplit = oneLine.split(",");
				if(oneLineSplit.length < 2)continue;
				if(reportSpeed.containsKey(oneLineSplit[0])){
					reportSpeedDouble = reportSpeed.get(oneLineSplit[0]);
				}else{
					reportSpeedDouble = new double[historySize];
				}
				reportSpeedDouble[nowIndex] = Double.parseDouble(oneLineSplit[1]);
				reportSpeed.put(oneLineSplit[0], reportSpeedDouble);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(bufferedReader != null){
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
	
	
	
	// ��ȡ��ǰ�������ʱ��
	public long getCurrentTime(){
		cal = Calendar.getInstance();
		int min = cal.get(Calendar.MINUTE);
		if(min%10<5){
			min = (min/10)*10;
		}else{
			min = (min/10)*10+5;
		}
		cal.set(Calendar.MINUTE, min);
		cal.set(Calendar.SECOND, 0);
//		System.out.println(min);
//		System.out.println(cal.getTimeInMillis()/1000);
		return  cal.getTimeInMillis()/1000;
//		cal.setTimeInMillis((cur-60*60)*1000);
//		System.out.println("format:"+format.format(cal.getTime()));
	}
	
	// ���ļ���������ʱ��
	public static String fileNameToTime(String name){
		//String test = "RTIC_CU_BEJ_G1_20150917114003.txt";
		if(name == null || name.indexOf("_")==-1) return null;
		return name.substring(name.lastIndexOf("_")+1,name.length()-6)+"00";
	}
	// 20150917114000 to long ʱ���
	private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
	private static Date date;
	public static long fileNameToStamp(String name){
		try {
			date = format.parse(name);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	
	public static HashMap<String, double[]> getReportSpeed() {
		return reportSpeed;
	}

	public static void setReportSpeed(HashMap<String, double[]> reportSpeed) {
		PrepareData.reportSpeed = reportSpeed;
	}

	public static int getHistorySize() {
		return historySize;
	}

	public static void setHistorySize(int historySize) {
		PrepareData.historySize = historySize;
	}

	public static String[][] getRoadList() {
		return roadList;
	}

	public static void setRoadList(String[][] roadList) {
		PrepareData.roadList = roadList;
	}

	public static long getCurTimeStamp() {
		return curTimeStamp;
	}

	public static void setCurTimeStamp(long curTimeStamp) {
		PrepareData.curTimeStamp = curTimeStamp;
	}

	public static int getNowIndex() {
		return nowIndex;
	}

	public static void setNowIndex(int nowIndex) {
		PrepareData.nowIndex = nowIndex;
	}

	public static void main(String[] args) {
		System.out.println(new PrepareData().fileNameToTime("RTIC_CU_BEJ_G1_20150917114003.txt"));
		new PrepareData().setInitData();
		//String curName ="RTIC_CU_BEJ_G1_20150917114003.txt" ;
		//System.out.println( curName.substring(0,curName.length()-6)+".txt");
	}
	
	
}
