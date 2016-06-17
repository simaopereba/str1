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
		new RelativeTime(3000 /* ms */, 0 /* ns */);
		
		RelativeTime deadline =
		new RelativeTime(100/* ms */, 0 /* ns */);
		
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
				double nvAgua;
				boolean bombStatus;
				String state = "INIT";
				while(waitForNextPeriod()){					
					nvAgua= c.getMedicaoAgua();
					bombStatus = b.getBombState();
					System.out.println("\n----------------------CONTROLE-------------------\n"+
					"||NIVEL LIDO:"+nvAgua +"\tBOMBA LIGADA = "+bombStatus+
					 "\t|\n||ESTADO ANTERIOR: "+state+
					 "\t\t\t\t|\n-------------------------------------------------");
					switch(estado){
						case(INIT):{
							System.out.println("\tINIT");
							state = "INIT";
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
							state = "NORMAL";
							if(!bombStatus){
								estado = DEGRADADO;
								break;
							}
							
							if(nvAgua> limiteAguaSUPCRITICO){
								System.out.println("STOP  NIVEL = "+ nvAgua);
								b.setContinuar(false);
								estado = STOP;
								continue;						
							}
							else if(nvAgua> limiteAguaSUP){
								System.out.println("DEGRADADO NIVEL = "+ nvAgua);
								System.out.println("\n\nDESLIGAR BOMBAS!!!\n ");
								b.setContinuar(false);
								b.ligarSegundaBomba(false);
								estado = DEGRADADO;
								continue;
							}
						
							else if(nvAgua< limiteAguaInfCRITICO){
								System.out.println("STOP NIVEL = "+ nvAgua);
								estado = STOP;
								continue;
							}
							else if(nvAgua< limiteAguaInf){
								System.out.println("RESGATE NIVEL = "+ nvAgua);
								b.ligarSegundaBomba(true);
								System.out.println("\n\nLIGAR SEGUNDA BOMBA!!!\n ");
								estado = RESGATE;
								continue;
							}
							else{
								//b.setContinuar(true);
							}
							
						
						break;
						}
						case(DEGRADADO):{
						System.out.println("\n\tDEGRADADO");
						state = "DEGRADADO";
						b.setContinuar(true);
						if(nvAgua> limiteAguaSUPCRITICO){
							System.out.println("\nSTOP NIVEL = "+ nvAgua);
							System.out.println("\nNIVEL MUITO ALTO DE AGUA, ACIONANDO VALVULA DE ESCAPE DE AGUA E DESLIGANDO A CALDEIRA. "+nvAgua);
							b.valvulaLigada(true);
							estado = STOP;
							continue;
						}
						if(nvAgua< limiteAguaSUP){
							//System.out.println("\nLigar Bomba principal!");
							System.out.println("\nNORMAL NIVEL = "+ nvAgua);
							estado = NORMAL;
							continue;
						}
						
						if(nvAgua< limiteAguaInfCRITICO){
								System.out.println("STOP NIVEL = "+ nvAgua);
								estado = STOP;
								continue;
							}
						if(nvAgua< limiteAguaInf){
								System.out.println("RESGATE NIVEL = "+ nvAgua);
								b.ligarSegundaBomba(true);
								System.out.println("\n\nLIGAR SEGUNDA BOMBA!!!\n ");
								estado = RESGATE;
								continue;
							}
						
						
						break;
						}
						case(RESGATE):{
						state = "RESGATE";
						System.out.println("\tRESGATE");
						if(nvAgua< limiteAguaInfCRITICO){
							System.out.println("STOP NIVEL =  "+ nvAgua);
							estado = STOP;
							continue;
						}
						else if(nvAgua> limiteAguaInf){
							System.out.println("NORMAL NIVEL = "+ nvAgua);
							estado = NORMAL;
							continue;
						}
						
						break;
						}
						case(STOP):{
							state = "STOP";
							System.out.println("\tSTOP");
							b.setON(false);
							c.setON(false);
							System.out.println("PARANDO A CALDEIRA, ELA PODE EXPLODIR!");
							try{
								esv.interrupt(); //join();
								
								erroCaldeira.interrupt();
								sen.interrupt();
								senBomb.interrupt();
								//System.exit(0);
								//sen.stop();
								if(nvAgua<limiteAguaInfCRITICO) {
									System.out.println("\nCALDEIRA EXPLODIU");
								}
								if(nvAgua>limiteAguaSUPCRITICO){ 
									System.out.println("\nCALDEIRA TRANSBORDOU");	
								}
								enc.interrupt();	
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
