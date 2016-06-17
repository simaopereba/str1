import javax.realtime.*;

public class MesaOperador{
	
	private static double limiteAguaSUP=15000, limiteAguaInf = 5000, limiteAguaInfCRITICO=1000, limiteAguaSUPCRITICO= 20000;
	public static void main(String[] args){
		Caldeira c = new Caldeira();
		Bomba b = new Bomba(c);
		
		Thread enc = b.encherDAgua();
		Thread esv = c.vapor();
		Thread sen = c.sensorAgua();
		Thread senBomb = b.sensorBomba();	
		Thread erroCaldeira =  c.erro();
		Thread erroBomba = b.erro();	
		
		final int  DEGRADADO=5, NORMAL=4, STOP=3, INIT=2,RESGATE=1;	
		int pri = PriorityScheduler.instance().getMaxPriority();
		PriorityParameters prip = new PriorityParameters(pri);		
		/* period: 20ms */
		RelativeTime period =
		new RelativeTime(5000 /* ms */, 0 /* ns */);
		
		RelativeTime deadline =
		new RelativeTime(1000/* ms */, 0 /* ns */);
		
		/* release parameters for periodic thread: */
		PeriodicParameters perp =
		new PeriodicParameters(null, period, null, deadline, null, null);
		
		/* create periodic thread: */
		new RealtimeThread(prip, perp){
			public void run(){				
				//--------------------
				int estado;
				estado =  INIT;
				enc.start();
				sen.start();
				senBomb.start();
				erroCaldeira.start();
				erroBomba.start();
				System.out.println("\tINICIANDO BOMBA nivel de agua="+ c.getMedicaoAgua());	
				while(waitForNextPeriod()){
					System.out.println("\n \tCONTROLE nivel:"+ c.getMedicaoAgua());
					switch(estado){
						case(INIT):{
							
							
							System.out.println("\tINIT");
							
							
							double nvAgua= c.getMedicaoAgua();
							boolean bombStatus = b.getBombState();
							if(!bombStatus){
								estado = STOP;
								break;
							}
							if( (nvAgua > (limiteAguaInf + (limiteAguaInf * 0.5)   ))
								&& (nvAgua < limiteAguaSUP ) ){
								esv.start();
								estado = NORMAL;
								break;
							
							}else if ((nvAgua > limiteAguaSUP )){
								estado = STOP;
								break;
							}
							break;
						}
						case(NORMAL):{
							System.out.println("\tNORMAL");
							boolean bombStatus = b.getBombState();
							if(!bombStatus){
								estado = DEGRADADO;
								break;
							}
							
							if(c.getMedicaoAgua()> limiteAguaSUPCRITICO){
								System.out.println("STOP  NIVEL = "+ c.getMedicaoAgua());
								b.setContinuar(false);
								estado = STOP;
								continue;						
							}
							else if(c.getMedicaoAgua()> limiteAguaSUP){
								System.out.println("DEGRADADO NIVEL = "+ c.getMedicaoAgua());
								System.out.println("\n\nDESLIGAR BOMBAS!!!\n ");
								b.setContinuar(false);
								b.ligarSegundaBomba(false);
								estado = DEGRADADO;
								continue;
							}
						
							else if(c.getMedicaoAgua()< limiteAguaInfCRITICO){
								System.out.println("STOP NIVEL = "+ c.getMedicaoAgua());
								estado = STOP;
								continue;
							}
							else if(c.getMedicaoAgua()< limiteAguaInf){
								System.out.println("RESGATE NIVEL = "+ c.getMedicaoAgua());
								b.ligarSegundaBomba(true);
								System.out.println("\n\nLIGAR SEGUNDA BOMBA!!!\n ");
								estado = RESGATE;
								continue;
							}
							else{
								b.setContinuar(true);
							}
							
						
						break;
						}
						case(DEGRADADO):{
						System.out.println("\tDEGRADADO");
						if(c.getMedicaoAgua()> limiteAguaSUPCRITICO){
							System.out.println("STOP NIVEL = "+ c.getMedicaoAgua());
							System.out.println("NIVEL MUITO ALTO DE AGUA, ACIONANDO VALVULA DE ESCAPE DE AGUA E DESLIGANDO A CALDEIRA. "+ c.getMedicaoAgua());
							b.valvulaLigada(true);
							estado = STOP;
							continue;
						}
						else if(c.getMedicaoAgua()< limiteAguaSUP){
							System.out.println("Ligar Bomba principal!");
							System.out.println("NORMAL NIVEL = "+ c.getMedicaoAgua());
							estado = NORMAL;
							continue;
						}
						break;
						}
						case(RESGATE):{
							System.out.println("\tRESGATE");
						if(c.getMedicaoAgua()< limiteAguaInfCRITICO){
							System.out.println("STOP NIVEL =  "+ c.getMedicaoAgua());
							estado = STOP;
							continue;
						}
						else if(c.getMedicaoAgua()> limiteAguaInf){
							System.out.println("NORMAL NIVEL = "+ c.getMedicaoAgua());
							estado = NORMAL;
							continue;
						}
						
						break;
						}
						case(STOP):{
							System.out.println("\tSTOP");
							b.setON(false);
							c.setON(false);
							System.out.println("PARANDO A CALDEIRA, ELA PODE EXPLODIR!");
							try{
								esv.interrupt(); //join();
								enc.interrupt();
								erroCaldeira.interrupt();
								sen.interrupt();
								senBomb.interrupt();
								//System.exit(0);
								//sen.stop();
								System.out.println("CALDEIRA PARADA");
								
							}
							catch(Exception e){
								System.out.println("DEU MERDA");
							}
							System.exit(0);
							return ;//
							//break ;
						}
								
					}
				}
				
				
				
				//--------------
				}
				
		}.start();	
		
		
		
		
		
		
	}
	
	
	
	
}
