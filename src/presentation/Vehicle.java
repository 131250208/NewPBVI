package presentation;

public class Vehicle {
	private int x,y;
	private int dis;
	private int velocity;
	
	private double[] intentions;//I1={0.8,0.1,0.1}��I2={0.1,0.8,0.1}��I3={0.1,0.1,0.8}���ֱ�Ϊ���١����١�������ͼ
	public static double[] I1={0.8,0.2};
	public static double[] I2={0.2,0.8};
//	public static double[] I3={0.1,0.1,0.8};
	
	private boolean host;
	
	public Vehicle(boolean host,int x,int y,int v) {
		// TODO Auto-generated constructor stub
		this.host=host;
		this.x=x;
		this.y=y;
		this.velocity=v;
	}
	public Vehicle(boolean host,int x,int y,int v,int inten_num) {
		// TODO Auto-generated constructor stub
		this(host,x,y,v);
		switch(inten_num){
			case 1:this.intentions=I1;
				break;
			case 2:this.intentions=I2;
				break;
//			case 3:this.intentions=I3;
//				break;
		}
	}
	
	public void execute(int a){//��λʱ�䲽ִ��a����
		int v2=velocity+a;
		if(v2<0)v2=0;
		
		int s;
		if(a==0){
			s=velocity;	
		}
		else s=(v2*v2-velocity*velocity)/(2*a);
		
		if(host){
			y-=s;
		}
		else x+=s;
		
		velocity=v2;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getVelocity() {
		return velocity;
	}
	public void setVelocity(int velocity) {
		this.velocity = velocity;
	}
	public double[] getIntentions() {
		return intentions;
	}
	public void setIntentions_dis(double[] intentions_dis) {
		this.intentions = intentions;
	}
	public boolean isHost() {
		return host;
	}
	public void setHost(boolean host) {
		this.host = host;
	}
	public int getDis() {
		if(host){
			return y-160;
		}
		return 507-x;
	}
}
