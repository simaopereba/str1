import javax.realtime.*;


public class Bomba{
	
	boolean segundaBomba = false;
	boolean continua = true;
	boolean valvula = false;
	private Caldeira caldeira;
	private boolean ON= true;
	
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
			}
			
		};
		
		return th;							
	}

	Thread sensorBomba(){
		return null;
	}

}
