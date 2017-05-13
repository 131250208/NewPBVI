package pomdpBuilder;

import java.util.ArrayList;
import java.util.Collections;

public class JointStateOperator {

	
	private int asc;
	private int con;
	private int des;
	private int v_0;//默认初速度不为0
	private int DTC_max;
	
	private int count_js=0;
	private ArrayList<int[]> jointstates=new ArrayList<int[]>();
	private ArrayList<int[]> observations=new ArrayList<int[]>();
	

	public JointStateOperator(int DTC_max,int v_0,int asc,int con,int des){
		this.DTC_max=DTC_max;
		this.v_0=v_0;
		this.asc=asc;
		this.con=con;
		this.des=des;
		
//		//计算最大速度和偏移量
//		/*默认当人类驾驶车辆通过十字路口后，自动驾驶车辆只会采取加速策略，
//		 * 所以最坏情况就是，在人类驾驶车辆通过十字路口前，自动驾驶车辆一
//		 * 直减速到0（这里我们计算偏移量时看作在原地不动，这样偏移量略大一点更保险）*/
//		int t1=(int)Math.ceil(Math.sqrt(2*asc*DTC_max+v_0*v_0)/asc);//人类驾驶车辆全程加速通过十字路口的时间
//		int t_max=2*t1;
//		this.offset=(v_0*t_max+asc*t_max*t_max/2)-DTC_max;
//		int v_max=(int)Math.ceil(v_0+asc*t_max);
//		
//		this.sign=new char[DTC_max+offset+1][v_max+1][DTC_max+offset+1][v_max+1];
				
		this.calculate_js();
		System.out.println("\nnumber of joint states: "+this.getNum_js());
	}
	
	public boolean exist_observation(int... ob){
		for(int i=0;i<observations.size();++i){
			if(observations.get(i)[0]==ob[0]
					&&observations.get(i)[1]==ob[1]
					&&observations.get(i)[2]==ob[2]
					&&observations.get(i)[3]==ob[3]
					&&observations.get(i)[4]==ob[4]
					&&observations.get(i)[5]==ob[5]){
				return true;
			}
		}
		return false;
	}
	private boolean exist_jstate(int... jstate){//没有意图的jstate
		for(int i=0;i<jointstates.size();++i){
			if(jointstates.get(i)[0]==jstate[0]
					&&jointstates.get(i)[1]==jstate[1]
					&&jointstates.get(i)[2]==jstate[2]
					&&jointstates.get(i)[4]==jstate[3]
					&&jointstates.get(i)[5]==jstate[4]
					&&jointstates.get(i)[6]==jstate[5]){
				return true;
			}
		}
		return false;
	}
	private boolean mark(int... jstate){//参数是不带意图的js
		
		if(exist_jstate(jstate))return true;
		else{
			int[] ob0={jstate[0], jstate[1], jstate[2], jstate[3],jstate[4],asc};
			int[] ob1={jstate[0], jstate[1], jstate[2], jstate[3],jstate[4],con};
			int[] ob2={jstate[0], jstate[1], jstate[2], jstate[3],jstate[4],des};
			observations.add(ob0);//没有意图，加入观察集
			observations.add(ob1);
			observations.add(ob2);
			
			int[] js0={jstate[0], jstate[1], jstate[2], 0,jstate[3],jstate[4],jstate[5]};//加上人类驾驶车辆的意图0（加速意图）
			int[] js1={jstate[0], jstate[1], jstate[2], 1,jstate[3],jstate[4],jstate[5]};//加上人类驾驶车辆的意图1（匀速意图）
			jointstates.add(js0);//加入联合状态到状态数组
			jointstates.add(js1);
			
			System.out.print("state"+(count_js++)+": ");
			for(int i=0;i<js0.length;++i){
				System.out.print(js0[i]);
				if(i==js0.length-1)System.out.print("\n");
				else System.out.print(",");
			}
			System.out.print("state"+(count_js++)+": ");
			for(int i=0;i<js1.length;++i){
				System.out.print(js1[i]);
				if(i==js1.length-1)System.out.print("\n");
				else System.out.print(",");
			}

			return false;
		}
	}
	private void calculate_js(){
	
		//先将起始状态加入
		int[] js_0={DTC_max,v_0,con,DTC_max,v_0,  con};
		mark(js_0);
		
		trans_js(DTC_max,v_0,asc,DTC_max,v_0,  asc);
		trans_js(DTC_max,v_0,asc,DTC_max,v_0,  con);
		trans_js(DTC_max,v_0,asc,DTC_max,v_0,  des);
		trans_js(DTC_max,v_0,con,DTC_max,v_0,  asc);
		trans_js(DTC_max,v_0,con,DTC_max,v_0,  con);
		trans_js(DTC_max,v_0,con,DTC_max,v_0,  des);
		trans_js(DTC_max,v_0,des,DTC_max,v_0,  asc);
		trans_js(DTC_max,v_0,des,DTC_max,v_0,  con);
		trans_js(DTC_max,v_0,des,DTC_max,v_0,  des);
	}

	private void trans_js(int DTC_human,int v_human,int a_human,int DTC_host,int v_host,int a_host){
		//联合状态更新
		int v2 = v_human + a_human;
		if(v2<0)v2=0;//刹车最多速度减到0

		if (a_human != 0) {
			int s = (v2*v2 - v_human*v_human) / (2 * a_human);
			DTC_human -= s;
		}
		else {
			DTC_human -= v_human;
		}
		v_human = v2;

		v2 = v_host + a_host;
		if(v2<0)v2=0;
		
		if (a_host != 0) {
			int s = (v2*v2 - v_host*v_host) / (2 * a_host);
			DTC_host -= s;
		}
		else {
			DTC_host -= v_host;
		}

		v_host = v2;

		//标记已经到达过的状态
		if(mark(DTC_human,v_human,a_human,DTC_host,v_host,a_host))return;//如果状态已经存在则直接返回
		
		
		//继续深搜
		if (DTC_host > 0) {
			if (DTC_human <= 0) {
				trans_js(DTC_human, v_human, asc, DTC_host, v_host, asc);
				trans_js(DTC_human, v_human,con, DTC_host, v_host,  asc);
				trans_js(DTC_human, v_human,des, DTC_host, v_host,  asc);
			}
			else
			{
				trans_js(DTC_human, v_human,asc, DTC_host, v_host,  asc);
				trans_js(DTC_human, v_human,asc, DTC_host, v_host, con);
				trans_js(DTC_human, v_human,asc, DTC_host, v_host,  des);
				trans_js(DTC_human, v_human,con, DTC_host, v_host,  asc);
				trans_js(DTC_human, v_human,con, DTC_host, v_host,  con);
				trans_js(DTC_human, v_human,con, DTC_host, v_host,  des);
				trans_js(DTC_human, v_human,des, DTC_host, v_host,  asc);
				trans_js(DTC_human, v_human,des, DTC_host, v_host,  con);
				trans_js(DTC_human, v_human,des, DTC_host, v_host,  des);
			}
		}
	}
	
	/*向外提供的接口*/
	public int[][] getNextJstates(int...jstate){//7元jstate，包括意图state[3]，返回两个可能的后续状态
		
		//联合状态更新
		int v2 = jstate[1] + jstate[2];
		if(v2<0)v2=0;//刹车最多速度减到0

		if (jstate[2] != 0) {
			int s = (v2*v2 - jstate[1]*jstate[1]) / (2 * jstate[2]);
			jstate[0] -= s;
		}
		else {
			jstate[0] -= jstate[1];
		}
		jstate[1] = v2;

		v2 = jstate[5] + jstate[6];
		if(v2<0)v2=0;
		
		if (jstate[6] != 0) {
			int s = (v2*v2 - jstate[5]*jstate[5]) / (2 * jstate[6]);
			jstate[4] -= s;
		}
		else {
			jstate[4] -= jstate[5];
		}

		jstate[5] = v2;
		
		int[][] res=new int[2][7];
		//判断后续状态是否存在
		if(!exist_jstate(jstate[0],jstate[1],jstate[2],jstate[4],jstate[5],jstate[6]))return null;//不存在，没有后续

		int[] js_0={jstate[0],jstate[1],jstate[2],0,jstate[4],jstate[5],jstate[6]};
		int[] js_1={jstate[0],jstate[1],jstate[2],1,jstate[4],jstate[5],jstate[6]};
		res[0]=js_0;
		res[1]=js_1;		
		return res;	
	}
	public int getNum_js(){//获得状态总数
		return jointstates.size();
	}
	
	public int getNum_ob(){//获得状态总数
		return observations.size();
	}
	
	public int[] getJState(int i){//获得第i个状态的向量
		return jointstates.get(i);
	}
	/*对外接口，获得状态的号码*/
	public int getIndex_jstate(int... jstate){//包含意图的jstate，7元
		for(int i=0;i<jointstates.size();++i){
			if(jointstates.get(i)[0]==jstate[0]
					&&jointstates.get(i)[1]==jstate[1]
					&&jointstates.get(i)[2]==jstate[2]
					&&jointstates.get(i)[3]==jstate[3]
					&&jointstates.get(i)[4]==jstate[4]
					&&jointstates.get(i)[5]==jstate[5]
					&&jointstates.get(i)[6]==jstate[6]){
				return i;
			}
		}
		return -1;
	}
	
	public int[] getObservation(int i){//获得第i个观察的向量
		return observations.get(i);
	}
	/*对外接口，获得状态的号码*/
	public int getIndex_ob(int... ob){//获得观察的号码
		for(int i=0;i<observations.size();++i){
			if(observations.get(i)[0]==ob[0]
					&&observations.get(i)[1]==ob[1]
					&&observations.get(i)[2]==ob[2]
					&&observations.get(i)[3]==ob[3]
					&&observations.get(i)[4]==ob[4]
					&&observations.get(i)[5]==ob[5]){
				return i;
			}
		}
		return -1;
	}
	
	/*将状态集提供给外部*/
	public ArrayList<int[]> getJointstates() {
		return jointstates;
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JointStateOperator jso=new JointStateOperator(100,50,50,0,-50);
		
	}
}
