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
	
	private int[] actions;
	private int v_0;//默认初速度不为0
	private int DTC_max;
	
	public int[] calSituationOfState(int i){
		return null;
	}
	
	int[][] jointStates={{80,1,1,80,1},{80,1,2,80,1}};
	
	
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
	

	//建立POMDP模型文件
	public void buildPomdpModelFile() {
		try {
			FileOp.createFile(pomdpModelFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//写入折扣因子
	public void writeDiscount(float discount) {
		FileOp.appendFileContent(pomdpModelFileName, "discount: " + discount + "\n");
	}

	//写入值函数计算方式（reward or cost
	public void writeValues(String values) {
		FileOp.appendFileContent(pomdpModelFileName, "values: " + values + "\n");
	}

	//写入状态个数
	public void writeStates() {
		FileOp.appendFileContent(pomdpModelFileName, "states: " + amountOfStates + "\n");
	}

	//写入动作个数
	public void writeActions() {
		FileOp.appendFileContent(pomdpModelFileName, "actions: 3\n");
	}

	//写入观察个数
	public void writeObservations() {
		FileOp.appendFileContent(pomdpModelFileName, "observations: " + amountOfObservations + "\n");
	}

	//写入初始信念状态
	public void writeStartingBeliefState() {
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
	}

	//写入状态转移函数
	public void writeStateTransitionProbabilities() {
		ArrayList<int[]> jstates=jso.getJointstates();
		for(int i=0;i<jstates.size();++i){
			for(int a_host=0;a_host<3;++a_host){
				for(int a_human=0;a_human<3;++a_human){
					int[] arg=jstates.get(i);//转移前状态
					int I_i=arg[3];//转移前状态的意图
					double p_a_human=hmmBuilder.getHmm().getOpdf(I_i)
							.probability(new ObservationInteger(a_human));//当前意图下选择该动作的概率
					arg[2]=actions[a_human];
					arg[6]=actions[a_host];
					
					int[][] next_states=jso.getNextJstates(arg);
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
	}

	//写入观察函数
	public void writeObservationProbabilities() {
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
				if (j == amountOfStates - 1) {
					FileOp.appendFileContent(pomdpModelFileName, "\n");
				} else {
					FileOp.appendFileContent(pomdpModelFileName, " ");
				}
			}
		}
	}


	//写入回报函数
	public void writeReward() {
		for(int i=0;i<jointStates.length;++i){
			for (int a = 0; a <=1; a++) {
				double reward = getReward(jointStates[i],a);
				int state=getNumber_jointState(jointStates[i]);
				FileOp.appendFileContent(pomdpModelFileName, 
				"R: " + a + " : "+state+" : * : * "+String.format("%.6f", reward)+"\n");
			}
		}
	}
}
