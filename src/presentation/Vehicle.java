package presentation;

public class Vehicle {
	private int DTC;

	private int v_0;
	private int[] action;//��ѡ������ʵ����ֵ�����Ǵ���
	private int a;//״̬�е�a���ţ��������״̬ǰִ�еĶ���
	
	private double[] intentions;
	public static double[] I0={0.7,0.2,0.1};
	public static double[] I1={0.3,0.5,0.2};
	
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
		if(v2<0)v2=0;//ɲ������ٶȼ���0

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
