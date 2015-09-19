package jingjinji.predict;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;

import jingjinji.getData.AutoDownloadThread;



/**
 * 
 * ����Ԥ���㷨������Ԥ����
 * 
 * @author jq
 *
 */
public class Predict {

	private static HashMap<String, double[]> predictSpeed = new HashMap<String, double[]>();
	
	public Predict(){
		
	}
	private static  long timeInterval = 120; //ÿ��������ִ��һ�� 
	public void predict(){
		System.out.println("Getting started!");
		
		// ���ﲻ�ٿ������ص��̣߳���������������е���������
		
		
//		Thread downloadThread = new Thread(new AutoDownloadThread());
//		downloadThread.start();
		
//		
//		try {
//			Thread.sleep(30*1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		System.out.println("Prepare data.");
		
		PrepareData prepareData = new PrepareData();
		prepareData.setInitData();
		
		Calendar cal = Calendar.getInstance();
		
		int base[] = {30,60,120};
		HashMap<String, double[]> reportSpeed;
		///
		// ����ÿÿ�������ִ��һ��
		double [] onePredict;
		while(true){
			reportSpeed = PrepareData.getReportSpeed();
			THPredict.setHistorySize(PrepareData.getHistorySize());
			THPredict.setNowIndex(PrepareData.getNowIndex());
			// Ԥ���������
			for(int i =0; i < base.length ; i++){
				THPredict.setBase(base[i]);
				for(String nilink:reportSpeed.keySet()){
					
					if(predictSpeed.containsKey(nilink)){
						onePredict = predictSpeed.get(nilink);
					}else{
						onePredict = new double[base.length];
					}
					onePredict[i] = THPredict.predict(reportSpeed.get(nilink));
					predictSpeed.put(nilink, onePredict);
				}
			}

			// ��Ԥ����д���ļ������߷��͸�sjgt
			cal.setTimeInMillis(prepareData.getCurrentTime()*1000);
			
			System.out.println("Predict Time:"+ cal.getTime());
			System.out.println("Predict Time:"+ prepareData.getCurrentTime());
			System.out.println("predict size:" + predictSpeed.size());	
			
			// ��������
			// ��ȡ���µ�������ݣ�����Ѿ����ھ�ֱ�Ӷ�ȡ�������ȴ���֪������ȫ�����
			// ����󻹻����curtimeʱ�䡣
			prepareData.updateSpeed();
			///
		}
			
		
	}
	public static void main(String[] args) {
		 new Predict().predict();
	}
	
}
