package edu.umich.si.inteco.tutorial2;


import java.util.List;

import edu.umich.si.inteco.minukucore.event.ActionEvent;
import edu.umich.si.inteco.minukucore.model.DataRecord;

/**
 * Created by mwnewman on 7/14/17.
 */

public class LocationChangeActionEvent extends ActionEvent {

    public LocationChangeActionEvent(String typeOfEvent, List<DataRecord> dataRecords) {
        super(typeOfEvent, dataRecords);
    }

}
