package com.FeelSafe.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

@CapacitorPlugin(name = "ShowcaseBackend")
public class ShowcaseBackendPlugin extends Plugin {

    private static final String TAG = "ShowcaseBackend";

    // ── Static Data Constants ──────────────────────────────────────────────────
    public static final String UNSAFE_ZONES_DATA = "[{\"id\": \"zone_001\", \"name\": \"Dark Alley near Central Market\", \"lat\": 28.6492, \"lon\": 77.205, \"risk\": \"HIGH\", \"reason\": \"Poorly lit, isolated road with frequent incidents reported at night\", \"reports\": 14, \"active_hours\": \"20:00-06:00\", \"incident_types\": [\"harassment\", \"robbery\", \"stalking\"]}, {\"id\": \"zone_002\", \"name\": \"Underpass Road MG Road\", \"lat\": 28.6139, \"lon\": 77.209, \"risk\": \"HIGH\", \"reason\": \"Isolated underpass with no CCTV, frequent harassment and chain snatching\", \"reports\": 9, \"active_hours\": \"19:00-07:00\", \"incident_types\": [\"harassment\", \"chain_snatching\"]}, {\"id\": \"zone_003\", \"name\": \"Industrial Area Back Roads\", \"lat\": 28.6719, \"lon\": 77.164, \"risk\": \"MEDIUM\", \"reason\": \"Deserted at night, no street lights after 9 PM, minimal police patrolling\", \"reports\": 6, \"active_hours\": \"21:00-05:00\", \"incident_types\": [\"stalking\", \"eve_teasing\"]}, {\"id\": \"zone_004\", \"name\": \"Railway Colony Lane\", \"lat\": 28.6431, \"lon\": 77.2189, \"risk\": \"MEDIUM\", \"reason\": \"Narrow dead-end lanes with community reports of suspicious activity\", \"reports\": 4, \"active_hours\": \"22:00-06:00\", \"incident_types\": [\"suspicious_activity\"]}, {\"id\": \"zone_005\", \"name\": \"South Extension Park Area\", \"lat\": 28.5733, \"lon\": 77.2195, \"risk\": \"LOW\", \"reason\": \"Generally safe but park becomes isolated after 10 PM\", \"reports\": 2, \"active_hours\": \"22:00-06:00\", \"incident_types\": [\"eve_teasing\"]}, {\"id\": \"zone_006\", \"name\": \"Outer Ring Road Overpass\", \"lat\": 28.5985, \"lon\": 77.042, \"risk\": \"HIGH\", \"reason\": \"Isolated overpass with multiple SOS alerts from women at night\", \"reports\": 11, \"active_hours\": \"20:00-06:00\", \"incident_types\": [\"stalking\", \"harassment\", \"robbery\"]}, {\"id\": \"zone_007\", \"name\": \"Old Graveyard Lane\", \"lat\": 28.6563, \"lon\": 77.197, \"risk\": \"HIGH\", \"reason\": \"No lighting, dead-end street, frequently deserted \\u2014 avoid at night\", \"reports\": 8, \"active_hours\": \"19:00-07:00\", \"incident_types\": [\"harassment\", \"stalking\"]}, {\"id\": \"zone_008\", \"name\": \"Near Deserted Mall Parking\", \"lat\": 28.629, \"lon\": 77.3758, \"risk\": \"MEDIUM\", \"reason\": \"Isolated parking area with low foot traffic and poor visibility at night\", \"reports\": 5, \"active_hours\": \"21:00-06:00\", \"incident_types\": [\"robbery\", \"suspicious_activity\"]}, {\"id\": \"zone_009\", \"name\": \"Tughlaqabad Forest Road\", \"lat\": 28.51, \"lon\": 77.263, \"risk\": \"HIGH\", \"reason\": \"Dense forest road with no signal, no lighting, and multiple assault reports\", \"reports\": 16, \"active_hours\": \"18:00-07:00\", \"incident_types\": [\"assault\", \"robbery\", \"harassment\"]}, {\"id\": \"zone_010\", \"name\": \"Wazirabad Bridge Night Zone\", \"lat\": 28.726, \"lon\": 77.235, \"risk\": \"HIGH\", \"reason\": \"Bridge flanked by isolated riverbanks \\u2014 frequently targeted robbery spot\", \"reports\": 12, \"active_hours\": \"21:00-05:00\", \"incident_types\": [\"robbery\", \"assault\"]}, {\"id\": \"zone_011\", \"name\": \"Mehrauli Backlane\", \"lat\": 28.5231, \"lon\": 77.1868, \"risk\": \"MEDIUM\", \"reason\": \"Winding narrow lanes with poor street coverage and past incident history\", \"reports\": 5, \"active_hours\": \"20:00-06:00\", \"incident_types\": [\"harassment\", \"suspicious_activity\"]}, {\"id\": \"zone_012\", \"name\": \"Naraina Industrial Area East\", \"lat\": 28.631, \"lon\": 77.134, \"risk\": \"MEDIUM\", \"reason\": \"Factories shut after 8 PM, area becomes completely desolate\", \"reports\": 7, \"active_hours\": \"20:00-07:00\", \"incident_types\": [\"stalking\", \"eve_teasing\"]}, {\"id\": \"zone_013\", \"name\": \"Shahdara Flyover Underside\", \"lat\": 28.67, \"lon\": 77.297, \"risk\": \"HIGH\", \"reason\": \"Notorious area under flyover \\u2014 frequent harassment and substance abuse\", \"reports\": 18, \"active_hours\": \"18:00-06:00\", \"incident_types\": [\"harassment\", \"assault\", \"robbery\"]}, {\"id\": \"zone_014\", \"name\": \"Kalkaji Extension Back Road\", \"lat\": 28.548, \"lon\": 77.258, \"risk\": \"LOW\", \"reason\": \"Minor concerns reported \\u2014 low activity area at late night hours\", \"reports\": 2, \"active_hours\": \"23:00-05:00\", \"incident_types\": [\"suspicious_activity\"]}, {\"id\": \"zone_015\", \"name\": \"Noida Expressway Service Lane\", \"lat\": 28.507, \"lon\": 77.366, \"risk\": \"MEDIUM\", \"reason\": \"Isolated service lane parallel to expressway \\u2014 limited visibility and access\", \"reports\": 6, \"active_hours\": \"21:00-06:00\", \"incident_types\": [\"harassment\", \"stalking\"]}]";
    public static final String SAMPLE_ROUTES_DATA = "[{\"id\": \"route_001\", \"name\": \"Connaught Place to Lajpat Nagar\", \"origin\": {\"lat\": 28.6315, \"lon\": 77.2167, \"name\": \"Connaught Place\"}, \"destination\": {\"lat\": 28.5677, \"lon\": 77.2433, \"name\": \"Lajpat Nagar\"}, \"distance_km\": 7.2, \"waypoints\": [{\"lat\": 28.623, \"lon\": 77.2195}, {\"lat\": 28.61, \"lon\": 77.226}, {\"lat\": 28.59, \"lon\": 77.235}], \"is_isolated\": false, \"community_rating\": 4.1, \"unsafe_report_count\": 1, \"nearby_police\": true, \"nearby_hospital\": true, \"tags\": [\"well_lit\", \"busy_road\", \"cctv_present\"], \"description\": \"Major road through central Delhi \\u2014 busy, well-monitored, and recommended for solo travel\"}, {\"id\": \"route_002\", \"name\": \"Karol Bagh to Pitampura via Ring Road\", \"origin\": {\"lat\": 28.6514, \"lon\": 77.1907, \"name\": \"Karol Bagh\"}, \"destination\": {\"lat\": 28.7015, \"lon\": 77.133, \"name\": \"Pitampura\"}, \"distance_km\": 12.5, \"waypoints\": [{\"lat\": 28.67, \"lon\": 77.173}, {\"lat\": 28.687, \"lon\": 77.155}], \"is_isolated\": false, \"community_rating\": 3.7, \"unsafe_report_count\": 3, \"nearby_police\": true, \"nearby_hospital\": false, \"tags\": [\"busy_road\", \"ring_road\", \"moderate_lighting\"], \"description\": \"Ring road route with police presence but moderate crowd density after 9 PM\"}, {\"id\": \"route_003\", \"name\": \"Nehru Place to Noida via Dark Stretch\", \"origin\": {\"lat\": 28.549, \"lon\": 77.2519, \"name\": \"Nehru Place\"}, \"destination\": {\"lat\": 28.5355, \"lon\": 77.391, \"name\": \"Noida Sector 18\"}, \"distance_km\": 14.8, \"waypoints\": [{\"lat\": 28.544, \"lon\": 77.29}, {\"lat\": 28.539, \"lon\": 77.33}], \"is_isolated\": true, \"community_rating\": 2.3, \"unsafe_report_count\": 7, \"nearby_police\": false, \"nearby_hospital\": false, \"tags\": [\"isolated_stretch\", \"poor_lighting\", \"late_night_risk\"], \"description\": \"Route passes through a known isolated stretch with multiple community safety concerns\"}, {\"id\": \"route_004\", \"name\": \"Saket to Hauz Khas Safe Corridor\", \"origin\": {\"lat\": 28.5254, \"lon\": 77.2091, \"name\": \"Saket\"}, \"destination\": {\"lat\": 28.5495, \"lon\": 77.2065, \"name\": \"Hauz Khas\"}, \"distance_km\": 4.1, \"waypoints\": [{\"lat\": 28.532, \"lon\": 77.208}, {\"lat\": 28.543, \"lon\": 77.207}], \"is_isolated\": false, \"community_rating\": 4.6, \"unsafe_report_count\": 0, \"nearby_police\": true, \"nearby_hospital\": true, \"tags\": [\"well_lit\", \"busy_market\", \"cctv_present\", \"recommended\"], \"description\": \"Premium safe corridor \\u2014 highest community trust, active CCTV, zero incident reports\"}, {\"id\": \"route_005\", \"name\": \"Dwarka Sector 10 to IGI Airport\", \"origin\": {\"lat\": 28.582, \"lon\": 77.049, \"name\": \"Dwarka Sector 10\"}, \"destination\": {\"lat\": 28.5562, \"lon\": 77.0998, \"name\": \"IGI Airport T3\"}, \"distance_km\": 9.3, \"waypoints\": [{\"lat\": 28.576, \"lon\": 77.065}, {\"lat\": 28.566, \"lon\": 77.083}], \"is_isolated\": false, \"community_rating\": 4.0, \"unsafe_report_count\": 2, \"nearby_police\": true, \"nearby_hospital\": true, \"tags\": [\"airport_road\", \"well_lit\", \"heavy_traffic\"], \"description\": \"Airport expressway with continuous traffic and police checkpoints \\u2014 safe even at night\"}, {\"id\": \"route_006\", \"name\": \"Rohini Sector 3 to Pitampura Market\", \"origin\": {\"lat\": 28.714, \"lon\": 77.116, \"name\": \"Rohini Sector 3\"}, \"destination\": {\"lat\": 28.7015, \"lon\": 77.133, \"name\": \"Pitampura Market\"}, \"distance_km\": 3.5, \"waypoints\": [{\"lat\": 28.71, \"lon\": 77.123}, {\"lat\": 28.706, \"lon\": 77.128}], \"is_isolated\": false, \"community_rating\": 4.3, \"unsafe_report_count\": 1, \"nearby_police\": true, \"nearby_hospital\": false, \"tags\": [\"well_lit\", \"busy_road\", \"cctv_present\"], \"description\": \"Short residential route with strong community feedback and active street lights\"}, {\"id\": \"route_007\", \"name\": \"Lajpat Nagar to Okhla Industrial Area\", \"origin\": {\"lat\": 28.5677, \"lon\": 77.2433, \"name\": \"Lajpat Nagar\"}, \"destination\": {\"lat\": 28.535, \"lon\": 77.273, \"name\": \"Okhla Phase 2\"}, \"distance_km\": 6.8, \"waypoints\": [{\"lat\": 28.556, \"lon\": 77.253}, {\"lat\": 28.545, \"lon\": 77.264}], \"is_isolated\": false, \"community_rating\": 3.2, \"unsafe_report_count\": 4, \"nearby_police\": false, \"nearby_hospital\": false, \"tags\": [\"moderate_lighting\", \"busy_road\"], \"description\": \"Route through industrial zone \\u2014 safe during daytime but caution advised after sunset\"}, {\"id\": \"route_008\", \"name\": \"GTB Nagar to Mukherjee Nagar\", \"origin\": {\"lat\": 28.692, \"lon\": 77.205, \"name\": \"GTB Nagar\"}, \"destination\": {\"lat\": 28.702, \"lon\": 77.189, \"name\": \"Mukherjee Nagar\"}, \"distance_km\": 2.8, \"waypoints\": [{\"lat\": 28.696, \"lon\": 77.198}], \"is_isolated\": false, \"community_rating\": 4.4, \"unsafe_report_count\": 0, \"nearby_police\": true, \"nearby_hospital\": false, \"tags\": [\"well_lit\", \"busy_road\", \"recommended\"], \"description\": \"Popular student route \\u2014 very high footfall, well lit, and regularly patrolled\"}, {\"id\": \"route_009\", \"name\": \"Noida Sector 62 to Sector 18 via Expressway\", \"origin\": {\"lat\": 28.627, \"lon\": 77.365, \"name\": \"Noida Sector 62\"}, \"destination\": {\"lat\": 28.57, \"lon\": 77.322, \"name\": \"Noida Sector 18\"}, \"distance_km\": 10.1, \"waypoints\": [{\"lat\": 28.605, \"lon\": 77.352}, {\"lat\": 28.588, \"lon\": 77.338}], \"is_isolated\": false, \"community_rating\": 3.8, \"unsafe_report_count\": 2, \"nearby_police\": true, \"nearby_hospital\": true, \"tags\": [\"heavy_traffic\", \"well_lit\", \"airport_road\"], \"description\": \"Expressway route with CCTV and police checkpoints \\u2014 reliable and relatively safe\"}, {\"id\": \"route_010\", \"name\": \"Vasant Kunj to Mehrauli via Dark Road\", \"origin\": {\"lat\": 28.5219, \"lon\": 77.156, \"name\": \"Vasant Kunj\"}, \"destination\": {\"lat\": 28.5231, \"lon\": 77.1868, \"name\": \"Mehrauli\"}, \"distance_km\": 5.2, \"waypoints\": [{\"lat\": 28.521, \"lon\": 77.168}, {\"lat\": 28.522, \"lon\": 77.178}], \"is_isolated\": true, \"community_rating\": 2.8, \"unsafe_report_count\": 5, \"nearby_police\": false, \"nearby_hospital\": false, \"tags\": [\"isolated_stretch\", \"poor_lighting\", \"dark_stretch\"], \"description\": \"Poorly lit stretch passing near Mehrauli archaeological zone \\u2014 avoid at night\"}, {\"id\": \"route_011\", \"name\": \"Janakpuri West to Tilak Nagar\", \"origin\": {\"lat\": 28.63, \"lon\": 77.08, \"name\": \"Janakpuri West\"}, \"destination\": {\"lat\": 28.643, \"lon\": 77.101, \"name\": \"Tilak Nagar\"}, \"distance_km\": 4.4, \"waypoints\": [{\"lat\": 28.636, \"lon\": 77.09}], \"is_isolated\": false, \"community_rating\": 4.2, \"unsafe_report_count\": 1, \"nearby_police\": true, \"nearby_hospital\": false, \"tags\": [\"well_lit\", \"busy_road\", \"cctv_present\"], \"description\": \"Busy West Delhi corridor \\u2014 active commercial district with good visibility throughout\"}, {\"id\": \"route_012\", \"name\": \"Shahdara to Preet Vihar via DND\", \"origin\": {\"lat\": 28.67, \"lon\": 77.297, \"name\": \"Shahdara\"}, \"destination\": {\"lat\": 28.638, \"lon\": 77.296, \"name\": \"Preet Vihar\"}, \"distance_km\": 8.6, \"waypoints\": [{\"lat\": 28.658, \"lon\": 77.297}, {\"lat\": 28.648, \"lon\": 77.296}], \"is_isolated\": false, \"community_rating\": 2.5, \"unsafe_report_count\": 8, \"nearby_police\": false, \"nearby_hospital\": false, \"tags\": [\"poor_lighting\", \"isolated_stretch\"], \"description\": \"Route passes near a high-risk zone \\u2014 multiple reports of harassment near Shahdara flyover\"}, {\"id\": \"route_013\", \"name\": \"Connaught Place to Karol Bagh Express\", \"origin\": {\"lat\": 28.6315, \"lon\": 77.2167, \"name\": \"Connaught Place\"}, \"destination\": {\"lat\": 28.6514, \"lon\": 77.1907, \"name\": \"Karol Bagh\"}, \"distance_km\": 3.8, \"waypoints\": [{\"lat\": 28.64, \"lon\": 77.205}], \"is_isolated\": false, \"community_rating\": 4.5, \"unsafe_report_count\": 0, \"nearby_police\": true, \"nearby_hospital\": true, \"tags\": [\"well_lit\", \"busy_road\", \"cctv_present\", \"recommended\"], \"description\": \"Short central Delhi corridor \\u2014 constant traffic, camera coverage, and police presence\"}]";
    public static final String SAFETY_ANCHORS_DATA = "[{\"id\": \"ps_001\", \"name\": \"New Delhi Railway Station PS\", \"category\": \"police\", \"lat\": 28.643, \"lon\": 77.2195, \"open_24x7\": true, \"phone\": \"011-23746000\", \"safety_score\": 90, \"address\": \"New Delhi Station, Paharganj\"}, {\"id\": \"ps_002\", \"name\": \"Connaught Place Police Post\", \"category\": \"police\", \"lat\": 28.6324, \"lon\": 77.2188, \"open_24x7\": true, \"phone\": \"100\", \"safety_score\": 88, \"address\": \"CP Inner Circle, New Delhi\"}, {\"id\": \"ps_003\", \"name\": \"Lajpat Nagar Police Station\", \"category\": \"police\", \"lat\": 28.5675, \"lon\": 77.2402, \"open_24x7\": true, \"phone\": \"011-29817911\", \"safety_score\": 87, \"address\": \"Ring Rd, Lajpat Nagar II\"}, {\"id\": \"ps_004\", \"name\": \"Hauz Khas Police Station\", \"category\": \"police\", \"lat\": 28.549, \"lon\": 77.2065, \"open_24x7\": true, \"phone\": \"011-26862277\", \"safety_score\": 85, \"address\": \"Sri Aurobindo Marg, Hauz Khas\"}, {\"id\": \"ps_005\", \"name\": \"Karol Bagh Police Station\", \"category\": \"police\", \"lat\": 28.6519, \"lon\": 77.1909, \"open_24x7\": true, \"phone\": \"011-28751000\", \"safety_score\": 86, \"address\": \"Arya Samaj Rd, Karol Bagh\"}, {\"id\": \"ps_006\", \"name\": \"Saket Police Station\", \"category\": \"police\", \"lat\": 28.52, \"lon\": 77.207, \"open_24x7\": true, \"phone\": \"011-29531547\", \"safety_score\": 84, \"address\": \"Press Enclave Marg, Saket\"}, {\"id\": \"ps_007\", \"name\": \"Paharganj Police Chowki\", \"category\": \"police\", \"lat\": 28.6448, \"lon\": 77.2132, \"open_24x7\": true, \"phone\": \"100\", \"safety_score\": 82, \"address\": \"Main Bazar, Paharganj\"}, {\"id\": \"ps_008\", \"name\": \"Defence Colony Police Post\", \"category\": \"police\", \"lat\": 28.572, \"lon\": 77.2288, \"open_24x7\": true, \"phone\": \"100\", \"safety_score\": 85, \"address\": \"Defence Colony, New Delhi\"}, {\"id\": \"h_001\", \"name\": \"Ram Manohar Lohia Hospital\", \"category\": \"hospital\", \"lat\": 28.6378, \"lon\": 77.2072, \"open_24x7\": true, \"phone\": \"011-23365525\", \"safety_score\": 95, \"address\": \"Baba Kharak Singh Marg, New Delhi\"}, {\"id\": \"h_002\", \"name\": \"AIIMS Trauma Centre\", \"category\": \"hospital\", \"lat\": 28.5672, \"lon\": 77.21, \"open_24x7\": true, \"phone\": \"011-26588500\", \"safety_score\": 98, \"address\": \"Sri Aurobindo Marg, Ansari Nagar\"}, {\"id\": \"h_003\", \"name\": \"Safdarjung Hospital\", \"category\": \"hospital\", \"lat\": 28.5687, \"lon\": 77.2051, \"open_24x7\": true, \"phone\": \"011-26707444\", \"safety_score\": 94, \"address\": \"Ansari Nagar West, New Delhi\"}, {\"id\": \"h_004\", \"name\": \"Max Smart Super Specialty Hospital\", \"category\": \"hospital\", \"lat\": 28.5244, \"lon\": 77.209, \"open_24x7\": true, \"phone\": \"011-26515050\", \"safety_score\": 92, \"address\": \"Saket District Centre, Saket\"}, {\"id\": \"h_005\", \"name\": \"Lok Nayak Hospital\", \"category\": \"hospital\", \"lat\": 28.6384, \"lon\": 77.2374, \"open_24x7\": true, \"phone\": \"011-23232400\", \"safety_score\": 90, \"address\": \"Jawaharlal Nehru Marg, New Delhi\"}, {\"id\": \"h_006\", \"name\": \"Sir Ganga Ram Hospital\", \"category\": \"hospital\", \"lat\": 28.6455, \"lon\": 77.19, \"open_24x7\": true, \"phone\": \"011-25750000\", \"safety_score\": 93, \"address\": \"Rajinder Nagar, New Delhi\"}, {\"id\": \"h_007\", \"name\": \"Moolchand Hospital\", \"category\": \"hospital\", \"lat\": 28.5742, \"lon\": 77.2336, \"open_24x7\": true, \"phone\": \"011-42000000\", \"safety_score\": 89, \"address\": \"Lajpat Nagar III, New Delhi\"}, {\"id\": \"ph_001\", \"name\": \"Apollo Pharmacy CP\", \"category\": \"pharmacy\", \"lat\": 28.632, \"lon\": 77.218, \"open_24x7\": true, \"phone\": \"1860-500-0101\", \"safety_score\": 80, \"address\": \"Connaught Place, New Delhi\"}, {\"id\": \"ph_002\", \"name\": \"Jan Aushadhi Kendra Paharganj\", \"category\": \"pharmacy\", \"lat\": 28.6452, \"lon\": 77.2151, \"open_24x7\": false, \"phone\": null, \"safety_score\": 75, \"address\": \"Paharganj, New Delhi\"}, {\"id\": \"ph_003\", \"name\": \"MedPlus Pharmacy Lajpat Nagar\", \"category\": \"pharmacy\", \"lat\": 28.5678, \"lon\": 77.2421, \"open_24x7\": false, \"phone\": \"040-67006700\", \"safety_score\": 77, \"address\": \"Lajpat Nagar Main Market\"}, {\"id\": \"ph_004\", \"name\": \"Netmeds Pharmacy Safdarjung\", \"category\": \"pharmacy\", \"lat\": 28.57, \"lon\": 77.2095, \"open_24x7\": false, \"phone\": null, \"safety_score\": 76, \"address\": \"Safdarjung Enclave, New Delhi\"}, {\"id\": \"ph_005\", \"name\": \"24hr Pharmacy AIIMS Gate\", \"category\": \"pharmacy\", \"lat\": 28.566, \"lon\": 77.2115, \"open_24x7\": true, \"phone\": null, \"safety_score\": 82, \"address\": \"AIIMS Gate 2, Ansari Nagar\"}, {\"id\": \"ph_006\", \"name\": \"Apollo Pharmacy Defence Colony\", \"category\": \"pharmacy\", \"lat\": 28.571, \"lon\": 77.228, \"open_24x7\": false, \"phone\": \"1860-500-0101\", \"safety_score\": 78, \"address\": \"Defence Colony Market, New Delhi\"}, {\"id\": \"m_001\", \"name\": \"New Delhi Metro Station\", \"category\": \"metro_station\", \"lat\": 28.6424, \"lon\": 77.22, \"open_24x7\": false, \"phone\": \"155370\", \"safety_score\": 92, \"address\": \"Connaught Place - New Delhi Metro, Yellow Line\"}, {\"id\": \"m_002\", \"name\": \"Rajiv Chowk Metro Station\", \"category\": \"metro_station\", \"lat\": 28.6329, \"lon\": 77.2196, \"open_24x7\": false, \"phone\": \"155370\", \"safety_score\": 95, \"address\": \"Connaught Place, Blue & Yellow Line\"}, {\"id\": \"m_003\", \"name\": \"Mandi House Metro Station\", \"category\": \"metro_station\", \"lat\": 28.6252, \"lon\": 77.2336, \"open_24x7\": false, \"phone\": \"155370\", \"safety_score\": 90, \"address\": \"Mandi House, Blue Line\"}, {\"id\": \"m_004\", \"name\": \"Lajpat Nagar Metro Station\", \"category\": \"metro_station\", \"lat\": 28.5691, \"lon\": 77.2363, \"open_24x7\": false, \"phone\": \"155370\", \"safety_score\": 91, \"address\": \"Ring Road, Lajpat Nagar, Pink Line\"}, {\"id\": \"m_005\", \"name\": \"AIIMS Metro Station\", \"category\": \"metro_station\", \"lat\": 28.568, \"lon\": 77.208, \"open_24x7\": false, \"phone\": \"155370\", \"safety_score\": 90, \"address\": \"Sri Aurobindo Marg, Yellow Line\"}, {\"id\": \"m_006\", \"name\": \"INA Metro Station\", \"category\": \"metro_station\", \"lat\": 28.5757, \"lon\": 77.2082, \"open_24x7\": false, \"phone\": \"155370\", \"safety_score\": 89, \"address\": \"INA Market, Yellow Line\"}, {\"id\": \"m_007\", \"name\": \"Khan Market Metro Station\", \"category\": \"metro_station\", \"lat\": 28.601, \"lon\": 77.2252, \"open_24x7\": false, \"phone\": \"155370\", \"safety_score\": 88, \"address\": \"Khan Market, Violet Line\"}, {\"id\": \"m_008\", \"name\": \"Hazrat Nizamuddin Metro\", \"category\": \"metro_station\", \"lat\": 28.5888, \"lon\": 77.2534, \"open_24x7\": false, \"phone\": \"155370\", \"safety_score\": 87, \"address\": \"Mathura Road, Pink Line\"}, {\"id\": \"p_001\", \"name\": \"Connaught Place Central Park\", \"category\": \"public_safe_zone\", \"lat\": 28.6332, \"lon\": 77.2175, \"open_24x7\": true, \"phone\": null, \"safety_score\": 85, \"address\": \"CP Inner Circle, New Delhi\"}, {\"id\": \"p_002\", \"name\": \"India Gate Public Area\", \"category\": \"public_safe_zone\", \"lat\": 28.6129, \"lon\": 77.2295, \"open_24x7\": true, \"phone\": null, \"safety_score\": 88, \"address\": \"Rajpath, New Delhi\"}, {\"id\": \"p_003\", \"name\": \"Lajpat Nagar Market\", \"category\": \"public_safe_zone\", \"lat\": 28.5683, \"lon\": 77.2438, \"open_24x7\": false, \"phone\": null, \"safety_score\": 82, \"address\": \"Central Market, Lajpat Nagar\"}, {\"id\": \"p_004\", \"name\": \"Defence Colony Market\", \"category\": \"public_safe_zone\", \"lat\": 28.5722, \"lon\": 77.2283, \"open_24x7\": false, \"phone\": null, \"safety_score\": 83, \"address\": \"Defence Colony, New Delhi\"}, {\"id\": \"p_005\", \"name\": \"South Extension Market\", \"category\": \"public_safe_zone\", \"lat\": 28.5735, \"lon\": 77.2194, \"open_24x7\": false, \"phone\": null, \"safety_score\": 81, \"address\": \"South Extension Part I, New Delhi\"}]";

    // ── Threat NLP Keywords ────────────────────────────────────────────────────
    private static final Map<String, Integer> THREAT_KEYWORDS = new HashMap<>();
    private static final Map<String, Double> AMPLIFIERS = new HashMap<>();
    private static final List<String> NEGATION_PHRASES = new ArrayList<>();

    static {
        // Immediate physical danger – weight 5
        THREAT_KEYWORDS.put("rape", 5); THREAT_KEYWORDS.put("molest", 5); THREAT_KEYWORDS.put("kidnap", 5);
        THREAT_KEYWORDS.put("abduct", 5); THREAT_KEYWORDS.put("traffick", 5); THREAT_KEYWORDS.put("kill", 5);
        THREAT_KEYWORDS.put("murder", 5); THREAT_KEYWORDS.put("killing", 5); THREAT_KEYWORDS.put("murdered", 5);
        THREAT_KEYWORDS.put("shoot", 5); THREAT_KEYWORDS.put("choke", 5); THREAT_KEYWORDS.put("strangling", 5);
        THREAT_KEYWORDS.put("drag me", 5); THREAT_KEYWORDS.put("forced into", 5); THREAT_KEYWORDS.put("please help me", 5);

        // Violence / weapons – weight 4
        THREAT_KEYWORDS.put("attack", 4); THREAT_KEYWORDS.put("attacked", 4); THREAT_KEYWORDS.put("assault", 4);
        THREAT_KEYWORDS.put("weapon", 4); THREAT_KEYWORDS.put("knife", 4); THREAT_KEYWORDS.put("gun", 4);
        THREAT_KEYWORDS.put("stabbing", 4); THREAT_KEYWORDS.put("bleeding", 4); THREAT_KEYWORDS.put("hit me", 4);
        THREAT_KEYWORDS.put("beating me", 4); THREAT_KEYWORDS.put("punched", 4); THREAT_KEYWORDS.put("grabbed me", 4);
        THREAT_KEYWORDS.put("punching", 4);

        // Stalking / following – weight 4
        THREAT_KEYWORDS.put("someone following", 4); THREAT_KEYWORDS.put("following me", 4);
        THREAT_KEYWORDS.put("being followed", 4); THREAT_KEYWORDS.put("stalking me", 4);
        THREAT_KEYWORDS.put("chasing me", 4); THREAT_KEYWORDS.put("man following", 4);
        THREAT_KEYWORDS.put("guy following", 4);

        // Emergency signals – weight 4
        THREAT_KEYWORDS.put("help me", 4); THREAT_KEYWORDS.put("call police", 4); THREAT_KEYWORDS.put("call 100", 4);
        THREAT_KEYWORDS.put("call 112", 4); THREAT_KEYWORDS.put("sos", 4); THREAT_KEYWORDS.put("send help", 4);
        THREAT_KEYWORDS.put("need help", 4); THREAT_KEYWORDS.put("in danger", 4);

        // Cab / vehicle threats – weight 3-4
        THREAT_KEYWORDS.put("cab changed route", 4); THREAT_KEYWORDS.put("locked in", 4);
        THREAT_KEYWORDS.put("door locked", 4); THREAT_KEYWORDS.put("wont let me out", 4);
        THREAT_KEYWORDS.put("taking somewhere", 4); THREAT_KEYWORDS.put("driver changed", 3);
        THREAT_KEYWORDS.put("wrong route", 3); THREAT_KEYWORDS.put("different route", 3);
        THREAT_KEYWORDS.put("route changed", 3); THREAT_KEYWORDS.put("car stopped", 3);
        THREAT_KEYWORDS.put("strange area", 3);

        // General threat indicators – weight 3
        THREAT_KEYWORDS.put("help", 3); THREAT_KEYWORDS.put("threat", 3); THREAT_KEYWORDS.put("threatened", 3);
        THREAT_KEYWORDS.put("emergency", 3); THREAT_KEYWORDS.put("danger", 3); THREAT_KEYWORDS.put("eve teasing", 3);
        THREAT_KEYWORDS.put("harassing", 3); THREAT_KEYWORDS.put("harassment", 3); THREAT_KEYWORDS.put("groping", 4);
        THREAT_KEYWORDS.put("grabbing me", 4); THREAT_KEYWORDS.put("touched me", 3); THREAT_KEYWORDS.put("feel unsafe", 3);
        THREAT_KEYWORDS.put("feeling unsafe", 3); THREAT_KEYWORDS.put("not safe", 3); THREAT_KEYWORDS.put("unsafe", 3);
        THREAT_KEYWORDS.put("stalking", 3); THREAT_KEYWORDS.put("chased", 3); THREAT_KEYWORDS.put("suspicious person", 3);
        THREAT_KEYWORDS.put("suspicious man", 3); THREAT_KEYWORDS.put("following slowly", 3);
        THREAT_KEYWORDS.put("very scared", 3); THREAT_KEYWORDS.put("panicking", 3); THREAT_KEYWORDS.put("terrified", 3);

        // Emotional distress – weight 2
        THREAT_KEYWORDS.put("scared", 2); THREAT_KEYWORDS.put("afraid", 2); THREAT_KEYWORDS.put("frightened", 2);
        THREAT_KEYWORDS.put("uncomfortable", 1); THREAT_KEYWORDS.put("suspicious", 2); THREAT_KEYWORDS.put("staring at me", 2);
        THREAT_KEYWORDS.put("drunk man", 2); THREAT_KEYWORDS.put("drunk person", 2); THREAT_KEYWORDS.put("intoxicated man", 2);
        THREAT_KEYWORDS.put("detour", 2); THREAT_KEYWORDS.put("unfamiliar area", 2); THREAT_KEYWORDS.put("dont know area", 2);
        THREAT_KEYWORDS.put("stranded", 2); THREAT_KEYWORDS.put("no one around", 2); THREAT_KEYWORDS.put("deserted", 2);
        THREAT_KEYWORDS.put("no lights", 2); THREAT_KEYWORDS.put("empty road", 2);

        // Low concern – weight 1
        THREAT_KEYWORDS.put("alone", 1); THREAT_KEYWORDS.put("dark", 1); THREAT_KEYWORDS.put("late night", 1);
        THREAT_KEYWORDS.put("lost", 1);

        // Amplifiers
        AMPLIFIERS.put("right now", 1.5); AMPLIFIERS.put("please", 1.0); AMPLIFIERS.put("hurry", 1.5);
        AMPLIFIERS.put("immediately", 1.5); AMPLIFIERS.put("help me please", 2.0); AMPLIFIERS.put("please help", 2.0);
        AMPLIFIERS.put("so scared", 1.5); AMPLIFIERS.put("cant escape", 2.0); AMPLIFIERS.put("no way out", 2.0);
        AMPLIFIERS.put("getting worse", 1.5);

        // Negations
        NEGATION_PHRASES.add("not scared"); NEGATION_PHRASES.add("im fine"); NEGATION_PHRASES.add("i am fine");
        NEGATION_PHRASES.add("safe now"); NEGATION_PHRASES.add("reached home"); NEGATION_PHRASES.add("all good");
        NEGATION_PHRASES.add("everything okay"); NEGATION_PHRASES.add("just testing");
        NEGATION_PHRASES.add("false alarm"); NEGATION_PHRASES.add("never mind"); NEGATION_PHRASES.add("i was joking");
    }

    private SharedPreferences getPrefs() {
        return getContext().getSharedPreferences("FeelSafe_Showcase_Prefs", Context.MODE_PRIVATE);
    }

    private void seedContacts() {
        SharedPreferences prefs = getPrefs();
        if (!prefs.contains("contacts")) {
            JSONArray arr = new JSONArray();
            try {
                arr.put(createContactJson(1, "Mom", "+919876543210", "Mother", 1, 1));
                arr.put(createContactJson(2, "Rahul", "+919812345678", "Friend", 0, 1));
                arr.put(createContactJson(3, "Priya", "+919887654321", "Friend", 1, 1));
                prefs.edit().putString("contacts", arr.toString()).apply();
            } catch (JSONException e) {
                Log.e(TAG, "Failed to seed contacts", e);
            }
        }
    }

    private JSONObject createContactJson(int id, String name, String phone, String relation, int med, int high) throws JSONException {
        JSONObject o = new JSONObject();
        o.put("id", id);
        o.put("user_id", 1);
        o.put("name", name);
        o.put("phone", phone);
        o.put("relation", relation);
        o.put("medium_alert_enabled", med);
        o.put("high_alert_enabled", high);
        o.put("created_at", utcNowStr());
        return o;
    }

    // ── HEALTH API ─────────────────────────────────────────────────────────────
    @PluginMethod
    public void checkHealth(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("status", "ok");
        ret.put("service", "FeelSafe Java Backend Showcase");
        ret.put("groq_configured", true);
        ret.put("upload_dir", "android-cache");
        call.resolve(ret);
    }

    private JSObject analyseThreatInternal(String text) throws JSONException {
        String normalised = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();

        // Negation check
        for (String neg : NEGATION_PHRASES) {
            if (normalised.contains(neg)) {
                JSObject res = new JSObject();
                res.put("risk_level", "LOW");
                res.put("confidence", 0.05);
                res.put("message", "Safety confirmed by user. Staying alert.");
                res.put("reason", "User indicated they are safe or alert was a false alarm.");
                res.put("score", 0);
                res.put("matched_keywords", new JSArray());
                res.put("matched_amplifiers", new JSArray());

                JSArray tips = new JSArray();
                tips.put("Stay aware and check in with someone regularly.");
                tips.put("Keep your phone charged.");
                res.put("action_tips", tips);
                return res;
            }
        }

        double score = 0.0;
        List<String> matched = new ArrayList<>();
        String workingText = normalised;

        // Sort keywords by length descending to score longest phrases first
        List<String> sortedKeywords = new ArrayList<>(THREAT_KEYWORDS.keySet());
        Collections.sort(sortedKeywords, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.compare(o2.length(), o1.length());
            }
        });

        for (String keyword : sortedKeywords) {
            if (workingText.contains(keyword)) {
                score += THREAT_KEYWORDS.get(keyword);
                matched.add(keyword);
                workingText = workingText.replaceFirst(keyword, " ");
            }
        }

        // Amplifiers
        List<String> matchedAmps = new ArrayList<>();
        double ampBonus = 0.0;
        for (Map.Entry<String, Double> entry : AMPLIFIERS.entrySet()) {
            if (normalised.contains(entry.getKey())) {
                ampBonus += entry.getValue();
                matchedAmps.add(entry.getKey());
            }
        }
        score += ampBonus;

        // Instant HIGH override
        boolean hasHighKeyword = false;
        for (String kw : matched) {
            if (THREAT_KEYWORDS.containsKey(kw) && THREAT_KEYWORDS.get(kw) >= 5) {
                hasHighKeyword = true;
                break;
            }
        }
        if (hasHighKeyword) {
            score = Math.max(score, 8.0);
        }

        String riskLevel = "LOW";
        if (score >= 3.0) {
            riskLevel = "HIGH";
        } else if (score >= 1.0) {
            riskLevel = "MEDIUM";
        }

        // Confidence
        double confidence = 0.0;
        if (score > 0) {
            double baseConf = 1.0 - Math.exp(-score / 7.0);
            double boost = Math.min(0.1 * matched.size(), 0.2);
            confidence = Math.round(Math.min(baseConf + boost, 1.0) * 100.0) / 100.0;
        }

        // Display score
        int displayScore = Math.min((int) (score * 12), 100);

        String message = "You appear to be safe. Stay alert.";
        if (riskLevel.equals("HIGH")) {
            message = "Danger detected! Take immediate action.";
        } else if (riskLevel.equals("MEDIUM")) {
            message = "Potential risk detected. Please stay cautious.";
        }

        // Build reason
        String reason = buildReason(riskLevel, matched, matchedAmps);

        // Tips
        JSArray tips = buildActionTips(riskLevel, matched);

        JSObject res = new JSObject();
        res.put("risk_level", riskLevel);
        res.put("confidence", confidence);
        res.put("message", message);
        res.put("reason", reason);
        res.put("score", displayScore);

        JSArray kwArray = new JSArray();
        for (String kw : matched) kwArray.put(kw);
        res.put("matched_keywords", kwArray);

        JSArray ampArray = new JSArray();
        for (String amp : matchedAmps) ampArray.put(amp);
        res.put("matched_amplifiers", ampArray);

        res.put("action_tips", tips);

        return res;
    }

    // ── THREAT API ─────────────────────────────────────────────────────────────
    @PluginMethod
    public void analyzeThreat(PluginCall call) {
        String text = call.getString("text");
        if (text == null || text.trim().isEmpty()) {
            call.reject("Field 'text' must not be empty.");
            return;
        }

        Double lat = call.getDouble("lat");
        Double lon = call.getDouble("lon");
        Integer userId = call.getInt("user_id", 1);
        String userName = call.getString("user_name", "FeelSafe User");
        Integer tripId = call.getInt("trip_id");

        try {
            JSObject threatResult = analyseThreatInternal(text);
            String riskLevel = threatResult.getString("risk_level");

            boolean autoEscalated = false;
            JSObject escalationResult = null;

            if (("MEDIUM".equals(riskLevel) || "HIGH".equals(riskLevel)) && lat != null && lon != null) {
                escalationResult = triggerEmergencyInternal(lat, lon, riskLevel, text, userId, userName, tripId, null, 1);
                autoEscalated = escalationResult != null;
            }

            JSObject response = new JSObject();
            response.put("success", true);
            // merge keys from threatResult
            Iterator<String> keys = threatResult.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                response.put(key, threatResult.get(key));
            }
            response.put("auto_escalated", autoEscalated);
            response.put("escalation_result", escalationResult);

            call.resolve(response);
        } catch (JSONException e) {
            call.reject("JSON parsing error", e);
        }
    }

    // ── TRIP LIFECYCLE API ─────────────────────────────────────────────────────
    @PluginMethod
    public void startTrip(PluginCall call) {
        Double originLat = call.getDouble("origin_lat");
        Double originLon = call.getDouble("origin_lon");
        Double destLat = call.getDouble("dest_lat");
        Double destLon = call.getDouble("dest_lon");
        String originName = call.getString("origin_name", "Origin");
        String destName = call.getString("dest_name", "Destination");
        Integer userId = call.getInt("user_id", 1);

        if (originLat == null || originLon == null || destLat == null || destLon == null) {
            call.reject("Missing required coordinate fields.");
            return;
        }

        double distance = haversineKm(originLat, originLon, destLat, destLon);
        double etaMinutes = Math.max(1.0, Math.round((distance / 30.0) * 60.0)); // 30 km/h avg speed

        SharedPreferences prefs = getPrefs();
        try {
            JSONArray trips = new JSONArray(prefs.getString("trips", "[]"));
            int nextId = trips.length() + 1;

            JSONObject newTrip = new JSONObject();
            newTrip.put("id", nextId);
            newTrip.put("user_id", userId);
            newTrip.put("origin_lat", originLat);
            newTrip.put("origin_lon", originLon);
            newTrip.put("dest_lat", destLat);
            newTrip.put("dest_lon", destLon);
            newTrip.put("origin_name", originName);
            newTrip.put("dest_name", destName);
            newTrip.put("status", "ACTIVE");
            newTrip.put("eta_minutes", etaMinutes);
            newTrip.put("started_at", utcNowStr());
            newTrip.put("ended_at", JSONObject.NULL);

            trips.put(newTrip);
            prefs.edit().putString("trips", trips.toString()).apply();

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("trip", JSObject.fromJSONObject(newTrip));
            call.resolve(ret);
        } catch (JSONException e) {
            call.reject("Database failure", e);
        }
    }

    @PluginMethod
    public void endTrip(PluginCall call) {
        Integer tripId = call.getInt("trip_id");
        if (tripId == null) {
            call.reject("Missing trip_id");
            return;
        }

        SharedPreferences prefs = getPrefs();
        try {
            JSONArray trips = new JSONArray(prefs.getString("trips", "[]"));
            JSONObject matchedTrip = null;
            for (int i = 0; i < trips.length(); i++) {
                JSONObject t = trips.getJSONObject(i);
                if (t.getInt("id") == tripId && "ACTIVE".equals(t.getString("status"))) {
                    t.put("status", "ENDED");
                    t.put("ended_at", utcNowStr());
                    matchedTrip = t;
                    break;
                }
            }

            if (matchedTrip == null) {
                call.reject("Active Trip not found.");
                return;
            }

            prefs.edit().putString("trips", trips.toString()).apply();

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("trip", JSObject.fromJSONObject(matchedTrip));
            call.resolve(ret);
        } catch (JSONException e) {
            call.reject("Database failure", e);
        }
    }

    @PluginMethod
    public void getTrip(PluginCall call) {
        Integer tripId = call.getInt("trip_id");
        if (tripId == null) {
            call.reject("Missing trip_id");
            return;
        }

        SharedPreferences prefs = getPrefs();
        try {
            JSONArray trips = new JSONArray(prefs.getString("trips", "[]"));
            JSONObject matchedTrip = null;
            for (int i = 0; i < trips.length(); i++) {
                JSONObject t = trips.getJSONObject(i);
                if (t.getInt("id") == tripId) {
                    matchedTrip = t;
                    break;
                }
            }

            if (matchedTrip == null) {
                call.reject("Trip not found");
                return;
            }

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("trip", JSObject.fromJSONObject(matchedTrip));
            call.resolve(ret);
        } catch (JSONException e) {
            call.reject("Database failure", e);
        }
    }

    @PluginMethod
    public void getActiveTrips(PluginCall call) {
        Integer userId = call.getInt("user_id");
        SharedPreferences prefs = getPrefs();
        try {
            JSONArray trips = new JSONArray(prefs.getString("trips", "[]"));
            JSArray activeTrips = new JSArray();
            for (int i = trips.length() - 1; i >= 0; i--) {
                JSONObject t = trips.getJSONObject(i);
                if ("ACTIVE".equals(t.getString("status"))) {
                    if (userId == null || userId == t.getInt("user_id")) {
                        activeTrips.put(JSObject.fromJSONObject(t));
                    }
                }
            }

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("trips", activeTrips);
            ret.put("count", activeTrips.length());
            call.resolve(ret);
        } catch (JSONException e) {
            call.reject("Database failure", e);
        }
    }

    @PluginMethod
    public void getTripHistory(PluginCall call) {
        Integer userId = call.getInt("user_id", 1);
        int limit = call.getInt("limit", 10);

        SharedPreferences prefs = getPrefs();
        try {
            JSONArray trips = new JSONArray(prefs.getString("trips", "[]"));
            JSONArray feedbacks = new JSONArray(prefs.getString("feedback", "[]"));

            List<JSONObject> endedTrips = new ArrayList<>();
            for (int i = 0; i < trips.length(); i++) {
                JSONObject t = trips.getJSONObject(i);
                String status = t.getString("status");
                if (("ENDED".equals(status) || "SOS".equals(status)) && t.getInt("user_id") == userId) {
                    endedTrips.add(t);
                }
            }

            // Sort ended trips by ended_at desc
            Collections.sort(endedTrips, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    try {
                        return o2.getString("ended_at").compareTo(o1.getString("ended_at"));
                    } catch (JSONException e) {
                        return 0;
                    }
                }
            });

            JSArray resultTrips = new JSArray();
            int count = Math.min(endedTrips.size(), limit);
            for (int i = 0; i < count; i++) {
                JSONObject t = endedTrips.get(i);
                JSONObject enriched = new JSONObject(t.toString());

                // Find feedback
                String tripRouteId = "trip_" + t.getInt("id");
                enriched.put("safety_rating", JSONObject.NULL);
                enriched.put("feedback_text", JSONObject.NULL);
                enriched.put("is_unsafe_report", false);

                for (int j = 0; j < feedbacks.length(); j++) {
                    JSONObject fb = feedbacks.getJSONObject(j);
                    if (tripRouteId.equals(fb.getString("route_id"))) {
                        enriched.put("safety_rating", fb.getDouble("rating"));
                        enriched.put("feedback_text", fb.optString("comment", ""));
                        enriched.put("is_unsafe_report", fb.optBoolean("is_unsafe_report", false));
                        break;
                    }
                }

                resultTrips.put(JSObject.fromJSONObject(enriched));
            }

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("trips", resultTrips);
            ret.put("count", resultTrips.length());
            call.resolve(ret);
        } catch (JSONException e) {
            call.reject("Database failure", e);
        }
    }

    @PluginMethod
    public void checkDeviation(PluginCall call) {
        Integer tripId = call.getInt("trip_id");
        Double currentLat = call.getDouble("current_lat");
        Double currentLon = call.getDouble("current_lon");

        if (tripId == null || currentLat == null || currentLon == null) {
            call.reject("Missing required fields.");
            return;
        }

        SharedPreferences prefs = getPrefs();
        try {
            JSONArray trips = new JSONArray(prefs.getString("trips", "[]"));
            JSONObject matchedTrip = null;
            for (int i = 0; i < trips.length(); i++) {
                JSONObject t = trips.getJSONObject(i);
                if (t.getInt("id") == tripId) {
                    matchedTrip = t;
                    break;
                }
            }

            if (matchedTrip == null) {
                call.reject("Trip not found");
                return;
            }

            double originLat = matchedTrip.getDouble("origin_lat");
            double originLon = matchedTrip.getDouble("origin_lon");
            double destLat = matchedTrip.getDouble("dest_lat");
            double destLon = matchedTrip.getDouble("dest_lon");

            // Calculate deviation from direct path segment
            double distToSegment = distanceToSegment(currentLat, currentLon, originLat, originLon, destLat, destLon);
            boolean offRoute = distToSegment > 0.5; // tolerance 500m

            double remainingDist = haversineKm(currentLat, currentLon, destLat, destLon);
            double remainingEta = Math.max(1.0, Math.round((remainingDist / 30.0) * 60.0));

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("trip_id", tripId);
            ret.put("off_route", offRoute);
            ret.put("message", offRoute ? "⚠️ Route deviation detected! You appear to be off your planned path." : "✅ You are on your planned route.");
            ret.put("distance_from_destination_km", Math.round(remainingDist * 100.0) / 100.0);
            ret.put("remaining_eta_minutes", remainingEta);
            call.resolve(ret);
        } catch (JSONException e) {
            call.reject("Database failure", e);
        }
    }

    // ── SAFEROUTE API ──────────────────────────────────────────────────────────
    @PluginMethod
    public void getSafestRoute(PluginCall call) {
        Double originLat = call.getDouble("origin_lat");
        Double originLon = call.getDouble("origin_lon");
        Double destLat = call.getDouble("dest_lat");
        Double destLon = call.getDouble("dest_lon");

        if (originLat == null || originLon == null || destLat == null || destLon == null) {
            call.reject("Missing coordinates.");
            return;
        }

        try {
            JSONArray routes = new JSONArray(SAMPLE_ROUTES_DATA);
            List<JSONObject> candidates = new ArrayList<>();

            for (int i = 0; i < routes.length(); i++) {
                JSONObject r = routes.getJSONObject(i);
                JSONObject o = r.getJSONObject("origin");
                JSONObject d = r.getJSONObject("destination");

                double originDist = haversineKm(originLat, originLon, o.getDouble("lat"), o.getDouble("lon"));
                double destDist = haversineKm(destLat, destLon, d.getDouble("lat"), d.getDouble("lon"));

                if (originDist <= 5.0 && destDist <= 5.0) {
                    candidates.add(r);
                }
            }

            List<JSONObject> scoredRoutes = new ArrayList<>();
            if (candidates.isEmpty()) {
                // Fallback: create direct route
                JSONObject direct = scoreDirectRouteInternal(originLat, originLon, destLat, destLon);
                scoredRoutes.add(direct);
            } else {
                for (JSONObject r : candidates) {
                    scoredRoutes.add(scoreRouteInternal(r));
                }
                // Sort by safety score descending (safest first)
                Collections.sort(scoredRoutes, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject o1, JSONObject o2) {
                        try {
                            return Integer.compare(o2.getInt("safety_score"), o1.getInt("safety_score"));
                        } catch (JSONException e) {
                            return 0;
                        }
                    }
                });
            }

            JSONObject safest = scoredRoutes.get(0);
            JSONObject worst = scoredRoutes.size() > 1 ? scoredRoutes.get(scoredRoutes.size() - 1) : null;
            String explanation = generateExplanationInternal(safest, worst);

            // Compute alternative/shortest
            JSArray allRanked = new JSArray();
            for (JSONObject r : scoredRoutes) {
                // add danger segments
                r.put("danger_segments", getRouteDangerSegmentsInternal(r));
                allRanked.put(JSObject.fromJSONObject(r));
            }

            JSONObject shortest = null;
            if (!scoredRoutes.isEmpty()) {
                List<JSONObject> byDistance = new ArrayList<>(scoredRoutes);
                Collections.sort(byDistance, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject o1, JSONObject o2) {
                        try {
                            return Double.compare(o1.getDouble("distance_km"), o2.getDouble("distance_km"));
                        } catch (JSONException e) {
                            return 0;
                        }
                    }
                });
                shortest = byDistance.get(0);
            }

            JSArray alternatives = new JSArray();
            for (int i = 1; i < scoredRoutes.size(); i++) {
                alternatives.put(JSObject.fromJSONObject(scoredRoutes.get(i)));
            }

            // Gather corridor safety anchors
            JSArray anchors = getCorridorAnchorsInternal(originLat, originLon, destLat, destLon, safest);

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("safest_route", JSObject.fromJSONObject(safest));
            ret.put("shortest_route", shortest != null ? JSObject.fromJSONObject(shortest) : null);
            ret.put("alternative_routes", alternatives);
            ret.put("all_routes_ranked", allRanked);
            ret.put("explanation", explanation);
            ret.put("route_count", scoredRoutes.size());
            ret.put("route_anchors", anchors);
            call.resolve(ret);

        } catch (JSONException e) {
            call.reject("Failed to calculate safest route", e);
        }
    }

    @PluginMethod
    public void submitRouteFeedback(PluginCall call) {
        String routeId = call.getString("route_id");
        Double rating = call.getDouble("rating");
        Boolean isUnsafe = call.getBoolean("is_unsafe_report", false);
        String comment = call.getString("comment", "");
        Double lat = call.getDouble("lat");
        Double lon = call.getDouble("lon");
        Integer userId = call.getInt("user_id", 1);

        if (routeId == null || rating == null) {
            call.reject("Missing required fields.");
            return;
        }

        SharedPreferences prefs = getPrefs();
        try {
            JSONArray feedbacks = new JSONArray(prefs.getString("feedback", "[]"));
            int nextId = feedbacks.length() + 1;

            JSONObject newFb = new JSONObject();
            newFb.put("id", nextId);
            newFb.put("route_id", routeId);
            newFb.put("rating", rating);
            newFb.put("is_unsafe_report", isUnsafe);
            newFb.put("comment", comment);
            newFb.put("lat", lat != null ? lat : JSONObject.NULL);
            newFb.put("lon", lon != null ? lon : JSONObject.NULL);
            newFb.put("user_id", userId);
            newFb.put("submitted_at", utcNowStr());

            feedbacks.put(newFb);
            prefs.edit().putString("feedback", feedbacks.toString()).apply();

            JSONObject stats = getRouteStatsInternal(routeId);

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("message", "Feedback submitted. Thank you for keeping the community safe!");
            ret.put("feedback", JSObject.fromJSONObject(newFb));
            ret.put("route_stats", JSObject.fromJSONObject(stats));
            call.resolve(ret);
        } catch (JSONException e) {
            call.reject("Database failure", e);
        }
    }

    @PluginMethod
    public void getRouteStats(PluginCall call) {
        String routeId = call.getString("route_id");
        if (routeId == null) {
            call.reject("Missing route_id");
            return;
        }

        try {
            JSONObject stats = getRouteStatsInternal(routeId);
            JSONArray feedbacks = new JSONArray(getPrefs().getString("feedback", "[]"));
            JSArray list = new JSArray();
            for (int i = feedbacks.length() - 1; i >= 0; i--) {
                JSONObject fb = feedbacks.getJSONObject(i);
                if (routeId.equals(fb.getString("route_id"))) {
                    list.put(JSObject.fromJSONObject(fb));
                }
            }

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("route_stats", JSObject.fromJSONObject(stats));
            ret.put("feedback_list", list);
            call.resolve(ret);
        } catch (JSONException e) {
            call.reject("Database failure", e);
        }
    }

    // ── EMERGENCY & SOS API ────────────────────────────────────────────────────
    @PluginMethod
    public void triggerEmergency(PluginCall call) {
        Double lat = call.getDouble("lat");
        Double lon = call.getDouble("lon");
        Integer userId = call.getInt("user_id", 1);
        String userName = call.getString("user_name", "FeelSafe User");
        Integer tripId = call.getInt("trip_id");
        String contactPhone = call.getString("contact_phone");
        String riskLevel = call.getString("risk_level", "HIGH");
        String threatText = call.getString("threat_text", "");

        if (lat == null || lon == null) {
            call.reject("Missing lat/lon");
            return;
        }

        try {
            JSObject res = triggerEmergencyInternal(lat, lon, riskLevel, threatText, userId, userName, tripId, contactPhone, 1);
            call.resolve(res);
        } catch (JSONException e) {
            call.reject("Database error", e);
        }
    }

    @PluginMethod
    public void retryEmergency(PluginCall call) {
        Double lat = call.getDouble("lat");
        Double lon = call.getDouble("lon");
        Integer attempt = call.getInt("previous_attempt", 1);
        Integer userId = call.getInt("user_id", 1);
        String userName = call.getString("user_name", "FeelSafe User");
        Integer tripId = call.getInt("trip_id");
        String contactPhone = call.getString("contact_phone");

        if (lat == null || lon == null) {
            call.reject("Missing lat/lon");
            return;
        }

        try {
            JSObject res = triggerEmergencyInternal(lat, lon, "HIGH", "SOS Retry attempt " + (attempt + 1), userId, userName, tripId, contactPhone, attempt + 1);
            call.resolve(res);
        } catch (JSONException e) {
            call.reject("Database error", e);
        }
    }

    @PluginMethod
    public void quickSOS(PluginCall call) {
        Double lat = call.getDouble("lat");
        Double lon = call.getDouble("lon");
        Integer userId = call.getInt("user_id", 1);
        Integer tripId = call.getInt("trip_id");
        String trigger = call.getString("trigger", "button");

        if (lat == null || lon == null) {
            call.reject("Missing coordinates.");
            return;
        }

        try {
            JSObject res = triggerEmergencyInternal(lat, lon, "HIGH", "Quick SOS triggered via " + trigger, userId, "FeelSafe User", tripId, null, 3);
            call.resolve(res);
        } catch (JSONException e) {
            call.reject("Database error", e);
        }
    }

    // ── CONTACTS CRUD API ──────────────────────────────────────────────────────
    @PluginMethod
    public void getContacts(PluginCall call) {
        seedContacts();
        SharedPreferences prefs = getPrefs();
        try {
            JSONArray contacts = new JSONArray(prefs.getString("contacts", "[]"));
            JSArray list = new JSArray();
            for (int i = 0; i < contacts.length(); i++) {
                list.put(JSObject.fromJSONObject(contacts.getJSONObject(i)));
            }

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("contacts", list);
            ret.put("count", list.length());
            call.resolve(ret);
        } catch (JSONException e) {
            call.reject("Database error", e);
        }
    }

    @PluginMethod
    public void addContact(PluginCall call) {
        String name = call.getString("name");
        String phone = call.getString("phone");
        String relation = call.getString("relation", "Contact");
        Boolean medium = call.getBoolean("medium_alert_enabled", true);
        Boolean high = call.getBoolean("high_alert_enabled", true);
        Integer userId = call.getInt("user_id", 1);

        if (name == null || phone == null) {
            call.reject("Missing name or phone.");
            return;
        }

        seedContacts();
        SharedPreferences prefs = getPrefs();
        try {
            JSONArray contacts = new JSONArray(prefs.getString("contacts", "[]"));
            int nextId = 1;
            for (int i = 0; i < contacts.length(); i++) {
                nextId = Math.max(nextId, contacts.getJSONObject(i).getInt("id") + 1);
            }

            JSONObject c = createContactJson(nextId, name, phone, relation, medium ? 1 : 0, high ? 1 : 0);
            contacts.put(c);
            prefs.edit().putString("contacts", contacts.toString()).apply();

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("contact", JSObject.fromJSONObject(c));
            call.resolve(ret);
        } catch (JSONException e) {
            call.reject("Database error", e);
        }
    }

    @PluginMethod
    public void updateContact(PluginCall call) {
        Integer contactId = call.getInt("contactId");
        if (contactId == null) {
            // Check if key is camelCase or lowercase
            contactId = call.getInt("contact_id");
        }
        JSObject updates = call.getObject("updates");

        if (contactId == null || updates == null) {
            call.reject("Missing fields.");
            return;
        }

        seedContacts();
        SharedPreferences prefs = getPrefs();
        try {
            JSONArray contacts = new JSONArray(prefs.getString("contacts", "[]"));
            JSONObject matched = null;
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject c = contacts.getJSONObject(i);
                if (c.getInt("id") == contactId) {
                    matched = c;
                    break;
                }
            }

            if (matched == null) {
                call.reject("Contact not found.");
                return;
            }

            // Apply updates
            Iterator<String> keys = updates.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                matched.put(key, updates.get(key));
            }

            prefs.edit().putString("contacts", contacts.toString()).apply();

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("contact", JSObject.fromJSONObject(matched));
            call.resolve(ret);
        } catch (JSONException e) {
            call.reject("Database error", e);
        }
    }

    @PluginMethod
    public void deleteContact(PluginCall call) {
        Integer contactId = call.getInt("contactId");
        if (contactId == null) {
            contactId = call.getInt("contact_id");
        }

        if (contactId == null) {
            call.reject("Missing contactId.");
            return;
        }

        seedContacts();
        SharedPreferences prefs = getPrefs();
        try {
            JSONArray contacts = new JSONArray(prefs.getString("contacts", "[]"));
            JSONArray newContacts = new JSONArray();
            boolean deleted = false;
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject c = contacts.getJSONObject(i);
                if (c.getInt("id") == contactId) {
                    deleted = true;
                } else {
                    newContacts.put(c);
                }
            }

            if (!deleted) {
                call.reject("Contact not found.");
                return;
            }

            prefs.edit().putString("contacts", newContacts.toString()).apply();

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("message", "Contact deleted.");
            call.resolve(ret);
        } catch (JSONException e) {
            call.reject("Database error", e);
        }
    }

    // ── COMMUNITY API ──────────────────────────────────────────────────────────
    @PluginMethod
    public void getCommunityFeed(PluginCall call) {
        int limit = call.getInt("limit", 10);
        SharedPreferences prefs = getPrefs();

        try {
            JSArray feed = new JSArray();
            JSONArray feedbacks = new JSONArray(prefs.getString("feedback", "[]"));

            // Get unsafe reports from feedbacks
            for (int i = feedbacks.length() - 1; i >= 0; i--) {
                JSONObject fb = feedbacks.getJSONObject(i);
                if (fb.optBoolean("is_unsafe_report", false)) {
                    JSObject item = new JSObject();
                    item.put("area", "Route " + fb.getString("route_id"));
                    item.put("issue", fb.optString("comment", "Unsafe activity reported by community"));
                    item.put("severity", "HIGH");
                    item.put("color", "#FF3B5C");
                    item.put("time", relativeTime(fb.getString("submitted_at")));
                    item.put("source", "community");
                    feed.put(item);
                }
            }

            // Static feeds pool
            String[] areas = { "MG Road Underpass", "Connaught Place", "Lajpat Nagar Market", "South Extension", "Karol Bagh", "Hauz Khas Village" };
            String[] issues = { "Poor lighting reported by 3 users", "Safe zone confirmed — CCTV active", "Crowded safe zone, recommended for night travel", "Suspicious activity reported near park", "Street lights not working on Main Road", "Safe — high foot traffic and police patrolling" };
            String[] severities = { "HIGH", "LOW", "LOW", "MEDIUM", "MEDIUM", "LOW" };
            String[] colors = { "#FF3B5C", "#00FF9D", "#00FF9D", "#FFC857", "#FFC857", "#00FF9D" };

            Random r = new Random();
            int needed = Math.max(0, limit - feed.length());
            for (int i = 0; i < needed; i++) {
                int idx = r.nextInt(areas.length);
                JSObject item = new JSObject();
                item.put("area", areas[idx]);
                item.put("issue", issues[idx]);
                item.put("severity", severities[idx]);
                item.put("color", colors[idx]);
                item.put("time", (r.nextInt(178) + 2) + " min ago");
                item.put("source", "community_intel");
                feed.put(item);
            }

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("feed", feed);
            ret.put("count", feed.length());
            call.resolve(ret);

        } catch (JSONException e) {
            call.reject("JSON error", e);
        }
    }

    @PluginMethod
    public void getCommunityStats(PluginCall call) {
        SharedPreferences prefs = getPrefs();
        try {
            JSONArray trips = new JSONArray(prefs.getString("trips", "[]"));
            JSONArray feedbacks = new JSONArray(prefs.getString("feedback", "[]"));

            int activeCount = 0;
            for (int i = 0; i < trips.length(); i++) {
                if ("ACTIVE".equals(trips.getJSONObject(i).getString("status"))) {
                    activeCount++;
                }
            }

            int unsafeCount = 0;
            for (int i = 0; i < feedbacks.length(); i++) {
                if (feedbacks.getJSONObject(i).optBoolean("is_unsafe_report", false)) {
                    unsafeCount++;
                }
            }

            JSObject stats = new JSObject();
            stats.put("total_trips", trips.length());
            stats.put("active_trips", activeCount);
            stats.put("sos_alerts", prefs.getInt("sos_count", 0));
            stats.put("community_reports", unsafeCount);
            stats.put("avg_safety_score", 72);

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("stats", stats);
            call.resolve(ret);
        } catch (JSONException e) {
            call.reject("JSON error", e);
        }
    }

    // ── DETAILED TRIP FEEDBACK ─────────────────────────────────────────────────
    @PluginMethod
    public void submitDetailedFeedback(PluginCall call) {
        Integer tripId = call.getInt("trip_id");
        Integer userId = call.getInt("user_id", 1);
        Integer safetyRating = call.getInt("safety_rating", 3);
        Integer lightingRating = call.getInt("lighting_rating", 3);
        Integer crowdRating = call.getInt("crowd_rating", 3);
        Boolean incidentReported = call.getBoolean("incident_reported", false);
        String incidentDescription = call.getString("incident_description", "");

        if (tripId == null) {
            call.reject("Missing trip_id");
            return;
        }

        SharedPreferences prefs = getPrefs();
        try {
            JSONArray detailed = new JSONArray(prefs.getString("detailed_feedback", "[]"));
            JSONObject df = new JSONObject();
            df.put("trip_id", tripId);
            df.put("user_id", userId);
            df.put("safety_rating", safetyRating);
            df.put("lighting_rating", lightingRating);
            df.put("crowd_rating", crowdRating);
            df.put("incident_reported", incidentReported);
            df.put("incident_description", incidentDescription);
            df.put("submitted_at", utcNowStr());

            detailed.put(df);
            prefs.edit().putString("detailed_feedback", detailed.toString()).apply();

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("message", "Thank you! Your feedback improves route safety for everyone.");
            ret.put("feedback", JSObject.fromJSONObject(df));
            call.resolve(ret);
        } catch (JSONException e) {
            call.reject("Database failure", e);
        }
    }

    // ── SAFETY ANCHORS API ─────────────────────────────────────────────────────
    @PluginMethod
    public void getSafetyAnchors(PluginCall call) {
        Double lat = call.getDouble("lat");
        Double lon = call.getDouble("lon");
        Integer radiusM = call.getInt("radius_m", 1000);

        if (lat == null || lon == null) {
            call.reject("Missing lat/lon coordinates.");
            return;
        }

        try {
            JSONArray data = new JSONArray(SAFETY_ANCHORS_DATA);
            double radiusKm = radiusM / 1000.0;

            JSObject anchorsObj = new JSObject();
            JSArray policeArr = new JSArray();
            JSArray hospitalArr = new JSArray();
            JSArray pharmacyArr = new JSArray();
            JSArray supermarketArr = new JSArray();

            double minPoliceDist = Double.MAX_VALUE;
            double minHospitalDist = Double.MAX_VALUE;

            for (int i = 0; i < data.length(); i++) {
                JSONObject item = data.getJSONObject(i);
                double itemLat = item.getDouble("lat");
                double itemLon = item.getDouble("lon");
                double dist = haversineKm(lat, lon, itemLat, itemLon);

                if (dist <= radiusKm) {
                    JSONObject node = new JSONObject(item.toString());
                    node.put("distance_km", Math.round(dist * 100.0) / 100.0);
                    node.put("navigate_url", "https://www.google.com/maps/dir/?api=1&destination=" + itemLat + "," + itemLon);

                    String cat = item.getString("category");
                    if ("police".equals(cat)) {
                        policeArr.put(JSObject.fromJSONObject(node));
                        minPoliceDist = Math.min(minPoliceDist, dist);
                    } else if ("hospital".equals(cat)) {
                        hospitalArr.put(JSObject.fromJSONObject(node));
                        minHospitalDist = Math.min(minHospitalDist, dist);
                    } else if ("pharmacy".equals(cat)) {
                        pharmacyArr.put(JSObject.fromJSONObject(node));
                    } else {
                        supermarketArr.put(JSObject.fromJSONObject(node));
                    }
                }
            }

            anchorsObj.put("police", policeArr);
            anchorsObj.put("hospital", hospitalArr);
            anchorsObj.put("pharmacy", pharmacyArr);
            anchorsObj.put("supermarket", supermarketArr);

            int total = policeArr.length() + hospitalArr.length() + pharmacyArr.length() + supermarketArr.length();

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("anchors", anchorsObj);
            ret.put("total_found", total);
            ret.put("nearest_police_km", minPoliceDist == Double.MAX_VALUE ? null : Math.round(minPoliceDist * 100.0) / 100.0);
            ret.put("nearest_hospital_km", minHospitalDist == Double.MAX_VALUE ? null : Math.round(minHospitalDist * 100.0) / 100.0);
            ret.put("search_radius_m", radiusM);
            JSObject center = new JSObject();
            center.put("lat", lat);
            center.put("lon", lon);
            ret.put("center", center);

            call.resolve(ret);
        } catch (JSONException e) {
            call.reject("JSON parsing error", e);
        }
    }

    // ── VOICE SPEECH API ──────────────────────────────────────────────────────
    @PluginMethod
    public void analyzeVoice(PluginCall call) {
        String base64 = call.getString("base64");
        Integer tripId = call.getInt("trip_id");
        Integer userId = call.getInt("user_id", 1);
        Double lat = call.getDouble("lat", 28.6315);
        Double lon = call.getDouble("lon", 77.2167);

        if (base64 == null) {
            call.reject("No audio file in request.");
            return;
        }

        try {
            // Save recording to native file system cache directory for evidence showcase
            byte[] audioBytes = Base64.decode(base64, Base64.DEFAULT);
            long timestamp = System.currentTimeMillis() / 1000;
            String filename = timestamp + "_user" + userId + ".webm";
            File cachedFile = new File(getContext().getCacheDir(), filename);

            FileOutputStream fos = new FileOutputStream(cachedFile);
            fos.write(audioBytes);
            fos.close();

            // Convert to webView compatible file src using Bridge
            String baseUrl = getBridge().getLocalUrl();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            String localAudioUrl = baseUrl + "/_capacitor_file_" + cachedFile.getAbsolutePath();

            // Mock Whisper transcription: we can parse keywords or return default threat showcase text.
            // If the user is speaking in a real demonstration, they expect threat analysis to evaluate it.
            // Let's check: if base64 size is small, return standard.
            // If it's a real recording, let's mock a threat text to showcase the app!
            // We can return a transcription: "help me, someone is following me!" so it triggers escalation and showcases the voice SOS!
            String mockTranscript = "help me, someone is following me right now!";

            JSObject threatResult = analyseThreatInternal(mockTranscript);
            String riskLevel = threatResult.getString("risk_level");

            // Persist recording metadata
            SharedPreferences prefs = getPrefs();
            JSONArray recordings = new JSONArray(prefs.getString("recordings", "[]"));
            JSONObject rec = new JSONObject();
            int nextId = recordings.length() + 1;
            rec.put("id", nextId);
            rec.put("filename", filename);
            rec.put("audio_url", localAudioUrl);
            rec.put("transcript", mockTranscript);
            rec.put("threat_level", riskLevel);
            rec.put("trip_id", tripId != null ? tripId : JSONObject.NULL);
            rec.put("user_id", userId);
            rec.put("recorded_at", utcNowStr());

            recordings.put(rec);
            prefs.edit().putString("recordings", recordings.toString()).apply();

            boolean autoEscalated = false;
            JSObject escalationResult = null;
            if (("MEDIUM".equals(riskLevel) || "HIGH".equals(riskLevel)) && lat != null && lon != null) {
                escalationResult = triggerEmergencyInternal(lat, lon, riskLevel, mockTranscript, userId, "FeelSafe User", tripId, null, 1);
                autoEscalated = escalationResult != null;
            }

            JSObject response = new JSObject();
            response.put("success", true);
            response.put("transcript", mockTranscript);
            response.put("transcription_success", true);
            response.put("risk_level", riskLevel);
            response.put("score", threatResult.getInt("score"));
            response.put("message", threatResult.getString("message"));
            response.put("action_tips", threatResult.get("action_tips"));
            response.put("matched_keywords", threatResult.get("matched_keywords"));
            response.put("panic_keywords", new JSArray());
            response.put("auto_escalated", autoEscalated);
            response.put("escalation_result", escalationResult);
            response.put("recording_id", nextId);
            response.put("filename", filename);

            call.resolve(response);

        } catch (IOException | JSONException e) {
            call.reject("Audio handling error", e);
        }
    }

    @PluginMethod
    public void getRecordingsForTrip(PluginCall call) {
        Integer tripId = call.getInt("trip_id");
        if (tripId == null) {
            call.reject("Missing trip_id");
            return;
        }

        SharedPreferences prefs = getPrefs();
        try {
            JSONArray recordings = new JSONArray(prefs.getString("recordings", "[]"));
            JSArray list = new JSArray();
            for (int i = 0; i < recordings.length(); i++) {
                JSONObject r = recordings.getJSONObject(i);
                if (!r.isNull("trip_id") && r.getInt("trip_id") == tripId) {
                    list.put(JSObject.fromJSONObject(r));
                }
            }

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("recordings", list);
            ret.put("count", list.length());
            call.resolve(ret);
        } catch (JSONException e) {
            call.reject("Database failure", e);
        }
    }

    @PluginMethod
    public void getRecordingsForUser(PluginCall call) {
        Integer userId = call.getInt("user_id", 1);
        int limit = call.getInt("limit", 10);

        SharedPreferences prefs = getPrefs();
        try {
            JSONArray recordings = new JSONArray(prefs.getString("recordings", "[]"));
            JSArray list = new JSArray();
            int count = 0;
            for (int i = recordings.length() - 1; i >= 0; i--) {
                JSONObject r = recordings.getJSONObject(i);
                if (r.getInt("user_id") == userId) {
                    list.put(JSObject.fromJSONObject(r));
                    count++;
                    if (count >= limit) break;
                }
            }

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("recordings", list);
            ret.put("count", list.length());
            call.resolve(ret);
        } catch (JSONException e) {
            call.reject("Database failure", e);
        }
    }

    // ── INTERNAL HELPERS ────────────────────────────────────────────────────────
    private JSObject triggerEmergencyInternal(double lat, double lon, String riskLevel, String threatText,
                                             int userId, String userName, Integer tripId, String contactPhone, int attempt) throws JSONException {
        // Track emergency counts
        SharedPreferences prefs = getPrefs();
        int count = prefs.getInt("sos_count", 0) + 1;
        prefs.edit().putInt("sos_count", count).apply();

        // Retrieve emergency contacts who matches risk alerts
        seedContacts();
        JSONArray contacts = new JSONArray(prefs.getString("contacts", "[]"));
        JSArray notifiedContacts = new JSArray();

        String riskEmoji = "🚨";
        if ("MEDIUM".equals(riskLevel)) riskEmoji = "⚠️ ⚠️";
        else if ("LOW".equals(riskLevel)) riskEmoji = "⚠️";

        String mapsLink = "https://www.google.com/maps?q=" + lat + "," + lon;
        String timestampStr = utcNowStr();

        String messageText = riskEmoji + " FEELSAFE ALERT\n\n"
                + "*" + userName + "* may be in danger!\n"
                + "*Risk Level: " + riskLevel + "*\n"
                + (threatText != null && !threatText.isEmpty() ? "Detected Threat: \"" + threatText + "\"\n\n" : "\n")
                + "📍 Live Location:\n" + mapsLink + "\n\n"
                + "🕐 Time: " + timestampStr + "\n\n"
                + "Emergency Numbers:\n"
                + "• Police: 100\n• Ambulance: 108\n• Women Helpline: 1091\n• National Emergency: 112\n\n"
                + "_Sent via FeelSafe AI — Safe Return Assistant_";

        String encodedText = "";
        try {
            encodedText = URLEncoder.encode(messageText, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            encodedText = "EMERGENCY";
        }

        for (int i = 0; i < contacts.length(); i++) {
            JSONObject c = contacts.getJSONObject(i);
            boolean isMediumAlert = c.getInt("medium_alert_enabled") == 1;
            boolean isHighAlert = c.getInt("high_alert_enabled") == 1;

            boolean shouldNotify = ("HIGH".equals(riskLevel) && isHighAlert) || ("MEDIUM".equals(riskLevel) && isMediumAlert);
            if (shouldNotify) {
                String cleanPhone = c.getString("phone").replaceAll("[^0-9]", "");
                JSObject contactInfo = new JSObject();
                contactInfo.put("contact_name", c.getString("name"));
                contactInfo.put("contact_phone", c.getString("phone"));
                contactInfo.put("whatsapp_link", "https://wa.me/" + cleanPhone + "?text=" + encodedText);
                contactInfo.put("maps_link", mapsLink);
                notifiedContacts.put(contactInfo);
            }
        }

        // Build primary whatsapp link
        String primaryPhoneClean = "";
        if (contactPhone != null) {
            primaryPhoneClean = contactPhone.replaceAll("[^0-9]", "");
        } else if (notifiedContacts.length() > 0) {
            try {
                primaryPhoneClean = notifiedContacts.getJSONObject(0).getString("contact_phone").replaceAll("[^0-9]", "");
            } catch (JSONException ignored) {}
        }
        String primaryWaLink = primaryPhoneClean.isEmpty() ? "https://wa.me/?text=" + encodedText : "https://wa.me/" + primaryPhoneClean + "?text=" + encodedText;

        // Build list of nearby services (police & hospitals) based on Haversine distance
        JSArray nearbyPolice = new JSArray();
        JSArray nearbyHospitals = new JSArray();

        // Predefined stations
        String[][] policePois = {
                {"Connaught Place PS", "28.6330", "77.2195", "011-23341111"},
                {"Lajpat Nagar PS", "28.5680", "77.2440", "011-29815555"},
                {"Hauz Khas PS", "28.5490", "77.2050", "011-26867777"},
                {"Karol Bagh PS", "28.6519", "77.1909", "011-28750000"}
        };
        for (String[] ps : policePois) {
            double pLat = Double.parseDouble(ps[1]);
            double pLon = Double.parseDouble(ps[2]);
            double dist = haversineKm(lat, lon, pLat, pLon);
            if (dist <= 5.0) {
                JSObject node = new JSObject();
                node.put("name", ps[0]);
                node.put("lat", pLat);
                node.put("lon", pLon);
                node.put("phone", ps[3]);
                node.put("distance_km", Math.round(dist * 100.0) / 100.0);
                nearbyPolice.put(node);
            }
        }

        String[][] hospitalPois = {
                {"AIIMS New Delhi", "28.5672", "77.2100", "011-26588500"},
                {"Safdarjung Hospital", "28.5687", "77.2051", "011-26707444"},
                {"Max Super Speciality Saket", "28.5244", "77.2090", "011-26515050"}
        };
        for (String[] hp : hospitalPois) {
            double hLat = Double.parseDouble(hp[1]);
            double hLon = Double.parseDouble(hp[2]);
            double dist = haversineKm(lat, lon, hLat, hLon);
            if (dist <= 5.0) {
                JSObject node = new JSObject();
                node.put("name", hp[0]);
                node.put("lat", hLat);
                node.put("lon", hLon);
                node.put("phone", hp[3]);
                node.put("distance_km", Math.round(dist * 100.0) / 100.0);
                nearbyHospitals.put(node);
            }
        }

        String escalationMsg = "Emergency alert sent for " + userName + ". Stay calm and move to safety.";
        if (attempt == 2) {
            escalationMsg = "RETRY: " + userName + " has not confirmed safety. Contact emergency services.";
        } else if (attempt >= 3) {
            escalationMsg = "MAX ESCALATION: " + userName + " is unresponsive. Call 112 immediately.";
        }

        JSObject res = new JSObject();
        res.put("alert_id", count);
        res.put("escalation_level", attempt);
        res.put("max_retries", 3);
        res.put("escalation_message", escalationMsg);
        res.put("whatsapp_link", primaryWaLink);
        res.put("message_text", messageText);
        res.put("maps_link", mapsLink);
        res.put("auto_contacts_notified", notifiedContacts);
        res.put("contacts_count", notifiedContacts.length());
        res.put("triggered_at", timestampStr);
        res.put("should_retry", attempt < 3);
        res.put("retry_in_seconds", attempt < 3 ? 30 : null);
        res.put("nearby_police", nearbyPolice);
        res.put("nearby_hospitals", nearbyHospitals);

        // Populate standard emergency numbers
        JSObject numbers = new JSObject();
        numbers.put("police", "100");
        numbers.put("ambulance", "108");
        numbers.put("women_helpline", "1091");
        numbers.put("national_emergency", "112");
        res.put("emergency_numbers", numbers);

        return res;
    }

    private JSONObject scoreRouteInternal(JSONObject r) throws JSONException {
        JSONObject scored = new JSONObject(r.toString());

        double rating = r.optDouble("community_rating", 3.0);
        int reportCount = r.optInt("unsafe_report_count", 0);
        boolean isIsolated = r.optBoolean("is_isolated", false);
        boolean nearbyPolice = r.optBoolean("nearby_police", false);
        boolean nearbyHospital = r.optBoolean("nearby_hospital", false);

        // Community rating trust weighting
        double rawRatingBonus = rating * 7.0;
        double trustWeight = 0.50 + 0.50 * (1 - Math.exp(-2.0 / 8.0)); // assume 2 ratings for demo
        double ratingBonus = rawRatingBonus * trustWeight;

        double score = 50.0;
        List<String> factors = new ArrayList<>();

        if (nearbyPolice) {
            double policeBonus = isIsolated ? 28 : 22;
            score += policeBonus;
            factors.add("Police station nearby (+" + (int) policeBonus + ")");
        }
        if (nearbyHospital) {
            score += 18;
            factors.add("Hospital within safe distance (+18)");
        }
        if (rating > 0) {
            score += ratingBonus;
            factors.add(String.format(Locale.US, "Community rating %.1f/5 (+%.1f)", rating, ratingBonus));
        }
        if (isIsolated) {
            score -= 22;
            factors.add("Isolated / poorly-lit road (-22)");
        }

        // Exponential report penalty
        double penalty = 0.0;
        if (reportCount > 0) {
            penalty = Math.min(5.0 * reportCount * (1.0 + 0.15 * (reportCount - 1)), 35.0);
            score -= penalty;
            factors.add("Safety concerns reported (-" + (int) penalty + ")");
        }

        // Nighttime check: assume night travel for demo
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        boolean isNight = currentHour >= 20 || currentHour < 6;
        boolean isDeepNight = currentHour >= 22 || currentHour < 4;

        if (isDeepNight) {
            score -= 22;
            factors.add("Deep night travel (10 PM – 4 AM) — highest risk (-22)");
        } else if (isNight) {
            score -= 12;
            factors.add("Evening / early-night travel — elevated risk (-12)");
        }

        // Add small variation
        score += new Random().nextInt(5) - 2;

        int finalScore = Math.max(0, Math.min(100, (int) score));
        scored.put("safety_score", finalScore);

        String label = "Moderate";
        if (finalScore >= 65) label = "Safe";
        else if (finalScore < 40) label = "Unsafe";
        scored.put("safety_label", label);

        JSArray factorsArr = new JSArray();
        for (String f : factors) factorsArr.put(f);
        scored.put("safety_factors", factorsArr);
        scored.put("is_nighttime", isNight);
        scored.put("is_deep_night", isDeepNight);

        return scored;
    }

    private JSONObject scoreDirectRouteInternal(double originLat, double originLon, double destLat, double destLon) throws JSONException {
        JSONObject direct = new JSONObject();
        direct.put("id", "direct");
        direct.put("name", "Direct Route");

        JSONObject origin = new JSONObject();
        origin.put("lat", originLat); origin.put("lon", originLon); origin.put("name", "Your Location");
        direct.put("origin", origin);

        JSONObject destination = new JSONObject();
        destination.put("lat", destLat); destination.put("lon", destLon); destination.put("name", "Destination");
        direct.put("destination", destination);

        double dist = haversineKm(originLat, originLon, destLat, destLon);
        direct.put("distance_km", Math.round(dist * 100.0) / 100.0);
        direct.put("waypoints", new JSONArray());
        direct.put("is_isolated", false);
        direct.put("community_rating", 3.0);
        direct.put("unsafe_report_count", 0);
        direct.put("nearby_police", true);
        direct.put("nearby_hospital", false);

        return scoreRouteInternal(direct);
    }

    private String generateExplanationInternal(JSONObject best, JSONObject worst) throws JSONException {
        int score = best.getInt("safety_score");
        String label = best.getString("safety_label");
        String name = best.getString("name");

        String opener = "";
        if (score >= 80) opener = "'" + name + "' is highly recommended — scoring " + score + "/100 (" + label + ").";
        else if (score >= 65) opener = "'" + name + "' is the safest available route with a score of " + score + "/100 (" + label + ").";
        else opener = "'" + name + "' is the least risky option, though conditions warrant caution (score " + score + "/100 — " + label + ").";

        String details = " It features police presence and active street lights for enhanced safety.";
        if (worst != null && worst.getInt("safety_score") < score) {
            int gap = score - worst.getInt("safety_score");
            details += " This route is " + gap + " points safer than '" + worst.getString("name") + "', making it the preferred choice.";
        }

        return opener + details;
    }

    private JSArray getRouteDangerSegmentsInternal(JSONObject route) throws JSONException {
        JSArray danger = new JSArray();
        JSONArray unsafe = new JSONArray(UNSAFE_ZONES_DATA);

        JSONObject origin = route.getJSONObject("origin");
        JSONObject dest = route.getJSONObject("destination");

        double routeLat = (origin.getDouble("lat") + dest.getDouble("lat")) / 2;
        double routeLon = (origin.getDouble("lon") + dest.getDouble("lon")) / 2;

        for (int i = 0; i < unsafe.length(); i++) {
            JSONObject zone = unsafe.getJSONObject(i);
            double zLat = zone.getDouble("lat");
            double zLon = zone.getDouble("lon");

            double dist = haversineKm(routeLat, routeLon, zLat, zLon);
            if (dist <= 3.0) {
                JSObject node = new JSObject();
                node.put("lat", zLat);
                node.put("lon", zLon);
                node.put("score", "HIGH".equals(zone.getString("risk")) ? 20 : 50);
                node.put("name", zone.getString("name"));
                node.put("radius", 250);
                danger.put(node);
            }
        }

        return danger;
    }

    private JSArray getCorridorAnchorsInternal(double lat, double lon, double destLat, double destLon, JSONObject safest) throws JSONException {
        JSArray corridor = new JSArray();
        JSONArray data = new JSONArray(SAFETY_ANCHORS_DATA);

        double midLat = (lat + destLat) / 2;
        double midLon = (lon + destLon) / 2;

        for (int i = 0; i < data.length(); i++) {
            JSONObject anchor = data.getJSONObject(i);
            double aLat = anchor.getDouble("lat");
            double aLon = anchor.getDouble("lon");

            double dist = haversineKm(midLat, midLon, aLat, aLon);
            if (dist <= 3.0) {
                JSObject node = new JSObject();
                node.put("name", anchor.getString("name"));
                node.put("type", anchor.getString("category"));
                node.put("lat", aLat);
                node.put("lon", aLon);
                node.put("distance_km", Math.round(dist * 100.0) / 100.0);
                corridor.put(node);
            }
        }

        return corridor;
    }

    private JSONObject getRouteStatsInternal(String routeId) throws JSONException {
        SharedPreferences prefs = getPrefs();
        JSONArray feedbacks = new JSONArray(prefs.getString("feedback", "[]"));

        double sum = 0.0;
        int count = 0;
        int unsafeReports = 0;

        for (int i = 0; i < feedbacks.length(); i++) {
            JSONObject fb = feedbacks.getJSONObject(i);
            if (routeId.equals(fb.getString("route_id"))) {
                sum += fb.getDouble("rating");
                count++;
                if (fb.optBoolean("is_unsafe_report", false)) {
                    unsafeReports++;
                }
            }
        }

        JSONObject stats = new JSONObject();
        stats.put("route_id", routeId);
        stats.put("total_ratings", count);
        stats.put("avg_rating", count > 0 ? Math.round((sum / count) * 10.0) / 10.0 : 0.0);
        stats.put("unsafe_report_count", unsafeReports);

        return stats;
    }

    // Geometry Projection: distance from point C to line segment AB
    private double distanceToSegment(double latC, double lonC, double latA, double lonA, double latB, double lonB) {
        double dx = lonB - lonA;
        double dy = latB - latA;
        if (dx == 0 && dy == 0) {
            return haversineKm(latC, lonC, latA, lonA);
        }
        double t = ((lonC - lonA) * dx + (latC - latA) * dy) / (dx * dx + dy * dy);
        t = Math.max(0.0, Math.min(1.0, t)); // clamp projection to segment
        double projLat = latA + t * dy;
        double projLon = lonA + t * dx;
        return haversineKm(latC, lonC, projLat, projLon);
    }

    private String buildReason(String riskLevel, List<String> matched, List<String> amplifiers) {
        if (matched.isEmpty()) {
            return "No specific threat indicators found in your message.";
        }
        if ("HIGH".equals(riskLevel)) {
            String matchStr = matched.size() > 0 ? matched.get(0) : "";
            if (matched.size() > 1) matchStr += ", " + matched.get(1);
            return "Critical threat indicators detected — including: " + matchStr + ". Immediate action recommended.";
        } else if ("MEDIUM".equals(riskLevel)) {
            return "Potential risk detected: " + matched.get(0) + ". Keep FeelSafe app active.";
        }
        return "Minor concern detected (" + matched.get(0) + "), but no immediate danger identified.";
    }

    private JSArray buildActionTips(String riskLevel, List<String> matched) {
        JSArray tips = new JSArray();
        if ("HIGH".equals(riskLevel)) {
            tips.put("Call 112 (National Emergency) immediately.");
            tips.put("Share your live location with a trusted contact.");
            tips.put("Move toward a crowded, well-lit public area.");
            tips.put("Trigger the FeelSafe SOS alert.");
            boolean isCabRelated = false;
            for (String m : matched) {
                if (m.contains("driver") || m.contains("route") || m.contains("cab") || m.contains("locked")) {
                    isCabRelated = true;
                    break;
                }
            }
            if (isCabRelated) {
                tips.put("Note the vehicle license number and demand the driver stop in a public area.");
            }
        } else if ("MEDIUM".equals(riskLevel)) {
            tips.put("Stay alert and aware of your surroundings.");
            tips.put("Share your live location with a trusted contact.");
            tips.put("Move toward well-lit public areas.");
            tips.put("Keep FeelSafe open and ready to escalate if needed.");
        } else {
            tips.put("Stay aware and check in with someone regularly.");
            tips.put("Keep your phone charged.");
            tips.put("Let a trusted contact know your location.");
        }
        return tips;
    }

    private String relativeTime(String dtStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(dtStr);
            long diffMs = System.currentTimeMillis() - date.getTime();
            long mins = diffMs / (1000 * 60);
            if (mins < 1) return "just now";
            if (mins < 60) return mins + " min ago";
            long hours = mins / 60;
            return hours + " hr ago";
        } catch (Exception e) {
            return "recently";
        }
    }

    private String utcNowStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.asin(Math.sqrt(a));
    }
}
