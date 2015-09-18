package jingjinji.predict;



/**
 * 
 * Ô¤²âËã·¨
 * 
 * @author jq
 *
 */
public class THPredict {

	
	private static  int base = 5;
	private static  int historySize; 
	private static  int nowIndex;
	
	static double Cf = 80,Cc=-15,deltaV = 20,Vc = 60,tao = 1.1/60;
	
	private static double exponential(double t ){
		return Math.exp(-Math.abs(t)/tao);
	}
	public static  double predict(double [] speeds){
		double Nc = 0,Nf = 0,Zc = 0,Zf = 0;
		for(int t=historySize*5,i=nowIndex;t > 0;t-=5,i=(i+1)%historySize){
				if(speeds[i]>1){
					double PHIc = exponential(-(t+base)/60.0);
					double PHIf = exponential(-(t+base)/60.0);
					Nc+=PHIc;
					Nf+=PHIf;
					Zc+=speeds[i]*PHIc;
					Zf+=speeds[i]*PHIf;
				}
		}
		if(Nc<=0||Nf<=0) return -1;
		Zc/=Nc;
		Zf/=Nf;
		double weight = 0.5*(1+Math.tanh((Vc-Math.min(Zc, Zf))/deltaV));
		return Zf+weight*(Zc-Zf);
	}
	
	
	public static int getBase() {
		return base;
	}


	public static void setBase(int base) {
		THPredict.base = base;
	}


	public static int getHistorySize() {
		return historySize;
	}


	public static void setHistorySize(int historySize) {
		THPredict.historySize = historySize;
	}


	public static int getNowIndex() {
		return nowIndex;
	}


	public static void setNowIndex(int nowIndex) {
		THPredict.nowIndex = nowIndex;
	}


	public static void main(String[] args) {

		
		
	}

}
