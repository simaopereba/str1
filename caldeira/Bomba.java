import javax.realtime.*;


public class Bomba{
	boolean continua = true;
	private Caldeira caldeira;
	
	public Bomba(Caldeira c){
		this.caldeira=c;
	}
	
	
	
	void setContinuar(boolean b){
		continua =b;
		
	}
	
	Thread encherDAgua(){
		Thread th = new Thread(){
			public void run(){
				while(true){
					//System.out.println("Agua");
					if(continua)
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
