package hmmBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.Observation;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.OpdfInteger;
import be.ac.ulg.montefiore.run.jahmm.OpdfIntegerFactory;
import be.ac.ulg.montefiore.run.jahmm.io.FileFormatException;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationIntegerReader;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationReader;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationRealReader;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationSequencesReader;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationVectorReader;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.learn.KMeansLearner;

public class HmmModelBuilder {
	private BaumWelchLearner bwl=new BaumWelchLearner();
	private Hmm<ObservationInteger> hmm;

	public HmmModelBuilder(){
		//实例化一个初始HMM模型
		/*
		 * 0代表意图不避让：{0.7,0.2,0.1} 加速、匀速、减速的概率分布
		 * 1代表意图避让：{0.1,0.2,0.7}
		 * */
		
		hmm =new Hmm <ObservationInteger >(2, new OpdfIntegerFactory (3));
		
		hmm.setPi (0, 0.50);
		hmm.setPi (1, 0.50);
		
		hmm.setOpdf (0, new OpdfInteger (new double [] {0.7,0.2,0.1}));
		hmm.setOpdf (1, new OpdfInteger (new double [] {0.1,0.2,0.7}));
		
		//转移矩阵
		hmm.setAij (0, 1, 0.3);
		hmm.setAij (0, 0, 0.7);
		hmm.setAij (1, 0, 0.5);
		hmm.setAij (1, 1, 0.5);
	}
	public void learn(String dir_observations){//参数为观察文件的位置
		Reader reader = null;
		List <List <ObservationInteger>> seqs =null;
		try {
			reader = new FileReader (dir_observations);
			seqs= ObservationSequencesReader .
					readSequences (new ObservationIntegerReader(),
					reader);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 catch (IOException | FileFormatException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
		}finally{
			try {
				if(reader!=null){
					reader.close ();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//用读出的观察序列对当前HMM模型进行训练
		hmm= bwl.iterate(hmm, seqs);
	}
	
	public Hmm<ObservationInteger> getHmm() {
		return hmm;
	}
	
	public static void main(String[] args) {
		HmmModelBuilder hmmbuider=new HmmModelBuilder();
		double test=hmmbuider.getHmm().getOpdf(0).probability(new ObservationInteger(1));
		System.out.println(test);
	}
}
