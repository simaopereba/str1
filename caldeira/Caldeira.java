import javax.realtime.*;

public class Caldeira {
	private double nivelAgua=0;
	private double vazaoAgua = 3,vazaoVapor = 1.4, medicaoSensorAgua=0;
	
	
	RealtimeThread sensorAgua(){
		
		int pri = 5;//PriorityScheduler.instance().getMinPriority() + 10;
		PriorityParameters prip = new PriorityParameters(pri);		
		/* period: 20ms */
		RelativeTime period =
		new RelativeTime(200 /* ms */, 0 /* ns */);
		
		/* release parameters for periodic thread: */
		PeriodicParameters perp =
		new PeriodicParameters(null, period, null, null, null, null);
		
		/* create periodic thread: */
		RealtimeThread rt = new RealtimeThread(prip, perp){
			public void run(){
				while (waitForNextPeriod() /*&& (n<it)*/){
						System.out.println("NIVEL AGUA = "+nivelAgua);
						medicaoSensorAgua = nivelAgua;
				}
			}	
		};		
		return rt;
		
		
		
	}
	
	public double getMedicaoAgua(){
		return medicaoSensorAgua;
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
