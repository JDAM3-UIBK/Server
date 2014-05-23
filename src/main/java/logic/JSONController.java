package logic;

import java.util.Date;

import model.LatLng;
import model.Route;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class JSONController {

	
	@RequestMapping(value = "{input}", method = RequestMethod.GET)
	public @ResponseBody
	Route[] getRouteInJSON(@PathVariable String input) {
		
		String text[] = {"FAILED","FAILED","FAILED","FAILED","FAILED","FAILED"};
		
		RouteController rc = new RouteController();
		UrlParser up = new UrlParser();
		Route[] routes = null;
		
		routes = rc.calcRoutes(up.parserFrom(input), up.parserTo(input));

		return routes;

	}
	

	

}