package logic;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Callable;

import model.LatLng;
import model.Route;
import de.schildbach.pte.IvbProvider;
import de.schildbach.pte.NetworkProvider;
import de.schildbach.pte.NetworkProvider.Accessibility;
import de.schildbach.pte.NetworkProvider.WalkSpeed;
import de.schildbach.pte.dto.Location;
import de.schildbach.pte.dto.LocationType;
import de.schildbach.pte.dto.Product;
import de.schildbach.pte.dto.QueryTripsResult;
import de.schildbach.pte.dto.Trip;

/*
 * TODO:
 * 		- Routenobjekt erstellen und zurückgeben
 * 		- Routendaten Aufarbeiten -> wie in IVB -> Leserlich
 * 
 */
public class PTE implements Callable<Route>{
	
	private NetworkProvider provider;
	
	private Location from = null;
	private Location to = null;
	private Date date = null;
	
	public PTE(String start, String stop, Date date){
		provider = new IvbProvider();
		getRoute(start, stop, date);
	}
	
	public PTE(LatLng start, LatLng stop, Date date){
		provider = new IvbProvider();
		getRoute(start, stop, date);
	}

	public Route getRoute(String start, String stop, Date date){
		
		from = new Location(LocationType.STATION, 0, null, start);
		to = new Location(LocationType.STATION, 0, null, stop);
		
		
		return calcRoute();
	}
	
	//lng / 1E6 = float lng 
	public Route getRoute(LatLng start, LatLng stop, Date date){
		
		int LatStart = (int) (start.getLat() * 1E6);
		int LngStart = (int) (start.getLng() * 1E6);
		
		int LatStop = (int) (stop.getLat() * 1E6);
		int LngStop = (int) (stop.getLng() * 1E6);
		
		from = new Location(LocationType.STATION, 0,LatStart, LngStart, null, null);
		to = new Location(LocationType.STATION, 0,LatStop,LngStop, null, null);
		
		return calcRoute();
	}
	
	private Route calcRoute(){
		
		Route route = null;
		
		QueryTripsResult result = null;
		
		try {
			result = provider.queryTrips(from, null, to, new Date(), true,
					Product.ALL, WalkSpeed.NORMAL, Accessibility.NEUTRAL, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(result != null)
		{
//			System.out.println("result != null " + result.toString());
			Trip trip = result.trips.get(0);
			int duration = (int) (trip.getLastArrivalTime().getTime() - trip.getFirstDepartureTime().getTime());
			int distance = -1;
			//String type = tran
			
			
			String[] text = {"pte"};
			route = new Route(duration, distance, "PTE", text, 0,0, new Date());
		}
		
		
//		System.out.println("PTE route: " + route.toString());		
		return route;
	}

	@Override
	public Route call() throws Exception {
		// TODO Auto-generated method stub
		return calcRoute();
	}
	
}
