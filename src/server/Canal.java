package server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

public class Canal implements Runnable {
	String ruta;
	int canal;
	String grupo;
	static final int SIZE = 65500; //65500
	public Canal(String pRuta, int pCanal, String pGrupo) {
		this.ruta = pRuta;
		this.canal = pCanal;
		this.grupo = pGrupo;
	}

	public void run() {
		try {
			System.setProperty("java.net.preferIPv4Stack", "true");

			DatagramPacket dgp;
			InetAddress addr;
			int port = 50005;
			System.out.println("Canal "+canal+" iniciado");
			/* OPEN CV STUFF*/
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			Mat frame = new Mat();
			VideoCapture camera = new VideoCapture(this.ruta);
			//			JFrame jframe = new JFrame("Servidor canal "+this.canal);
			//			jframe.setSize(640,360);
			//
			//			jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//			JLabel vidpanel = new JLabel();
			//			jframe.setContentPane(vidpanel);
			//			jframe.setVisible(true);


			byte[] data = new byte[SIZE]; //4K

			addr = InetAddress.getByName(this.grupo);
			MulticastSocket socket = new MulticastSocket();

			final DatagramPacket packet = new DatagramPacket(data, data.length);
			while (true) {
				if (camera.read(frame)) {
					MatOfByte mob=new MatOfByte();
					Imgcodecs.imencode(".jpg", frame, mob);
					byte ba[]=mob.toArray();
					ByteArrayInputStream bas = new ByteArrayInputStream(ba);
					// Read the next chunk of data from the TargetDataLine.
					bas.read(data, 0, data.length);
					//					ByteArrayInputStream zz = new ByteArrayInputStream(data);
					//
					//					BufferedImage bi=ImageIO.read(zz);
					//					ImageIcon image =  new ImageIcon(bi);
					//					vidpanel.setIcon(image);
					//					vidpanel.repaint();

					// Save this chunk of data.
					dgp = new DatagramPacket (data,data.length,addr,port);
					socket.send(dgp);
				}
				else {
					camera = new VideoCapture(this.ruta);
				}
			}
		}
		catch (UnknownHostException e) {
			System.out.println(e);
			// TODO: handle exception
		} catch (SocketException e1) {
			System.out.println(e1);
			// TODO: handle exception
		} catch (IOException e2) {
			System.out.println(e2);
			// TODO: handle exception
		}
	}
}
