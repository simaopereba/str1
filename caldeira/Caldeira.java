import javax.realtime.*;

public class Caldeira {
	private double nivelAgua=0;
	
	public static void main(String[] args){
		Caldeira c = new Caldeira();
		Bomba b = new Bomba(c);
		
		
		b.encherDAgua().start();
		c.vapor().start();
	//	c.supervisionarbomba().start();	
		
	}
	
	RealtimeThread sensorAgua(){
		
		int pri = 2;//PriorityScheduler.instance().getMinPriority() + 10;
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
				
				while (waitForNextPeriod() /*&& (n<it)*/){
					System.out.println("AGUA"+nivelAgua);
				}
			}
		};		
		return rt;
		
		
		
	}
	
	public void addAgua(){
		nivelAgua += 100;
	}
	
	Thread vapor(){
		
		Thread rt = new Thread(){
			public void run(){
				while(true){
					System.out.println(nivelAgua);
					nivelAgua = nivelAgua * 0.7;
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
		new RelativeTime(2000 /* ms */, 0 /* ns */);
		
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
