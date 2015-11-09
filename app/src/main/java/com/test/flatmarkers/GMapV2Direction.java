package com.test.flatmarkers;


        import java.io.InputStream;
        import java.util.ArrayList;
        import java.util.List;

        import javax.xml.parsers.DocumentBuilder;
        import javax.xml.parsers.DocumentBuilderFactory;

        import org.apache.http.HttpResponse;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.apache.http.protocol.BasicHttpContext;
        import org.apache.http.protocol.HttpContext;
        import org.w3c.dom.Document;
        import org.w3c.dom.Node;
        import org.w3c.dom.NodeList;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;
        import android.util.Log;

public class GMapV2Direction {
    public final static String MODE_DRIVING = "driving";
    public final static String MODE_WALKING = "walking";

    public GMapV2Direction() {
    }

    public Document getDocument(LatLng start, LatLng end, String mode) {
        String url = "https://maps.googleapis.com/maps/api/directions/xml?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude
                + "&units=metric&mode="+mode+"&alternatives=true"
                + "&departure_time=now&client=" // client value to be added with signature
                ;
        try {
            String signedUrl  = UrlSign.signUrl(url);
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(signedUrl);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            InputStream in = response.getEntity().getContent();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = builder.parse(in);
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDurationText(Document doc) {
        try {

            NodeList nl1 = doc.getElementsByTagName("duration");
            Node node1 = nl1.item(0);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "text"));
            Log.i("DurationText", node2.getTextContent());
            return node2.getTextContent();
        } catch (Exception e) {
            return "0";
        }
    }

    public String[] getDurationInTraffic(Document doc) {
        try {

            NodeList nl1 = doc.getElementsByTagName("duration_in_traffic");
            Node node1 = nl1.item(0);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            String durationValue = node2.getTextContent();
            Log.i("DurationValue", durationValue);
            Node node3 = nl2.item(getNodeIndex(nl2, "text"));
            String durationText = node3.getTextContent();
            Log.i("DurationText", durationText);
            String[] duration = {durationValue, durationText};
            return duration;
        } catch (Exception e) {
            return new String[]{null, null};
        }
    }

    public int getDurationValue(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("duration");
            Node node1 = nl1.item(0);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            Log.i("DurationValue", node2.getTextContent());
            return Integer.parseInt(node2.getTextContent());
        } catch (Exception e) {
            return -1;
        }
    }

    public String getDistanceText(Document doc) {
    /*
     * while (en.hasMoreElements()) { type type = (type) en.nextElement();
     *
     * }
     */

        try {
            NodeList nl1;
            nl1 = doc.getElementsByTagName("distance");

            Node node1 = nl1.item(nl1.getLength() - 1);
            NodeList nl2 = null;
            nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            Log.d("DistanceText", node2.getTextContent());
            return node2.getTextContent();
        } catch (Exception e) {
            return "-1";
        }

    /*
     * NodeList nl1; if(doc.getElementsByTagName("distance")!=null){ nl1=
     * doc.getElementsByTagName("distance");
     *
     * Node node1 = nl1.item(nl1.getLength() - 1); NodeList nl2 = null; if
     * (node1.getChildNodes() != null) { nl2 = node1.getChildNodes(); Node
     * node2 = nl2.item(getNodeIndex(nl2, "value")); Log.d("DistanceText",
     * node2.getTextContent()); return node2.getTextContent(); } else return
     * "-1";} else return "-1";
     */
    }

    public int getDistanceValue(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("distance");
            Node node1 = null;
            node1 = nl1.item(nl1.getLength() - 1);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            Log.i("DistanceValue", node2.getTextContent());
            return Integer.parseInt(node2.getTextContent());
        } catch (Exception e) {
            return -1;
        }
    /*
     * NodeList nl1 = doc.getElementsByTagName("distance"); Node node1 =
     * null; if (nl1.getLength() > 0) node1 = nl1.item(nl1.getLength() - 1);
     * if (node1 != null) { NodeList nl2 = node1.getChildNodes(); Node node2
     * = nl2.item(getNodeIndex(nl2, "value")); Log.i("DistanceValue",
     * node2.getTextContent()); return
     * Integer.parseInt(node2.getTextContent()); } else return 0;
     */
    }

    public String getStartAddress(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("start_address");
            Node node1 = nl1.item(0);
            Log.i("StartAddress", node1.getTextContent());
            return node1.getTextContent();
        } catch (Exception e) {
            return "-1";
        }

    }

    public String getEndAddress(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("end_address");
            Node node1 = nl1.item(0);
            Log.i("StartAddress", node1.getTextContent());
            return node1.getTextContent();
        } catch (Exception e) {
            return "-1";
        }
    }
    public String getCopyRights(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("copyrights");
            Node node1 = nl1.item(0);
            Log.i("CopyRights", node1.getTextContent());
            return node1.getTextContent();
        } catch (Exception e) {
            return "-1";
        }

    }

    public ArrayList<DirectionsRoute>  getDirection(Document doc) {
        NodeList   nl3;
        NodeList routes;
        ArrayList<DirectionsRoute> allGeopoints = new ArrayList<DirectionsRoute>();
        NodeList statusList = doc.getElementsByTagName("status");
        String status = statusList.item(0).getTextContent();
        if (!"OK".equalsIgnoreCase(status)){
            return null;
        }
        routes = doc.getElementsByTagName("route");

        if (routes.getLength() > 0) {
            for (int jj = 0; jj < routes.getLength(); jj++) {
                ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
                DirectionsRoute route = new DirectionsRoute();
                Node theRoute = routes.item(jj);
                NodeList subRouteList = theRoute.getChildNodes();
                int summaryIndex = getNodeIndex(subRouteList, "summary");
                Node summary = subRouteList.item(summaryIndex);
                route.setSummary(summary.getTextContent());
                Integer[] legIndeces = getNodeIndeces(subRouteList, "leg");
                int polyIndex = getNodeIndex(subRouteList, "overview_polyline");
                Node boundsNode = subRouteList.item(getNodeIndex(subRouteList, "bounds"));
                NodeList bounds = boundsNode.getChildNodes();
                Node southWestNode = bounds.item(getNodeIndex(bounds, "southwest"));
                Node northEastNode = bounds.item(getNodeIndex(bounds, "northeast"));
                NodeList southWestChildren = southWestNode.getChildNodes();
                NodeList northEastChildren = northEastNode.getChildNodes();
                double southWestLat = Double.parseDouble(southWestChildren.item(getNodeIndex(southWestChildren, "lat")).getTextContent());
                double southWestLong = Double.parseDouble(southWestChildren.item(getNodeIndex(southWestChildren, "lng")).getTextContent());
                double northEastLat = Double.parseDouble(northEastChildren.item(getNodeIndex(northEastChildren, "lat")).getTextContent());
                double northEastLong = Double.parseDouble(northEastChildren.item(getNodeIndex(northEastChildren, "lng")).getTextContent());
                LatLng southWest = new LatLng(southWestLat, southWestLong);
                LatLng northEast = new LatLng(northEastLat, northEastLong);
                route.setSouthWest(southWest);
                route.setNorthEast(northEast);

                Node polyNode = subRouteList.item(polyIndex);
                nl3 = polyNode.getChildNodes();
                Node latLongNode = nl3.item(getNodeIndex(nl3, "points"));
                ArrayList<LatLng> arr = decodePoly(latLongNode.getTextContent());
                int markerIndex = arr.size() / 2;
                if (markerIndex < 1){
                    markerIndex = 1;
                }
                for (int j = 0; j < arr.size(); j++) {

                    listGeopoints.add(new LatLng(arr.get(j).latitude, arr
                            .get(j).longitude));
                    if (j == markerIndex){
                        route.setMarker(new MarkerOptions().position(new LatLng(arr.get(j).latitude, arr
                                .get(j).longitude)));
                    }
                }
                for (int k : legIndeces) {
                    Node legNode = subRouteList.item(k);
                    NodeList legChildNodeList = legNode.getChildNodes();
                    Integer[] stepIndeces = getNodeIndeces(legChildNodeList, "step");
                    int stepsLength = stepIndeces.length;
                    if(stepsLength != 0){
                        int durationIndex = getNodeIndex(legChildNodeList,"duration_in_traffic");
                        Node durationNode = legChildNodeList.item(durationIndex);
                        NodeList durationChildNodes = durationNode.getChildNodes();
                        int durationValueIndex = getNodeIndex(durationChildNodes, "value");
                        Node durationValueNode = durationChildNodes.item(durationValueIndex);
                        int durationTextIndex = getNodeIndex(durationChildNodes, "text");
                        Node durationTextode = durationChildNodes.item(durationTextIndex);
                        route.setDuration(Long.parseLong(durationValueNode.getTextContent()));
                        route.setDurationText(durationTextode.getTextContent().replace(" ","\n"));


                        int viaIndex = getNodeIndex(legChildNodeList,"via_waypoint");
                        if(viaIndex != -1) {
                            Node waypointNode = legChildNodeList.item(viaIndex);
                            NodeList waypointNodeChildNodes = waypointNode.getChildNodes();
                            int locationIndex = getNodeIndex(waypointNodeChildNodes, "location");
                            Node locationNode = waypointNodeChildNodes.item(locationIndex);
                            NodeList waypointLocationChildren = locationNode.getChildNodes();
                            String waypointLat = waypointLocationChildren.item(getNodeIndex(waypointLocationChildren,"lat")).getTextContent();
                            String waypointLng = waypointLocationChildren.item(getNodeIndex(waypointLocationChildren,"lng")).getTextContent();
                            LatLng waypoint = new LatLng(Double.parseDouble(waypointLat), Double.parseDouble(waypointLng));
                            route.setWaypoint(waypoint);
                        }


//                        for (int i : stepIndeces) { //OLD WAY USING STEPs TO DRAW
//                            Node node1 = legChildNodeList.item(i);
//
//
//                                nl2 = node1.getChildNodes();
//
//                                Node locationNode = nl2
//                                        .item(getNodeIndex(nl2, "start_location"));
//                                nl3 = locationNode.getChildNodes();
//                                Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
//                                double lat = Double.parseDouble(latNode.getTextContent());
//                                Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
//                                double lng = Double.parseDouble(lngNode.getTextContent());
//                                listGeopoints.add(new LatLng(lat, lng));
//
//                                locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
//                                nl3 = locationNode.getChildNodes();
//                                latNode = nl3.item(getNodeIndex(nl3, "points"));
//                                ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
//                                for (int j = 0; j < arr.size(); j++) {
//                                    listGeopoints.add(new LatLng(arr.get(j).latitude, arr
//                                            .get(j).longitude));
//                                }
//
//                                locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
//                                nl3 = locationNode.getChildNodes();
//                                latNode = nl3.item(getNodeIndex(nl3, "lat"));
//                                lat = Double.parseDouble(latNode.getTextContent());
//                                lngNode = nl3.item(getNodeIndex(nl3, "lng"));
//                                lng = Double.parseDouble(lngNode.getTextContent());
//                                listGeopoints.add(new LatLng(lat, lng));
//
//
//                        }
                    }

                }

                route.setGeopoints(listGeopoints);
                allGeopoints.add(route);
            }
        }
        return allGeopoints;

    }

    private int getNodeIndex(NodeList nl, String nodename) {
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
    }

    private Integer[] getNodeIndeces(NodeList nl, String nodename) {

    List<Integer> indexList = new ArrayList<Integer>() ;
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals(nodename))
               indexList.add(i);
        }
        Integer[] result = new Integer[indexList.size()];
        return indexList.toArray(result);
    }

    private ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }



}