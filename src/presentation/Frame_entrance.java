package presentation;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fileOperator.FileOp;
import pomdp.environments.POMDP;
import pomdp.utilities.BeliefState;
import pomdp.valuefunction.LinearValueFunctionApproximation;
import pomdpBuilder.JointStateOperator;
import presentation.Frame_entrance.Simulator;

/*
 * 
 * 507,126左上
 * 542,160右下
 * 
 * */
public class Frame_entrance extends JFrame implements MouseListener{

	private int x_host=527;
	private int y_human=146;
	private int end_host=97;
	private int end_human=542;
	private int[] actions=new int[3];
	
	private POMDP pomdp;
	private LinearValueFunctionApproximation valueFunction;
	private JointStateOperator jso;
	
	private Vehicle v_host;
	private Vehicle v_human;
	private JLabel vehicle_host;
	private JLabel vehicle_human;
	public Frame_entrance(int DTC_max,int v_0,int asc,int con,int des) {
		// TODO Auto-generated constructor stub
		actions[0]=asc;
		actions[1]=con;
		actions[2]=des;
		
		//获取POMDP文件和ValueFuction
		pomdp=(POMDP)FileOp.readObj("POMDP.TXT");
		valueFunction=(LinearValueFunctionApproximation)FileOp.readObj("ValueFunction.txt");
		jso=(JointStateOperator)FileOp.readObj("JointStateOperator.txt");
		
		Dimension dimension=Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds((dimension.width-627)/2, (dimension.height-533)/2, 627, 533);
		this.setUndecorated(true);
		this.addMouseListener(this);
		
		
		JPanel jp=(JPanel) this.getContentPane();
		JLabel bg=new JLabel();
		bg.setBounds(0, 0, 627, 533);
		bg.setIcon(new ImageIcon("img/interaction.png"));
		
		
		v_host=new Vehicle(DTC_max,v_0,0);//true, 527, 497, 50
		v_human=new Vehicle(DTC_max,v_0,0,0);	//false, 142, 146, 50,1
		vehicle_host=new JLabel();
		vehicle_human=new JLabel();
		vehicle_host.setSize(11, 29);
		vehicle_host.setIcon(new ImageIcon("img/vehicle_host.png"));
		vehicle_human.setSize(29, 11);
		vehicle_human.setIcon(new ImageIcon("img/vehicle_human.png"));
		
		vehicle_host.setLocation(x_host, end_host+DTC_max);
		vehicle_human.setLocation(end_human-DTC_max, y_human);
		System.out.print("joint state:{"+v_human.getDTC()+","+v_human.getV_0()+","+v_human.getA()+",");
		System.out.println(v_host.getDTC()+","+v_host.getV_0()+","+v_host.getA());
		
		
		jp.add(vehicle_host,0);
		jp.add(vehicle_human,1);
		jp.add(bg,2);
		this.setVisible(true);
		
	}
	public void startFlash(){
		Thread t=new Thread(new Simulator());
		t.start();
	}

	
	public class Simulator implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			BeliefState bs=pomdp.getBeliefStateFactory().getInitialBeliefState();//当前信念状态
			while(v_host.getDTC()>0&&v_human.getDTC()>0){
				int a_ind=valueFunction.getBestAction(bs);//获取最佳动作
				int a_host=actions[a_ind];
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				v_host.execute(a_host);//执行动作
				vehicle_host.setLocation(x_host,end_host+v_host.getDTC());//展示在图形界面上

				//人类驾驶车辆根据意图对应的动作概率分布选择动作
				int a_human=0;
				double[] inten=v_human.getIntentions();
				double random=Math.random();
				if(random>=0.0&&random<=inten[0])a_human=actions[0];
				else if(random>inten[0]&&random<=inten[0]+inten[1])a_human=actions[1];
				else if(random>inten[0]+inten[1]&&random<=inten[0]+inten[1]+inten[2])a_human=actions[2];
				
				v_human.execute(a_human);
				vehicle_human.setLocation(end_human-v_human.getDTC(),y_human);//展示在图形界面上
				
				//输出观察
				System.out.print("joint state:{"+v_human.getDTC()+","+v_human.getV_0()+","+v_human.getA()+",");
				System.out.println(v_host.getDTC()+","+v_host.getV_0()+","+v_host.getA());
				
				int[] ob_new={v_human.getDTC(),v_human.getV_0(),v_human.getA(),
						v_host.getDTC(),v_host.getV_0(),v_host.getA()};
				
				int ob_ind=jso.getIndex_ob(ob_new);
				if(ob_ind!=-1){//如果观察存在，获取下一个信念状态
					bs=bs.nextBeliefState(a_ind, ob_ind);
				}else{
					System.out.println("一个观察不存在,模拟器停止……");
					break;
				}
			}
		}
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println(e.getX()+","+e.getY());
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Frame_entrance jFrame=new Frame_entrance(400,50,50,0,-50);
		jFrame.startFlash();
	}
}
