package com.massisframework.massis.model.building;

public interface BuildingProgressMonitor {

    public void onFinished();

    public void onUpdate(final double progress, final String msg);
}
