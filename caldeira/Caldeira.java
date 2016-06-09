import javax.realtime.*;

public class Caldeira {
	private double nivelAgua=0;
	private double vazaoAgua = 100,vazaoVapor = 100,limiteAguaSUP=400000, limiteAguaInf = 500;
	
	public static void main(String[] args){
		Caldeira c = new Caldeira();
		Bomba b = new Bomba(c);
				
		Thread enc = b.encherDAgua();
		Thread esv = c.vapor();
				
		esv.start();
		enc.start();
		
		c.sensorAgua(enc,esv).start();
		
		
	//	c.supervisionarbomba().start();	
		/*
		
		
		
		*/
	}
	
	RealtimeThread sensorAgua(Thread enc, Thread esv){
		
		int pri = 1;//PriorityScheduler.instance().getMinPriority() + 10;
		PriorityParameters prip = new PriorityParameters(pri);		
		/* period: 20ms */
		RelativeTime period =
		new RelativeTime(100 /* ms */, 0 /* ns */);
		
		/* release parameters for periodic thread: */
		PeriodicParameters perp =
		new PeriodicParameters(null, period, null, null, null, null);
		
		/* create periodic thread: */
		RealtimeThread rt = new RealtimeThread(prip, perp){
			public void run(){
				
				while (waitForNextPeriod() /*&& (n<it)*/){
					System.out.println("Nivel d\'AGUA "+nivelAgua);
					if(nivelAgua<= limiteAguaInf){
						System.out.println("Nivel d\'AGUA "+nivelAgua+" AGUA ABAIXO DO NIVEL "+esv.getState());
						try{
							if(!esv.isInterrupted()){
								System.out.println("--");
								//esv.interrupt();
							}
							System.out.println(esv.getState());
							
						}
						catch(Exception e){
							System.out.println("EXCEPTION1");
						}
						
					}else if(nivelAgua>=limiteAguaSUP){
						System.out.println("Nivel d\'AGUA "+nivelAgua+" AGUA ACIMA DO NIVEL ");
						try{
							//enc.sleep(3000);
							if(enc.getState() == Thread.State.RUNNABLE  ){
								System.out.println("--");
								enc.checkAccess();
								//enc.sleep(2000);
								System.out.println("--"+enc.getState());
							}
							
							
						}
						catch(Exception e){
							System.out.println("EXCEPTION2");
						}
						
					}else{
						System.out.println("Nivel d\'AGUA "+nivelAgua+"NORMAL");
						if(esv.isInterrupted()){
								System.out.println("--");
								esv.interrupted();
							}
						if(esv.isInterrupted()){
								System.out.println("--");
								esv.interrupted();
							}
						
						
					}
					System.out.println("-----------c--");
				}
			}
		};		
		return rt;
		
		
		
	}
	
	public synchronized void  addAgua(){
		nivelAgua += vazaoAgua;
	}
	
	synchronized Thread  vapor(){
		
		Thread rt = new Thread(){
			public void run(){
				while(true){
					nivelAgua -=vazaoVapor;
				}
			}
			
		};
		return rt;	
	}
	
	RealtimeThread supervisionarbomba(){
		
		int pri = 2;//PriorityScheduler.instance().getMinPriority() + 10;
		PriorityParameters prip = new PriorityParameters(pri);
		
		/* period: 20ms */
		RelativeTime period =
		new RelativeTime(200/* ms */, 0 /* ns */);
		
		/* release parameters for periodic thread: */
		PeriodicParameters perp =
		new PeriodicParameters(null, period, null, null, null, null);
		
		/* create periodic thread: */
		RealtimeThread rt = new RealtimeThread(prip, perp){
			public void run(){
				int n=1;
				while (waitForNextPeriod() /*&& (n<it)*/){
					System.out.println("Supervisionar Bomba "+nivelAgua);
					n++;
				}
			}
		};		
		return rt;
		
	}

}
