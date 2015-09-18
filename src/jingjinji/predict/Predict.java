package jingjinji.predict;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;

import jingjinji.getData.AutoDownloadTask;



/**
 * 
 * 调用预测算法，返回预测结果
 * 
 * @author jq
 *
 */
public class Predict {

	private static HashMap<String, double[]> predictSpeed = new HashMap<String, double[]>();
	
	public Predict(){
		
	}
	private static  long timeInterval = 120; //每隔两分钟执行一次 
	public void predict(){
		System.out.println("Getting started!");
		
		// 开启一个线程
		
		Timer timer  = new Timer();
		timer.schedule(new AutoDownloadTask(),timeInterval);
		
		try {
			Thread.sleep(30*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Prepare data.");
		
		PrepareData prepareData = new PrepareData();
		prepareData.setInitData();
		
		Calendar cal = Calendar.getInstance();
		
		int base[] = {30,60,120};
		HashMap<String, double[]> reportSpeed;
		///
		// 下面每每隔五分钟执行一次
		double [] onePredict;
		while(true){
			reportSpeed = PrepareData.getReportSpeed();
			THPredict.setHistorySize(PrepareData.getHistorySize());
			THPredict.setNowIndex(PrepareData.getNowIndex());
			// 预测三个结果
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

			// 将预测结果写入文件，或者发送给sjgt
			cal.setTimeInMillis(prepareData.getCurrentTime()*1000);
			
			System.out.println("Predict Time:"+ cal.getTime());
			System.out.println("Predict Time:"+ prepareData.getCurrentTime());
			System.out.println("predict size:" + predictSpeed.size());	
			
			// 更新数据
			// 读取最新到达的数据，如果已经存在就直接读取，否则会等待，知道数据全部到达。
			// 在最后还会更新curtime时间。
			prepareData.updateSpeed();
			///
		}
			
		
	}
	public static void main(String[] args) {
		 new Predict().predict();
	}
	
}
