package pomdp;

import fileOperator.FileOp;
import pomdp.algorithms.ValueIteration;
import pomdp.algorithms.pointbased.PointBasedValueIteration;
import pomdp.environments.POMDP;
import pomdp.utilities.Logger;
import pomdp.valuefunction.LinearValueFunctionApproximation;

public class POMDPSolver {

	public static void main(String[] args) {
		String sPath = "Models/";// �õ�model·��
		String sModelName = "intersection";// model��
		String sMethodName = "PBVI";//������
		Logger.getInstance().setOutput(true);//�������
		Logger.getInstance().setSilent(false);//�������������̨
		try {
			String sOutputDir = "logs/POMDPSolver";// ���·��
			String sFileName = sModelName + "_" + sMethodName + ".txt";// ����ļ���
			Logger.getInstance().setOutputStream(sOutputDir, sFileName);
		} catch (Exception e) {
			System.err.println(e);
		}

		POMDP pomdp = null;
		double dTargetADR = 100.0;// Ŀ��ƽ���ۿۻر�ֵ�����ƽ�������
		try {
			pomdp = new POMDP();
			pomdp.load(sPath + sModelName + ".POMDP");// ����pomdpģ��
			
			//������ر�ֵ����С�ر�ֵ
    	    Logger.getInstance().logln("max is " + pomdp.getMaxR() + " min is " + pomdp.getMinR());
		} catch (Exception e) {
			Logger.getInstance().logln(e);
			e.printStackTrace();
			System.exit(0);
		}
		// �������ģ�⣬����ƽ���ر�ֵ
		pomdp.computeAverageDiscountedReward(2, 100,new RandomWalkPolicy(pomdp.getActionCount()));
		
		try
    	{
    		Logger.getInstance().setOutputStream( pomdp.getName() + "_" + sMethodName + ".txt" );
    	}
    	catch(Exception e)
    	{
    		System.err.println(e);
    	}
		
		//����blind policy�����PointBasedValueIteration
    	ValueIteration viAlgorithm = new PointBasedValueIteration(pomdp);
    	int cMaxIterations = 10;
    	try
    	{
    		/* run POMDP solver */
    		viAlgorithm.valueIteration(cMaxIterations, 0.001, dTargetADR);
    		
    		/* compute the averaged return */
    		double dDiscountedReward = pomdp.computeAverageDiscountedReward( 2000, 150, viAlgorithm );
			Logger.getInstance().log( "POMDPSolver", 0, "main", "ADR = " + dDiscountedReward );
    	}
    	catch(Exception e)
    	{
    		Logger.getInstance().logln( e );
			e.printStackTrace();
    	}
    	catch(Error err)
    	{
    		Runtime rtRuntime = Runtime.getRuntime();
			Logger.getInstance().logln( 
					" POMDPSolver: " + err +
					" allocated " + ( rtRuntime.totalMemory() - rtRuntime.freeMemory() ) / 1000000 +
					" free " + rtRuntime.freeMemory() / 1000000 +
					" max " + rtRuntime.maxMemory() / 1000000 );
			Logger.getInstance().log( " Stack trace: " );
			err.printStackTrace();
    	}
    	FileOp.writeObj(pomdp, "POMDP.TXT");
	}
}
