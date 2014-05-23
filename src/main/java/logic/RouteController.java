package logic;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import enums.GoogleMapModi;
import model.Route;




public class RouteController {

	private int taskNum = 3;

	public RouteController(){}

	public Route[] calcRoutes(String origin, String destination){

		Route[] routes = new Route[taskNum];
		
		
		GoogleMap gmCar = new GoogleMap(origin,destination, GoogleMapModi.driving);
		FutureTask<Route> taskGMcar = new FutureTask<Route>(gmCar);
		routes[0] = subCalcRoutes(taskGMcar);
				
//		PTE pte = new PTE(origin, destination, new Date());
//		FutureTask<Route> taskPTE = new FutureTask<Route>(pte);
//		routes[1] = subCalcRoutes(taskPTE);
		String text[] = {"FAILED","FAILED","FAILED","FAILED","FAILED","FAILED"};
		routes[1] = new Route(123, 456, "PTE", text, 23, 234, new Date());
		
		GoogleMap gmBike = new GoogleMap(origin,destination, GoogleMapModi.bicycling);
		FutureTask<Route> taskGMbike = new FutureTask<Route>(gmBike);	
		routes[2] = subCalcRoutes(taskGMbike);	
		
		return routes;
	}

	private Route subCalcRoutes(FutureTask<Route> task){

		final ExecutorService service;

		service = Executors.newFixedThreadPool(1);

		service.execute(task);

		Route routes = null;
		try {
			routes = task.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		service.shutdown();

		return routes;
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RouteController rc = new RouteController();
		Route[] rs = rc.calcRoutes("Innsbruck", "Wien");

		if(rs != null){
			for(int i = 0; i < rs.length; ++i)
				System.out.println(rs[i].toString());
		}
		else
			System.out.println("failed");

	}
}
