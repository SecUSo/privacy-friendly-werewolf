package org.secuso.privacyfriendlywerwolf.activity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.widget.ExpandableListView;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.helpers.ExpandableListAdapter;
import org.secuso.privacyfriendlywerwolf.helpers.HelpDataDump;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Help activity to show the user a list of FAQs
 *
 * Class structure taken from tutorial at http://www.journaldev.com/9942/android-expandablelistview-example-tutorial
 * last access 27th October 2016
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 */
public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ExpandableListAdapter expandableListAdapter;
        HelpDataDump helpDataDump = new HelpDataDump(this);

        ExpandableListView generalExpandableListView = findViewById(R.id.generalExpandableListView);

        LinkedHashMap<String, List<String>> expandableListDetail = helpDataDump.getDataGeneral();
        List<String> expandableListTitleGeneral = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new ExpandableListAdapter(this, expandableListTitleGeneral, expandableListDetail);
        generalExpandableListView.setAdapter(expandableListAdapter);

        overridePendingTransition(0, 0);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.action_help);
    }

    protected int getNavigationDrawerID() {
        return R.id.nav_help;
    }

}
