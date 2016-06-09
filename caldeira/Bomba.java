import javax.realtime.*;


public class Bomba{
	private Caldeira caldeira;
	
	public Bomba(Caldeira c){
		this.caldeira=c;
	}
	
	
	RealtimeThread vapor(){
		int pri = 1;//PriorityScheduler.instance().getMinPriority() + 10;
		PriorityParameters prip = new PriorityParameters(pri);
		
		/* period: 20ms */
		RelativeTime period =
		new RelativeTime(2000 /* ms */, 0 /* ns */);
		
		/* release parameters for periodic thread: */
		PeriodicParameters perp =
		new PeriodicParameters(null, period, null, null, null, null);
		
		/* create periodic thread: */
		RealtimeThread rt = new RealtimeThread(prip, perp){
			public void run(){
				int n=1;
				while (waitForNextPeriod() /*&& (n<it)*/){
					System.out.println("Vapor");
					
				}
			}
		};
		return rt;
		
		
	}
	
	
	Thread encherDAgua(){
		Thread th = new Thread(){
			public void run(){
				while(true){
					//System.out.println("Agua");
					caldeira.addAgua();
					try{
					//	Thread.sleep(1000);
					}
					catch(Exception e){
						
					}	
				}
			}
			
		};
		
		return th;							
	}

	Thread sensorBomba(){
		return null;
	}

}
