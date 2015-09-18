package jingjinji.predict;


public class THfiltration {
	//前historySize-1时间范围内的影响，空间影响最大的范围maxDistkm
	static int historySize = 13,nowIndex ,maxDist = 30;
	static double Cf = 80,Cc=-15,deltaV = 20,Vc = 60,sigma = 0.6,tao = 1.1/60;
	static int base = 10;
 
	public static  void setHistorySize(int size){
		historySize = size;
	}
	 
	private static double exponential(double t ){
		return Math.exp(-Math.abs(t)/tao);
	}
	
    public static double filtering(double [] speeds ){
		double Nc = 0,Nf = 0,Zc = 0,Zf = 0;
		for(int t=0,i=nowIndex;t<historySize*5;t+=5,i=(i-1+historySize)%historySize){
				if(speeds[i]>1){
					double PHIc = exponential((-t+base)/60.0);
					double PHIf = exponential((-t+base)/60.0);
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
	public static int getNowIndex(){
		return nowIndex;
	}
	public static void setNowIndex(int nowIndex) {
		THfiltration.nowIndex = nowIndex;
	}
	public static int getBase() {
		return base;
	}
	public static void setBase(int base) {
		THfiltration.base = base;
	}
	 
	
}
