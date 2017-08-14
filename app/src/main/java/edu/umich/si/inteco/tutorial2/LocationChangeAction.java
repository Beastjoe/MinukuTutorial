package edu.umich.si.inteco.tutorial2;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import edu.umich.si.inteco.minuku.logger.Log;
import edu.umich.si.inteco.minukucore.event.ShowNotificationEvent;
import edu.umich.si.inteco.minukucore.event.ShowNotificationEventBuilder;
import edu.umich.si.inteco.minukucore.model.DataRecord;


/**
 * Created by mwnewman on 7/14/17.
 */

public class LocationChangeAction {

    public LocationChangeAction() {
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void handleLocationChangeEvent(LocationChangeActionEvent locationChangeActionEvent) {
        Log.d("MKU", "handling change event");
        ShowNotificationEvent evt = new ShowNotificationEventBuilder()
                .setExpirationAction(ShowNotificationEvent.ExpirationAction.DISMISS)
                .setExpirationTimeSeconds(30)
                .setViewToShow(MainActivity.class)
                .setIconID(R.drawable.cast_ic_notification_small_icon)
                .setTitle("Pinggg!")
                .setMessage("You didn't move!")
                .setCategory("")
                .setParams(new HashMap<String, String>())
                .createShowNotificationEvent();
        EventBus.getDefault().post(evt);
    }

}
