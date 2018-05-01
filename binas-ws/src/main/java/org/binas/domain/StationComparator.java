package org.binas.domain;

import java.util.Comparator;

import org.binas.ws.CoordinatesView;
import org.binas.ws.StationView;

/**
 * Comparator for two StationView, using to sort.
 */
class StationComparator implements Comparator<StationView> { 
	private int x;
	private int y;
	
	public StationComparator(CoordinatesView coordinates){
		this.x = coordinates.getX();
		this.y = coordinates.getY();
	}
	
    @Override
    public int compare(StationView sv1, StationView sv2) {
		int x1 = sv1.getCoordinate().getX();
		int y1 = sv1.getCoordinate().getY();
		int x2 = sv2.getCoordinate().getX();
		int y2 = sv2.getCoordinate().getY();
		int distance1 = (int) (Math.pow(x1-this.x, 2) + Math.pow(y1-this.y, 2));
		int distance2 = (int) (Math.pow(x2-this.x, 2) + Math.pow(y2-this.y, 2));
        return distance1 - distance2;
    }
}
