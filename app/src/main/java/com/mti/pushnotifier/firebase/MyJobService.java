/*
 * Created by Tareq Islam on 6/27/18 8:40 PM
 *
 *  Last modified 6/27/18 8:40 PM
 */

package com.mti.pushnotifier.firebase;

/***
 * Created by Tareq on 27,June,2018.
 */
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class MyJobService extends JobService {

    private static final String TAG = "MyJobService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Performing long running task in scheduled job");
        // TODO(developer): add long running task here.
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

}