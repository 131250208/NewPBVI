package presentation;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import presentation.Frame_entrance.Simulator;

/*
 * ��ײ��
 * 507,126左上
 * 542,160右下
 * 
 * */
public class Frame_entrance extends JFrame implements MouseListener{

	private Vehicle v_host;
	private Vehicle v_human;
	private JLabel vehicle_host;
	private JLabel vehicle_human;
	public Frame_entrance() {
		// TODO Auto-generated constructor stub
	
		Dimension dimension=Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds((dimension.width-627)/2, (dimension.height-533)/2, 627, 533);
		this.setUndecorated(true);
		this.addMouseListener(this);
		
		
		JPanel jp=(JPanel) this.getContentPane();
		JLabel bg=new JLabel();
		bg.setBounds(0, 0, 627, 533);
		bg.setIcon(new ImageIcon("img/interaction.png"));
		
		
		v_host=new Vehicle(true, 527, 497, 50);
		v_human=new Vehicle(false, 142, 146, 50,1);//�����ʻ���ĳ�ʼ��ͼΪI1		
		vehicle_host=new JLabel();
		vehicle_human=new JLabel();
		vehicle_host.setSize(11, 29);//����ͳ�
		vehicle_host.setIcon(new ImageIcon("img/vehicle_host.png"));
		vehicle_human.setSize(29, 11);
		vehicle_human.setIcon(new ImageIcon("img/vehicle_human.png"));
		
		vehicle_host.setLocation(v_host.getX(), v_host.getY());
		vehicle_human.setLocation(v_human.getX(), v_human.getY());
		System.out.print("host:("+v_host.getX()+","+ v_host.getY()+")---");
		System.out.println("human:("+v_human.getX()+","+ v_human.getY()+")");
		
		
		jp.add(vehicle_host,0);
		jp.add(vehicle_human,1);
		jp.add(bg,2);
		this.setVisible(true);
		
		Thread t=new Thread(new Simulator());
		t.start();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame jFrame=new Frame_entrance();
	}
	
	public class Simulator implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(v_host.getY()+29>126){//���Զ���ʻ�����ĳ�β�뿪��ײ�����˴�ģ��ͽ���
				//�Զ���ʻ����ͨ��POMDP�����Ѷ�����ִ��
				int a_host=200;
				//���ýӿڶ�ȡ������POMDP�ļ�����ʼ������Ԫ�أ������õ���Ѷ���
				//��������ʡ��
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				v_host.execute(a_host);
				vehicle_host.setLocation(v_host.getX(), v_host.getY());
				System.out.print("host:("+v_host.getX()+","+ v_host.getY()+")---");
				//�����ʻ����������ͼ�Ķ�Ӧ�������ʷֲ�ȡ����ִ��
				int a_human=0;
				double[] inten=v_human.getIntentions();
				double random=Math.random();
				if(random>=0.0&&random<=inten[0])a_human=200;
				else if(random>inten[0]&&random<=inten[0]+inten[1])a_human=0;
				
				v_human.execute(a_human);
				vehicle_human.setLocation(v_human.getX(), v_human.getY());
				System.out.println("human:("+v_human.getX()+","+ v_human.getY()+")");
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

}
