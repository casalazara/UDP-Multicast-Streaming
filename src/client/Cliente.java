package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;

public class Cliente {
	static int port = 50005;
	static int canal;
	static InetAddress group;
	static final int SIZE = 65500; //65500
	static boolean pause =  false;

	public static void main(String args[]) throws Exception
	{
		iniciar();
	}
	public static void iniciar() throws IOException {

		String[] grupos = {"225.6.7.8","224.3.29.71","224.22.65.7"};
		System.out.println("Escriba el canal al que desea conectarse(1,2 o 3)");
		Scanner lectorConsola = new Scanner(System.in);
		canal = lectorConsola.nextInt(); 

		System.setProperty("java.net.preferIPv4Stack", "true");

		group = InetAddress.getByName(grupos[canal-1]);
		MulticastSocket mSocket = new MulticastSocket(port);
		mSocket.setReuseAddress(true);
		mSocket.joinGroup(group);

		/**
		 * Formula for lag = (byte_size/sample_rate)*2
		 * Byte size 9728 will produce ~ 0.45 seconds of lag. Voice slightly broken.
		 * Byte size 1400 will produce ~ 0.06 seconds of lag. Voice extremely broken.
		 * Byte size 4000 will produce ~ 0.18 seconds of lag. Voice slightly more broken then 9728.
		 */

		System.out.println("Conectado");

		JFrame jframe = new JFrame();
		jframe.setSize(640,360);
		JLabel vidpanel = new JLabel();
		jframe.setTitle("Canal "+canal);
		JPanel x = new JPanel();
		x.setLayout(new BorderLayout());
		JButton l = new JButton("<");

		l.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (pause) {
						mSocket.joinGroup(group);
						pause = false;
					}
					mSocket.leaveGroup(group);
					canal = Math.max(1, canal-1);
					group = InetAddress.getByName(grupos[canal-1]);
					mSocket.joinGroup(group);
					jframe.setTitle("Canal "+canal);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}	
		});

		JButton r = new JButton(">");
		r.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (pause) {
						mSocket.joinGroup(group);
						pause = false;
					}
					mSocket.leaveGroup(group);
					canal = Math.min(3, canal+1);
					group = InetAddress.getByName(grupos[canal-1]);
					mSocket.joinGroup(group);
					jframe.setTitle("Canal "+canal);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}	
		});

		JButton p = new JButton("Pause");
		p.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (pause) {
						mSocket.joinGroup(group);
						pause = false;
					}
					else{
						mSocket.leaveGroup(group);
						pause = true;
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}	
		});


		x.add(vidpanel,BorderLayout.CENTER);
		x.add(r,BorderLayout.EAST);
		x.add(l,BorderLayout.WEST);
		x.add(p,BorderLayout.SOUTH);

		jframe.getContentPane().add(x);

		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jframe.setVisible(true);
		byte[] receiveData = new byte[SIZE];


		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

		while (true)
		{
			mSocket.receive(receivePacket);
			byte[] recv = receivePacket.getData();
			ByteArrayInputStream bas = new ByteArrayInputStream(recv);
			BufferedImage bi=ImageIO.read(bas);
			ImageIcon image =  new ImageIcon(bi);
			vidpanel.setIcon(image);
			vidpanel.repaint();
		}
	}
}


