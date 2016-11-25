package com.epam.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.epam.data.RoadAccident;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;

/**
 * This is to be completed by mentees
 */
public class DataProcessor {

    private final List<RoadAccident> roadAccidentList;

    public DataProcessor(List<RoadAccident> roadAccidentList){
        this.roadAccidentList = roadAccidentList;
    }


//    First try to solve task using java 7 style for processing collections

    /**
     * Return road accident with matching index
     * @param index
     * @return
     */
	public RoadAccident getAccidentByIndex7(String index) {
		if (index != null) {
			for (RoadAccident ra : roadAccidentList) {
				if (index.equals(ra.getAccidentId())) {
					return ra;
				}
			}
		}
		return null;
	}


    /**
     * filter list by longtitude and latitude values, including boundaries
     * @param minLongitude
     * @param maxLongitude
     * @param minLatitude
     * @param maxLatitude
     * @return
     */
    public Collection<RoadAccident> getAccidentsByLocation7(float minLongitude, float maxLongitude, float minLatitude, float maxLatitude){
		List<RoadAccident> retList = new LinkedList<>();
		for (RoadAccident ra : roadAccidentList) {
			if (withinArea(ra, minLongitude, maxLongitude, minLatitude, maxLatitude)) {
				retList.add(ra);
			}
		}
		return retList;
    }
    
	private boolean withinArea(RoadAccident ra, float minLongitude, float maxLongitude, float minLatitude,
			float maxLatitude) {
		return ra != null && ra.getLongitude() > minLongitude && ra.getLongitude() < maxLongitude
				&& ra.getLatitude() > minLatitude && ra.getLatitude() < maxLatitude;
	}

    /**
     * count incidents by road surface conditions
     * ex:
     * wet -> 2
     * dry -> 5
     * @return
     */
	public Map<String, Long> getCountByRoadSurfaceCondition7() {

		Map<String, Long> accidentsCountByRoadSurface = new HashMap<>();
		String roadSurfaceCondition = null;
		long count = 0;

		for (RoadAccident ra : roadAccidentList) {
			if (ra.getRoadSurfaceConditions() == null) {
				continue; // in case
			}
			roadSurfaceCondition = ra.getRoadSurfaceConditions();
			if (accidentsCountByRoadSurface.containsKey(roadSurfaceCondition)) {
				count = accidentsCountByRoadSurface.get(roadSurfaceCondition) + 1l;
				accidentsCountByRoadSurface.put(roadSurfaceCondition, count);
			} else {
				accidentsCountByRoadSurface.put(roadSurfaceCondition, 1l);
			}
		}
		return accidentsCountByRoadSurface;
	}

    /**
     * find the weather conditions which caused the top 3 number of incidents
     * as example if there were 10 accidence in rain, 5 in snow, 6 in sunny and 1 in foggy, then your result list should contain {rain, sunny, snow} - top three in decreasing order
     * @return
     */
	public List<String> getTopThreeWeatherCondition7() {

		Map<String, Long> countByWeather = countAccidentsByWeatherCondition();

		TreeMap<Long, String> sortedCountsByWeather = sortAccidentsCountsByWeather(countByWeather);

		return getTopThreeWeatherConditionFromSortedMap(sortedCountsByWeather);
	}

	private Map<String, Long> countAccidentsByWeatherCondition() {

		Map<String, Long> accidentsCountByWeather = new HashMap<>();
		String weatherCondition = null;
		long count = 0;

		for (RoadAccident ra : roadAccidentList) {
			if (ra.getWeatherConditions() == null) {
				continue; // in case
			}
			weatherCondition = ra.getWeatherConditions();
			if (accidentsCountByWeather.containsKey(weatherCondition)) {
				count = accidentsCountByWeather.get(weatherCondition) + 1l;
				accidentsCountByWeather.put(weatherCondition, count);
			} else {
				accidentsCountByWeather.put(weatherCondition, 1l);
			}
		}
		return accidentsCountByWeather;
	}

	private TreeMap<Long, String> sortAccidentsCountsByWeather(Map<String, Long> counted) {

		BiMap<String, Long> countedBiMap = HashBiMap.create(counted);
		TreeMap<Long, String> sorted = new TreeMap<>(countedBiMap.inverse());
		return sorted;
	}
	
	private List<String> getTopThreeWeatherConditionFromSortedMap(TreeMap<Long, String> sorted) {

		Iterator<Long> itr = sorted.descendingKeySet().iterator();
		List<String> retList = new ArrayList<>(3);
		int i = 0;
		String weather = null;
		while (itr.hasNext() && i < 3) {
			i++;
			weather = sorted.get(itr.next());
			retList.add(weather);
		}
		return retList;
	}

    /**
     * return a multimap where key is a district authority and values are accident ids
     * ex:
     * authority1 -> id1, id2, id3
     * authority2 -> id4, id5
     * @return
     */
	public Multimap<String, String> getAccidentIdsGroupedByAuthority7() {

		Multimap<String, String> accidentIdsGroupedByAuthority = ArrayListMultimap.create();
		for (RoadAccident ra : roadAccidentList) {
			accidentIdsGroupedByAuthority.put(ra.getDistrictAuthority(), ra.getAccidentId());
		}
		return accidentIdsGroupedByAuthority;
	}


    // Now let's do same tasks but now with streaming api



	public RoadAccident getAccidentByIndex(String index) {
		return roadAccidentList
				.stream()
				.filter(ra -> index.equals(ra.getAccidentId()))
				.findFirst()
				.orElse(null);
	}


    /**
     * filter list by longtitude and latitude fields
     * @param minLongitude
     * @param maxLongitude
     * @param minLatitude
     * @param maxLatitude
     * @return
     */
	public Collection<RoadAccident> getAccidentsByLocation(float minLongitude, float maxLongitude, float minLatitude,
			float maxLatitude) {
		return roadAccidentList
				.stream()
				.filter(ra -> withinArea(ra, minLongitude, maxLongitude, minLatitude, maxLatitude))
				.collect(Collectors.toList());
	}

    /**
     * find the weather conditions which caused max number of incidents
     * @return
     */
	public List<String> getTopThreeWeatherCondition() {
		return roadAccidentList
				.stream()
				.collect(Collectors.groupingBy(RoadAccident::getWeatherConditions, Collectors.counting()))
				.entrySet()
				.stream()
				.sorted(Comparator.comparing(Map.Entry<String, Long>::getValue).reversed())
				.limit(3)
				.map(Map.Entry<String, Long>::getKey)
				.collect(Collectors.toList());
	}

    /**
     * count incidents by road surface conditions
     * @return
     */
	public Map<String, Long> getCountByRoadSurfaceCondition() {
		return roadAccidentList
				.stream()
				.collect(Collectors.groupingBy(RoadAccident::getRoadSurfaceConditions, Collectors.counting()));
	}

    /**
     * To match streaming operations result, return type is a java collection instead of multimap
     * @return
     */
	public Map<String, List<String>> getAccidentIdsGroupedByAuthority() {
		return roadAccidentList.stream().collect(
				Collectors.groupingBy(RoadAccident::getDistrictAuthority,
						Collectors.mapping(RoadAccident::getAccidentId, Collectors.toList())));
	}

}
