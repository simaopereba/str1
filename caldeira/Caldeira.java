import javax.realtime.*;

public class Caldeira {
	private double nivelAgua=0;
	private double vazaoAgua = 100,vazaoVapor = 5.4, medicaoSensorAgua=nivelAgua, vazaoValvula=1000;
	private int contadorExplosao = 0;
	private boolean ON = true;
	private boolean SensorON = true;
	
	RealtimeThread sensorAgua(){		
		int pri = 5;//PriorityScheduler.instance().getMinPriority() + 10;
		PriorityParameters prip = new PriorityParameters(pri);		
		/* period: 20ms */
		RelativeTime period =
		new RelativeTime(1000 /* ms */, 0 /* ns */);
		
		RelativeTime dead =
		new RelativeTime(1 /* ms */, 0 /* ns */);
		
		/* release parameters for periodic thread: */
		PeriodicParameters perp =
		new PeriodicParameters(null, period, null, dead, null, null);
		
		/* create periodic thread: */
		return new RealtimeThread(prip, perp){
			public void run(){
				while (waitForNextPeriod() && SensorON/*&& (n<it)*/){
						System.out.print("\rNIVEL AGUA = "+nivelAgua+"\r");
						medicaoSensorAgua = nivelAgua;
				}
			}	
		};		
	}
	public void setSensorON(boolean b){
		SensorON= b;
	}
	
	
	public void setON(boolean b){
			ON = b;	
		}
	
	public double getMedicaoAgua(){
		return medicaoSensorAgua;
	}
	
	public synchronized void  addAgua(int mult){
		nivelAgua += vazaoAgua*mult;
	}
	
	public synchronized void  ligarValvula(){
		nivelAgua -= vazaoValvula;
	}
	
	
	
	synchronized Thread  vapor(){		
		Thread rt = new Thread(){
			public void run(){
				while(ON){
					try
					{
						Thread.sleep(10);
					}
					catch (Exception e)
					{
						System.out.println("Erro na thread da CALDEIRA.");
					}
					if (nivelAgua>vazaoVapor){
						nivelAgua -=vazaoVapor;
						
					}
					else{
						contadorExplosao++;
						if(contadorExplosao==2000000)
						{
							System.out.println("Caldeira explodiu. FIM.");
							System.exit(0);
						}
						// Se não tem água, fica em 0
					}
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
