public class MesaOperador{
	private static double limiteAguaSUP=15000, limiteAguaInf = 5000, limiteAguaInfCRITICO=1000, limiteAguaSUPCRITICO= 20000;
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
					System.out.println("INICIANDO BOMBA "+ c.getMedicaoAgua());	
					try{
					Thread.sleep(500);
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
					if(c.getMedicaoAgua()> limiteAguaSUPCRITICO){
						System.out.println("PARA STOP COM NIVEL = "+ c.getMedicaoAgua());
						b.setContinuar(false);
						estado = STOP;
						continue;						
					}
					else if(c.getMedicaoAgua()> limiteAguaSUP){
						System.out.println("PARA DEGRADADO "+ c.getMedicaoAgua());
						System.out.println("\n\nDESLIGAR BOMBAS!!!\n ");
						b.setContinuar(false);
						b.ligarSegundaBomba(false);
						estado = DEGRADADO;
						continue;
					}
				
					else if(c.getMedicaoAgua()< limiteAguaInfCRITICO){
						System.out.println("PARA STOP COM "+ c.getMedicaoAgua());
						estado = STOP;
						continue;
					}
					else if(c.getMedicaoAgua()< limiteAguaInf){
						System.out.println("PARA RESGATE "+ c.getMedicaoAgua());
						b.ligarSegundaBomba(true);
						System.out.println("\n\nLIGAR SEGUNDA BOMBA!!!\n ");
						estado = RESGATE;
						continue;
					}
					else{
						b.setContinuar(true);
					}
					
					
				break;
				
				case(DEGRADADO):
				
				if(c.getMedicaoAgua()> limiteAguaSUPCRITICO){
					System.out.println("PARA STOP COM "+ c.getMedicaoAgua());
					System.out.println("NIVEL MUITO ALTO DE AGUA, ACIONANDO VALVULA DE ESCAPE DE AGUA E DESLIGANDO A CALDEIRA."+ c.getMedicaoAgua());
					b.valvulaLigada(true);
					estado = STOP;
					continue;
				}
				else if(c.getMedicaoAgua()< limiteAguaSUP){
					System.out.println("PARA NORMAL COM "+ c.getMedicaoAgua());
					System.out.println("Ligar Bomba principal!");
					estado = NORMAL;
					continue;
				}
				break;
				
				case(RESGATE):
				if(c.getMedicaoAgua()< limiteAguaInfCRITICO){
					System.out.println("PARA STOP COM "+ c.getMedicaoAgua());
					estado = STOP;
					continue;
				}
				else if(c.getMedicaoAgua()> limiteAguaInf){
					System.out.println("PARA NORMAL COM "+ c.getMedicaoAgua());
					estado = NORMAL;
					continue;
				}
				
				break;
				case(STOP):
					System.out.println("PARANDO A CALDEIRA, ELA PODE EXPLODIR!");
					try{
						enc.join();
						esv.join();
						System.exit(0);
						sen.stop();
					}
					catch(Exception e){
						System.out.println("DEU MERDA");
					}
					System.exit(0);
					break ;
				
						
			}
		}
		
	
	}
	
}
