public class MesaOperador{
	private static double limiteAguaSUP=40000, limiteAguaInf = 500, limiteAguaInfCRITICO=200, limiteAguaSUPCRITICO= 50000;
	public static void main(String[] args){
		Caldeira c = new Caldeira();
		Bomba b = new Bomba(c);
				
		Thread enc = b.encherDAgua();
		Thread esv = c.vapor();
		Thread sen = c.sensorAgua();
		int estado;
		final int  DEGRADADO=5, NORMAL=4, STOP=3, INIT=2,RESGATE=1;	
		estado =  INIT;
		while(true){
			switch(estado){
				case(INIT):
					System.out.println("INICIANDO BOMBA");	
					try{
					Thread.sleep(5000);
					enc.start();
					sen.start();
					esv.start();
					estado = NORMAL;
					}
					catch(Exception e){
						System.out.println("Errooou");
						estado =  STOP;	
					}					
				break;
				case(NORMAL):
					//System.out.println("NORMAL");
					if(c.getMedicaoAgua()> limiteAguaSUPCRITICO){
						System.out.println("PARA THREAD");
						b.setContinuar(false);
						estado = STOP;
						continue;						
					}
					if(c.getMedicaoAgua()> limiteAguaSUP){
						System.out.println("PARA THREAD");
						b.setContinuar(false);
						estado = STOP;
						continue;
					}
				
					if(c.getMedicaoAgua()< limiteAguaInfCRITICO){
						System.out.println("PARA THREAD");
						
						estado = STOP;
						continue;
					}
					if(c.getMedicaoAgua()< limiteAguaInf){
						System.out.println("PARA THREAD");
						
						estado = NORMAL;
						continue;
					}
					
					
				break;
				case(DEGRADADO):
				
				break;
				case(RESGATE):
				
				break;
				case(STOP):
					System.out.println("STOP");
					try{
						enc.join();
						esv.join();
						sen.stop();
					}
					catch(Exception e){
						
					}
					return ;
				
						
			}
		}
		
	
	}
	
}
