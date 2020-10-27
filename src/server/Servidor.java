package server;

public class Servidor {
	private static final String RUTA = "data/";
	public Servidor() {
		String[] grupos = {"225.6.7.8","224.3.29.71","224.22.65.7"};
		try {
			for(int i=0;i<3;i++) { // inicializacion de un nuevo canal en el socket
				new Thread(new Canal(RUTA+(i+1)+".mp4",i+1,grupos[i])).start();
			}

		} catch (Exception e) {
			System.out.println("-------------------------------");
			System.out.println("ERROR SERVIDOR: ");
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			System.out.println("-------------------------------");
		}
	}

	public static void main(String[] args) {
		new Servidor();
	}
}
