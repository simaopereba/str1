import javax.realtime.*;


public class Bomba{
	
	boolean segundaBomba = false;
	boolean continua = true;
	boolean valvula = false;
	private Caldeira caldeira;
	private boolean ON= true;
	private boolean status = true;
	
	public Bomba(Caldeira c){
		this.caldeira=c;
	}
	
	
	
	void setContinuar(boolean b){
		continua =b;
		
	}
	
	void ligarSegundaBomba(boolean b){
		segundaBomba = b;
	}
	
	void valvulaLigada(boolean b){
		valvula = b;
	}
	public void setON(boolean b){
			ON = b;	
		}
	
	Thread encherDAgua(){
		Thread th = new Thread(){
			public void run(){
				while(ON){
					//System.out.println("Agua");
					if(valvula){
						caldeira.ligarValvula();
					}
					else{
						
						if(continua){
							if(segundaBomba){
								caldeira.addAgua(5);
							}
							else{
								caldeira.addAgua(1);
							}
						}
					}
					
					try{
						Thread.sleep(400);
					}
					catch(Exception e){
						
					}	
				}
				return;
			}
			
		};
		
		return th;							
	}

	
	RealtimeThread sensorBomba(){
		
		int pri = 5;//PriorityScheduler.instance().getMinPriority() + 10;
		PriorityParameters prip = new PriorityParameters(pri);
		
		/* period: 20ms */
		RelativeTime period =
		new RelativeTime(1000/* ms */, 0 /* ns */);
		RelativeTime period2 =
		new RelativeTime(5000/* ms */, 0 /* ns */);
		
		/* release parameters for periodic thread: */
		PeriodicParameters perp =
		new PeriodicParameters(null, period, null, period2, null, null);
		
		/* create periodic thread: */
		RealtimeThread rt = new RealtimeThread(prip, perp){
			
			public void run(){
				int n=1;
				while (waitForNextPeriod() /*&& (n<it)*/){
					status = (ON && continua);
				//	System.out.println("\nSENSOR BOMBA :: BOMBA LIGADA="+status);
					
				}
			}
		};		
		return rt;
		
	}

	
	public boolean getBombState(){
		return status;
	}
	
	Thread erro(){
		
		int pri = 11;//PriorityScheduler.instance().getMinPriority() + 10;
		PriorityParameters prip = new PriorityParameters(pri);
		
		RelativeTime period =
		new RelativeTime(30000 /* ms */, 0 /* ns */);
		
		RelativeTime start =
		new RelativeTime(25000 /* ms */, 0 /* ns */);
		
		RelativeTime dead =
		new RelativeTime(100 /* ms */, 0 /* ns */);
		
		/* release parameters for periodic thread: */
		ReleaseParameters perp =
		new PeriodicParameters(start,period,null, dead, null, null);
		
		/* create periodic thread: */
		RealtimeThread rt = new RealtimeThread(prip, perp){
			
			public void run(){
				int n=1;
				while (waitForNextPeriod()  &&ON/*&& (n<it)*/){
					
					
					continua = !continua;
					System.out.println("\n\t\t\t ERRR NA BOMBA ligada="+continua);
				}
			}
		};		
		return rt;
		
	}

	
	
}
