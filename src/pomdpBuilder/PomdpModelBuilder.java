package pomdpBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;
import fileOperator.FileOp;
import hmmBuilder.HmmModelBuilder;

public class PomdpModelBuilder {
	private String pomdpModelFileName;
	private JointStateOperator jso;
	private int amountOfStates;
	private int amountOfObservations;
	private HmmModelBuilder hmmBuilder;
	
	private int[] actions=new int[3];
	private int v_0;//默认初速度不为0
	private int DTC_max;
	
	public int[] calSituationOfState(int i){
		return null;
	}
	HashMap<int[], Double> getMap_State_Probability(int[] state,int a){
		HashMap<int[], Double> map = new HashMap<int[], Double>();
		return map;
	}
	int getNumber_jointState(int[] state){
		return 0;
	}
	
	public PomdpModelBuilder(int DTC_max,int v_0,int asc,int con,int des) {
		pomdpModelFileName = "intersection.POMDP";
		jso=new JointStateOperator(DTC_max,v_0,asc,con,des);
		this.DTC_max=DTC_max;
		this.v_0=v_0;
		actions[0]=asc;
		actions[1]=con;
		actions[2]=des;
		
		/*	因为训练时间可能过长
		 * 更优的办法是离线训练并将HMM模型序列化保存在本地，
		 * 这里直接反序列化取用即可
		 * */
		this.hmmBuilder=new HmmModelBuilder();
		//读取观察序列的数据文件并进行HMM模型的训练
		
		amountOfStates = jso.getNum_js();
		amountOfObservations=jso.getNum_ob();
	}
	double getReward(int[] state,int a){
		return 0;
	}
	
	public void writeBlank() {
		FileOp.appendFileContent(pomdpModelFileName,"\n");
	}

	//建立POMDP模型文件
	public void buildPomdpModelFile() {
		System.out.println("buildPomdpModelFile start……");
		try {
			FileOp.createFile(pomdpModelFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("buildPomdpModelFile complete");
	}

	//写入折扣因子
	public void writeDiscount(float discount) {
		System.out.println("writeDiscount start……");
		FileOp.appendFileContent(pomdpModelFileName, "discount: " + discount + "\n");
		System.out.println("writeDiscount complete");
	}

	//写入值函数计算方式（reward or cost
	public void writeValues(String values) {
		System.out.println("writeValues start……");
		FileOp.appendFileContent(pomdpModelFileName, "values: " + values + "\n");
		System.out.println("writeValues complete");
	}

	//写入状态个数
	public void writeStates() {
		System.out.println("writeStates start……");
		FileOp.appendFileContent(pomdpModelFileName, "states: " + amountOfStates + "\n");
		System.out.println("writeStates complete");
	}

	//写入动作个数
	public void writeActions() {
		System.out.println("writeActions start……");
		FileOp.appendFileContent(pomdpModelFileName, "actions: 3\n");
		System.out.println("writeActions complete");
	}

	//写入观察个数
	public void writeObservations() {
		System.out.println("writeObservations start……");
		FileOp.appendFileContent(pomdpModelFileName, "observations: " + amountOfObservations + "\n");
		System.out.println("writeObservations complete");
	}

	//写入初始信念状态
	public void writeStartingBeliefState() {
		System.out.println("writeStartingBeliefState start……");
		//两个可能的初始状态
		int[] state_0={DTC_max,v_0,0,0,DTC_max,v_0,0};//默认意图为不避让的概率为0.8
		int[] state_1={DTC_max,v_0,0,1,DTC_max,v_0,0};//默认意图为避让的概率为0.2
		
		FileOp.appendFileContent(pomdpModelFileName, "start: ");
		
		for (int i = 0; i < amountOfStates; i++) {
			double pr=0.0;
			if(i==jso.getIndex_jstate(state_0))pr=0.8;
			if(i==jso.getIndex_jstate(state_1))pr=0.2;
			
			FileOp.appendFileContent(pomdpModelFileName, String.format("%.6f", pr));
			if (i != amountOfStates - 1) {
				FileOp.appendFileContent(pomdpModelFileName, " ");
			} else {
				FileOp.appendFileContent(pomdpModelFileName, "\n");
			}
		}
		System.out.println("writeStartingBeliefState complete");
	}

	//写入状态转移函数
	public void writeStateTransitionProbabilities() {
		System.out.println("writeStateTransitionProbabilities start……");
		ArrayList<int[]> jstates=jso.getJointstates();
		for(int i=0;i<jstates.size();++i){
			for(int a_host=0;a_host<3;++a_host){
				for(int a_human=0;a_human<3;++a_human){
					int[] arg=jstates.get(i);//转移前状态
					int I_i=arg[3];//转移前状态的意图
					double p_a_human=hmmBuilder.getHmm().getOpdf(I_i)
							.probability(new ObservationInteger(a_human));//当前意图下选择该动作的概率
					int[][] next_states=jso.getNextJstates(arg[0],arg[1],actions[a_human],arg[3],
							arg[4],arg[5],actions[a_host]);
					
					if(next_states!=null){
						//执行a_host动作从状态i转移到下一个意图为不避让的状态next_states[0]的概率
						double p0=p_a_human*hmmBuilder.getHmm().getAij(I_i, 0);
						double p1=p_a_human*hmmBuilder.getHmm().getAij(I_i, 1);
						
						FileOp.appendFileContent(pomdpModelFileName,
								"T: " + a_host + " : " + i + " : " + jso.getIndex_jstate(next_states[0]) 
								+ " " + String.format("%.6f", p0) + "\n");
						
						FileOp.appendFileContent(pomdpModelFileName,
								"T: " + a_host + " : " + i + " : " + jso.getIndex_jstate(next_states[1]) 
								+ " " + String.format("%.6f", p1) + "\n");
					}
				}
			}
		}
		System.out.println("writeStateTransitionProbabilities complete");
	}

	//写入观察函数
	public void writeObservationProbabilities() {
		System.out.println("writeObservationProbabilities start……");
		for (int i = 0; i < amountOfStates; i++) {
			FileOp.appendFileContent(pomdpModelFileName, "O: * : " + i + "\n");
			int[] js=jso.getJState(i);
			
			int[] ob0={js[0],js[1],js[2],js[4],js[5],actions[0]};
			int[] ob1={js[0],js[1],js[2],js[4],js[5],actions[1]};
			int[] ob2={js[0],js[1],js[2],js[4],js[5],actions[2]};
			int ind_ob0=jso.getIndex_ob(ob0);
			int ind_ob1=jso.getIndex_ob(ob1);
			int ind_ob2=jso.getIndex_ob(ob2);
			
			for (int j = 0; j < amountOfObservations; j++) {
				if (j == ind_ob0||j == ind_ob1||j == ind_ob2) {
					if(jso.getObservation(j)[5]==jso.getJState(i)[6]){
						FileOp.appendFileContent(pomdpModelFileName, String.format("%.6f", 0.8));
					}
					else FileOp.appendFileContent(pomdpModelFileName, String.format("%.6f", 0.1));
				} else {
					FileOp.appendFileContent(pomdpModelFileName,
							String.format("%.6f", 0.0));
				}
				if (j == amountOfObservations - 1) {
					FileOp.appendFileContent(pomdpModelFileName, "\n");
				} else {
					FileOp.appendFileContent(pomdpModelFileName, " ");
				}
			}
		}
		System.out.println("writeObservationProbabilities complete");
	}

	private double getRewards_safe(int[] jstate){
		int DTC_human=jstate[0];
		int v_human=jstate[1];
		int a_human=jstate[2];
		
		int DTC_host=jstate[4];
		int v_host=jstate[5];
		int a_host=jstate[6];
		
		if(DTC_human>0&&DTC_host<0)return 100.;//自动驾驶先通过
		if(DTC_human<0&&DTC_host>0)return 80.;//人类驾驶先通过
		if(DTC_human<0&&DTC_host<0)return -100.;//同一时间段通过，碰撞风险很大，收益最低
		
		int v2_2=0;
		double t_human=0;
		if(a_human!=0){
			v2_2=2*a_human*DTC_human+v_human*v_human;
			
			if(v2_2<=0)t_human=100;
			else{
				t_human=(Math.sqrt(v2_2)-v_human)/a_human;
			}
		}else{
			if(v_human==0)t_human=100;
			else t_human=DTC_human/v_human;
		}
		
		double t_host=0;
		if(a_host!=0){
			v2_2=2*a_host*DTC_host+v_host*v_host;
			if(v2_2<=0)return -50.;//自动驾驶车辆停下来等，不符合快速通过原则，视为负收益仅次于碰撞
			
			else t_host=(Math.sqrt(v2_2)-v_host)/a_host;
		}else{
			if(v_host==0)return -50.;//自动驾驶车辆停下来等视为负收益仅次于碰撞
			else t_host=DTC_host/v_host;
		}
		
		double res=Math.abs(t_human-t_host);
		if(res<=1.1)return -100.;//差距在一个时间单位里视为具有碰撞的高风险
		return res;
		
	}

	//写入回报函数
	public void writeReward(double u1,double u2,double u3) {//参数是三种回报的重量系数
		System.out.println("writeReward start……");
		for(int i=0;i<amountOfStates;++i){
			for(int a_host=0;a_host<3;++a_host){
				for(int a_human=0;a_human<3;++a_human){
					int[] arg=jso.getJState(i);//转移前状态
					
					double rw_cost=0;
					if(actions[a_host]!=0)rw_cost=-0.5;
					
					int[][] next_states=jso.getNextJstates(arg[0],arg[1],actions[a_human],arg[3],
							arg[4],arg[5],actions[a_host]);
					if(next_states!=null){
						int js_0=jso.getIndex_jstate(next_states[0]);
						int js_1=jso.getIndex_jstate(next_states[1]);
						double rw_safe_js0=getRewards_safe(next_states[0]);
						double rw_safe_js1=getRewards_safe(next_states[1]);
						double rw_time_js0=arg[4]-next_states[0][4];
						double rw_time_js1=arg[4]-next_states[1][4];
						
						double rw_js0=u1*rw_safe_js0+u2*rw_time_js0+u3*rw_cost;
						double rw_js1=u1*rw_safe_js1+u2*rw_time_js1+u3*rw_cost;
//						
//						System.out.println("R: " + a_host + " : "+i+" : "+js_0+" : * "+ String.format("%.6f", rw_js0)+"---"+
//								next_states[0][0]+","+next_states[0][1]+","+next_states[0][2]+","+next_states[0][3]
//										+","+next_states[0][4]+","+next_states[0][5]+","+next_states[0][6]+"\n");
//						System.out.println("R: " + a_host + " : "+i+" : "+js_1+" : * "+ String.format("%.6f", rw_js1)+"---"+
//								next_states[1][0]+","+next_states[1][1]+","+next_states[1][2]+","+next_states[1][3]
//								+","+next_states[1][4]+","+next_states[1][5]+","+next_states[1][6]+"\n");
						
						FileOp.appendFileContent(pomdpModelFileName, 
								"R: " + a_host + " : "+i+" : "+js_0+" : * "+ String.format("%.6f", rw_js0)+"\n");
						FileOp.appendFileContent(pomdpModelFileName, 
								"R: " + a_host + " : "+i+" : "+js_1+" : * "+ String.format("%.6f", rw_js1)+"\n");
					}
				}
			}
		}
		System.out.println("writeReward complete");
	}
	
	public static void main(String[] args) {
		PomdpModelBuilder pmd=new PomdpModelBuilder(400,50,50,0,-50);
		pmd.buildPomdpModelFile();
		pmd.writeDiscount(0.90f);
		pmd.writeValues("reward");
		pmd.writeStates();
		pmd.writeActions();
		pmd.writeObservations();
		pmd.writeBlank();
		
		pmd.writeStartingBeliefState();
		pmd.writeBlank();
		
		pmd.writeStateTransitionProbabilities();
		pmd.writeBlank();
		
		pmd.writeObservationProbabilities();
		pmd.writeBlank();

		pmd.writeReward(0.5,0.3,0.2);
	}
}
