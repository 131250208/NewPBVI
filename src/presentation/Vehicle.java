package presentation;

public class Vehicle {
	private int DTC;

	private int v_0;
	private int[] action;//可选动作，实际数值，不是代号
	private int a;//状态中的a代号，到达这个状态前执行的动作
	
	private double[] intentions;
	public static double[] I0={0.7,0.2,0.1};
	public static double[] I1={0.1,0.2,0.7};
	
	private boolean host;
	
	public Vehicle(int DTC,int v_0,int a) {
		// TODO Auto-generated constructor stub
		this.DTC=DTC;
		this.v_0=v_0;
		this.a=a;
	}
	public Vehicle(int DTC,int v_0,int a,int inten_num) {
		// TODO Auto-generated constructor stub
		this(DTC,v_0,a);
		switch(inten_num){
			case 0:this.intentions=I0;
				break;
			case 1:this.intentions=I1;
				break;
		}
	}
	
	public void execute(int a){
		int v2 = v_0 + a;
		if(v2<0)v2=0;//刹车最多速度减到0

		if (a != 0) {
			int s = (v2*v2 - v_0*v_0) / (2 * a);
			DTC -= s;
		}
		else {
			DTC -= v_0;
		}
		v_0 = v2;
		this.a=a;
	}
	
	public double[] getIntentions() {
		return intentions;
	}
	public void setIntentions(double[] intentions) {
		this.intentions = intentions;
	}
	public int getDTC() {
		return DTC;
	}
	public void setDTC(int dTC) {
		DTC = dTC;
	}
	public int getV_0() {
		return v_0;
	}
	public void setV_0(int v_0) {
		this.v_0 = v_0;
	}
	public int getA() {
		return a;
	}
	public void setA(int a) {
		this.a = a;
	}
}
