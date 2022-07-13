package org.secuso.privacyfriendlywerwolf.helpers;

import android.content.Context;

import org.secuso.privacyfriendlywerwolf.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Class structure taken from tutorial at http://www.journaldev.com/9942/android-expandablelistview-example-tutorial
 * last access 27th October 2016
 */
public class HelpDataDump {
    private final Context context;

    public HelpDataDump(Context context) {
        this.context = context;
    }

    public LinkedHashMap<String, List<String>> getDataGeneral() {
        LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();

        List<String> general = new ArrayList<String>();
        general.add(context.getResources().getString(R.string.help_whatis_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_whatis), general);

        List<String> roles = new ArrayList<String>();
        roles.add(context.getResources().getString(R.string.help_roles_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_roles), roles);

        List<String> role_villager = new ArrayList<String>();
        role_villager.add(context.getResources().getString(R.string.help_roles_villager_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_roles_villager), role_villager);

        List<String> role_werewolfe = new ArrayList<String>();
        role_werewolfe.add(context.getResources().getString(R.string.help_roles_werewolfe_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_roles_werewolfe), role_werewolfe);

        List<String> role_witch = new ArrayList<String>();
        role_witch.add(context.getResources().getString(R.string.help_roles_witch_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_roles_witch), role_witch);

        List<String> role_seer = new ArrayList<String>();
        role_seer.add(context.getResources().getString(R.string.help_roles_seer_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_roles_seer), role_seer);

        List<String> game_sequence = new ArrayList<String>();
        game_sequence.add(context.getResources().getString(R.string.help_sequence_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_sequence), game_sequence);

        List<String> game_end = new ArrayList<String>();
        game_end.add(context.getResources().getString(R.string.help_end_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_end), game_end);

        List<String> privacy = new ArrayList<String>();
        privacy.add(context.getResources().getString(R.string.help_privacy_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_privacy), privacy);

        List<String> permissions = new ArrayList<String>();
        permissions.add(context.getResources().getString(R.string.help_permission_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_permission), permissions);

        return expandableListDetail;
    }
}
