package org.eclipse.featuremodel.diagrameditor.utilities;

import java.util.List;

import org.eclipse.featuremodel.Group;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IPeService;

/**
 * This class responds to calculate the middle point of the curve which belongs to the given group.
 *
 */
public class CalCurveMiddleUtil {
	
	private static final int POLIGON_SIZE = 40;

	/**
	 * Calculate the value of x coordinate of the middle point of the curve.
	 * 
	 * @param group
	 * @param fp
	 * @return
	 */
	public static int calXCurveMiddle(Group group, IFeatureProvider fp) {
		List<Connection> connections = BOUtil
				.getAllPictogramElementsForBusinessObject(group,
						Connection.class, fp);
		Connection[] outerConn = getOuterConnections(connections);
		IPeService peService = Graphiti.getPeService();
		ILocation p0 = peService.getLocationRelativeToDiagram(outerConn[0]
				.getStart());
		Point p1 = calculatePoint(outerConn[0], POLIGON_SIZE);
		Point p2 = calculatePoint(outerConn[1], POLIGON_SIZE);
		int x0 = p0.getX();
		int x1 = p1.getX();
		int x2 = p2.getX();

		int xCurveMiddle = x1 + ((x2 - x1) / 2);
		if (xCurveMiddle != x0) {
			xCurveMiddle -= (x0 - xCurveMiddle) / 2;
		}
		return xCurveMiddle;
	}

	/**
	 * Calculate the value of y coordinate of the middle point of the curve.
	 * 
	 * @param group
	 * @param fp
	 * @return
	 */
	public static int calYCurveMiddle(Group group, IFeatureProvider fp) {
		List<Connection> connections = BOUtil
				.getAllPictogramElementsForBusinessObject(group,
						Connection.class, fp);
		Connection[] outerConn = getOuterConnections(connections);
		IPeService peService = Graphiti.getPeService();
		ILocation p0 = peService.getLocationRelativeToDiagram(outerConn[0]
				.getStart());
		Point p1 = calculatePoint(outerConn[0], POLIGON_SIZE);
		Point p2 = calculatePoint(outerConn[1], POLIGON_SIZE);
		int y0 = p0.getY();
		int y1 = p1.getY();
		int y2 = p2.getY();

		int yCurveMiddle = y2 + ((y1 - y2) / 2);
		if (yCurveMiddle != y0) {
			yCurveMiddle -= (y0 - yCurveMiddle) / 2;
		}
		return yCurveMiddle;
	}

	/**
	 * Gets two outer connections of set relation.
	 * 
	 * @param connections
	 *            The connections represent relations.
	 * @return The array of two outer connections.
	 */
	private static Connection[] getOuterConnections(List<Connection> connections) {
		int xMin = Integer.MAX_VALUE; // X coordinate of the left outer
										// connection
		int xMax = Integer.MIN_VALUE; // X coordinate of the right outer
										// connection
		Connection[] result = new Connection[2];

		// run trough all connections a look for X coordinate of the connection
		// end
		for (Connection conn : connections) {
			Point p = calculatePoint(conn, 40);

			if (p.getX() < xMin) {
				result[0] = conn;
				xMin = p.getX();
			}

			if (p.getX() >= xMax) {
				result[1] = conn;
				xMax = p.getX();
			}
		}
		return result;
	}

	/**
	 * Calculates the coordinates of a connection line point in according to the
	 * given distance <code>dis</code> from the connection start point.
	 * 
	 * @param connection
	 *            the connection.
	 * @param dis
	 *            the distance from the connection start point.
	 * @return The calculated point.
	 */
	private static Point calculatePoint(Connection connection, double dis) {
		// determine line start and end points
		ILocation a = Graphiti.getPeService().getLocationRelativeToDiagram(
				connection.getStart());
		ILocation b = Graphiti.getPeService().getLocationRelativeToDiagram(
				connection.getEnd());
		// line vector
		Point ba = Graphiti.getGaService().createPoint(b.getX() - a.getX(),
				b.getY() - a.getY());
		// norm of the line vector
		double norm = Math.sqrt(ba.getX() * ba.getX() + ba.getY() * ba.getY());
		// calculate coordinates
		double x = a.getX() + dis * (ba.getX() / norm);
		double y = a.getY() + dis * (ba.getY() / norm);

		return Graphiti.getGaService().createPoint((int) x, (int) y);
	}
}
