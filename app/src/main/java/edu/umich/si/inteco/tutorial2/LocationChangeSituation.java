package edu.umich.si.inteco.tutorial2;


import android.location.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import edu.umich.si.inteco.minuku.logger.Log;
import edu.umich.si.inteco.minuku.manager.MinukuSituationManager;
import edu.umich.si.inteco.minuku.model.LocationDataRecord;
import edu.umich.si.inteco.minukucore.event.ActionEvent;
import edu.umich.si.inteco.minukucore.event.MinukuEvent;
import edu.umich.si.inteco.minukucore.event.StateChangeEvent;
import edu.umich.si.inteco.minukucore.exception.DataRecordTypeNotFound;
import edu.umich.si.inteco.minukucore.model.DataRecord;
import edu.umich.si.inteco.minukucore.model.StreamSnapshot;
import edu.umich.si.inteco.minukucore.situation.Situation;

/**
 * Created by mwnewman on 7/14/17.
 */

public class LocationChangeSituation implements Situation {

    public LocationChangeSituation() {
        try {
            MinukuSituationManager.getInstance().register(this);
        } catch (DataRecordTypeNotFound dataRecordTypeNotFound) {
            dataRecordTypeNotFound.printStackTrace();
        }

    }

    @Override
    public <T extends ActionEvent> T assertSituation(StreamSnapshot streamSnapshot, MinukuEvent minukuEvent) {
        if (minukuEvent instanceof StateChangeEvent) {
            if (shouldShowNotification(streamSnapshot)) {
                List<DataRecord> dataRecords = new ArrayList<>();
                return (T) new LocationChangeActionEvent("LOCATION_CHANGE_SITUATION", dataRecords);
            }
        }
        return null;
    }

    private boolean shouldShowNotification(StreamSnapshot streamSnapshot) {
        LocationDataRecord currentLocationDataRecord = streamSnapshot.getCurrentValue(LocationDataRecord.class);
        LocationDataRecord previousLocationLocationDataRecord = streamSnapshot.getPreviousValue(LocationDataRecord.class);
        if (currentLocationDataRecord == null || previousLocationLocationDataRecord == null) {
            return false;
        }

        Location currentLocation = new Location("");
        currentLocation.setLatitude(currentLocationDataRecord.getLatitude());
        currentLocation.setLongitude(currentLocationDataRecord.getLongitude());

        Location previousLocation = new Location("");
        previousLocation.setLatitude(previousLocationLocationDataRecord.getLatitude());
        previousLocation.setLongitude(previousLocationLocationDataRecord.getLongitude());

        float distanceInMeters = currentLocation.distanceTo(previousLocation);

        if (distanceInMeters == 0.0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Class<? extends DataRecord>> dependsOnDataRecordType() throws DataRecordTypeNotFound {
        List<Class<? extends DataRecord>> dependsOn = new ArrayList<>();
        dependsOn.add(LocationDataRecord.class);
        return dependsOn;
    }

}
