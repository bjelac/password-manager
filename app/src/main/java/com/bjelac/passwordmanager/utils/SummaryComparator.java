package com.bjelac.passwordmanager.utils;

import com.bjelac.passwordmanager.models.IDataModel;

import java.util.Comparator;

public class SummaryComparator implements Comparator<IDataModel> {

    @Override
    public int compare(IDataModel o1, IDataModel o2) {
        if (o1.getSummary() == null || o2.getSummary() == null) {
            return 0;
        } else {
            return o1.getSummary().compareToIgnoreCase(o2.getSummary());
        }
    }
}
