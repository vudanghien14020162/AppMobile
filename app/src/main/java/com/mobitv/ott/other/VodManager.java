package com.mobitv.ott.other;

import com.mobitv.ott.model.VodDetails;
import com.mobitv.ott.model.VodModel;

import java.util.ArrayList;


public class VodManager {
    private static VodManager ourInstance = null;
    private ArrayList<VodModel> episodeList;
    private VodDetails vodDetails;
    private int currentEpisodeID;
    private String currentVideoType;

    public static VodManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new VodManager();
        }
        return ourInstance;
    }


    public ArrayList<VodModel> getEpisodeList() {
        return episodeList;
    }

    public void setEpisodeList(ArrayList<VodModel> episodeList) {
        this.episodeList = episodeList;
    }

    public int getCurrentEpisodeID() {
        return currentEpisodeID;
    }

    public void setCurrentEpisodeID(int currentEpisodeID) {
        this.currentEpisodeID = currentEpisodeID;
    }

    public VodDetails getVodDetails() {
        return vodDetails;
    }

    public void setVodDetails(VodDetails vodDetails) {
        this.vodDetails = vodDetails;
    }

    public String getCurrentVideoType() {
        return currentVideoType;
    }

    public void setCurrentVideoType(String currentVideoType) {
        this.currentVideoType = currentVideoType;
    }
}
